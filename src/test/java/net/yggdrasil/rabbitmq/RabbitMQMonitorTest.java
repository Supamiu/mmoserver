package net.yggdrasil.rabbitmq;

import com.rabbitmq.client.*;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Miu on 14/01/2017.
 */
public class RabbitMQMonitorTest {


    @Test
    public void smokeConstructorTest() {
        assertNotNull(new RabbitMQMonitor());
    }

    @Test
    public void publishTest() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Consumer consumer = mock(Consumer.class);

        channel.queueDeclare("testing", false, false, false, null);

        channel.basicConsume("testing", true, consumer);

        RabbitMQMonitor.send("testing", "coucou");

        verify(consumer).handleDelivery(
                any(String.class),
                any(Envelope.class),
                any(AMQP.BasicProperties.class),
                eq("coucou".getBytes()));
    }
}
