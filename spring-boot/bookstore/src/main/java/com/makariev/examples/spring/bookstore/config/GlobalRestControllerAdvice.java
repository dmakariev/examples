package com.makariev.examples.spring.bookstore.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.annotation.ResponseStatus;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException e) {
        return e.getMessage();  // Returns the exception message as the response body
    }
}
