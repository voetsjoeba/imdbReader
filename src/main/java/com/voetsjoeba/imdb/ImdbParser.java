package com.voetsjoeba.imdb;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voetsjoeba.imdb.domain.LimitedTitle;
import com.voetsjoeba.imdb.domain.StandardEpisode;
import com.voetsjoeba.imdb.domain.StandardMovie;
import com.voetsjoeba.imdb.domain.StandardName;
import com.voetsjoeba.imdb.domain.StandardSeason;
import com.voetsjoeba.imdb.domain.StandardSeries;
import com.voetsjoeba.imdb.domain.api.BaseTitle;
import com.voetsjoeba.imdb.domain.api.Episode;
import com.voetsjoeba.imdb.domain.api.Movie;
import com.voetsjoeba.imdb.domain.api.Name;
import com.voetsjoeba.imdb.domain.api.Season;
import com.voetsjoeba.imdb.domain.api.Series;
import com.voetsjoeba.imdb.domain.api.Title;
import com.voetsjoeba.imdb.util.HttpUtils;


/**
 * Provides utility functions for parsing fetched IMDb pages and extracting useful data.
 * 
 * @author Jeroen De Ridder
 */
@ThreadSafe
public class ImdbParser {
	
	private static final Logger log = LoggerFactory.getLogger(ImdbParser.class);
	
	/**
	 * Represents the possible different page types to fetch/parse.
	 * 
	 * @author Jeroen De Ridder
	 */
	public enum PageType {
		TITLE_PAGE,
		SEARCH_RESULTS
	}
	
	// Patterns are always thread-safe; Matchers, however, are not.
	protected static final Pattern hrefIdPattern = Pattern.compile("/title/([^/]+)/?");
	protected static final Pattern searchResultYearPattern = Pattern.compile("^\\((\\d{4})\\)");
	protected static final Pattern episodeNumberPattern = Pattern.compile("Season\\s+(\\d+),\\s+Episode\\s+(\\d+)");
	
	// DateFormats are not thread-safe by default! Care must be taken to avoid using them concurrently from separate
	// threads.
	protected static final List<DateFormat> episodeDateFormats;
	static {
		episodeDateFormats = new ArrayList<DateFormat>();
		episodeDateFormats.add(new SimpleDateFormat("d MMMM yyyy", Locale.US));
		episodeDateFormats.add(new SimpleDateFormat("yyyy", Locale.US));
	}
	
	/**
	 * Helper method; parses an episode airdate in textual form and returns it as a Date instance, or null if the
	 * parsing failed.
	 */
	protected static Date parseEpisodeAirdate(String dateText) {
		
		if(dateText == null || dateText.equals("????"))
			return null;
		
		Date result = null;
		
		
		// important -- DateFormats are not thread-safe, so we need to synchronize their access (especially for the 
		// prefetcher threads. Since it's tricky to get a grasp on the locking order with these for loops, we'll 
		// just lock on the entire list of date formats and pray parsing the dates doesn't take too long.
		
		synchronized(episodeDateFormats){
			
			for(DateFormat episodeDateFormat : episodeDateFormats){
				try {
					result = episodeDateFormat.parse(dateText);
				}
				catch(ParseException pex) {
					// no go, try next one
				}
			}
			
		}
		
		if(result == null) log.debug("Failed to parse date: {}", dateText);
		return result;
	}
	
