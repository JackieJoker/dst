package dst.ass3.event;

import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

public class EventProcessingEnvironment implements IEventProcessingEnvironment {
    private Time matchingDurationTimeout;
    private SinkFunction<LifecycleEvent> lifecycleEventStreamSink;
    private SinkFunction<MatchingDuration> matchingDurationStreamSink;
    private SinkFunction<AverageMatchingDuration> averageMatchingDurationStreamSink;
    private SinkFunction<MatchingTimeoutWarning> matchingTimeoutWarningStreamSink;
    private SinkFunction<TripFailedWarning> tripFailedWarningStreamSink;
    private SinkFunction<Alert> alertStreamSink;

    @Override
    public void initialize(StreamExecutionEnvironment env) {
        DataStream<ITripEventInfo> sourceTrip = env.addSource(EventProcessingFactory.createEventSourceFunction());
        // Filter and map
        DataStream<LifecycleEvent> outputLifecycle = sourceTrip
                .filter(t -> t.getRegion() != null)
                .map(LifecycleEvent::new);
        // Add watermarks
        WatermarkStrategy<LifecycleEvent> watermarkStrategy = WatermarkStrategy
                .forGenerator(x -> new PunctuatedAssigner())
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());
        outputLifecycle.assignTimestampsAndWatermarks(watermarkStrategy);
        outputLifecycle.addSink(lifecycleEventStreamSink);

        // If it doesn't work, I can switch to non-lambda version

//        DataStream<ITripEventInfo> filteredTrip = sourceTrip.filter(new FilterFunction<ITripEventInfo>() {
//            @Override
//            public boolean filter(ITripEventInfo iTripEventInfo) throws Exception {
//                return (iTripEventInfo.getRegion() != null);
//            }
//        });
//        DataStream<LifecycleEvent> eventStream = filteredTrip.map(new MapFunction<ITripEventInfo, LifecycleEvent>() {
//            @Override
//            public LifecycleEvent map(ITripEventInfo iTripEventInfo) throws Exception {
//                return new LifecycleEvent(iTripEventInfo);
//            }
//        });
//        eventStream.addSink(lifecycleEventStreamSink);
    }

    @Override
    public void setMatchingDurationTimeout(Time time) {
        matchingDurationTimeout = time;
    }

    @Override
    public void setLifecycleEventStreamSink(SinkFunction<LifecycleEvent> sink) {
        lifecycleEventStreamSink = sink;
    }

    @Override
    public void setMatchingDurationStreamSink(SinkFunction<MatchingDuration> sink) {
        matchingDurationStreamSink = sink;
    }

    @Override
    public void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink) {
        averageMatchingDurationStreamSink = sink;
    }

    @Override
    public void setMatchingTimeoutWarningStreamSink(SinkFunction<MatchingTimeoutWarning> sink) {
        matchingTimeoutWarningStreamSink = sink;
    }

    @Override
    public void setTripFailedWarningStreamSink(SinkFunction<TripFailedWarning> sink) {
        tripFailedWarningStreamSink = sink;
    }

    @Override
    public void setAlertStreamSink(SinkFunction<Alert> sink) {
        alertStreamSink = sink;
    }
}
