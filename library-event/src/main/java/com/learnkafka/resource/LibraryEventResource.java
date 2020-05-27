package com.learnkafka.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import com.learnkafka.producer.LibraryEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/v1/library-events")
public class LibraryEventResource {

    private final LibraryEventProducer producer;

    @PostMapping(path = "asynchronous")
    @ResponseStatus(CREATED)
    public ResponseEntity<LibraryEvent> saveAsynchronous(@RequestBody @Valid final LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("Sending asynchronous message");
        libraryEvent.setType(LibraryEventType.NEW);
        producer.sendLibraryEvent(libraryEvent);
        return ResponseEntity.status(CREATED).body(libraryEvent);
    }

    @PostMapping(path = "synchronous")
    @ResponseStatus(CREATED)
    public ResponseEntity<LibraryEvent> saveSynchronous(@RequestBody final LibraryEvent libraryEvent) throws Exception {
        log.info("Sending synchronous message");
        libraryEvent.setType(LibraryEventType.NEW);
        SendResult<Integer, String> sendResult = producer.sendLibraryEventSynchronous(libraryEvent);
        log.info("SendResult is {}", sendResult);
        return ResponseEntity.ok(libraryEvent);
    }

    @PutMapping(path = "asynchronous/{id}")
    @ResponseStatus(OK)
    public ResponseEntity<LibraryEvent> updateAsynchronous(@RequestBody @Valid final LibraryEvent libraryEvent, @PathVariable @NotNull final Integer id) throws JsonProcessingException {
        libraryEvent.setId(id);
        libraryEvent.setType(LibraryEventType.UPDATE);
        producer.sendLibraryEvent(libraryEvent);
        return ResponseEntity.status(OK).body(libraryEvent);
    }

}
