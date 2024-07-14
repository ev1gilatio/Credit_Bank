package ru.evig.dealservice.controller;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.evig.dealservice.exception.StatementDeniedException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<String> handleException(RuntimeException e, WebRequest r) {
        logging(e, r);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<String> handleException(HttpMessageNotReadableException e, WebRequest r) {
        logging(e, r);

        return new ResponseEntity<>("Invalid JSON payload", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException e, WebRequest r) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logging(e, r);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    private ResponseEntity<String> handleException(FeignException e, WebRequest r) {
        logging(e, r);

        return new ResponseEntity<>("Error from another service: " + e.contentUTF8(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatementDeniedException.class)
    private ResponseEntity<String> handleException(StatementDeniedException e, WebRequest r) {
        logging(e, r);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private void logging(Exception e, WebRequest r) {
        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getSimpleName(), e.getMessage(), r.getDescription(false));

        log.error(logMessage);
    }
}
