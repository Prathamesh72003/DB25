package com.sharemgt.fullstackshareapp.exception;

public class ShareAlreadyExsistException extends RuntimeException {

    public ShareAlreadyExsistException() {
        super();
    }

    public ShareAlreadyExsistException(String message) {
        super(message);
    }
}
