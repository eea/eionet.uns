package com.eurodyn.uns.service.channelserver;

public class NotFoundException extends Exception {
    public NotFoundException() {
    }

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(Exception e) {
        super(e);
    }

}
