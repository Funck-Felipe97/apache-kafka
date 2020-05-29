package com.learnkafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.service.LibraryEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Component
public class LibraryEventConsumer {

    private final LibraryEventService libraryEventService;

    @KafkaListener(topics = {"library-events"})
    public void onMessage(final ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("Consumer record: {} ", consumerRecord);
        libraryEventService.processLibraryEvent(consumerRecord);
    }

}
