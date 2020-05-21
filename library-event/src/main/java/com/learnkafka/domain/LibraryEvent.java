package com.learnkafka.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LibraryEvent {

    private Integer id;
    @Valid
    @NotNull(message = "Book is mandatory")
    private Book book;
    private LibraryEventType type;

}
