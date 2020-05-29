package com.learnkafka.domain;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class LibraryEvent {

    @Id
    @GeneratedValue
    private Integer id;

    @ToString.Exclude
    @OneToOne(mappedBy = "libraryEvent", cascade = {CascadeType.ALL})
    private Book book;

    @Enumerated(EnumType.STRING)
    private LibraryEventType type;

}
