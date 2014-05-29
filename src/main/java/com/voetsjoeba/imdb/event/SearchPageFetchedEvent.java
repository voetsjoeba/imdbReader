package com.voetsjoeba.imdb.event;

import java.util.EventObject;

@SuppressWarnings("serial")
public class SearchPageFetchedEvent extends EventObject {
	
	private String imdbPageHtml;
	
	public SearchPageFetchedEvent(Object source, String imdbPageHtml) {
		super(source);
		this.imdbPageHtml = imdbPageHtml;
	}

	public String getImdbPageHtml() {
		return imdbPageHtml;
	}
	
}
