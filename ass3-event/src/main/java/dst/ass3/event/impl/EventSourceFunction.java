package dst.ass3.event.impl;

import dst.ass3.event.Constants;
import dst.ass3.event.EventSubscriber;
import dst.ass3.event.IEventSourceFunction;
import dst.ass3.event.model.domain.ITripEventInfo;
import org.apache.flink.api.common.functions.AbstractRichFunction;
import org.apache.flink.configuration.Configuration;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

//TODO: is it correct? I just copied what is done in RichSourceFunction
public class EventSourceFunction extends AbstractRichFunction implements IEventSourceFunction, Serializable {
    private static final long serialVersionUID = 1L;
    //TODO: maybe transient (?)
    private EventSubscriber eventSubscriber;
    private volatile boolean isRunning = true;


    @Override
    public void open(Configuration parameters) throws Exception {
        SocketAddress socketAddress = new InetSocketAddress(Constants.EVENT_PUBLISHER_PORT);
        eventSubscriber = EventSubscriber.subscribe(socketAddress);
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        eventSubscriber.close();
        super.close();
    }

    @Override
    public void run(SourceContext<ITripEventInfo> sourceContext) throws Exception {
        while (isRunning) {
            ITripEventInfo received = eventSubscriber.receive();
            if(received != null) {
                sourceContext.collect(received);
                //TODO: check if just returning is ok
            } else return;
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
