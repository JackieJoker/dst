package dst.ass3.messaging.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IWorkloadMonitor;
import dst.ass3.messaging.Region;
import dst.ass3.messaging.WorkerResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class WorkloadMonitor implements IWorkloadMonitor {
    private Client client;
    private Connection connection;
    private Channel channel;
    private String topicQueue;
    private Map<Region, List<Long>> queuesTimes;

    public WorkloadMonitor(ConnectionFactory connectionFactory) {
        queuesTimes = new HashMap<>();
        for (Region r : Region.values()) {
            queuesTimes.put(r, Collections.synchronizedList(new ArrayList<>()));
        }
        // Create a RabbitMQ client
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
        // Create connection and channel for RabbitMQ
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            topicQueue = channel.queueDeclare().getQueue();
            // Bind the new created queue to each topic starting with "requests."
            channel.queueBind(topicQueue, Constants.TOPIC_EXCHANGE, "requests.#");
            // Set the consumer to read all the QUEUEs
            channel.basicConsume(topicQueue, true, deliverCallback, consumerTag -> {
            });
        } catch (TimeoutException | IOException e) {
            System.out.println("An error occurred while opening the connection or setting the consumer.");
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

    /**
     * Get the processing time in <strong>milliseconds</strong>.
     * @return A map containing the processing time for each Region
     */
    @Override
    public Map<Region, Double> getAverageProcessingTime() {
        Map<Region, Double> averageLastTen = new HashMap<>();
        for (Region r : Region.values()) {
            List<Long> all = queuesTimes.get(r);
            // Get the last 10 elements if there are more than 10,
            // otherwise get all the elements
            List<Long> lastTen = all.subList(Math.max(all.size() - 10, 0), all.size());
            double average = lastTen.stream().mapToDouble(d -> d).average().orElse(0.0);
            averageLastTen.put(r, average);
        }
        System.out.println(averageLastTen);
        return averageLastTen;
    }

    /**
     * Removes the queue associated with the current WorkLoad Monitor
     * and closes the channel and connection
     *
     * @throws IOException in case an error occurred closing the connection
     */
    @Override
    public void close() throws IOException {
        channel.queueDelete(topicQueue);
        try {
            channel.close();
        } catch (TimeoutException e) {
            System.out.println("An error occured while closing the channel.");
        }
        connection.close();
    }

    private final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String routingKey = delivery.getEnvelope().getRoutingKey();
        Region region;
        switch (routingKey) {
            case Constants.ROUTING_KEY_AT_LINZ:
                region = Region.AT_LINZ;
                break;
            case Constants.ROUTING_KEY_AT_VIENNA:
                region = Region.AT_VIENNA;
                break;
            case Constants.ROUTING_KEY_DE_BERLIN:
                region = Region.DE_BERLIN;
                break;
            default:
                return;
        }
        ObjectMapper mapper = new ObjectMapper();
        WorkerResponse response = mapper.readValue(delivery.getBody(), WorkerResponse.class);
        queuesTimes.get(region).add(response.getProcessingTime());
    };
}
