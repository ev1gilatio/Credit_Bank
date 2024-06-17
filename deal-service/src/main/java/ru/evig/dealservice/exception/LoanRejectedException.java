package ru.evig.dealservice.exception;

public class LoanRejectedException extends RuntimeException {
    public LoanRejectedException(String message) {
        super(message);
    }
}