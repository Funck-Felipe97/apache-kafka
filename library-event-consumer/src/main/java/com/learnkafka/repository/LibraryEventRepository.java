package com.learnkafka.repository;

import com.learnkafka.entities.LibraryEvent;
import org.springframework.data.repository.CrudRepository;

public interface LibraryEventRepository extends CrudRepository<LibraryEvent, Integer> {

}
