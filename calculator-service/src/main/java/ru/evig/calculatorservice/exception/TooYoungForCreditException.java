package ru.evig.calculatorservice.exception;

public class TooYoungForCreditException extends RuntimeException {
    public TooYoungForCreditException(String message) {
        super(message);
    }
}
