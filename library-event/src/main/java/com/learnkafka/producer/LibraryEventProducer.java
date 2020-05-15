package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class LibraryEventProducer {

    private static final String TOPIC = "library-events";

    private final KafkaTemplate<Integer, String> template;
    private final ObjectMapper mapper;
    private final ListenableFutureCallback<SendResult<Integer, String>> callback;

    public void sendLibraryEvent(final LibraryEvent event) throws JsonProcessingException {
        Integer key = event.getId();
        String value = mapper.writeValueAsString(event.getBook());
        ListenableFuture<SendResult<Integer, String>> listenableFuture = template.sendDefault(key, value);
        listenableFuture.addCallback(callback);
    }

    public void sendLibraryEventWithTopic(final LibraryEvent event) throws JsonProcessingException {
        Integer key = event.getId();
        String value = mapper.writeValueAsString(event.getBook());
        ListenableFuture<SendResult<Integer, String>> listenableFuture = template.send(buildProducerRecord(key, value, TOPIC));
        listenableFuture.addCallback(callback);
    }

    public SendResult<Integer, String> sendLibraryEventSynchronous(final LibraryEvent event) throws Exception {
        Integer key = event.getId();
        String value = mapper.writeValueAsString(event.getBook());
        SendResult<Integer, String> sendResul = null;
        try {
             sendResul = template.sendDefault(key, value).get();
        } catch (Exception e) {
            log.error("Exception sending the message and the exception is {}", e.getMessage());
            throw e;
        }
        return sendResul;
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        final List<Header> headers = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord(TOPIC, null, key, value, headers);
    }

}
