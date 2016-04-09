package com.voetsjoeba.imdb.domain;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voetsjoeba.imdb.domain.api.Name;
import com.voetsjoeba.imdb.util.HttpUtils;

/**
 * Default {@link Name} implementation.
 * 
 * @author Jeroen
 */
@ThreadSafe
@Immutable
public class StandardName implements Name
{
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(StandardName.class);
	
	protected final String id;
	protected final String name;
	
	public StandardName(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	public String getUrl()
	{
		if (id == null)
		{
			
			// no ID known for this person, make it a link to the search page
			// for their name
			return "http://www.imdb.com/find?s=all&q=" + HttpUtils.urlEncodeUtf8(name);
		}
		else
		{
			// ID known, link directly to this person's page
			return "http://www.imdb.com/name/" + id;
		}
	}
}
