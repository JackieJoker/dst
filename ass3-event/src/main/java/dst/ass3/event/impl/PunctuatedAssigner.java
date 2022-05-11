package dst.ass3.event.impl;

import dst.ass3.event.model.events.LifecycleEvent;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

public class PunctuatedAssigner implements WatermarkGenerator<LifecycleEvent> {
    @Override
    public void onEvent(LifecycleEvent lifecycleEvent, long timeStamp, WatermarkOutput watermarkOutput) {
        watermarkOutput.emitWatermark(new Watermark(timeStamp));
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput watermarkOutput) {

    }
}
