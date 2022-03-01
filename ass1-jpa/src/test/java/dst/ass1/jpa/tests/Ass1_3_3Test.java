package dst.ass1.jpa.tests;

import dst.ass1.jpa.interceptor.SQLInterceptor;
import dst.ass1.jpa.util.Constants;
import org.junit.Test;

import javax.persistence.Query;

import static org.junit.Assert.assertEquals;

public class Ass1_3_3Test extends Ass1_TestBase {

    @Test
    public void sqlInterceptor_countsSelectsCorrectly() {
        em.getTransaction().begin();
        SQLInterceptor.resetCounter();
        assertEquals(0, SQLInterceptor.getSelectCount());

        Query c = em.createQuery("select v from " + Constants.T_VEHICLE + " v");
        c.getResultList();

        assertEquals(0, SQLInterceptor.getSelectCount());

        c = em.createQuery("select distinct l from " + Constants.T_LOCATION + " l");
        c.getResultList();

        assertEquals(1, SQLInterceptor.getSelectCount());

        c = em.createQuery("select t from " + Constants.T_TRIP + " t");
        c.getResultList();
        assertEquals(2, SQLInterceptor.getSelectCount());

        c = em.createQuery("select distinct t from " + Constants.T_TRIP + " t");
        c.getResultList();
        assertEquals(3, SQLInterceptor.getSelectCount());
    }

}
