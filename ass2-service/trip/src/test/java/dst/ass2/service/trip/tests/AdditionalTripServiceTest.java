package dst.ass2.service.trip.tests;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.tests.TestData;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.DriverNotAvailableException;
import dst.ass2.service.api.trip.ITripService;
import dst.ass2.service.api.trip.MatchDTO;
import dst.ass2.service.api.trip.MoneyDTO;
import dst.ass2.service.trip.TripApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TripApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testdata")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdditionalTripServiceTest implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AdditionalTripServiceTest.class);

    private ApplicationContext ctx;

    @MockBean
    private IMatchingService matchingService;

    private ITripService tripService;
    private IDAOFactory daoFactory;
    private TestData testData;
    private ITripDAO tripDAO;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    @Before
    public void setUp() {
        LOG.info("Test resolving beans from application context");
        daoFactory = ctx.getBean(IDAOFactory.class);
        tripService = ctx.getBean(ITripService.class);
        testData = ctx.getBean(TestData.class);
        tripDAO = daoFactory.createTripDAO();
    }

    @Test
    // Tests both the DAO countMatchesOfDriver method and that the transaction is committed
    public void testMatch_CountSuccesfullMatching() throws Exception {
        Long tripId = testData.trip10Id;

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setFare(getOne());
        matchDTO.setDriverId(testData.driver4Id + 1);
        matchDTO.setVehicleId(testData.vehicle1Id);

        IDriver driver = daoFactory.createDriverDAO().findById(matchDTO.getDriverId());
        Long before = daoFactory.createMatchDAO().countMatchesOfDriver(driver);

        tripService.match(tripId, matchDTO);

        Long after = daoFactory.createMatchDAO().countMatchesOfDriver(driver);
        assertEquals(after, (Long) (before + 1));
    }

    @Test
    // Tests both the DAO countMatchesOfDriver method and that the transaction is rolled back
    public void testMatch_CountExceptionMatching() throws Exception {
        Long tripId = testData.trip10Id;

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setFare(getTen());
        matchDTO.setDriverId(testData.driver4Id);
        matchDTO.setVehicleId(testData.vehicle1Id);

        IDriver driver = daoFactory.createDriverDAO().findById(matchDTO.getDriverId());
        Long before = daoFactory.createMatchDAO().countMatchesOfDriver(driver);

        try {
            tripService.match(tripId, matchDTO);
        } catch (DriverNotAvailableException ex) {
            verify(matchingService, times(1)).queueTripForMatching(any());

            Long after = daoFactory.createMatchDAO().countMatchesOfDriver(driver);
            assertEquals(after, before);
        }
    }

    private MoneyDTO getOne() {
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setCurrency("EUR");
        moneyDTO.setValue(BigDecimal.ONE);
        return moneyDTO;
    }

    private MoneyDTO getTen() {
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setValue(BigDecimal.TEN);
        moneyDTO.setCurrency("EUR");
        return moneyDTO;
    }
}
