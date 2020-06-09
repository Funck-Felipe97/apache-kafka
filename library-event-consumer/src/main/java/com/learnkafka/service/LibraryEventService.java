package com.learnkafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.entities.LibraryEvent;
import com.learnkafka.repository.LibraryEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Service
public class LibraryEventService {

    private final ObjectMapper mapper;
    private final LibraryEventRepository libraryEventRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processLibraryEvent(final ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        final LibraryEvent libraryEvent = mapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("LibraryEvent : {}", libraryEvent);
        switch (libraryEvent.getType()) {
            case NEW:
                save(libraryEvent);
                break;
            case UPDATE:
                validate(libraryEvent);
                save(libraryEvent);
                break;
            default:
                log.error("Invalid library event type");
        }
    }

    private void save(final LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventRepository.save(libraryEvent);
        log.info("Successfully persisted the library event: {}", libraryEvent);
    }

    private void validate(final LibraryEvent libraryEvent) {
        Optional.ofNullable(libraryEvent.getId()).ifPresentOrElse(id ->
                        libraryEventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not a valid library event"))
                , () -> {
                    throw new IllegalArgumentException("Library event id is missing");
                });
        log.info("Validation is success for the library event: {} ", libraryEvent);
    }

}
