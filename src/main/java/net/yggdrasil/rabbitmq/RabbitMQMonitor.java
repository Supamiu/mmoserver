package net.yggdrasil.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Miu on 14/01/2017.
 */
public class RabbitMQMonitor {

    /**
     * Sends a message to a given queue.
     * <p>
     * It creates a new channel for every call, to allow thread safe operations.
     *
     * @param type    The queue type.
     * @param message The message to be sent.
     */
    public static void send(MonitoringType type, String message) {
        Connection connection = RabbitMQConnector.getInstance().getConnection();
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(type.getName(), false, false, false, null);
            channel.basicPublish("", type.getName(), null, message.getBytes());
            channel.close();
        } catch (IOException | TimeoutException ignored) {
        }

    }
}
