package com.voetsjoeba.imdb.event;

import java.util.EventObject;

import org.jsoup.nodes.Document;

@SuppressWarnings("serial")
public class SearchPageParsedEvent extends EventObject
{
	private Document imdbPage;
	
	public SearchPageParsedEvent(Object source, Document imdbPage)
	{
		super(source);
		this.imdbPage = imdbPage;
	}
	
	public Document getImdbPage()
	{
		return imdbPage;
	}
}
