package com.voetsjoeba.imdb.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerUtils
{
	private static final Logger log = LoggerFactory.getLogger(ListenerUtils.class);
	
	/**
	 * In the Java convention, listeners should not throw exceptions -- neither checked exceptions, nor unchecked exceptions.
	 * The contrary usually indicates a programming error.
	 * 
	 * To prevent other listeners from not receiving the event, dispatching code must make sure to catch any runtime exception
	 * thrown by listeners. This code is however not adequately capable of dealing with the exception. Since there isn't much 
	 * we can do, we'll log the exception for future review. This method centralizes that logging, to prevent large amounts
	 * of copy/pasta.
	 * 
	 * @param rex the runtime exception thrown by a listener
	 * @l the listener that threw the exception. May be null if unknown (for example when delegating to
	 *    PropertyChangeSupport and the like)
	 */
	public static void handleListenerException(RuntimeException rex, EventListener l)
	{
		if (l != null)
		{
			log.error("A listener threw a RuntimeException; this should not happen. See DEBUG logs for full exception details.", l);
		}
		else
		{
			log.error("Listener \"{}\" threw a RuntimeException; this should not happen. See DEBUG logs for full exception details.", l);
		}
		log.debug(rex.getMessage(), rex);
	}
	
	/**
	 * Returns the stack trace of a Throwable, exactly as it would be printed to stderr.
	 */
	public String getStackTrace(Throwable aThrowable)
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
