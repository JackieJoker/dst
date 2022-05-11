package dst.ass3.event.impl;

import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.LifecycleEvent;
import dst.ass3.event.model.events.MatchingDuration;
import dst.ass3.event.model.events.MatchingTimeoutWarning;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.functions.TimedOutPartialMatchHandler;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;
import java.util.Map;

public class MatchingDurationCalculator extends PatternProcessFunction<LifecycleEvent, MatchingDuration> implements TimedOutPartialMatchHandler<LifecycleEvent> {
    @Override
    public void processMatch(Map<String, List<LifecycleEvent>> map, Context context, Collector<MatchingDuration> collector) throws Exception {
        LifecycleEvent created = map.get(TripState.CREATED.name()).get(0);
        LifecycleEvent matched = map.get(TripState.MATCHED.name()).get(0);
        long duration = matched.getTimestamp() - created.getTimestamp();
        collector.collect(new MatchingDuration(matched.getTripId(), matched.getRegion(), duration));
    }

    @Override
    public void processTimedOutMatch(Map<String, List<LifecycleEvent>> map, Context context) throws Exception {
        OutputTag<MatchingTimeoutWarning> outputTag = new OutputTag<>("warning") {};
        LifecycleEvent created = map.get(TripState.CREATED.name()).get(0);
        context.output(outputTag , new MatchingTimeoutWarning(created.getTripId(), created.getRegion()));
    }
}