	/**
	 * Helper method for {@link #parseSearchResults(Document)}; constructs a single {@link LimitedTitle} instance from a search result root node.
	 */
	@SuppressWarnings("unchecked")
	protected static LimitedTitle parseSearchResult(Node matchNode) {
		
		String id = null;
		
		Node searchResultHrefNode = matchNode.selectSingleNode("a[1]/@href");
		if(searchResultHrefNode instanceof Attribute){
			
			String href = searchResultHrefNode.getText().trim();
			
			Matcher matcher = hrefIdPattern.matcher(href);
			if(matcher.find()){
				id = matcher.group(1);
			}
			
		}
		
		if(id == null) return null;
		
		LimitedTitle searchResult = new LimitedTitle(id);
		List<String> extraInfo = new ArrayList<String>();
		
		//log.debug("Found popular title node: {}", popularTitleNode.asXML());
		
		Node searchResultTitleNode = matchNode.selectSingleNode("a[1]/text()");
		if(searchResultTitleNode instanceof Text){
			String title = StringUtils.strip(searchResultTitleNode.getText().trim(), "\"");
			searchResult.setTitle(title);
		}
		
		
		
		Node searchResultYearNode = matchNode.selectSingleNode("text()[1]");
		if(searchResultYearNode instanceof Text){
			
			String yearString = searchResultYearNode.getText().trim();
			
			Matcher matcher = searchResultYearPattern.matcher(yearString);
			if(matcher.find()){
				Integer year = Integer.parseInt(matcher.group(1));
				searchResult.setYear(year);
				
				// use the remainder of the matched date string as extra info (e.g. TV series have an extra " (TV series)" after the year
				int matchStart = matcher.start();
				int matchEnd = matcher.end();
				
				String moreYearInfo = "";
				if (matchStart > 0)                     moreYearInfo = yearString.substring(0, matchStart) + moreYearInfo;
				if (matchEnd < yearString.length() - 1) moreYearInfo = moreYearInfo + yearString.substring(matchEnd + 1); 
				
				moreYearInfo = moreYearInfo.trim();
				if (!StringUtils.isEmpty(moreYearInfo))
					extraInfo.add(moreYearInfo);
			}
		}
		
		// not sure if this is still present -- used to contain small bits of extra info like "(TV series)", but now
		// seems to have moved into the same text as the year string (Jan 04, 2013) 
		List<Node> extraInfoNodes = matchNode.selectNodes("small[last()]/text()");
		for(Node extraInfoNode : extraInfoNodes){
			extraInfo.add(extraInfoNode.getText());
		}
		
		if(extraInfo.size() > 0)
			searchResult.setExtraInfo(extraInfo);
		
		return searchResult;
		
	}
	
	/**
	 * Parses a search results page into a list of {@link BaseTitle}s.
	 */
	@SuppressWarnings("unchecked")
	protected static List<LimitedTitle> parseSearchResults(Document document){
		
		List<LimitedTitle> matches = new LinkedList<LimitedTitle>();
		
		List<Node> titleListNodes = new LinkedList<Node>(); // tables containing title result rows
		titleListNodes.add(document.selectSingleNode("//div[@id='main']//div[@class='findSection']/table[1]"));
		
		for(Node titleListNode : titleListNodes){
			
			if(titleListNode instanceof Element){
				
				Element titleList = (Element) titleListNode;
				List<Node> titleNodes = titleList.selectNodes("tr/td[2]"); // table cells containing link + year, one for each row
				
				for(Node titleNode : titleNodes){
					
					LimitedTitle limitedTitle = parseSearchResult(titleNode);
					if(limitedTitle != null) matches.add(limitedTitle);
					
				}
				
			}
			
		}
		
		return matches;
		
	}
	
	/**
	 * Parses a single title page into its corresponding {@link Title}.
	 * 
	 * @param document The document to parse.
	 * @param fetchThumbnail Whether or not to also fetch the title's thumbnail image (if any).
	 */
	protected static Title parseTitlePage(Document document, boolean fetchThumbnail){
		
		// get title ID
		
		String id = null;
		String canonicalHref = getCanonicalHref(document);
		
		if(canonicalHref != null){
			
			Matcher matcher = hrefIdPattern.matcher(canonicalHref);
			if(matcher.find()){
				id = matcher.group(1);
			}
			
		}
		
		if(id == null) return null;
		
		// determine type (movie/series) and parse accordingly
		
		Node tvSeriesNode = document.selectSingleNode("//td[@id='overview-top']/div[@class='infobar']/text()");
		boolean isTvSeries = (tvSeriesNode != null && tvSeriesNode.getText().toLowerCase().contains("series"));
		
		Title imdbTitle;
		
		if(isTvSeries){
			imdbTitle = new StandardSeries(id);
			parseSeriesInfo(document, (Series) imdbTitle);
		} else {
			imdbTitle = new StandardMovie(id);
			parseMovieInfo(document, (Movie) imdbTitle);
		}
		
		
		Node thumbnailNode = document.selectSingleNode("//td[@id='img_primary']//a[1]/img[1]");
		if(thumbnailNode instanceof Element){
			
			Element thumbnailElement = (Element) thumbnailNode;
			Attribute srcAttribute = thumbnailElement.attribute("src");
			
			if(srcAttribute != null){
				
				String thumbnailSrc = srcAttribute.getValue();
				imdbTitle.setThumbnailUrl(thumbnailSrc);
				
				if(fetchThumbnail){
					
					try {
						BufferedImage image = HttpUtils.fetchImage(thumbnailSrc);
						imdbTitle.setThumbnail(image);
					}
					catch(IOException e) {
						log.error(e.getMessage());
					}
					catch(HttpException e) {
						log.error(e.getMessage());
					}
					
				}
				
			}
			
		}
		
		return imdbTitle;
		
	}
	
