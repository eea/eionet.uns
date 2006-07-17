package com.eurodyn.uns.service.channelserver;

public class DisabledException extends Exception {
	public DisabledException() {
	}

	public DisabledException(String msg) {
		super(msg);
	}

	public DisabledException(Exception e) {
		super(e);
	}

}

