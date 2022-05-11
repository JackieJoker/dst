package dst.ass3.event.impl;

import dst.ass3.event.EventProcessingFactory;
import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;


public class EventProcessingEnvironment implements IEventProcessingEnvironment {
    private Time matchingDurationTimeout;
    private SinkFunction<LifecycleEvent> lifecycleEventStreamSink;
    private SinkFunction<MatchingDuration> matchingDurationStreamSink;
    private SinkFunction<AverageMatchingDuration> averageMatchingDurationStreamSink;
    private SinkFunction<MatchingTimeoutWarning> matchingTimeoutWarningStreamSink;
    private SinkFunction<TripFailedWarning> tripFailedWarningStreamSink;
    private SinkFunction<Alert> alertStreamSink;

    private Pattern<LifecycleEvent, ?> getPattern(TripState before, TripState after) {
        return Pattern
                .<LifecycleEvent>begin(before.name())
                .where(new SimpleCondition<>() {
                    @Override
                    public boolean filter(LifecycleEvent lifecycleEvent) throws Exception {
                        return lifecycleEvent.getState().equals(before);
                    }
                })
                .followedBy(after.name())
                .where(new SimpleCondition<>() {
                    @Override
                    public boolean filter(LifecycleEvent lifecycleEvent) throws Exception {
                        return lifecycleEvent.getState().equals(after);
                    }
                });
    }

    @Override
    public void initialize(StreamExecutionEnvironment env) {
        DataStream<ITripEventInfo> sourceTrip = env.addSource(EventProcessingFactory.createEventSourceFunction());
        // Filter and map
        DataStream<LifecycleEvent> lifecycleEvent = sourceTrip
                .filter(t -> t.getRegion() != null)
                .map(LifecycleEvent::new);
        // Add watermarks
        WatermarkStrategy<LifecycleEvent> watermarkStrategy = WatermarkStrategy
                .forGenerator(x -> new PunctuatedAssigner())
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());
        lifecycleEvent = lifecycleEvent.assignTimestampsAndWatermarks(watermarkStrategy);
        lifecycleEvent.addSink(lifecycleEventStreamSink);

        //TODO: when everything works, try to change with lambda
        DataStream<LifecycleEvent> lifecycleById = lifecycleEvent.keyBy(new KeySelector<LifecycleEvent, Long>() {
            @Override
            public Long getKey(LifecycleEvent value) throws Exception {
                return value.getTripId();
            }
        });

        Pattern<LifecycleEvent, ?> createdAndMatched = getPattern(TripState.CREATED, TripState.MATCHED).within(matchingDurationTimeout);
        PatternStream<LifecycleEvent> patternStream = CEP.pattern(lifecycleById, createdAndMatched).inEventTime();
        SingleOutputStreamOperator<MatchingDuration> matchingDuration = patternStream.process(new MatchingDurationCalculator());
        matchingDuration.addSink(matchingDurationStreamSink);
        OutputTag<MatchingTimeoutWarning> outputTag = new OutputTag<>("warning") {
        };
        matchingDuration.getSideOutput(outputTag).addSink(matchingTimeoutWarningStreamSink);

        Pattern<LifecycleEvent, ?> matchedAndQueued = getPattern(TripState.MATCHED, TripState.QUEUED);
        Pattern<LifecycleEvent, ?> matchedAndQueued3Times = Pattern.begin(matchedAndQueued).timesOrMore(3);
        PatternStream<LifecycleEvent> anomalousPattern = CEP.pattern(lifecycleById, matchedAndQueued3Times).inEventTime();
        SingleOutputStreamOperator<TripFailedWarning> tripFailedWarning = anomalousPattern.process(new AnomalousRequestDetection());
        tripFailedWarning.addSink(tripFailedWarningStreamSink);
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
