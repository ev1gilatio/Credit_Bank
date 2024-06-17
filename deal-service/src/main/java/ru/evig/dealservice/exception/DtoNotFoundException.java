package ru.evig.dealservice.exception;

public class DtoNotFoundException extends RuntimeException {
    public DtoNotFoundException(String message) {
        super(message);
    }
}
