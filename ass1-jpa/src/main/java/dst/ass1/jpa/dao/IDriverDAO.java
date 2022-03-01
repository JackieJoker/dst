package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IDriver;

import java.util.Collection;

public interface IDriverDAO extends GenericDAO<IDriver> {


    /**
     * Find all drivers that have active employments (active at least one month) in more than the given number
     * of organizations
     *
     * @param numberOfOrganizations number of organizations
     * @return a collection containing drivers
     */
    Collection<IDriver> findActiveInMultipleOrganizationsDrivers(Long numberOfOrganizations);

}
