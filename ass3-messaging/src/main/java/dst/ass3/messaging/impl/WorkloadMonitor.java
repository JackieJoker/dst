package dst.ass3.messaging.impl;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class WorkloadMonitor implements IWorkloadMonitor {
    private Client client;

    public WorkloadMonitor() {
        try {
            client = new Client(
                    new ClientParameters()
                            .url(Constants.RMQ_API_URL)
                            .username(Constants.RMQ_USER)
                            .password(Constants.RMQ_PASSWORD)
            );
        } catch (URISyntaxException | MalformedURLException e) {
            System.out.println("Error with the URL parsing.");
        }
    }

    @Override
    public Map<Region, Long> getRequestCount() {
        Map<Region, Long> requestCount = new HashMap<>();
        requestCount.put(Region.AT_LINZ, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_AT_LINZ).getMessagesReady());
        requestCount.put(Region.AT_VIENNA, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_AT_VIENNA).getMessagesReady());
        requestCount.put(Region.DE_BERLIN, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_DE_BERLIN).getMessagesReady());
        return requestCount;
    }

    @Override
    public Map<Region, Long> getWorkerCount() {
        Map<Region, Long> workerCount = new HashMap<>();
        workerCount.put(Region.AT_LINZ, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_AT_LINZ).getConsumerCount());
        workerCount.put(Region.AT_VIENNA, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_AT_VIENNA).getConsumerCount());
        workerCount.put(Region.DE_BERLIN, client.getQueue(Constants.RMQ_VHOST, Constants.QUEUE_DE_BERLIN).getConsumerCount());
        return workerCount;
    }

    @Override
    public Map<Region, Double> getAverageProcessingTime() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
