package dst.ass3.event.impl;

import dst.ass3.event.EventProcessingFactory;
import dst.ass3.event.IEventProcessingEnvironment;
import dst.ass3.event.model.domain.ITripEventInfo;
import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

import java.util.List;


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
        DataStream<LifecycleEvent> lifecycleEvent = sourceTrip
                .filter(t -> t.getRegion() != null)
                .map(LifecycleEvent::new);
        // Add watermarks
        WatermarkStrategy<LifecycleEvent> watermarkStrategy = WatermarkStrategy
                .forGenerator(x -> new PunctuatedAssigner())
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());
        lifecycleEvent = lifecycleEvent.assignTimestampsAndWatermarks(watermarkStrategy);
        lifecycleEvent.addSink(lifecycleEventStreamSink);

        KeyedStream<LifecycleEvent, Long> lifecycleById = lifecycleEvent.keyBy((KeySelector<LifecycleEvent, Long>) LifecycleEvent::getTripId);
        Tuple2<DataStream<MatchingDuration>, DataStream<MatchingTimeoutWarning>> durationAndWarning = calculateMatchingDuration(lifecycleById);
        DataStream<MatchingTimeoutWarning> matchingTimeoutWarning = durationAndWarning.f1;
        DataStream<TripFailedWarning> tripFailedWarning = detectAnomalousTrips(lifecycleById);

        emitRegionalAlerts(matchingTimeoutWarning, tripFailedWarning);
        DataStream<MatchingDuration> matchingDuration = durationAndWarning.f0;
        calculateAverageMatchingDuration(matchingDuration);

        System.out.println(env.getExecutionPlan());
    }

    private void calculateAverageMatchingDuration(DataStream<MatchingDuration> matchingDuration) {
        KeyedStream<MatchingDuration, String> regionalMatching = matchingDuration.keyBy(x -> x.getRegion().name());
        DataStream<AverageMatchingDuration> averageMatching = regionalMatching.countWindow(5).aggregate(new AggregateAverageMatching());
        averageMatching.addSink(averageMatchingDurationStreamSink);
    }

    private void emitRegionalAlerts(DataStream<MatchingTimeoutWarning> matchingTimeoutWarning, DataStream<TripFailedWarning> tripFailedWarning) {
        DataStream<Warning> mtWarning = matchingTimeoutWarning.map(x -> (Warning) x);
        DataStream<Warning> tfWarning = tripFailedWarning.map(x -> (Warning) x);
        DataStream<Warning> warnings = mtWarning.union(tfWarning);

        KeyedStream<Warning, String> regionalWarning = warnings.keyBy(x -> x.getRegion().name());
        DataStream<List<Warning>> aggregatedWarnings = regionalWarning.countWindow(3).aggregate(new AggregateWarnings());
        DataStream<Alert> alerts = aggregatedWarnings.map(x -> new Alert(x.get(0).getRegion(), x));
        alerts.addSink(alertStreamSink);
    }

    private Tuple2<DataStream<MatchingDuration>, DataStream<MatchingTimeoutWarning>> calculateMatchingDuration(DataStream<LifecycleEvent> lifecycleById) {
        Pattern<LifecycleEvent, ?> createdAndMatched = getPattern(TripState.CREATED, TripState.MATCHED).within(matchingDurationTimeout);
        PatternStream<LifecycleEvent> patternStream = CEP.pattern(lifecycleById, createdAndMatched).inEventTime();
        SingleOutputStreamOperator<MatchingDuration> matchingDuration = patternStream.process(new MatchingDurationCalculator());
        matchingDuration.addSink(matchingDurationStreamSink);
        OutputTag<MatchingTimeoutWarning> outputTag = new OutputTag<>("warning") {
        };
        DataStream<MatchingTimeoutWarning> matchingTimeoutWarning = matchingDuration.getSideOutput(outputTag);
        matchingTimeoutWarning.addSink(matchingTimeoutWarningStreamSink);
        return new Tuple2<>(matchingDuration, matchingTimeoutWarning);
    }

    private DataStream<TripFailedWarning> detectAnomalousTrips(DataStream<LifecycleEvent> lifecycleById) {
        Pattern<LifecycleEvent, ?> matchedAndQueued = getPattern(TripState.MATCHED, TripState.QUEUED);
        Pattern<LifecycleEvent, ?> matchedAndQueued3Times = Pattern.begin(matchedAndQueued).timesOrMore(3);
        PatternStream<LifecycleEvent> anomalousPattern = CEP.pattern(lifecycleById, matchedAndQueued3Times).inEventTime();
        SingleOutputStreamOperator<TripFailedWarning> tripFailedWarning = anomalousPattern.process(new AnomalousRequestDetection());
        tripFailedWarning.addSink(tripFailedWarningStreamSink);
        return tripFailedWarning;
    }

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
