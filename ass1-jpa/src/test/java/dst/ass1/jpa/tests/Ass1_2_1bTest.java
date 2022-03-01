package dst.ass1.jpa.tests;

import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.util.Constants;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Ass1_2_1bTest extends Ass1_TestBase {

    @Test
    public void namedQuery_returnsCorrectResult() throws Exception {
        TypedQuery<IDriver> singleResultQuery = em.createNamedQuery(Constants.Q_ACTIVE_IN_MULITIPLE_ORGANIZATIONS_DRIVERS, IDriver.class);
        singleResultQuery.setParameter("organizations", 2L);

        List<IDriver> resultList = singleResultQuery.getResultList();
        assertThat("Expected a single result", resultList.size(), is(1));

        IDriver result = resultList.get(0);
        assertThat(result.getName(), is(TestData.DRIVER_1_NAME));

        TypedQuery<IDriver> twoResultsQuery = em.createNamedQuery(Constants.Q_ACTIVE_IN_MULITIPLE_ORGANIZATIONS_DRIVERS, IDriver.class);
        twoResultsQuery.setParameter("organizations", 1L);

        List<IDriver> twoResults = twoResultsQuery.getResultList();
        assertThat("Expected two results", twoResults.size(), is(2));

        List<String> names = map(twoResults, IDriver::getName);
        assertThat(names, hasItems(TestData.DRIVER_1_NAME, TestData.DRIVER_2_NAME));
    }

    @Test
    public void daoFind_returnsCorrectResult() throws Exception {
        IDriverDAO dao = daoFactory.createDriverDAO();
        Collection<IDriver> resultList;

        resultList = dao.findActiveInMultipleOrganizationsDrivers(3L);
        assertThat("Expected an empty result for " + 3, resultList.isEmpty(), is(true));

        resultList = dao.findActiveInMultipleOrganizationsDrivers(4L);
        assertThat("Expected an empty result for " + 4, resultList.isEmpty(), is(true));

        resultList = dao.findActiveInMultipleOrganizationsDrivers(2L);
        assertThat("Expected a single result for " + 2, resultList.size(), is(1));
        IDriver result = resultList.iterator().next();
        assertThat(result.getName(), is(TestData.DRIVER_1_NAME));

        resultList = dao.findActiveInMultipleOrganizationsDrivers(1L);
        assertThat("Expected two results for " + 1, resultList.size(), is(2));
        List<String> names = map(resultList, IDriver::getName);
        assertThat(names, hasItems(TestData.DRIVER_1_NAME, TestData.DRIVER_2_NAME));

    }

}
