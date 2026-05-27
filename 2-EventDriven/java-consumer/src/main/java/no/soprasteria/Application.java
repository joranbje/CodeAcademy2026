package no.soprasteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("Starting RabbitMQ Application");
        SpringApplication app = new SpringApplication(Application.class);
        if (harIkkeSattEnvVariablerForProd(args)) {
            app.setAdditionalProfiles("local");
        }
        app.run(args);
    }

    private static boolean harIkkeSattEnvVariablerForProd(String[] args) {
        boolean sattEnv = System.getenv().keySet().stream().anyMatch(k -> k.toLowerCase().startsWith("spring_profiles_active"));
        boolean argsSatt = Arrays.stream(args).anyMatch(arg -> arg.toLowerCase().startsWith("spring.profiles.active"));
        return !(sattEnv || argsSatt);
    }
}