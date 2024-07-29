package ru.evig.gatewayservice.controller;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<String> handleException(HttpMessageNotReadableException e, WebRequest r) {
        logging(e, r);

        return new ResponseEntity<>("Invalid JSON payload", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<String> handleException(FeignException e, WebRequest r) {
        logging(e, r);

        return new ResponseEntity<>(e.contentUTF8(), HttpStatus.BAD_REQUEST);
    }

    private void logging(Exception e, WebRequest r) {
        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getSimpleName(), e.getMessage(), r.getDescription(false));

        log.error(logMessage);
    }
}
