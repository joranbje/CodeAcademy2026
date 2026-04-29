package no.soprasteria.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import no.soprasteria.domain.IdemDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static no.soprasteria.rabbit.RabbitMQConfiguration.EXCHANGE_NAME_FANOUT;

@Service
public class FanOutMessageService {

    private static final Logger log = LoggerFactory.getLogger(FanOutMessageService.class);
    private final RabbitMQConnectionHelper connectionHelper;
    private final ObjectMapper mapper;
    private final Environment environment;

    @Autowired
    public FanOutMessageService(RabbitMQConnectionHelper connectionHelper, ObjectMapper mapper, Environment environment) {
        this.connectionHelper = connectionHelper;
        this.mapper = mapper;
        this.environment = environment;
    }

    private static void publishMessageToQueue(Channel channel, String message, String exchange, String routingKey) throws IOException {
        /* channel.basicPublish(
                exchange,
                routingKey,
                null,
                message.getBytes());
        log.info("[key={}] Sent '{}'", routingKey, message); */
    }

    @Scheduled(fixedRate = 5000)
    public void publishMessageToQueue() {
        var erIkkeProduction = Arrays.stream(environment.getActiveProfiles()).noneMatch("production"::equals);
        if (erIkkeProduction) {
            publishMessageToQueue(new IdemDataDTO(UUID.randomUUID().toString(), "Leeroy", "Alright chums, (I’m back)! Let’s do this… LEEROOOOOOOOOOOOOOOOOOOOY JEEEEEENKIIIIIIIIIIINS!", LocalDateTime.now()), EXCHANGE_NAME_FANOUT, "");
        }
    }

    public void publishMessageToQueue(IdemDataDTO msgToSend, String exchange, String routingKey) {
        RabbitMQConfiguration rabbitMQConfiguration = new RabbitMQConfiguration();
        try {
            Channel channel = rabbitMQConfiguration.ensureQueuesAndExchanges(connectionHelper.getConnection().createChannel());
            publishMessageToQueue(channel, mapper.writeValueAsString(msgToSend), exchange, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish message: {}", e.getMessage(), e);
        }
    }
}