	/**
	 * Parses information that is specific to series from the given {@link Document} title page and writes it to the provided {@link Series}.
	 */
	@SuppressWarnings("unchecked")
	protected static void parseSeriesInfo(Document document, Series series){
		
		// parse seasons and episodes
		
		parseGenericTitleInfo(document, series);
		
		Node yearNode = document.selectSingleNode("//td[@id='overview-top']/h1[contains(@class,'header')]/span[2]/text()");
		if(yearNode instanceof Text){
			
			String yearText = yearNode.getText();
			yearText = StringUtils.remove(yearText, "(");
			yearText = StringUtils.remove(yearText, ")");
			yearText = StringUtils.remove(yearText, "TV Series");
			yearText = yearText.trim();
			
			if (yearText.length() >= 4){
				yearText = yearText.substring(0, 4);
				try {
					series.setYear(Integer.parseInt(yearText));
				} catch(NumberFormatException nfex){
					// ok np, couldn't parse the year
				}
			}
			
		}
		
		log.debug("\tYear: {}", series.getYear());
		
		try {
			
			// the /episodes page is no longer listing all of the episodes at once; instead you have a dropdown to select
			// the series number that will then load the listing of episodes in that episode via AJAX.
			// the episode cast page still contains all of them at once (plus their descriptions and air dates), but in a
			// horribly linear, non-structured way, but we can still make sense of it by anchoring on the h4 title nodes
			// and from each one select its next few sibling nodes that contain the data we're after
			
			String episodeListUrl = series.getUrl() + "/epcast";
			String episodeListHtml = HttpUtils.getPage(episodeListUrl);
			Document episodeList = HttpUtils.parsePage(episodeListHtml);
			
			Map<Integer, Season> seasonMap = new HashMap<Integer, Season>(); // map season numbers to Season instances
			List<Node> seasonTitleNodes = episodeList.selectNodes("//div[@id='tn15content']/h4");
			
			for (Node seasonTitleNode : seasonTitleNodes) {
				
				Integer seasonNr  = null;
				Integer episodeNr = null;
				
				String seasonEpisodeNrString = seasonTitleNode.getText();
				Matcher matcher = episodeNumberPattern.matcher(seasonEpisodeNrString);
				if (matcher.find()) {
					seasonNr = Integer.parseInt(matcher.group(1)); // shouldn't throw, regex matches on numeric chars only
					episodeNr = Integer.parseInt(matcher.group(2));
				}
				
				// can't do anything if we don't know the season and episode nr
				if (seasonNr == null || episodeNr == null)
					continue;
				
				String title = null;
				String plot = null;
				Calendar airDate = null;
				
				Node titleNode = seasonTitleNode.selectSingleNode("a[1]/text()");
				Node airdateNode = seasonTitleNode.selectSingleNode("following-sibling::b[1]/text()");
				Node plotNode = seasonTitleNode.selectSingleNode("following-sibling::br[1]/following-sibling::text()");
				
				if(titleNode instanceof Text){
					title = StringUtils.trimToNull(titleNode.getText());
				}
				
				if (plotNode instanceof Text){
					plot = StringUtils.trimToNull(plotNode.getText());
				}
				
				if(airdateNode instanceof Text){
					
					String dateText = StringUtils.trimToNull(airdateNode.getText());
					Date parsedDate = parseEpisodeAirdate(dateText);
					
					if (parsedDate != null) {
						airDate = Calendar.getInstance();
						airDate.setTime(parsedDate);
					}
				}
				
				// create season if it doesn't already exist
				Season season = seasonMap.get(seasonNr);
				if (season == null) {
					season = new StandardSeason(seasonNr);
					seasonMap.put(seasonNr, season);
				}
				
				// if the episode already exists within the season, then we have a duplicate
				Episode existingEpisode = season.getEpisode(episodeNr);
				if (existingEpisode == null)
				{
					Episode newEpisode = new StandardEpisode(season);
					newEpisode.setNumber(episodeNr);
					newEpisode.setAirDate(airDate);
					newEpisode.setPlot(plot);
					newEpisode.setTitle(title);
					
					// TODO: create an addEpisode member function, srsly
					List<Episode> episodes = new ArrayList<Episode>(season.getEpisodes());
					episodes.add(newEpisode);
					season.setEpisodes(episodes);
				} else {
					log.error("Encountered duplicate episode number '"+episodeNr+"' in season '{}' of '{}'", seasonNr, series.getTitle());
				}
				
			}
			
			// create a list out of the map of seasons
			int numSeasons = seasonMap.keySet().size();
			
			// create a list with enough elements to hold all seasons
			List<Season> seasonList = new ArrayList<Season>(numSeasons);
			for (int i = 0; i < numSeasons; i++) {
				seasonList.add(null);
			}
			
			// add all the seasons in their respective places
			for (Map.Entry<Integer, Season> entry : seasonMap.entrySet()) {
				seasonList.set(entry.getKey() - 1, entry.getValue());
			}
			
			series.setSeasons(seasonList);
		}
		catch(IOException ioex) {
			
		}
		catch(HttpException hex){
			
		}
		
	}
	
