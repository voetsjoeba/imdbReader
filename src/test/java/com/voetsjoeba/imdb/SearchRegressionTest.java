package com.voetsjoeba.imdb;

import com.voetsjoeba.imdb.domain.api.Series;
import com.voetsjoeba.imdb.domain.api.Title;

/**
 * Regression tests for searches that are known to have been wrong in the past.
 * 
 * @author Jeroen
 */
public class SearchRegressionTest extends AbstractImdbTestCase {
	
	/**
	 * Californication's year used to get reported as null.
	 */
	public void testCalifornicationDate() throws Exception {
		
		ImdbSearchResults results = search("tt0904208");
		assertTrue(results.isExactMatch());
		
		Title title = results.getExactMatch();
		assertTrue(title instanceof Series);
		assertNotNull(title.getId());
		assertNotNull(title.getTitle());
		assertTrue(title.getId().equals("tt0904208"));
		assertTrue(title.getTitle().equals("Californication"));
		
		assertNotNull(title.getYear());
		assertTrue(title.getYear().equals(2007));
		
	}
}
