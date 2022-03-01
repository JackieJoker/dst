package dst.ass1.jpa.tests;

import dst.ass1.jpa.ORMService;
import dst.ass1.jpa.model.IPreferences;
import dst.ass1.jpa.util.Constants;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests that IPreferences is persisted correctly.
 */
public class Ass1_1_1_06Test {

    @Rule
    public ORMService orm = new ORMService();

    @Test
    public void testPreferencesMap() {
        IPreferences pref1 = orm.getModelFactory().createPreferences();

        pref1.putData("key1", "value1");
        pref1.putData("key2", "value2");

        EntityManager em = orm.getEntityManager();
        em.getTransaction().begin();

        em.persist(pref1);
        em.flush();
        em.getTransaction().commit();

        EntityManager em2 = orm.createEntityManager();
        IPreferences md2 = em2.find(pref1.getClass(), pref1.getId());

        Map<String, String> map = md2.getData();

        assertThat(map.size(), is(2));
        assertThat(map.keySet(), hasItems("key1", "key2"));
        assertThat(map.get("key1"), is("value1"));
        assertThat(map.get("key2"), is("value2"));
    }

    @Test
    public void testPreferencesMapJdbc() {
        assertTrue(orm.getDatabaseGateway().isTable(Constants.T_PREFERENCES));
        assertTrue(orm.getDatabaseGateway().isTable(Constants.J_PREFERENCES_DATA));
        assertTrue(orm.getDatabaseGateway().isColumnInTable(Constants.J_PREFERENCES_DATA, Constants.I_PREFERENCES));
        assertTrue(orm.getDatabaseGateway().isColumnInTable(Constants.J_PREFERENCES_DATA, Constants.M_PREFERENCES_DATA + "_KEY"));
    }

}
