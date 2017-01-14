package net.yggdrasil.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * Created by Miu on 14/01/2017.
 */
public class RabbitMQConnector {

    private static final RabbitMQConnector INSTANCE = new RabbitMQConnector();
    private Connection connection;

    private RabbitMQConnector() {

        ConnectionFactory factory = new ConnectionFactory();

        String resourceName = "rabbitmq.properties"; // could also be a constant
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties prop = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {

            prop.load(resourceStream);

            factory.setUsername(prop.getProperty("username", "guest"));
            factory.setPassword(prop.getProperty("password", "guest"));
            factory.setVirtualHost(prop.getProperty("vhost", "/"));
            factory.setHost(prop.getProperty("host", "localhost"));
            factory.setPort(Integer.valueOf(prop.getProperty("port", "5672")));

            connection = factory.newConnection();
        } catch (IOException | TimeoutException ignored) {
        }
    }

    public static RabbitMQConnector getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }

}
