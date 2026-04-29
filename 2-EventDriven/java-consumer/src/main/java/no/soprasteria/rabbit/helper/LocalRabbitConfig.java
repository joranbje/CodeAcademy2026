package no.soprasteria.rabbit.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Objects;

@Configuration
@Profile("local")
@ConfigurationProperties(prefix = "rabbitmq")
public class LocalRabbitConfig implements RabbitConfig {
    private String userName;
    private String password;
    private String host;
    private String vhost;
    private Integer port;
    private Boolean useSsl;

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = Objects.requireNonNull(userName, "Username must not be null");
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "Password must not be null");
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = Objects.requireNonNull(host, "Host must not be null");
    }

    @Override
    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = Objects.requireNonNull(vhost, "Vhost must not be null");
    }

    @Override
    public Integer getPort() {
        return port;
    }

    public void setPort(String port) {
        if (port == null) {
            this.port = 5672;
        } else {
            this.port = Integer.parseInt(port);
        }
    }

    @Override
    public Boolean getUseSsl() {
        return useSsl != null && useSsl;
    }

    public void setUseSsl(Boolean useSsl) {
        this.useSsl = Objects.requireNonNullElse(useSsl, false);
    }
}