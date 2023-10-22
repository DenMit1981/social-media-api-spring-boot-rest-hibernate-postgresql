package com.training.social_media.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String msg) {
        super(msg);
    }
}
