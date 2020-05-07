package com.learnkafka.resource;

import com.learnkafka.domain.LibraryEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/v1/library-events")
public class LibraryEventResource {

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<LibraryEvent> save(@RequestBody final LibraryEvent libraryEvent) {
        // invoke kafka producer
        return ResponseEntity.ok(libraryEvent);
    }

}
