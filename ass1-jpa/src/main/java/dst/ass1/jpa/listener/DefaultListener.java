package dst.ass1.jpa.listener;
import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DefaultListener {

    private static int loads;
    private static int updates;
    private static int removes;
    private static int persists;
    //total time to persist (ms)
    private static long total;
    //average time to persist (ms)
    private static double average;
    private static Map<Integer,LocalDateTime> startTimes = new HashMap<>();

    @PostLoad
    private synchronized static void load(Object entity) {
        loads++;
    }

    @PostUpdate
    private synchronized static void update(Object entity) {
        updates++;
    }

    @PostRemove
    private synchronized static void remove(Object entity) {
        removes++;
    }

    @PrePersist
    private synchronized static void prePersist(Object entity) {
        startTimes.put(entity.hashCode(), LocalDateTime.now());
    }

    @PostPersist
    private synchronized static void postPersist(Object entity) {
        Duration persistTime = Duration.between(startTimes.remove(entity.hashCode()), LocalDateTime.now());
        persists++;
        total = total + persistTime.toMillis();
        average = (double) total / persists;
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