	/**
	 * Parses information that is specific to movies from the given {@link Document} title page and writes it to the provided {@link Movie}.
	 */
	protected static void parseMovieInfo(Document document, Movie movie){
		
		parseGenericTitleInfo(document, movie);
		
		Node yearNode = document.selectSingleNode("//td[@id='overview-top']/h1[@class='header']/span/a/text()");
		if(yearNode instanceof Text){
			
			String yearText = StringUtils.trimToEmpty(yearNode.getText());
			
			try {
				movie.setYear(Integer.parseInt(yearText));
			} catch(NumberFormatException nfex){
				// ok np, couldn't parse the year
			}
			
		}
		
		log.debug("\tYear: {}", movie.getYear());
		
		// TODO: more stuff about movies to parse here
		
	}
	
	/**
	 * Helper method; parses information that is generic to all IMDb titles from the given {@link Document} title page and writes it to the provided {@link Title}.
	 * This method is intended to be called from within more specialized parsing methods.
	 * 
	 * @param document The document to parse the information from
	 * @param imdbTitle The {@link Title} to write the parsed information to
	 */
	@SuppressWarnings("unchecked")
	protected static void parseGenericTitleInfo(Document document, Title imdbTitle){
		
		Node titleNode = document.selectSingleNode("//td[@id='overview-top']/h1[@class='header']/span[1]/text()");
		if(titleNode instanceof Text){
			String title = StringUtils.strip(titleNode.getText().trim(), "\"");
			imdbTitle.setTitle(title);
		}
		
		// ------------------------------------------------------------------------------------
		
		// year extraction has been moved to the movie/series info methods -- different for each type
		
		// ------------------------------------------------------------------------------------
		
		Node ratingNode = document.selectSingleNode("//td[@id='overview-top']/div[contains(@class, 'star-box')]/div[contains(@class, 'star-box-details')]/strong[1]/span[1]/text()");
		if(ratingNode instanceof Text){
			
			String ratingText = StringUtils.trimToEmpty(ratingNode.getText());
			//String ratingScore = ratingText.substring(0, ratingText.indexOf('/'));
			try {
				double ratingDouble = Double.parseDouble(ratingText);
				Integer rating = Integer.valueOf(Double.valueOf(Math.floor(ratingDouble*10)).intValue());
				imdbTitle.setRating(rating);
			}
			catch(NumberFormatException nfex){
				
			}
			
		}
		
		// ------------------------------------------------------------------------------------
		
		List<String> genres = new LinkedList<String>();
		List<Node> genreNodes = document.selectNodes("//div[@id='main']/div[contains(@class, 'article')]/div[contains(@class, 'see-more')][contains(h4,'Genres:')]/a/text()");
		
		for(Node genreNode : genreNodes){
			
			if(genreNode instanceof Text){
				genres.add(genreNode.getText().trim());
			}
			
		}
		
		imdbTitle.setGenres(genres);
		
		// ------------------------------------------------------------------------------------
		
		List<Name> stars = new LinkedList<Name>();
		List<Node> starNodes = document.selectNodes("//td[@id='overview-top']/div[contains(@class, 'txt-block')][contains(h4, 'Stars:')]/a");
		
		for(Node starNode : starNodes){
			
			String id = null;
			//String name = starNode.getText();
			//String name = null;
			
			Node hrefAttribute = starNode.selectSingleNode("@href");
			
			if(hrefAttribute != null){
				id = StringUtils.removeStart(StringUtils.removeEnd(hrefAttribute.getText(), "/"), "/name/");
				id = StringUtils.trimToNull(id);
			}
			
			Node nameSpan = starNode.selectSingleNode("span[1]");
			String name = nameSpan.getText().trim(); 
					
			if(!StringUtils.isEmpty(name) && !StringUtils.isEmpty(id)){
				Name star = new StandardName(id, name);
				stars.add(star);
			}
			
		}
		
		imdbTitle.setStars(stars);
		
		// ------------------------------------------------------------------------------------
		
		Node taglineNode = document.selectSingleNode("//div[@id='maindetails_center_bottom']/div[contains(@class, 'article')]/div[contains(@class, 'txt-block')][contains(h4,'Taglines')]/text()[1]");
		if(taglineNode instanceof Text){
			imdbTitle.setTagline(taglineNode.getText().trim());
		}
		
		// ------------------------------------------------------------------------------------
		
		Node plotNode = document.selectSingleNode("//div[@id='maindetails_center_bottom']/div[contains(@class, 'article')]/h2[text()='Storyline']/following-sibling::p[1]/text()");
		if(plotNode instanceof Text){
			imdbTitle.setPlot(plotNode.getText().trim());
		}
		
		// ------------------------------------------------------------------------------------
		
		log.debug("Parsed title page:");
		log.debug("\tID: {}", imdbTitle.getId());
		log.debug("\tTitle: {}", imdbTitle.getTitle());
		log.debug("\tURL: {}", imdbTitle.getUrl());
		log.debug("\tRating: {}", imdbTitle.getRating());
		log.debug("\tGenres: {}", imdbTitle.getGenres());
		log.debug("\tStars: {}", imdbTitle.getStars());
		log.debug("\tTagline: {}", imdbTitle.getTagline());
		log.debug("\tPlot: {}", imdbTitle.getPlot());
		
	}
	
