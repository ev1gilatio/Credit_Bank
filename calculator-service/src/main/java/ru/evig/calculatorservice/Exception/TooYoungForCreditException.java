package ru.evig.calculatorservice.Exception;

public class TooYoungForCreditException extends RuntimeException {
    public TooYoungForCreditException(String message) {
        super(message);
    }
}
