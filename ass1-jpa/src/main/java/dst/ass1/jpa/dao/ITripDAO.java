package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.ITrip;

import java.util.Collection;
import java.util.Date;

public interface ITripDAO extends GenericDAO<ITrip> {

    /**
     * Find all canceled trips that were created in the given date range.
     *
     * @param start start of the date range
     * @param end   end of the date range
     * @return the cancelled trips
     */
    Collection<ITrip> findCancelledTrips(Date start, Date end);
}
