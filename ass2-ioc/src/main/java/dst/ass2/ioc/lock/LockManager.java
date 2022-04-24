package dst.ass2.ioc.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    private static final LockManager lockManager = new LockManager();
    private static final Map<String, Lock> managedLocks = new HashMap<>();

    private LockManager() {
    }

    public static LockManager getInstance() {
        return lockManager;
    }

    public synchronized Lock getLock(String name) {
        if (managedLocks.containsKey(name)) {
            return managedLocks.get(name);
        }
        else {
            // It works recursively using a counter
            Lock newLock = new ReentrantLock();
            managedLocks.put(name, newLock);
            return newLock;
        }
    }
}
