package com.voetsjoeba.imdb.domain;

import com.voetsjoeba.imdb.domain.api.Movie;

/**
 * Default {@link Movie} implementation.
 * 
 * @author Jeroen De Ridder
 */
public class StandardMovie extends AbstractTitle implements Movie
{
	private static final long serialVersionUID = 1L;
	
	public StandardMovie(String id)
	{
		super(id);
	}
	
	public String getTypeString()
	{
		return "Movie";
	}
}
