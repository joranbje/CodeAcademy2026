package no.soprasteria.rabbit;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RabbitMQConfiguration {

    public static String EXCHANGE_NAME_FANOUT = "chat";
    public static String FANOUT_QUEUE_NAME = "chat_all";


    public Channel ensureQueuesAndExchanges(Channel channel) throws IOException {
        Map<String, Object> argsMap = new HashMap<>();
        argsMap.put("x-queue-type", "quorum");

        channel.basicQos(1);

        channel.exchangeDeclare(EXCHANGE_NAME_FANOUT, "fanout", false);
        channel.queueDeclare(FANOUT_QUEUE_NAME, false, false, true, null);
        channel.queueBind(FANOUT_QUEUE_NAME, EXCHANGE_NAME_FANOUT, "");

        return channel;
    }
}
