package dst.ass3.event.impl;

import dst.ass3.event.model.domain.Region;
import dst.ass3.event.model.events.AverageMatchingDuration;
import dst.ass3.event.model.events.MatchingDuration;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class AggregateAverageMatching implements AggregateFunction<MatchingDuration, Tuple2<Long, Long>, AverageMatchingDuration> {
    private String region;
    @Override
    public Tuple2<Long, Long> createAccumulator() {
        return new Tuple2<>(0L, 0L);
    }

    @Override
    public Tuple2<Long, Long> add(MatchingDuration matchingDuration, Tuple2<Long, Long> accumulator) {
        region = matchingDuration.getRegion().name();
        return new Tuple2<>(accumulator.f0 + matchingDuration.getDuration(), accumulator.f1 + 1L);
    }

    @Override
    public AverageMatchingDuration getResult(Tuple2<Long, Long> accumulator) {
        double average = ((double) accumulator.f0) / accumulator.f1;
        return new AverageMatchingDuration(Region.valueOf(region), average);
    }

    @Override
    public Tuple2<Long, Long> merge(Tuple2<Long, Long> a, Tuple2<Long, Long> b) {
        return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
    }
}
