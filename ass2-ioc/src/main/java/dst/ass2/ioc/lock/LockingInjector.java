package dst.ass2.ioc.lock;

import dst.ass2.ioc.di.annotation.Component;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class LockingInjector implements ClassFileTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockingInjector.class);

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] byteCode;

        try {
            ClassPool classPool = ClassPool.getDefault();
            String dottedClassName = className
                    .replaceAll("/", ".");
            CtClass ctClass = classPool.get(dottedClassName);
            if (!ctClass.hasAnnotation(Component.class)) {
                // The official documentation asks to return null if no changes are made
                return null;
            }
            CtMethod[] methods =  ctClass.getDeclaredMethods();
            for (CtMethod ctMethod: methods) {
                if(ctMethod.hasAnnotation(Lock.class)) {
                    Lock annotation = (Lock) ctMethod.getAnnotation(Lock.class);
                    String value = annotation.value();
                    LOGGER.info("Injecting the lock: " + value + " in the bytecode of the Class: " + dottedClassName);

                    ctMethod.insertBefore("dst.ass2.ioc.lock.LockManager.getInstance().getLock(\"" + value + "\").lock();");
                    ctMethod.insertAfter("dst.ass2.ioc.lock.LockManager.getInstance().getLock(\"" + value + "\").unlock();", true);
                }
            }
            byteCode = ctClass.toBytecode();
            ctClass.detach();
        } catch (NotFoundException | CannotCompileException | IOException | ClassNotFoundException e) {
            throw new IllegalClassFormatException(e.getMessage());
        }
        return byteCode;
    }
}
