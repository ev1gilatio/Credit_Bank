package ru.evig.calculatorservice.exception;

public class LoanRejectedException extends RuntimeException {
    public LoanRejectedException(String message) {
        super(message);
    }
}