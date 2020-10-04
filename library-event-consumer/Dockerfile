FROM openjdk:11
VOLUME /app
COPY target/library-event-consumer-0.0.1-SNAPSHOT.jar library-event-consumer.jar
ENTRYPOINT ["java", "-jar", "library-event-consumer.jar"]