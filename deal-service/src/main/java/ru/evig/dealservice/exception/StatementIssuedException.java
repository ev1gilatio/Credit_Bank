package ru.evig.dealservice.exception;

public class StatementIssuedException extends RuntimeException {
    public StatementIssuedException(String message) {
        super(message);
    }
}
