package com.kafkalearn.unit.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.LibraryEventApplication;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;
import com.learnkafka.resource.LibraryEventResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = LibraryEventApplication.class)
@WebMvcTest(LibraryEventResource.class)
public class LibraryEventResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryEventProducer producer;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void postLibraryEvent() throws Exception {
        // given
        Book book = Book.builder()
                .author("Felipe")
                .name("Kafka")
                .id(2)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .build();

        byte[] libraryEventBytes = mapper.writeValueAsBytes(libraryEvent);

        doNothing().when(producer).sendLibraryEvent(libraryEvent);

        // expect
        mockMvc.perform(post("/v1/library-events/asynchronous")
        .content(libraryEventBytes)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        // given
        Book book = Book.builder()
                .author("Felipe")
                .id(2)
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .book(book)
                .build();

        byte[] libraryEventBytes = mapper.writeValueAsBytes(libraryEvent);

        doNothing().when(producer).sendLibraryEvent(libraryEvent);

        // expect
        mockMvc.perform(post("/v1/library-events/asynchronous")
                .content(libraryEventBytes)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("book.name - must not be blank"));
    }

}
