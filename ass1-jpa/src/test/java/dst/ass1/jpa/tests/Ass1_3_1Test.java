package dst.ass1.jpa.tests;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

public class Ass1_3_1Test {

    @Rule
    public ORMService orm = new ORMService();

    @Test
    public void entityListener_prePersistSetsPropertiesCorrectly() throws InterruptedException {
        Date then = new Date();
        Thread.sleep(50);

        EntityManager em = orm.getEntityManager();

        ILocation pickup = orm.getModelFactory().createLocation();
        pickup.setName("name1");
        pickup.setLocationId(1L);

        ILocation destination = orm.getModelFactory().createLocation();
        destination.setName("name2");
        destination.setLocationId(2L);


        ITrip trip = orm.getModelFactory().createTrip();
        trip.setState(TripState.CREATED);
        trip.setPickup(pickup);
        trip.setDestination(destination);


        EntityTransaction tx;
        // persist
        tx = em.getTransaction();
        tx.begin();
        em.persist(pickup);
        em.persist(destination);
        em.persist(trip);
        em.flush();
        tx.commit();

        assertNotNull(trip.getCreated());
        assertNotNull(trip.getUpdated());

        assertThat(trip.getCreated(), greaterThan(then));
        assertThat(trip.getUpdated(), greaterThan(then));
    }


    @Test
    public void entityListener_preUpdateSetsPropertiesCorrectly() throws InterruptedException {
        EntityManager em = orm.getEntityManager();

        ILocation pickup = orm.getModelFactory().createLocation();
        pickup.setName("name1");
        pickup.setLocationId(1L);

        ILocation destination = orm.getModelFactory().createLocation();
        destination.setName("name2");
        destination.setLocationId(2L);


        ITrip trip = orm.getModelFactory().createTrip();
        trip.setCreated(new Date());
        trip.setUpdated(new Date());
        trip.setState(TripState.CREATED);
        trip.setPickup(pickup);
        trip.setDestination(destination);

        EntityTransaction tx;
        // persist
        tx = em.getTransaction();
        tx.begin();
        em.persist(pickup);
        em.persist(destination);
        em.persist(trip);
        em.flush();
        tx.commit();

        Date then = new Date();

        Thread.sleep(2000);

        // update
        tx = em.getTransaction();
        tx.begin();
        trip.setState(TripState.QUEUED);
        em.persist(trip);
        em.flush();
        tx.commit();

        assertNotNull(trip.getUpdated());
        assertThat(trip.getUpdated(), greaterThan(then));
    }

}
