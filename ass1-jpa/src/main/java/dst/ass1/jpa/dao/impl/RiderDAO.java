package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IMoney;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.impl.Rider;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.*;

public class RiderDAO extends AbstractDAO<IRider> implements IRiderDAO {

    public RiderDAO() {
        setClassT(IRider.class);
    }

    @Override
    public IRider findByEmail(String email) {
        try {
            return entityManager.createNamedQuery("riderByEmail", IRider.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Double getTotalDistanceOfMostRecentRider() {
        try {
            return entityManager
                    .createNamedQuery("sumDistanceOfRiderWithMostRecentTrip", Double.class)
                    .getSingleResult();
        } catch (Exception e) {
            return 0D;
        }
    }

    @Override
    public Map<IRider, Map<String, IMoney>> getRecentSpending() {
        //CriteriaBuilder BoilerPlate
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RecentSpending> cr = cb.createQuery(RecentSpending.class);
        Root<Rider> root = cr.from(Rider.class);
        Join<IRider, ITrip> infos = root.join("trips").join("tripInfo");

        //CURRENT_DATE - 1 MONTH
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date d = cal.getTime();

        //select Rider, Currency, sum(currencyValue)
        cr.multiselect(root, infos.get("total").get("currency"), cb.sum(infos.get("total").get("currencyValue")))
                //Riders who has completed at least one trip in the past 30 days
                .where(cb.greaterThanOrEqualTo(
                        infos.get("completed"),
                        d
                        )
                ).groupBy(root,infos.get("total").get("currency"));

        TypedQuery<RecentSpending> query = entityManager.createQuery(cr);
        return query.getResultStream()
                .collect(SpendingCollector.toMapOfMaps());
    }
}
