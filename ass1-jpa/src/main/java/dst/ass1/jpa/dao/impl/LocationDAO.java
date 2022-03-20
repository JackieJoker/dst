package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.model.ILocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LocationDAO extends AbstractDAO<ILocation> implements ILocationDAO {

    public LocationDAO() {
        setClassT(ILocation.class);
    }

    @Override
    public Collection<Long> findReachedLocationIds() {
        Set<Long> locations = new HashSet<>();
        try{
            locations.addAll(entityManager.createNamedQuery("reachedLocations", Long.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            locations.addAll(entityManager.createNamedQuery("reachedLocations2", Long.class).getResultList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }
}
