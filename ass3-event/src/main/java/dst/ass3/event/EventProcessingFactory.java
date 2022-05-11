package dst.ass3.event;

/**
 * Creates your {@link IEventProcessingEnvironment} and {@link IEventSourceFunction} implementation instances.
 */
public class EventProcessingFactory {
    public static IEventProcessingEnvironment createEventProcessingEnvironment() {
        return new EventProcessingEnvironment();
    }

    public static IEventSourceFunction createEventSourceFunction() {
        return new EventSourceFunction();
    }
}
