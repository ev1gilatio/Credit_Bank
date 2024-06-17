package ru.evig.dealservice.exception;

public class TooYoungForCreditException extends RuntimeException {
    public TooYoungForCreditException(String message) {
        super(message);
    }
}
