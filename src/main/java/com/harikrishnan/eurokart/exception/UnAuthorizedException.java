package com.harikrishnan.eurokart.exception;

public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException (String message) {
        super(message);
    }
}
