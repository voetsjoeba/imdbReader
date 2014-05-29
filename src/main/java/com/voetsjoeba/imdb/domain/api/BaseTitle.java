package com.voetsjoeba.imdb.domain.api;


/**
 * Holds information that is common to any title type, be it full or limited.
 * 
 * @author Jeroen De Ridder
 */
public interface BaseTitle extends Searchable {
	
	/**
	 * Returns the IMDb ID string for this title.
	 */
	public String getId();
	
	/**
	 * Returns the title of this IMDb item.
	 */
	public String getTitle();
	
	/**
	 * Returns the year of publication of this IMDb title.
	 */
	public Integer getYear();
	
	/**
	 * Returns the URL to this item on the IMDb site.
	 */
	public String getUrl();
	
	public void setTitle(String title);
	public void setYear(Integer year);
	
}
