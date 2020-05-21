package com.learnkafka.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class LibraryEventResourceAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleRequestBody(final MethodArgumentNotValidException ex) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        final String errorMessage = fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(" , "));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
