package com.voetsjoeba.imdb;

import junit.framework.TestCase;

import com.voetsjoeba.imdb.domain.LimitedTitle;
import com.voetsjoeba.imdb.domain.api.BaseTitle;

/**
 * Helper class for some utility functions.
 * 
 * @author Jeroen De Ridder, 2011
 */
public abstract class AbstractImdbTestCase extends TestCase {
	
	private ImdbSearcher searcher = new ImdbSearcher();
	
	protected void assertMultipleResults(ImdbSearchResults results){
		
		assertFalse(results.isExactMatch());
		
		for(BaseTitle title : results.getMatches()){
			assertTrue(title instanceof LimitedTitle);
		}
		
	}
	
	protected ImdbSearchResults search(String query) throws Exception {
		return searcher.search(query);
	}
	
}
