package dst.ass1.jpa.tests;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.listener.DefaultListener;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.IVehicle;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

public class Ass1_3_2Test {

    @Rule
    public ORMService orm = new ORMService();

    @Test
    public void testDefaultListener() {
        DefaultListener.clear();

        IDAOFactory daoFactory = orm.getDaoFactory();
        IModelFactory modelFactory = orm.getModelFactory();
        EntityManager em = orm.getEntityManager();

        em.getTransaction().begin();

        // do some inserts
        IVehicle vehicle = modelFactory.createVehicle();
        vehicle.setLicense(TestData.VEHICLE_1_LICENSE);
        vehicle.setType(TestData.TYPE_1);
        vehicle.setColor(TestData.COLOR_1);
        em.persist(vehicle);
        em.flush();
        em.remove(vehicle);
        em.flush();

        vehicle = modelFactory.createVehicle();
        vehicle.setLicense(TestData.VEHICLE_2_LICENSE);
        vehicle.setType(TestData.TYPE_2);
        vehicle.setColor(TestData.COLOR_2);
        em.persist(vehicle);

        assertEquals(1, DefaultListener.getRemoveOperations());
        assertTrue(DefaultListener.getPersistOperations() > 0);
        assertEquals((double) DefaultListener.getOverallTimeToPersist() / DefaultListener.getPersistOperations(), DefaultListener.getAverageTimeToPersist(), 0.6);

        List<IVehicle> vehicles = daoFactory.createVehicleDAO().findAll();
        assertNotNull(vehicles);
        assertFalse(vehicles.isEmpty());

        int loadOperations = DefaultListener.getLoadOperations();
        em.refresh(vehicles.get(0));
        assertEquals(loadOperations + 1, DefaultListener.getLoadOperations());

        vehicles = daoFactory.createVehicleDAO().findAll();
        assertNotNull(vehicles);
        assertFalse(vehicles.isEmpty());

        vehicles.get(0).setLicense("updated");
        em.persist(vehicles.get(0));
        em.flush();

        assertEquals(1, DefaultListener.getUpdateOperations());
    }
}
