package dst.ass2.aop.logging;

import dst.ass2.aop.IPluginExecutable;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.util.logging.Logger;

@Aspect
public class LoggingAspect {

    @Pointcut("target(plugin) && execution(void dst.ass2.aop.IPluginExecutable.execute()) && !@annotation(dst.ass2.aop.logging.Invisible)")
    public void execute(IPluginExecutable plugin) {
    }

    @Before("execute(plugin)")
    public void before(IPluginExecutable plugin) {
        Class<?> type = plugin.getClass();
        String started = type.getName() + " started to execute";
        Logger logger = getLogger(plugin, type);
        if (logger != null) {
            logger.info(started);
        } else {
            System.out.println(started);
        }
    }

    // return null if logger not found
    private Logger getLogger(IPluginExecutable plugin, Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(Logger.class)) {
                field.setAccessible(true);
                try {
                    return (Logger) field.get(plugin);
                } catch (IllegalAccessException ignored) {

                }
            }
        }
        return null;
    }

    @After("execute(plugin)")
    public void after(IPluginExecutable plugin) {
        Class<?> type = plugin.getClass();
        String finished = type.getName() + " is finished";
        Logger logger = getLogger(plugin, type);
        if (logger != null) {
            logger.info(finished);
        } else {
            System.out.println(finished);
        }
    }
}
