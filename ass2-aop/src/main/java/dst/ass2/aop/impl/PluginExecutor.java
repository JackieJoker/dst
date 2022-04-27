package dst.ass2.aop.impl;

import dst.ass2.aop.IPluginExecutable;
import dst.ass2.aop.IPluginExecutor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dst.ass2.aop.ObjectCreationException;

public class PluginExecutor implements IPluginExecutor {
    private final Map<File, WatchKey> fileKey;
    private final Map<WatchKey, File> keyFile;
    private WatchService watchService;
    private final Thread monitorThread;
    private final ThreadPoolExecutor executor;

    public PluginExecutor() {
        fileKey = new HashMap<>();
        keyFile = new HashMap<>();
        monitorThread = new Thread(this::monitorPlugins);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(File dir) {
        Path path = dir.toPath();
        try {
            WatchKey watchKey = path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            fileKey.put(dir, watchKey);
            keyFile.put(watchKey, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopMonitoring(File dir) {
        WatchKey watchKey = fileKey.remove(dir);
        keyFile.remove(watchKey);
        watchKey.cancel();
    }

    @Override
    public void start() {
        Set<File> directories = fileKey.keySet();
        List<File> jars = new ArrayList<>();
        for (File directory : directories) {
            File[] files = directory.listFiles(((dir, name) -> name.endsWith(".jar")));
            if (files != null) {
                jars.addAll(Arrays.asList(files));
            }
        }
        for (File jar : jars) {
            executePlugins(jar.toPath());
        }
        monitorThread.start();
    }

    @Override
    public void stop() {
        try {
            monitorThread.interrupt();
            watchService.close();
        } catch (IOException e) {
            System.out.println("There was an IOException during monitoring phase");
        }
    }

    private void monitorPlugins() {
        WatchKey key;
        try {
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                    Path fileRelativePath = (Path) event.context();
                    File directoryFile = keyFile.get(key);
                    Path directoryPath = directoryFile.toPath();
                    Path fileAbsolutePath = directoryPath.resolve(fileRelativePath);

                    // fileAbsolutePath.toFile().length() > 0 ensures that we execut the plugins only ones
                    // in case of double events fired at creation (ENTRY MODIFY + ENTRY CREATE).
                    // This is because some OS creates the file with 0 Bytes and then modify it.
                    // https://stackoverflow.com/questions/39147735/watchservice-fires-entry-modify-sometimes-twice-and-sometimes-once
                    if (fileAbsolutePath.toFile().length() > 0 &&
                            fileAbsolutePath.toString().endsWith(".jar")) {
                        executePlugins(fileAbsolutePath);
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            System.out.println("Plugin Executor stopped");
        }
    }

    private void executePlugins(Path fileAbsolutePath) {
        try {
            URL fileURL = fileAbsolutePath.toUri().toURL();

            URL[] urls = new URL[]{fileURL};
            ClassLoader classLoader = URLClassLoader.newInstance(urls);

            JarFile jarFile = new JarFile(fileAbsolutePath.toFile());
            List<Class<?>> plugins = getPlugins(jarFile, classLoader);
            System.out.println("Plugins: " + plugins);
            for (Class<?> type : plugins) {
                IPluginExecutable object;
                try {
                    object = (IPluginExecutable) createObject(type);
                    executor.execute(object::execute);
                } catch (ObjectCreationException e) {
                    System.out.println("There was an error creating the object");
                }
            }
        } catch (IOException e) {
            System.out.println("There was an IOException during monitoring phase");
        }
    }

    private List<Class<?>> getPlugins(JarFile jar, ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<>();

        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            String file = entry.getName();
            if (file.endsWith(".class")) {
                String classname = file.replace('/', '.').substring(0, file.length() - 6);
                try {
                    Class<?> c = classLoader.loadClass(classname);
                    if (Arrays.asList(c.getInterfaces()).contains(IPluginExecutable.class))
                        classes.add(c);
                } catch (Throwable e) {
                    System.out.println("Failed to instantiate " + classname + " from " + file);
                }
            }
        }
        return classes;
    }

    private <T> T createObject(Class<T> type) throws ObjectCreationException {
        T object;
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();
            return object;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ObjectCreationException("There was an error trying to call the default constructor.");
        }
    }
}
