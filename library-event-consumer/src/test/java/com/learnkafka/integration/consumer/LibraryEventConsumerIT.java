package com.learnkafka.integration.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.LibraryEventConsumerApplication;
import com.learnkafka.consumer.LibraryEventConsumer;
import com.learnkafka.entities.Book;
import com.learnkafka.entities.LibraryEvent;
import com.learnkafka.entities.LibraryEventType;
import com.learnkafka.repository.LibraryEventRepository;
import com.learnkafka.service.LibraryEventService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LibraryEventConsumerApplication.class)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventConsumerIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    private LibraryEventConsumer libraryEventConsumer;

    @SpyBean
    private LibraryEventService libraryEventService;

    @Autowired
    private LibraryEventRepository libraryEventRepository;

    @BeforeEach
    void setUp() {
        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getAllListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @AfterEach
    void tearDown() {
        libraryEventRepository.deleteAll();
    }

    @Test
    void publishNewLibraryEvent() throws Exception {
        // given
        String json = new ObjectMapper().writeValueAsString(createLibraryEvent());
        kafkaTemplate.sendDefault(json).get();

        // when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // then
        verify(libraryEventConsumer).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventService).processLibraryEvent(isA(ConsumerRecord.class));

        final List<LibraryEvent> libraryEvents = (List<LibraryEvent>) libraryEventRepository.findAll();
        assert libraryEvents.size() == 1;
        libraryEvents.forEach(libraryEvent -> {
            assert libraryEvent.getId() != null;
            assertEquals(2, libraryEvent.getBook().getId());
        });
    }

    @Test
    void publishUpdateLibraryEvent() throws Exception {
        // given
        LibraryEvent libraryEvent = createLibraryEvent();
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventRepository.save(libraryEvent);

        Book updatedBook = Book.builder().id(2).name("Kafka 2").author("Felipe").build();
        libraryEvent.setType(LibraryEventType.UPDATE);
        libraryEvent.setBook(updatedBook);

        final String updatedJson = new ObjectMapper().writeValueAsString(libraryEvent);

        // when
        kafkaTemplate.sendDefault(libraryEvent.getId(), updatedJson).get();
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // then
        verify(libraryEventConsumer).onMessage(isA(ConsumerRecord.class));
        verify(libraryEventService).processLibraryEvent(isA(ConsumerRecord.class));
        final LibraryEvent savedLibraryEvent = libraryEventRepository.findById(libraryEvent.getId()).get();
        assertEquals("Kafka 2", savedLibraryEvent.getBook().getName());
    }

    public LibraryEvent createLibraryEvent() {
        Book book = Book.builder()
                .author("Felipe")
                .name("Kafka")
                .id(2)
                .build();

        return LibraryEvent.builder()
                .book(book)
                .type(LibraryEventType.NEW)
                .build();
    }

}
