package com.voetsjoeba.imdb.exception;

/**
 * Indicates an unknown page type.
 * 
 * @author Jeroen De Ridder
 */
@SuppressWarnings("serial")
public class UnknownPageTypeException extends ImdbException {

	public UnknownPageTypeException() {
		super();
	}

	public UnknownPageTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownPageTypeException(String message) {
		super(message);
	}

	public UnknownPageTypeException(Throwable cause) {
		super(cause);
	}
	
}
