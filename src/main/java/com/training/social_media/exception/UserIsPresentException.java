package com.training.social_media.exception;

public class UserIsPresentException extends RuntimeException {

    public UserIsPresentException(String msg) {
        super(msg);
    }
}
