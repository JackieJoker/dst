package dst.ass2.ioc.tests.di;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Initialize;
import dst.ass2.ioc.di.impl.ObjectContainerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class CustomInitializeTest {

    private IObjectContainerFactory factory;
    private IObjectContainer container;

    @Before
    public void setUp() throws Exception {
        factory = new ObjectContainerFactory();
        container = factory.newObjectContainer(new Properties());
    }

    public static abstract class AbstractComponent {
        boolean superInitializeCalled = false;

        @Initialize
        public void setSuperInitializeCalled() {
            superInitializeCalled = true;
        }
    }

    @Component
    public static class ConcreteComponent extends AbstractComponent {
        boolean initializeCalled = false;

        @Initialize
        public void setInitializeCalled() {
            initializeCalled = true;
        }
    }

    @Test
    public void super_class_initialize_called() throws Exception {
        ConcreteComponent obj = container.getObject(ConcreteComponent.class);

        assertNotNull("getObject(ConcreteComponent.class) returned null", obj);
        assertTrue("The initialize method of the inner class must be called", obj.initializeCalled);
        assertTrue("The initialize method of the super class must be called", obj.superInitializeCalled);
    }

    public static abstract class AbstractComponent2 {
        boolean superInitializeCalled = false;

        @Initialize
        public void sameName() {
            superInitializeCalled = true;
        }
    }

    @Component
    public static class ConcreteComponent2 extends AbstractComponent2 {
        boolean initializeCalled = false;

        @Initialize
        public void sameName() {
            initializeCalled = true;
        }
    }

    @Test
    public void overriden_initialize_called_only_on_inner_class() throws Exception {
        ConcreteComponent2 obj = container.getObject(ConcreteComponent2.class);

        assertNotNull("getObject(ConcreteComponent2.class) returned null", obj);
        assertTrue("The initialize method of the inner class must be called", obj.initializeCalled);
        assertFalse("The initialize method of the super class must not be called", obj.superInitializeCalled);
    }
}
