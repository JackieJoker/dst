package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.impl.Trip;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TripDAO extends AbstractDAO<ITrip> implements ITripDAO {
    public TripDAO() {
        setClassT(ITrip.class);
    }

    @Override
    public Collection<ITrip> findCancelledTrips(Date start, Date end) {
        //CriteriaBuilder BoilerPlate
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ITrip> cr = cb.createQuery(ITrip.class);
        Root<Trip> root = cr.from(Trip.class);

        //Constructing list of parameters
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("state"), TripState.CANCELLED));
        //Adding optional parameters
        if(start != null) {
            predicates.add(cb.greaterThan(root.get("created"), start));
        }
        if(end != null) {
            predicates.add(cb.lessThan(root.get("created"), end));
        }

        cr.select(root).where(predicates.toArray(new Predicate[]{}));

        TypedQuery<ITrip> query = entityManager.createQuery(cr);
        return query.getResultList();
    }
}
