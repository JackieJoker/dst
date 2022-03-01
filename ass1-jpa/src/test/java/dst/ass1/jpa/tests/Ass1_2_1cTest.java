package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.model.*;
import dst.ass1.jpa.util.Constants;
import org.junit.Test;

import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;

import static dst.ass1.jpa.tests.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Ass1_2_1cTest extends Ass1_TestBase {


    @Test
    public void namedQuery_returnsCorrectResult() {
        TypedQuery<Double> q = em.createNamedQuery(Constants.Q_SUM_DISTANCE_MOST_RECENT_TRIP, Double.class);

        Double result = q.getSingleResult();
        assertNotNull(result);
        assertEquals(TRIP_2_DISTANCE + TRIP_4_DISTANCE, result, 0.0);

    }

    @Test
    public void namedQuery_withAdditionalAssociation_returnsCorrectResult() throws Exception {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        ITrip trip = modelFactory.createTrip();
        ITripInfo tripInfo = modelFactory.createTripInfo();
        IMatch match = modelFactory.createMatch();
        IDriverDAO driverDAO = daoFactory.createDriverDAO();
        IDriver driver = driverDAO.findById(testData.driver2Id);
        IRider rider = daoFactory.createRiderDAO().findById(testData.rider1Id);
        ILocationDAO locationDAO = daoFactory.createLocationDAO();
        ILocation pickup = locationDAO.findById(testData.location1Id);
        ILocation destination = locationDAO.findById(testData.location2Id);
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
        trip.setPickup(pickup);
        trip.setDestination(destination);
        trip.setRider(rider);
        trip.setMatch(match);
        trip.setTripInfo(tripInfo);

        double distance = 22.22;
        tripInfo.setDistance(distance);
        tripInfo.setCompleted(new Date());
        tripInfo.setTotal(total);
        tripInfo.setDriverRating(3);
        tripInfo.setRiderRating(4);
        tripInfo.setTrip(trip);

        em.persist(trip);
        em.persist(match);
        em.persist(tripInfo);
        em.flush();
        tx.commit();

        TypedQuery<Double> q = em.createNamedQuery(Constants.Q_SUM_DISTANCE_MOST_RECENT_TRIP, Double.class);

        Double resultList = q.getSingleResult();
        assertNotNull(resultList);
        assertEquals(TRIP_1_DISTANCE + TRIP_5_DISTANCE + distance, resultList, 0.0);
    }

    @Test
    public void daoFind_returnsCorrectResult() {
        double result = daoFactory.createRiderDAO().getTotalDistanceOfMostRecentRider();

        assertEquals(TRIP_2_DISTANCE + TRIP_4_DISTANCE, result, 0.0);
    }

}
