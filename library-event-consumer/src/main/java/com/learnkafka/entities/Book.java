package com.learnkafka.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Book {

    @Id
    private Integer id;
    private String name;
    private String author;

    @JoinColumn(name = "libraryEventId")
    @OneToOne
    private LibraryEvent libraryEvent;

}
