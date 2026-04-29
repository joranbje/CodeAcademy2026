package no.soprasteria.rabbit.helper;

import java.util.Properties;

public interface RabbitConfig {
    static RabbitConfig mapFromProperties(Properties properties) {
        var config = new LocalRabbitConfig();

        config.setUserName(properties.getProperty("rabbitmq.userName"));
        config.setPassword(properties.getProperty("rabbitmq.password"));
        config.setVhost(properties.getProperty("rabbitmq.vhost"));
        config.setHost(properties.getProperty("rabbitmq.host"));
        config.setPort(properties.getProperty("rabbitmq.port", "5672"));

        return config;
    }

    String getUserName();

    String getPassword();

    String getHost();

    String getVhost();

    Integer getPort();

    Boolean getUseSsl();
}
