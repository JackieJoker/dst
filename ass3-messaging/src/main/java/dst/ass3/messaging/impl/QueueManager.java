package dst.ass3.messaging.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IQueueManager;
import dst.ass3.messaging.Region;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueManager implements IQueueManager {
    private Connection connection;
    private Channel channel;

    public QueueManager(ConnectionFactory connectionFactory) {
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (TimeoutException | IOException e) {
            System.out.println("An error occured while opening the connection or the channel.");
        }
    }

    @Override
    public void setUp() {
        try {
            channel.queueDeclare(Constants.QUEUE_AT_LINZ, true, false, false, null);
            channel.queueDeclare(Constants.QUEUE_AT_VIENNA, true, false, false, null);
            channel.queueDeclare(Constants.QUEUE_DE_BERLIN, true, false, false, null);
            channel.exchangeDeclare(Constants.TOPIC_EXCHANGE, "topic");
            channel.queueBind(Constants.QUEUE_AT_LINZ, Constants.TOPIC_EXCHANGE, Region.AT_LINZ.name());
            channel.queueBind(Constants.QUEUE_AT_VIENNA, Constants.TOPIC_EXCHANGE, Region.AT_VIENNA.name());
            channel.queueBind(Constants.QUEUE_DE_BERLIN, Constants.TOPIC_EXCHANGE, Region.DE_BERLIN.name());
        } catch (IOException e) {
            System.out.println("An error occured while creating the queues.");
        }
    }

    @Override
    public void tearDown() {
        try {
            channel.queueDelete(Constants.QUEUE_AT_LINZ);
            channel.queueDelete(Constants.QUEUE_AT_VIENNA);
            channel.queueDelete(Constants.QUEUE_DE_BERLIN);
            channel.exchangeDelete(Constants.TOPIC_EXCHANGE);
        } catch (IOException e) {
            System.out.println("An error occured while deleting the queues.");
        }
    }

    @Override
    public void close() throws IOException {
        try {
            channel.close();
        } catch (TimeoutException e) {
            System.out.println("An error occured while closing the channel.");
        }
        connection.close();
    }
}