	/**
	 * Returns the IMDb page type of the provided document, or null if it could not be determined.
	 */
	protected static PageType determinePageType(Document document){
		
		/*
		 * First, try to determine the page type based on the <link rel="canonical" href="..." /> tag.
		 * For title pages, the tag will look like <link rel="canonical" href="http://www.imdb.com/title/tt1127180/" />,
		 * for search results, the tag will look like <link rel="canonical" href="http://www.imdb.com/find?s=all&q=something" />.
		 */
		
		String canonicalHref = getCanonicalHref(document);
		
		if(canonicalHref != null){
			
			if(canonicalHref.indexOf("/title/") > -1){
				return PageType.TITLE_PAGE;
			} else if(canonicalHref.indexOf("/find") > -1){
				return PageType.SEARCH_RESULTS;
			}
			
		}
		
		// no go with the canonical href element, try by checking for <meta name="title" content="IMDb Search">
		// or, failing that, the page title (<title>IMDb Search</title>)
		String title = getTitle(document);
		String metaTitleContent = getMetaTitleContent(document);
		
		if(metaTitleContent != null){
			if(metaTitleContent.indexOf("IMDb Search") >= 0) return PageType.SEARCH_RESULTS;
		}
		
		if(title != null){
			if(title.indexOf("IMDb Search") >= 0) return PageType.SEARCH_RESULTS;
		}
		
		return null;
		
	}
	
	/**
	 * Returns the string value of the href-attribute of the &lt;link rel="canonical" href="..." /&gt; tag, or null if no such value exists.
	 */
	protected static String getCanonicalHref(Document document){
		
		Node linkHrefAttributeNode = document.selectSingleNode("/html/head/link[@rel='canonical']/@href");
		if(linkHrefAttributeNode instanceof Attribute){
			return linkHrefAttributeNode.getText();
		}
		
		return null;
		
	}
	
	/**
	 * Returns the string value of the content attribute of the &lt;meta name="title" /&gt; tag, or null if no such value exists. 
	 */
	protected static String getMetaTitleContent(Document document){
		
		Node metaTitleContentAttributeNode = document.selectSingleNode("/html/head/meta[@name='title']/@content");
		if(metaTitleContentAttributeNode instanceof Attribute){
			return metaTitleContentAttributeNode.getText();
		}
		
		return null;
		
	}
	
	/**
	 * Returns the string value of the HTML &lt;title&gt;&lt;/title&gt; tag, or null if no such value exists.
	 */
	protected static String getTitle(Document document){
		
		Node titleTextNode = document.selectSingleNode("/html/head/title/text()");
		if(titleTextNode instanceof Text){
			return titleTextNode.getText();
		}
		
		return null;
		
	}
	
}
