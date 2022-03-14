package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.model.ILocation;

import java.util.Collection;

public class LocationDAO extends AbstractDAO<ILocation> implements ILocationDAO {

    public LocationDAO() {
        setClassT(ILocation.class);
    }

    @Override
    public Collection<Long> findReachedLocationIds() {
        return null;
    }
}
