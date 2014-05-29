package com.voetsjoeba.imdb.exception;

import com.voetsjoeba.imdb.ImdbSearchResults;

/**
 * Indicates that an exact match was request from an {@link ImdbSearchResults} instance, but that the results did not contain 
 * an exact match.
 * 
 * @author Jeroen
 */
@SuppressWarnings("serial")
public class NoExactMatchException extends ImdbException {

	public NoExactMatchException() {
		super();
	}

	public NoExactMatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoExactMatchException(String message) {
		super(message);
	}

	public NoExactMatchException(Throwable cause) {
		super(cause);
	}
	
}
