package com.eurodyn.uns.web.exceptions;

public class EndpointCallException extends Exception{

    public EndpointCallException() {
    }

    public EndpointCallException(String message) {
        super(message);
    }


    public EndpointCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
