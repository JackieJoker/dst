package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ITrip;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class Ass1_2_3b_01Test extends Ass1_TestBase {

    private ITripDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = daoFactory.createTripDAO();
    }

    @Test
    public void testFindCanceledTripsBetweenStartAndFinish() {
        Collection<ITrip> trips = dao.findCancelledTrips(null, null);
        Collection<Long> ids = map(trips, ITrip::getId);

        assertThat(ids.size(), is(7));
        assertThat(ids, hasItems(testData.trip8Id, testData.trip11Id, testData.trip12Id, testData.trip13Id, testData.trip14Id, testData.trip15Id, testData.trip16Id));
    }

    @Test
    public void testFindCanceledTripsCreatedBetweenStartAndFinish2() {
        Date start = createDate(2019, 1, 1, 1, 1);
        Collection<ITrip> trips = dao.findCancelledTrips(start, null);
        Collection<Long> ids = map(trips, ITrip::getId);

        assertThat(ids.size(), is(4));
        assertThat(ids, hasItems(testData.trip8Id, testData.trip13Id, testData.trip14Id, testData.trip15Id));
    }

    @Test
    public void testFindCanceledTripsForStatusCreatedBetweenStartAndFinish3() {
        Date start = dao.findById(testData.trip15Id).getCreated();
        Collection<ITrip> trips = dao.findCancelledTrips(start, null);
        Collection<Long> ids = map(trips, ITrip::getId);

        assertThat(ids.size(), is(1));
        assertThat(ids, hasItems(testData.trip8Id));
    }

    @Test
    public void testFindCanceledTripsForStatusCreatedBetweenStartAndFinish4() {
        Date end = dao.findById(testData.trip11Id).getCreated();
        Collection<ITrip> trips = dao.findCancelledTrips(null, end);

        assertThat(trips.size(), is(1));
        assertThat(map(trips, ITrip::getId), hasItems(testData.trip12Id));
    }

    @Test
    public void testFindCanceledEventsForStatusCreatedBetweenStartAndFinish5() {
        Date start = dao.findById(testData.trip12Id).getCreated();
        Date end = dao.findById(testData.trip15Id).getCreated();
        Collection<ITrip> trips = dao.findCancelledTrips(start, end);

        assertThat(trips.size(), is(4));
        assertThat(map(trips, ITrip::getId), hasItems(testData.trip11Id, testData.trip13Id, testData.trip14Id, testData.trip16Id));
    }

    @Test
    public void testFindCanceledEventsForStatusCreatedBetweenStartAndFinish6() {
        Date start = dao.findById(testData.trip8Id).getCreated();
        Collection<ITrip> cancelledTrips = dao.findCancelledTrips(start, null);

        assertTrue(cancelledTrips.isEmpty());
    }

    private Date createDate(int year, int month, int day, int hours, int minutes) {
        return new GregorianCalendar(year, month, day, hours, minutes).getTime();
    }

}
