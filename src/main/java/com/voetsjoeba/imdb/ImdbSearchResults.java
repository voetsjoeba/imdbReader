package com.voetsjoeba.imdb;

import java.util.LinkedList;
import java.util.List;

import com.voetsjoeba.imdb.domain.AbstractTitle;
import com.voetsjoeba.imdb.domain.LimitedTitle;
import com.voetsjoeba.imdb.domain.api.BaseTitle;
import com.voetsjoeba.imdb.domain.api.Title;
import com.voetsjoeba.imdb.exception.NoExactMatchException;

/**
 * Represents IMDb search results. Results can be either a single {@link Title} instance (i.e. an exact match), or a list of matching 
 * {@link LimitedTitle}s.
 * 
 * @author Jeroen De Ridder
 */
public class ImdbSearchResults
{
	private List<BaseTitle> matches;
	
	public ImdbSearchResults()
	{
		matches = new LinkedList<BaseTitle>();
	}
	
	public ImdbSearchResults(List<BaseTitle> matches)
	{
		this.matches = matches;
	}
	
	/**
	 * Creates an ImdbSearchResults instance for an exact match.
	 */
	public ImdbSearchResults(Title exactMatch)
	{
		this(); // invoke default constructor
		matches.add(exactMatch);
	}
	
	/**
	 * Returns the list of matches. If the result of the search was a single title, then this method will return a {@link List} that contains exactly that single item as a full {@link AbstractTitle} object.
	 * Otherwise, the list will contain {@link LimitedTitle}s only. You may check for this using the {@link #isExactMatch()} method.
	 * @see #isExactMatch()
	 */
	public List<BaseTitle> getMatches()
	{
		return matches;
	}
	
	/**
	 * Returns true if the search resulted in an exact match, false otherwise. An exact match equals a search result that is 
	 * redirected by IMDb itself to a single detail page. Note that this is different from search result listings that carry
	 * only a single match.
	 * @see #getMatches()
	 */
	public boolean isExactMatch()
	{
		return (matches.size() == 1 && matches.get(0) instanceof Title);
	}
	
	/**
	 * Returns an exact match held in these results.
	 * If these results do not hold an exact match, a {@link NoExactMatchException} is thrown. Hence, you should probably only use
	 * this method after having checked with {@link #isExactMatch()}. 
	 * 
	 * @throws NoExactMatchException if these results do not hold an exact match
	 * @return the exact result (if any)
	 */
	public Title getExactMatch()
	{
		if (!isExactMatch())
			throw new NoExactMatchException();
		return (Title) matches.get(0);
	}
}
