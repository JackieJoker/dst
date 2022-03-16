package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.dao.impl.DAOFactory;
import dst.ass1.jpa.interceptor.SQLInterceptor;
import dst.ass1.jpa.model.*;
import dst.ass1.jpa.util.Constants;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static dst.ass1.jpa.tests.TestData.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class Ass1_2_2Test extends Ass1_TestBase {

    private ILocationDAO locationDAO;

    @Before
    public void setUp() throws Exception {
        // makes sure there's no cache from creating entities
        EntityManager em = orm.createEntityManager();
        IDAOFactory daoFactory = new DAOFactory(em);

        locationDAO = daoFactory.createLocationDAO();
    }

    @Test
    public void testReachedLocationsExists() {
        em.createNamedQuery(Constants.Q_REACHED_LOCATIONS);
        System.out.println(SQLInterceptor.getSelectCount());
    }

    @Test
    public void testReachedLocationsQuery() {
        Collection<Long> ids = locationDAO.findReachedLocationIds();
        System.out.println(SQLInterceptor.getSelectCount());
        assertThat(ids.size(), is(5));
        assertThat(ids, hasItems(LOCATION_1_ID, LOCATION_2_ID, LOCATION_3_ID, LOCATION_4_ID, LOCATION_5_ID));
    }

    @Test
    public void testReachedLocationsQuery_withAdditionalAssociations() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        final Long ADD_REACHED_1 = 12L;
        final Long ADD_REACHED_2 = 13L;
        ILocation location1 = modelFactory.createLocation();
        location1.setLocationId(11L);
        location1.setName("location11");
        ILocation location2 = modelFactory.createLocation();
        location2.setLocationId(ADD_REACHED_1);
        location2.setName("location12");
        ILocation location3 = modelFactory.createLocation();
        location3.setLocationId(ADD_REACHED_2);
        location3.setName("location13");
        ILocation location4 = modelFactory.createLocation();
        location4.setLocationId(14L);
        location4.setName("location14");

        ITrip trip = modelFactory.createTrip();
        ITripInfo tripInfo = modelFactory.createTripInfo();
        IMatch match = modelFactory.createMatch();
        IDriverDAO driverDAO = daoFactory.createDriverDAO();
        IDriver driver = driverDAO.findById(testData.driver2Id);
        IRider rider = daoFactory.createRiderDAO().findById(testData.rider1Id);
        IVehicle vehicle = daoFactory.createVehicleDAO().findById(testData.vehicle1Id);
        IMoney fare = modelFactory.createMoney();
        fare.setCurrency(CURRENCY_1);
        fare.setCurrencyValue(BigDecimal.TEN);
        IMoney total = modelFactory.createMoney();
        total.setCurrency(CURRENCY_1);
        total.setCurrencyValue(BigDecimal.TEN);

        match.setDate(new Date());
        match.setFare(fare);
        match.setDriver(driver);
        match.setVehicle(vehicle);
        match.setTrip(trip);

        trip.setCreated(new Date());
        trip.setUpdated(new Date());
        trip.setState(TripState.COMPLETED);
        trip.setPickup(location1);
        trip.setDestination(location2);
        trip.setRider(rider);
        trip.setMatch(match);
        trip.setTripInfo(tripInfo);
        List<ILocation> stops = new LinkedList<>();
        stops.add(location3);
        trip.setStops(stops);

        tripInfo.setDistance(22.22);
        tripInfo.setCompleted(createDate(2016, 2, 23, 2, 2));
        tripInfo.setTotal(total);
        tripInfo.setDriverRating(3);
        tripInfo.setRiderRating(4);
        tripInfo.setTrip(trip);

        em.persist(location1);
        em.persist(location2);
        em.persist(location3);
        em.persist(location4);
        em.persist(trip);
        em.persist(match);
        em.persist(tripInfo);
        em.flush();
        tx.commit();

        Collection<Long> ids = locationDAO.findReachedLocationIds();

        System.out.println(SQLInterceptor.getSelectCount());

        assertThat(ids, hasItems(LOCATION_1_ID, LOCATION_2_ID, LOCATION_3_ID, LOCATION_4_ID, LOCATION_5_ID, ADD_REACHED_1, ADD_REACHED_2));
    }

    @Test
    public void testReachedLocationsQuery_onEmptyDatabase() {
        db.truncateTables();
        Collection<Long> ids = locationDAO.findReachedLocationIds();
        System.out.println(SQLInterceptor.getSelectCount());
        assertTrue(ids.isEmpty());
    }


}
