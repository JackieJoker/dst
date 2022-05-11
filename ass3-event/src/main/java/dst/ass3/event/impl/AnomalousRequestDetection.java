package dst.ass3.event.impl;

import dst.ass3.event.model.domain.TripState;
import dst.ass3.event.model.events.LifecycleEvent;
import dst.ass3.event.model.events.TripFailedWarning;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;

public class AnomalousRequestDetection extends PatternProcessFunction<LifecycleEvent, TripFailedWarning> {
    @Override
    public void processMatch(Map<String, List<LifecycleEvent>> map, Context context, Collector<TripFailedWarning> collector) throws Exception {
        LifecycleEvent matched = map.get(TripState.MATCHED.name()).get(0);
        collector.collect(new TripFailedWarning(matched.getTripId(), matched.getRegion()));
    }
}
