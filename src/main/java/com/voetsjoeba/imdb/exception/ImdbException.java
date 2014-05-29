package com.voetsjoeba.imdb.exception;

/**
 * Superclass for all exceptions in the IMDb package.
 * 
 * @author Jeroen De Ridder
 */
@SuppressWarnings("serial")
public class ImdbException extends RuntimeException {

	public ImdbException() {
		super();
	}

	public ImdbException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImdbException(String message) {
		super(message);
	}

	public ImdbException(Throwable cause) {
		super(cause);
	}
	
}
