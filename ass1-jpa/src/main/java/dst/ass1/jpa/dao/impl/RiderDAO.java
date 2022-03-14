package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.IMoney;

import java.util.Map;

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
        return null;
    }
}
