package com.sharemgt.fullstackshareapp.exception;

public class NoSuchShareException extends RuntimeException {

    public NoSuchShareException() {
        super();
    }

    public NoSuchShareException(String message) {
        super(message);
    }
}
