package com.learnkafka.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/v1/library-events")
public class LibraryEventResource {

    private final LibraryEventProducer producer;

    @PostMapping(path = "asynchronous")
    @ResponseStatus(CREATED)
    public ResponseEntity<LibraryEvent> saveAsynchronous(@RequestBody final LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("Sending asynchronous message");
        producer.sendLibraryEvent(libraryEvent);
        return ResponseEntity.ok(libraryEvent);
    }

    @PostMapping(path = "synchronous")
    @ResponseStatus(CREATED)
    public ResponseEntity<LibraryEvent> saveSynchronous(@RequestBody final LibraryEvent libraryEvent) throws Exception {
        log.info("Sending synchronous message");
        SendResult<Integer, String> sendResult = producer.sendLibraryEventSynchronous(libraryEvent);
        log.info("SendResult is {}", sendResult);
        return ResponseEntity.ok(libraryEvent);
    }

}
