package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityTransaction;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class Ass1_2_3b_02Test extends Ass1_TestBase {

    private ITripDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = daoFactory.createTripDAO();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        ITrip trip = dao.findById(testData.trip6Id);
        trip.setState(TripState.CANCELLED);
        em.persist(trip);
        em.flush();
        tx.commit();
    }

    @Test
    public void testFindCanceledEventsForStatusCreatedBetweenStartAndFinish() {
        Collection<ITrip> trips = dao.findCancelledTrips(null, null);
        Collection<Long> ids = map(trips, ITrip::getId);

        assertThat(ids.size(), is(8));
        assertThat(ids, hasItems(testData.trip6Id, testData.trip8Id, testData.trip11Id, testData.trip12Id, testData.trip13Id, testData.trip14Id, testData.trip15Id, testData.trip16Id));
    }

    @Test
    public void testFindCanceledEventsForStatusCreatedBetweenStartAndFinish2() {
        Date start = createDate(2019, 1, 1, 1, 1);
        Collection<ITrip> trips = dao.findCancelledTrips(start, null);
        Collection<Long> ids = map(trips, ITrip::getId);

        assertThat(ids.size(), is(5));
        assertThat(ids, hasItems(testData.trip6Id, testData.trip8Id, testData.trip13Id, testData.trip14Id, testData.trip15Id));
    }

    @Test
    public void testFindCanceledEventsForStatusCreatedBetweenStartAndFinish3() {
        Date start = dao.findById(testData.trip12Id).getCreated();
        Date end = dao.findById(testData.trip15Id).getCreated();
        Collection<ITrip> trips = dao.findCancelledTrips(start, end);

        assertThat(trips.size(), is(5));
        assertThat(map(trips, ITrip::getId), hasItems(testData.trip6Id, testData.trip11Id, testData.trip13Id, testData.trip14Id, testData.trip16Id));
    }

    @Test
    public void testFindCanceledEventsForStatusCreatedBetweenStartAndFinish4() {
        Date start = dao.findById(testData.trip8Id).getCreated();
        Collection<ITrip> cancelledTrips = dao.findCancelledTrips(start, null);

        assertTrue(cancelledTrips.isEmpty());
    }

    private Date createDate(int year, int month, int day, int hours, int minutes) {
        return new GregorianCalendar(year, month, day, hours, minutes).getTime();
    }

}
