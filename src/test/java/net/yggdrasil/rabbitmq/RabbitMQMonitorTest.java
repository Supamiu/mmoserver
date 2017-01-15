package net.yggdrasil.rabbitmq;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;

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

        RabbitMQMonitor.send(MonitoringType.TESTING, "coucou");

        Thread.sleep(10);

        verify(consumer).handleDelivery(
                any(String.class),
                any(Envelope.class),
                any(AMQP.BasicProperties.class),
                eq("coucou".getBytes()));
    }

    @Test
    public void exceptionCatchTest() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenThrow(IOException.class);

        //Here we don't expect an exception, if one is raised, then the test will fail.
        RabbitMQMonitor.sendWithConnection(connection, MonitoringType.TESTING, "should error");
    }
}
