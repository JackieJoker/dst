package dst.ass3.event.impl;

import dst.ass3.event.model.events.Warning;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.util.ArrayList;
import java.util.List;

public class AggregateWarnings implements AggregateFunction<Warning, List<Warning>, List<Warning>> {
    @Override
    public List<Warning> createAccumulator() {
        return new ArrayList<>();
    }

    @Override
    public List<Warning> add(Warning warning, List<Warning> warnings) {
        warnings.add(warning);
        return warnings;
    }

    @Override
    public List<Warning> getResult(List<Warning> warnings) {
        return warnings;
    }

    @Override
    public List<Warning> merge(List<Warning> warnings, List<Warning> acc1) {
        warnings.addAll(acc1);
        return warnings;
    }
}
