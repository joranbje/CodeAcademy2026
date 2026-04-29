package no.soprasteria.rabbit;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import no.soprasteria.rabbit.helper.RabbitConfig;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Service
public class RabbitMQConnectionHelper {

    private final RabbitConfig config;

    public RabbitMQConnectionHelper(RabbitConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(config.getUserName());
        factory.setPassword(config.getPassword());
        factory.setVirtualHost(config.getVhost());
        factory.setHost(config.getHost());
        factory.setPort(config.getPort());

        if (config.getUseSsl()) {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, null);
            factory.useSslProtocol(sslContext);
        }

        return factory.newConnection();
    }
}
