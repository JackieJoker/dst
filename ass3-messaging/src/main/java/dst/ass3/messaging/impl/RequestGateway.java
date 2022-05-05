package dst.ass3.messaging.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.Constants;
import dst.ass3.messaging.IRequestGateway;
import dst.ass3.messaging.Region;
import dst.ass3.messaging.TripRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RequestGateway implements IRequestGateway {
    private Connection connection;
    private Channel channel;

    public RequestGateway(ConnectionFactory connectionFactory) {
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (TimeoutException | IOException e) {
            System.out.println("An error occurred while opening the connection or the channel.");
        }
    }

    @Override
    public void submitRequest(TripRequest request) {
        Region region = request.getRegion();
        ObjectMapper mapper = new ObjectMapper();
        try {
            byte[] jsonBytes = mapper.writeValueAsBytes(request);
            channel.basicPublish(Constants.TOPIC_EXCHANGE, region.name(), null, jsonBytes);
        } catch (JsonProcessingException e) {
            System.out.println("An error occurred in json processing the TripRequest object.");
        } catch (IOException e) {
            System.out.println("An error occurred when publishing the topic.");
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
