package com.voetsjoeba.imdb.domain.api;

/**
 * Identifies a general person involved in movie production (an actor, director, producer, screenwriter, ...).
 * 
 * @author Jeroen
 */
public interface Name {
	
	public String getId();
	public String getName();
	
	/**
	 * Returns the IMDb detail page URL for this person. If the ID of this person is not known, then a link to a search
	 * query page may be returned instead.
	 */
	public String getUrl();
	
}
