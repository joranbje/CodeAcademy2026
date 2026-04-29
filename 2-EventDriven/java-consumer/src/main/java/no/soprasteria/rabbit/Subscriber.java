package no.soprasteria.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import jakarta.annotation.PostConstruct;
import no.soprasteria.db.DataRepository;
import no.soprasteria.db.MessageData;
import no.soprasteria.domain.IdemDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

import static no.soprasteria.rabbit.RabbitMQConfiguration.FANOUT_QUEUE_NAME;

@Service
public class Subscriber {

    private final Logger log = LoggerFactory.getLogger(Subscriber.class);

    private final RabbitMQConnectionHelper connectionHelper;
    private final DataRepository dataRepository;
    private final ObjectMapper mapper;

    public Subscriber(RabbitMQConnectionHelper rabbitMQConnectionHelper, DataRepository dataRepository, ObjectMapper mapper) {
        this.connectionHelper = rabbitMQConnectionHelper;
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @PostConstruct
    public void subscribe() {
        try {
            RabbitMQConfiguration config = new RabbitMQConfiguration();
            //Ensure queuesAndExchanges
            //basicConsume m/ defaultConsumer --> channel.basicAck()
            Channel channel = config.ensureQueuesAndExchanges(connectionHelper.getConnection().createChannel());
            // TODO: Consume messages og persister via DataRepository

        } catch (Exception e) {
            log.error("Failed {}", e.getMessage());
        }
    }
}
