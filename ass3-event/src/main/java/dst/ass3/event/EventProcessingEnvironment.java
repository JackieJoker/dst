package dst.ass3.event;

import dst.ass3.event.model.events.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

public class EventProcessingEnvironment implements IEventProcessingEnvironment{
    @Override
    public void initialize(StreamExecutionEnvironment env) {

    }

    @Override
    public void setMatchingDurationTimeout(Time time) {

    }

    @Override
    public void setLifecycleEventStreamSink(SinkFunction<LifecycleEvent> sink) {

    }

    @Override
    public void setMatchingDurationStreamSink(SinkFunction<MatchingDuration> sink) {

    }

    @Override
    public void setAverageMatchingDurationStreamSink(SinkFunction<AverageMatchingDuration> sink) {

    }

    @Override
    public void setMatchingTimeoutWarningStreamSink(SinkFunction<MatchingTimeoutWarning> sink) {

    }

    @Override
    public void setTripFailedWarningStreamSink(SinkFunction<TripFailedWarning> sink) {

    }

    @Override
    public void setAlertStreamSink(SinkFunction<Alert> sink) {

    }
}
