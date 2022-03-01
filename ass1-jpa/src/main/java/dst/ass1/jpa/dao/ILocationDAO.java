package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.ILocation;

import java.util.Collection;

public interface ILocationDAO extends GenericDAO<ILocation> {

    /**
     * Finds all distinct Location.locationId that have been reached as a Trip destination or additional
     * stops. Only takes completed trips in account.
     *
     * @return a list containing Location.locationIds
     */
    Collection<Long> findReachedLocationIds();
}
