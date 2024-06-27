package ru.evig.statementservice.exception;

public class TooYoungForCreditException extends RuntimeException {
    public TooYoungForCreditException(String message) {
        super(message);
    }
}
