package net.yggdrasil.rabbitmq;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Miu on 14/01/2017.
 */
public class RabbitMQConnectorTest {

    @Test
    public void singletonTest() {
        assertNotNull(RabbitMQConnector.getInstance());
        assertNotNull(RabbitMQConnector.getInstance().getConnection());
    }
}
