package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.*;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static dst.ass1.jpa.tests.TestData.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class Ass1_2_3a_02Test extends Ass1_TestBase {

    private static final BigDecimal TRIP_INFO_9_VALUE = new BigDecimal(55.0);
    private ITripDAO tripDAO;
    private IRiderDAO riderDAO;

    static Map<Long, Map<String, IMoney>> convertMapKeys(Map<IRider, Map<String, IMoney>> result) {
        return result.entrySet().stream().collect(Collectors.toMap(
            kv -> kv.getKey().getId(),
            kv -> kv.getValue()
        ));
    }

    @Before
    public void setUpDatabase() throws Exception {
        tripDAO = daoFactory.createTripDAO();
        riderDAO = daoFactory.createRiderDAO();

        IMoney total = modelFactory.createMoney();
        total.setCurrencyValue(TRIP_INFO_9_VALUE);
        total.setCurrency(CURRENCY_2);


        EntityTransaction tx = em.getTransaction();
        tx.begin();
        ITrip trip = tripDAO.findById(testData.trip9Id);
        trip.setState(TripState.COMPLETED);
        ITripInfo tripInfo = modelFactory.createTripInfo();
        tripInfo.setTrip(trip);
        tripInfo.setRiderRating(3);
        tripInfo.setDriverRating(3);
        tripInfo.setTotal(total);
        tripInfo.setCompleted(new Date());
        tripInfo.setDistance(2.2);
        trip.setTripInfo(tripInfo);
        em.persist(tripInfo);
        em.flush();
        tx.commit();
    }

    @Test
    public void testRecentspending() {
        final IMoney spendingCurrency1Rider1 = modelFactory.createMoney();
        spendingCurrency1Rider1.setCurrencyValue(TRIP_INFO_1_MONEY_VALUE);
        spendingCurrency1Rider1.setCurrency(CURRENCY_1);

        final IMoney spendingCurrency2Rider1 = modelFactory.createMoney();
        spendingCurrency2Rider1.setCurrencyValue(TRIP_INFO_5_MONEY_VALUE.add(TRIP_INFO_9_VALUE));
        spendingCurrency2Rider1.setCurrency(CURRENCY_2);

        final IMoney spendingCurrency1Rider2 = modelFactory.createMoney();
        spendingCurrency1Rider2.setCurrencyValue(TRIP_INFO_4_MONEY_VALUE);
        spendingCurrency1Rider2.setCurrency(CURRENCY_1);

        final IMoney spendingCurrency2Rider2 = modelFactory.createMoney();
        spendingCurrency2Rider2.setCurrencyValue(TRIP_INFO_2_MONEY_VALUE);
        spendingCurrency2Rider2.setCurrency(CURRENCY_2);

        final IMoney spendingCurrency3Rider3 = modelFactory.createMoney();
        spendingCurrency3Rider3.setCurrencyValue(TRIP_INFO_3_MONEY_VALUE);
        spendingCurrency3Rider3.setCurrency(CURRENCY_3);

        Map<IRider, Map<String, IMoney>> spending = riderDAO.getRecentSpending();

        assertThat(spending.size(), is(3));

        Map<Long, Map<String, IMoney>> mapped = convertMapKeys(spending);

        assertTrue(mapped.containsKey(testData.rider1Id));
        assertTrue(mapped.containsKey(testData.rider2Id));
        assertTrue(mapped.containsKey(testData.rider3Id));

        assertThat(mapped.get(testData.rider1Id).size(), is(2));
        assertThat(mapped.get(testData.rider2Id).size(), is(2));
        assertThat(mapped.get(testData.rider3Id).size(), is(1));

        assertTrue(mapped.get(testData.rider1Id).containsKey(CURRENCY_1));
        assertTrue(mapped.get(testData.rider1Id).containsKey(CURRENCY_2));
        assertTrue(mapped.get(testData.rider2Id).containsKey(CURRENCY_2));
        assertTrue(mapped.get(testData.rider2Id).containsKey(CURRENCY_2));
        assertTrue(mapped.get(testData.rider3Id).containsKey(CURRENCY_3));

        assertEquals(spendingCurrency1Rider1.getCurrencyValue(), scaleDown(mapped.get(testData.rider1Id).get(CURRENCY_1)));
        assertEquals(spendingCurrency2Rider1.getCurrencyValue(), scaleDown(mapped.get(testData.rider1Id).get(CURRENCY_2)));
        assertEquals(spendingCurrency1Rider2.getCurrencyValue(), scaleDown(mapped.get(testData.rider2Id).get(CURRENCY_1)));
        assertEquals(spendingCurrency2Rider2.getCurrencyValue(), scaleDown(mapped.get(testData.rider2Id).get(CURRENCY_2)));
        assertEquals(spendingCurrency3Rider3.getCurrencyValue(), scaleDown(mapped.get(testData.rider3Id).get(CURRENCY_3)));
    }

    @Test
    public void testRecentSpending_onEmptyDatabase() {
        db.truncateTables();
        Map<IRider, Map<String, IMoney>> recentSpending = riderDAO.getRecentSpending();
        assertTrue(recentSpending.isEmpty());
    }

    private BigDecimal scaleDown(IMoney money) {
        return money.getCurrencyValue().setScale(0, RoundingMode.DOWN);
    }


}
