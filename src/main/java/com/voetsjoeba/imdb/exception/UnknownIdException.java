package com.voetsjoeba.imdb.exception;

/**
 * Indicates that a title's ID value could not be found or has not yet been set.
 * 
 * @author Jeroen De Ridder
 */
@SuppressWarnings("serial")
public class UnknownIdException extends ImdbException
{
	public UnknownIdException()
	{
		super();
	}
	
	public UnknownIdException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public UnknownIdException(String message)
	{
		super(message);
	}
	
	public UnknownIdException(Throwable cause)
	{
		super(cause);
	}
}
