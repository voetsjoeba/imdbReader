package com.voetsjoeba.imdb.domain.api;

/**
 * Represents an object that can be used to represent search terms, i.e. any object that can be searched for.
 * 
 * @author Jeroen De Ridder
 */
public interface Searchable {
	
	/**
	 * Returns the search string that should be used when looking up this object.
	 */
	public String getSearchTerm();
	
}
