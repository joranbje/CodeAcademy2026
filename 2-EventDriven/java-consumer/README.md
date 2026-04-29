# Java Event Consumer

A Java application that consumes `idem.created` events from RabbitMQ and writes them to PostgreSQL.

## Prerequisites

- Java 21+
- Maven 3.8+
- RabbitMQ running on localhost:5672 (see docker-compose.yml)
- PostgreSQL running on localhost:5435 (see docker-compose.yml)

NB! If you are experiencing port conflicts, then you are probably already running a service on that port. Stop the existing container and try again.

## Quick Start

### Using Maven Directly

```bash
mvn compile exec:java
```

### Using Docker

```bash
# Build the image
mvn clean install && docker build -t java-consumer .

# Run with host networking (for local development)
docker run --network host java-consumer

# Or run in the docker compose network
docker run --network codeacademy_default \
  -e RABBITMQ_URL=amqp://guest:guest@rabbitmq:5672 \
  -e DATABASE_URL=jdbc:postgresql://postgres:5432/codeacademy?user=codeacademy\&password=codeacademy \
  java-consumer
```
