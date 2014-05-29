package com.voetsjoeba.imdb.event;

import java.util.EventObject;

import com.voetsjoeba.imdb.ImdbParser.PageType;

/**
 * Signifies that the page type of a search results was determined.
 * 
 * @author Jeroen
 */
@SuppressWarnings("serial")
public class SearchPageTypeDeterminedEvent extends EventObject {
	
	private PageType imdbPageType;
	
	public SearchPageTypeDeterminedEvent(Object source, PageType imdbPageType) {
		super(source);
		this.imdbPageType = imdbPageType;
	}
	
	public PageType getImdbPageType() {
		return imdbPageType;
	}
	
}
