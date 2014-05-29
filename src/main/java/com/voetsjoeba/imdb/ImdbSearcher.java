package com.voetsjoeba.imdb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voetsjoeba.imdb.ImdbParser.PageType;
import com.voetsjoeba.imdb.domain.StringSearchable;
import com.voetsjoeba.imdb.domain.api.BaseTitle;
import com.voetsjoeba.imdb.domain.api.Searchable;
import com.voetsjoeba.imdb.domain.api.Title;
import com.voetsjoeba.imdb.event.SearchListener;
import com.voetsjoeba.imdb.event.SearchPageFetchedEvent;
import com.voetsjoeba.imdb.event.SearchPageParsedEvent;
import com.voetsjoeba.imdb.event.SearchPageTypeDeterminedEvent;
import com.voetsjoeba.imdb.exception.ImdbException;
import com.voetsjoeba.imdb.exception.UnknownPageTypeException;
import com.voetsjoeba.imdb.util.HttpUtils;
import com.voetsjoeba.imdb.util.ListenerUtils;

/**
 * Submits search queries and fetches the results from IMDb.
 * 
 * @author Jeroen De Ridder
 */
@ThreadSafe
public class ImdbSearcher {
	
	private static final Logger log = LoggerFactory.getLogger(ImdbSearcher.class);
	
	private EventListenerList listeners;
	
	public ImdbSearcher(){
		listeners = new EventListenerList();
	}
	
	public void addSearchListener(SearchListener l){
		listeners.add(SearchListener.class, l);
	}
	
	/**
	 * Main method for searching IMDb titles. Use this method to search for an IMDb entry.
	 * 
	 * @param searchable The object to look up.
	 * @param fetchThumbnail Whether the thumbnail image for this title should also be fetched (if applicable).
	 * @return An {@link ImdbSearchResults} object holding the fetched result data, or null if no query could be extracted from the provided Searchable.
	 * 
	 * @throws IOException if an I/O exception occured
	 * @throws HttpException if the required IMDb page could not be fetched
	 * @see ImdbSearchResults
	 */
	public ImdbSearchResults search(Searchable searchable, boolean fetchThumbnail) throws IOException, HttpException {
		
		if(searchable == null) return null;
		
		String query = StringUtils.trimToNull(searchable.getSearchTerm());
		if(query == null) return null;
		
		try {
			
			String imdbPageHtml = HttpUtils.getPage(getSearchURL(query));
			fireSearchPageFetched(new SearchPageFetchedEvent(this, imdbPageHtml));
			
			Document imdbPage = HttpUtils.parsePage(imdbPageHtml);
			fireSearchPageParsed(new SearchPageParsedEvent(this, imdbPage));
			
			List<BaseTitle> resultList = new LinkedList<BaseTitle>();
			PageType imdbPageType = ImdbParser.determinePageType(imdbPage);
			fireSearchPageTypeDetermined(new SearchPageTypeDeterminedEvent(this, imdbPageType));
			
			if (imdbPageType == null) {
				log.error("Could not determine page type of search results for '{}'", searchable.getSearchTerm());
				return null;
			}
			
			switch(imdbPageType){
				
				case SEARCH_RESULTS:
					resultList.addAll(ImdbParser.parseSearchResults(imdbPage));
					break;
					
				case TITLE_PAGE:
					Title imdbTitle = ImdbParser.parseTitlePage(imdbPage, fetchThumbnail);
					if(imdbTitle != null) resultList.add(imdbTitle);
					break;
					
				default:
					throw new UnknownPageTypeException("Encountered unknown page type for query \"" + query + "\"");
					
			}
			
			ImdbSearchResults results = new ImdbSearchResults(resultList);
			return results;
			
		}
		catch(MalformedURLException muex){
			throw new ImdbException(muex); // shouldn't happen
		}
		catch(UnsupportedEncodingException ueex) {
			throw new ImdbException(ueex); // shouldn't happen
		}
		
	}
	
	/**
	 * Convenience method; delegates to {@link #search(Searchable, boolean)} with the value <tt>TRUE</tt> for the fetchThumbnail
	 * argument.
	 */
	public ImdbSearchResults search(Searchable searchable) throws IOException, HttpException {
		return search(searchable, true);
	}
	
	/**
	 * Convenience method for searching IMDb; delegates to {@link #search(Searchable)} with a {@link StringSearchable}.
	 * This method can be used if you are only interested in the {@link ImdbSearchResults} and do not wish to attach any event 
	 * listeners.
	 * 
	 * @param query The search query to submit to IMDb.
	 * @throws IOException if an I/O exception occured during lookup
	 * @throws HttpException if the required IMDb page could not be fetched
	 * @see #search(Searchable)
	 */
	public ImdbSearchResults search(String query) throws IOException, HttpException{
		return search(new StringSearchable(query));
	}
	
	protected void fireSearchPageFetched(SearchPageFetchedEvent e) {
		for(SearchListener l : listeners.getListeners(SearchListener.class)) {
			try {
				l.searchPageFetched(e);
			}
			catch(RuntimeException rex) {
				ListenerUtils.handleListenerException(rex, l);
			}
		}
	}
	
	protected void fireSearchPageParsed(SearchPageParsedEvent e) {
		for(SearchListener l : listeners.getListeners(SearchListener.class)) {
			try {
				l.searchPageParsed(e);
			}
			catch(RuntimeException rex) {
				ListenerUtils.handleListenerException(rex, l);
			}
		}
	}
	
	protected void fireSearchPageTypeDetermined(SearchPageTypeDeterminedEvent e) {
		for(SearchListener l : listeners.getListeners(SearchListener.class)) {
			try {
				l.searchPageTypeDetermined(e);
			}
			catch(RuntimeException rex) {
				ListenerUtils.handleListenerException(rex, l);
			}
		}
	}
	
	/**
	 * Builds the search URL that will be used to fetch results from.
	 */
	protected String getSearchURL(String query){
		
		try {
			return "http://www.imdb.com/find?s=all&q=" + URLEncoder.encode(query, "UTF-8");
		}
		catch(UnsupportedEncodingException e) {
			// won't happen, UTF-8 is supported
			log.error("UTF-8 not supported. What is this I don't even");
			throw new Error(e);
		}
		
	}
	
}
