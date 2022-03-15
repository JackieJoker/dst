package dst.ass1.jpa.listener;


import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultListener {

    private static int loads;
    private static int updates;
    private static int removes;
    private static int persists;
    //total time to persist
    private static long total;
    //average time to persist
    private static double average;
    private LocalDateTime start;
    private final ReentrantLock lock = new ReentrantLock();

    @PostLoad
    private synchronized static void load(Object entity){
        loads ++;
    }

    @PostUpdate
    private synchronized static void update(Object entity){
        updates ++;
    }

    @PostRemove
    private synchronized static void remove(Object entity){
        removes ++;
    }

    @PrePersist
    private void prePersist(Object entity){
        lock.lock();
        start = LocalDateTime.now();
    }

    @PostPersist
    private void postPersist(Object entity){
        try {
            Duration persistTime = Duration.between(start, LocalDateTime.now());
            persists++;
            total = total + persistTime.getSeconds();
            average = (double) total / persists;
            start = null;
        }
        finally {
            lock.unlock();
        }
    }

    public static int getLoadOperations() {
        return loads;
    }

    public static int getUpdateOperations() {
        return updates;
    }

    public static int getRemoveOperations() {
        return removes;
    }

    public static int getPersistOperations() {
        return persists;
    }

    public static long getOverallTimeToPersist() {
        return total;
    }

    public static double getAverageTimeToPersist() {
        return average;
    }

    /**
     * Clears the internal data structures that are used for storing the operations.
     */
    public static void clear() {
        loads = 0;
        updates = 0;
        removes = 0;
        persists = 0;
        total = 0;
        average = 0;
    }
}
