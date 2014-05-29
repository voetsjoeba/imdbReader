package com.voetsjoeba.imdb.domain;

import com.voetsjoeba.imdb.domain.api.Searchable;

/**
 * The most basic and commonly used {@link Searchable}. Provides a straightforward way of using strings or the string representations
 * of objects where {@link Searchable}s are required.
 * 
 * @author Jeroen
 */
public class StringSearchable implements Searchable {
	
	protected String searchTerm;
	
	public StringSearchable(String string) {
		this.searchTerm = string;
	}
	
	public StringSearchable(Object object){
		this.searchTerm = object.toString();
	}
	
	public void setSearchTerm(String searchTerm){
		this.searchTerm = searchTerm;
	}
	
	public String getSearchTerm() {
		return searchTerm;
	}
	
}
