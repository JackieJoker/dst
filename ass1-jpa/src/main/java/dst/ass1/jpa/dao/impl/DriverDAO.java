package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.model.IDriver;

import java.util.Collection;

public class DriverDAO extends AbstractDAO<IDriver> implements IDriverDAO {

    public DriverDAO() {
        setClassT(IDriver.class);
    }

    @Override
    public Collection<IDriver> findActiveInMultipleOrganizationsDrivers(Long numberOfOrganizations) {
        try {
            return entityManager
                    .createNamedQuery("activeInMultipleOrganizationsDrivers", IDriver.class)
                    .setParameter("organizations", numberOfOrganizations)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
