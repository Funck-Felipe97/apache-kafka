package com.kafkalearn.unit.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.callback.LIbraryEventCallback;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerTest {

    @InjectMocks
    private LibraryEventProducer producer;

    @Mock
    private KafkaTemplate<Integer, String> template;

    @Spy
    private LIbraryEventCallback callback;

    @Spy
    private ObjectMapper mapper;

    @Test
    void sendLibraryEventWithTopic_fail() {
        // given
        Book book = Book.builder()
                .author("Felipe")
                .name("Kafka")
                .id(2)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .build();

        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Exception calling kafka"));

        when(template.send(isA(ProducerRecord.class))).thenReturn(future);

        // expected
        assertThrows(Exception.class, () -> producer.sendLibraryEventWithTopic(libraryEvent).get());
    }

    @Test
    void sendLibraryEventWithTopic_success() throws JsonProcessingException, ExecutionException, InterruptedException {
        // given
        Book book = Book.builder()
                .author("Felipe")
                .name("Kafka")
                .id(2)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .build();

        SettableListenableFuture future = new SettableListenableFuture();
        SendResult<Integer, String> sendResult = createSendResult(libraryEvent);
        future.set(sendResult);

        when(template.send(isA(ProducerRecord.class))).thenReturn(future);

        // when
        final ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = producer.sendLibraryEventWithTopic(libraryEvent);

        // then
        SendResult<Integer, String> result = sendResultListenableFuture.get();
        assert result.getRecordMetadata().partition() == 1;
    }

    private SendResult<Integer, String> createSendResult(LibraryEvent libraryEvent) throws JsonProcessingException {
        final String record= mapper.writeValueAsString(libraryEvent);
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>("library-events", libraryEvent.getId(), record);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", 1),
                1, 1, 342, System.currentTimeMillis(), 1, 2);
        return (SendResult<Integer, String>) new SendResult(producerRecord, recordMetadata);
    }

}
