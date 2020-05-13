package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class LibraryEventProducer {

    private final KafkaTemplate<Integer, String> template;
    private final ObjectMapper mapper;
    private final ListenableFutureCallback<SendResult<Integer, String>> callback;

    public void sendLibraryEvent(final LibraryEvent event) throws JsonProcessingException {
        Integer key = event.getId();
        String value = mapper.writeValueAsString(event.getBook());
        ListenableFuture<SendResult<Integer, String>> listenableFuture = template.sendDefault(key, value);
        listenableFuture.addCallback(callback);
    }

}
