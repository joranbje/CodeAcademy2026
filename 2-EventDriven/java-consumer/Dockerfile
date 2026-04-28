FROM eclipse-temurin:21-jre-alpine

COPY target/java-consumer-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
