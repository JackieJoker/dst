package dst.ass1.jpa.tests;

import dst.ass1.jpa.model.IPreferences;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.ITrip;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Tests if the IRider and IPreferences relation is implemented correctly.
 */
public class Ass1_1_1_07Test extends Ass1_TestBase {

    @Test
    public void testPreferencesAssociation() {
        IRider rider1 = daoFactory.createRiderDAO().findById(testData.rider1Id);
        assertNotNull(rider1);
        assertEquals(testData.preferences1Id, rider1.getPreferences().getId());

        IRider rider2 = daoFactory.createRiderDAO().findById(testData.rider2Id);
        assertNotNull(rider2);
        assertEquals(testData.preferences2Id, rider2.getPreferences().getId());
    }

    @Test
    public void testPreferencesNonOptionalConstraint() {

        IRider rider1 = daoFactory.createRiderDAO().findById(testData.rider1Id);
        assertNotNull(rider1);
        rider1.setPreferences(null);

        var e = assertThrows(PersistenceException.class, () -> {
            em.getTransaction().begin();
            em.persist(rider1);
            em.flush();
        });
        assertThat(e.getMessage(), containsString("not-null property"));
        assertThat(e.getCause(), is(instanceOf(PropertyValueException.class)));
    }

    @Test
    public void testPreferencesUniqueConstraint() throws Exception {
        IRider rider1 = daoFactory.createRiderDAO().findById(testData.rider1Id);
        IPreferences preferences1 = rider1.getPreferences();

        IRider rider5 = modelFactory.createRider();
        rider5.setEmail("email@example.com");
        rider5.setName("rider5");
        rider5.setTel("tel");
        rider5.setPreferences(preferences1);

        em.getTransaction().begin();
        var missingException = "Persisting the same metadata object with a different course should result in a constraint violation";
        var e = assertThrows(missingException, PersistenceException.class, () -> {
            em.persist(rider5);
            em.flush();
        });
        assertThat(missingException, e.getCause(), is(instanceOf(ConstraintViolationException.class)));
    }

    @Test
    public void testRiderPreferencesDeleteCascade() throws Exception {
        IRider rider = daoFactory.createRiderDAO().findById(testData.rider4Id);
        Long preferencesId = rider.getPreferences().getId();

        em.getTransaction().begin();
        try {
            for (ITrip trip : rider.getTrips()) {
                trip.setRider(null);
            }
            em.remove(rider);
            em.flush();
        } catch (PersistenceException e) {
            throw new AssertionError("Removing a rider should not result in a PersistenceException", e);
        }
        em.getTransaction().commit();

        em.getTransaction().begin();
        IPreferences preferences = em.find(modelFactory.createPreferences().getClass(), preferencesId);
        assertNull("Expected preferences to be null after associated rider was deleted", preferences);
    }


}
