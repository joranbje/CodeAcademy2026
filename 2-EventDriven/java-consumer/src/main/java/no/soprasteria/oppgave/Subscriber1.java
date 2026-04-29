package no.soprasteria.oppgave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import no.soprasteria.Application;
import no.soprasteria.JacksonConfig;
import no.soprasteria.db.MessageData;
import no.soprasteria.domain.IdemDataDTO;
import no.soprasteria.rabbit.RabbitMQConfiguration;
import no.soprasteria.rabbit.RabbitMQConnectionHelper;
import no.soprasteria.rabbit.helper.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static no.soprasteria.rabbit.RabbitMQConfiguration.FANOUT_QUEUE_NAME;

public class Subscriber1 {

    private static final Logger log = LoggerFactory.getLogger(Subscriber1.class);
    private static final Properties properties;

    static {
        properties = new Properties();

        try {
            ClassLoader classLoader = Application.class.getClassLoader();
            InputStream applicationPropertiesStream = classLoader.getResourceAsStream("application.properties");
            properties.load(applicationPropertiesStream);
        } catch (Exception e) {
            // process the exception
        }
    }

    private final ObjectMapper mapper = new JacksonConfig().objectMapper();

    public static void main(String[] args) throws Exception {
        new Subscriber1().run();
    }

    private void run() throws Exception {
        RabbitMQConfiguration config = new RabbitMQConfiguration();
        RabbitConfig rabbitConfig = RabbitConfig.mapFromProperties(properties);
        RabbitMQConnectionHelper connectionHelper = new RabbitMQConnectionHelper(rabbitConfig);
        Channel channel = config.ensureQueuesAndExchanges(connectionHelper.getConnection().createChannel());

        // TODO: Implement me please
    }
}
