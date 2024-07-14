package ru.evig.dealservice.exception;

public class StatementDeniedException extends RuntimeException {
    public StatementDeniedException(String message) {
        super(message);
    }
}
