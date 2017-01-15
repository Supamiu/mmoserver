package net.yggdrasil.rabbitmq;

/**
 * Declares the types of monitoring A.K.A queues in RabbitMQ to avoid typo when typed as string.
 *
 * Created by Miu on 15/01/2017.
 */
public enum MonitoringType {

    //Should be used only for testing purpose.
    TESTING("testing"),
    //Queue for connection state messages.
    SOCKET_CONNECTION("connection");

    private String name;

    MonitoringType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
