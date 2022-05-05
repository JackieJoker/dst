package dst.ass3.messaging.impl;

import com.rabbitmq.client.ConnectionFactory;
import dst.ass3.messaging.*;

import javax.xml.registry.QueryManager;

public class MessagingFactory implements IMessagingFactory {
    private ConnectionFactory connectionFactory;

    public MessagingFactory() {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.RMQ_HOST);
        connectionFactory.setPort(Integer.parseInt(Constants.RMQ_PORT));
        connectionFactory.setVirtualHost(Constants.RMQ_VHOST);
        connectionFactory.setUsername(Constants.RMQ_USER);
        connectionFactory.setPassword(Constants.RMQ_PASSWORD);
    }

    @Override
    public IQueueManager createQueueManager() {
        return new QueueManager(connectionFactory);
    }

    @Override
    public IRequestGateway createRequestGateway() {
        return new RequestGateway(connectionFactory);
    }

    @Override
    public IWorkloadMonitor createWorkloadMonitor() {
        return new WorkloadMonitor();
    }

    @Override
    public void close() {
        // implement if needed
    }
}
