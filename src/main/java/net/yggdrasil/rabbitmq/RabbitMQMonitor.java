package net.yggdrasil.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * Created by Miu on 14/01/2017.
 */
public class RabbitMQMonitor {

    public static void send(String queue, String message) throws Exception {
        Connection connection = RabbitMQConnector.getInstance().getConnection();

        Channel channel = connection.createChannel();
        channel.queueDeclare(queue, false, false, false, null);
        channel.basicPublish("", queue, null, message.getBytes());
        channel.close();
    }
}
