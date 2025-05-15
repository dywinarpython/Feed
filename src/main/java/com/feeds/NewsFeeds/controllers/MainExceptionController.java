package com.feeds.NewsFeeds.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class MainExceptionController {
    private String messageError;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handlerNoSuchElementException(NoSuchElementException ex) {
        messageError = "Ошибка нахождения элемента: " + ex.getMessage();
        log.error(messageError);
        return new ResponseEntity<>(
                messageError,
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerException(Exception ex){
        messageError = ex.getMessage();
        log.error(messageError, ex);
        return new ResponseEntity<>(
                messageError, HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
