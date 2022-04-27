package dst.ass2.aop.management;

import dst.ass2.aop.IPluginExecutable;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


@Aspect
public class ManagementAspect {
    private final ScheduledExecutorService executor;
    private final Map<IPluginExecutable, ScheduledFuture<?>> schedule;

    public ManagementAspect() {
        executor = Executors.newScheduledThreadPool(5);
        schedule = new HashMap<>();
    }

    @Pointcut("target(plugin) && execution(void dst.ass2.aop.IPluginExecutable.execute()) && @annotation(dst.ass2.aop.management.Timeout)")
    public void execute(IPluginExecutable plugin) {

    }

    // It can also be done using @Around, probably it would be simpler but since we are free
    // to choose our approach i prefer to split between Before and After
    @Before("execute(plugin)")
    public void before(IPluginExecutable plugin) {
        Class<?> type = plugin.getClass();
        try {
            Method execute = type.getDeclaredMethod("execute");
            long timeout = execute.getAnnotation(Timeout.class).value();
            ScheduledFuture<?> future = executor.schedule(() -> interruptPlugin(plugin), timeout, TimeUnit.MILLISECONDS);
            // If the timer is already timed out, doesn't do anything
            schedule.put(plugin, future);
        } catch (NoSuchMethodException e) {
            System.out.println("Execute method not found.");
        }
    }

    @After("execute(plugin)")
    public void after(IPluginExecutable plugin) {
        ScheduledFuture<?> future = schedule.remove(plugin);
        future.cancel(false);
    }

    private void interruptPlugin(IPluginExecutable plugin) {
        plugin.interrupted();
    }
}
