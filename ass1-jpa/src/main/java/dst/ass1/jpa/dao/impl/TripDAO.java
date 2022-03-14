package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;

import java.util.Collection;
import java.util.Date;

public class TripDAO extends AbstractDAO<ITrip> implements ITripDAO {
    public TripDAO() {
        setClassT(ITrip.class);
    }

    @Override
    public Collection<ITrip> findCancelledTrips(Date start, Date end) {
        return null;
    }

}
