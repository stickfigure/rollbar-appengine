package com.voodoodyne.rollbar;

/**
 * We need to throw a special exception out of the RollbarTask that doesn't trip the filter and cause
 * a loop that creates an exploding number of additional rollbar tasks...
 */
public class RollbarException extends RuntimeException {

	public RollbarException(String msg) {
		super(msg);
	}

	public RollbarException(Throwable throwable) {
		super(throwable);
	}
}
