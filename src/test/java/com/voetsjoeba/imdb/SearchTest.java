package com.voetsjoeba.imdb;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voetsjoeba.imdb.domain.StandardMovie;
import com.voetsjoeba.imdb.domain.StandardSeries;
import com.voetsjoeba.imdb.domain.api.Episode;
import com.voetsjoeba.imdb.domain.api.Movie;
import com.voetsjoeba.imdb.domain.api.Season;
import com.voetsjoeba.imdb.domain.api.Series;
import com.voetsjoeba.imdb.domain.api.Title;

public class SearchTest extends ImdbTestCase {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(SearchTest.class);
	
	@Override @Before
	public void setUp() throws Exception {
		
	}
	
	@Override @After
	public void tearDown() throws Exception {
		//Thread.sleep(1000);
	}
	
	public void testMovieExactMatch() throws Exception {
		
		//ImdbResults dmthResults = Imdb.search("drag me to hell");
		ImdbSearchResults dmthResults = search("tt1127180");
		assertTrue(dmthResults.isExactMatch());
		
		Title dmth = dmthResults.getExactMatch();
		assertTrue(dmth instanceof StandardMovie);
		assertNotNull(dmth.getId());
		assertNotNull(dmth.getYear());
		assertNotNull(dmth.getTitle());
		assertTrue(dmth.getId().equals("tt1127180"));
		assertTrue(dmth.getYear().equals(2009));
		assertTrue(dmth.getTitle().equals("Drag Me to Hell"));
	}
	
	public void testMovieMultipleSearchResults() throws Exception {
		
		ImdbSearchResults results = search("ring");
		assertMultipleResults(results);
		
	}
	
	public void testSeriesExactMatch_HIMYM() throws Exception {
		
		ImdbSearchResults himymResults = search("tt0460649");
		assertTrue(himymResults.isExactMatch());
		
		Title himym = (Title) himymResults.getMatches().get(0);
		assertTrue(himym instanceof StandardSeries);
		assertTrue(himym.getId().equals("tt0460649"));
		assertTrue(himym.getYear().equals(2005));
		assertTrue(himym.getTitle().equals("How I Met Your Mother"));
		
	}
	
	public void testSeriesMultipleSearchResults() throws Exception {
		
		ImdbSearchResults results = search("stargate");
		assertMultipleResults(results);
		
	}
	
	public void testSingleSearchResult() throws Exception {
		
		// TODO: find a new title that only returns a single result
		/*ImdbSearchResults results = search("eurotrip");
		assertFalse(results.isExactMatch());
		assertEquals(results.getMatches().size(), 1);*/
		
	}
	
	/**
	 * Make sure ratings fetched for series are not null.
	 */
	public void testSeriesRatingFetching() throws Exception {
		
		ImdbSearchResults results = search("tt0773262");
		Title dexter = results.getExactMatch();
		
		assertTrue(dexter instanceof Series);
		assertNotNull(dexter.getRating());
		
	}
	
	/**
	 * Make sure ratings fetched for movies are not null.
	 */
	public void testMoviesRatingFetching() throws Exception {
		
		ImdbSearchResults results = search("tt0151804");
		Title officeSpace = results.getExactMatch();
		
		assertTrue(officeSpace instanceof Movie);
		assertNotNull(officeSpace.getRating());
		
	}
	
	public void testNoMatches() throws Exception {
		ImdbSearchResults results = search("dfsdfsdfsdfsdf");
		assertFalse(results.isExactMatch());
		assertTrue(results.getMatches().size() == 0);
	}
	
	public void testSeriesAndEpisodes_TheOffice() throws Exception {
		
		ImdbSearchResults theOfficeResults = search("tt0386676");
		assertTrue(theOfficeResults.isExactMatch());
		
		Object theOfficeObj = (Object) theOfficeResults.getMatches().get(0);
		assertTrue(theOfficeObj instanceof Series);
		Series theOffice = (Series) theOfficeObj;
		
		// ----------------------------------------------------
		List<Season> seasons = theOffice.getSeasons();
		
		assertTrue(seasons.size() >= 9);
		
		Season season1 = seasons.get(0);
		assertNotNull(season1);
		assertNotNull(season1.getEpisodes());
		
		Episode S01E01 = season1.getEpisode(1);
		assertNotNull(S01E01);
		assertEquals("Pilot", S01E01.getTitle());
	}
	
}
