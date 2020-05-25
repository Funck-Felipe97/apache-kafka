package com.kafkalearn.integration.resource;

import com.learnkafka.LibraryEventApplication;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LibraryEventApplication.class)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventResourceIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void postLibraryEvent() {
        // given
        Book book = Book.builder()
                .author("Felipe")
                .name("Kafka")
                .id(2)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> response = restTemplate.exchange("/v1/library-events/asynchronous", POST, request, LibraryEvent.class);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void putLibraryEvent() {
        // given
        Book book = Book.builder()
                .author("Felipe")
                .name("Kafka")
                .id(2)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .id(1)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> response = restTemplate.exchange("/v1/library-events/asynchronous/1", PUT, request, LibraryEvent.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void putLibraryEvent_4xx() {
        // given
        Book book = Book.builder()
                .author("Felipe")
                .name("Kafka")
                .id(2)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .id(1)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        // when
        ResponseEntity<LibraryEvent> response = restTemplate.exchange("/v1/library-events/asynchronous/null", PUT, request, LibraryEvent.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
