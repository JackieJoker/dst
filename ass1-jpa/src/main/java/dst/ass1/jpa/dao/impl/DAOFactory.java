package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.*;

import javax.persistence.EntityManager;

public class DAOFactory implements IDAOFactory {

    EntityManager em;

    public DAOFactory(EntityManager em) {
        this.em = em;
        AbstractDAO.setEntityManager(em);
    }

    @Override
    public IDriverDAO createDriverDAO() {
        return new DriverDAO();
    }

    @Override
    public IEmploymentDAO createEmploymentDAO() {
        return new EmploymentDAO();
    }

    @Override
    public ILocationDAO createLocationDAO() {
        return new LocationDAO();
    }

    @Override
    public IMatchDAO createMatchDAO() {
        return new MatchDAO();
    }

    @Override
    public IOrganizationDAO createOrganizationDAO() {
        return new OrganizationDAO();
    }

    @Override
    public IRiderDAO createRiderDAO() {
        return new RiderDAO();
    }

    @Override
    public ITripDAO createTripDAO() {
        return new TripDAO();
    }

    @Override
    public ITripInfoDAO createTripInfoDAO() {
        return new TripInfoDAO();
    }

    @Override
    public IVehicleDAO createVehicleDAO() {
        return new VehicleDAO();
    }
}
