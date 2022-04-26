package dst.ass2.aop.tests;

import dst.ass2.aop.IPluginExecutor;
import dst.ass2.aop.PluginExecutorFactory;
import dst.ass2.aop.event.EventBus;
import dst.ass2.aop.event.EventType;
import dst.ass2.aop.util.PluginUtils;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AdditionalTest {
    static final String SIMPLE_PLUGIN = "dst.ass2.aop.sample.SimplePluginExecutable";
    IPluginExecutor executor;
    EventBus eventBus = EventBus.getInstance();

    @BeforeClass
    public static void beforeClass() {
        Assert.assertEquals("Cannot create temporary plugin directory: " + PluginUtils.PLUGINS_DIR.getAbsolutePath(),
                true, PluginUtils.PLUGINS_DIR.isDirectory() || PluginUtils.PLUGINS_DIR.mkdirs());
    }

    @AfterClass
    public static void afterClass() throws IOException {
        FileUtils.forceDeleteOnExit(PluginUtils.PLUGINS_DIR);
    }

    @Before
    public void before() throws IOException {
        PluginUtils.cleanPluginDirectory();
        // Preparing new plugin
        PluginUtils.preparePlugin(PluginUtils.SIMPLE_FILE);

        executor = PluginExecutorFactory.createPluginExecutor();
        executor.monitor(PluginUtils.PLUGINS_DIR);
        executor.start();
        eventBus.reset();
    }

    @After
    public void after() {
        executor.stop();
        eventBus.reset();
        PluginUtils.cleanPluginDirectory();
    }

    // This test verifies that in addition to monitoring for changes, the plugin executor
    // also check the directories once when initializing the application.
    @Test(timeout = PluginUtils.PLUGIN_TEST_TIMEOUT)
    public void checkDirectoriesAtStartup() throws Exception {
        // Periodically check for the plugin to be executed
        while (eventBus.size() != 2) {
            Thread.sleep(100);
        }

        // Verify that the plugin was started and stopped orderly
        assertTrue(SIMPLE_PLUGIN + " was not started properly.", eventBus.has(SIMPLE_PLUGIN, EventType.PLUGIN_START));
        assertTrue(SIMPLE_PLUGIN + " did not finish properly.", eventBus.has(SIMPLE_PLUGIN, EventType.PLUGIN_END));
    }
}
