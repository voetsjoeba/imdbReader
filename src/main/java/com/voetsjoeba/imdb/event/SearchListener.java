package com.voetsjoeba.imdb.event;

import java.util.EventListener;

public interface SearchListener extends EventListener
{
	public void searchPageFetched(SearchPageFetchedEvent e);
	public void searchPageParsed(SearchPageParsedEvent e);
	public void searchPageTypeDetermined(SearchPageTypeDeterminedEvent e);
}
