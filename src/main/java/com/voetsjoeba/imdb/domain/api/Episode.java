package com.voetsjoeba.imdb.domain.api;

import java.util.Calendar;

import com.voetsjoeba.imdb.domain.SeasonEpisodeNumber;

/**
 * Represents an episode within a {@link Season}.
 * 
 * @author Jeroen De Ridder
 */
public interface Episode {
	
	/**
	 * Returns the number of this episode within its season.
	 */
	public int getNumber();
	
	/**
	 * Returns the title of this episode.
	 */
	public String getTitle();
	
	/**
	 * Returns the date this episode was first aired.
	 */
	public Calendar getAirDate();
	
	/**
	 * Returns the plot of this episode.
	 */
	public String getPlot();
	
	/**
	 * Returns the season this episode belongs to.
	 */
	public Season getSeason();
	
	/**
	 * Returns the season/episode number for this episode.
	 */
	public SeasonEpisodeNumber getSeasonEpisodeNumber();
	
	public void setNumber(int number);
	public void setTitle(String title);
	public void setAirDate(Calendar airDate);
	public void setPlot(String plot);
	public void setSeason(Season season);
	
	/**
	 * Returns true if this episode does not really have a title and was instead assigned a generic name by IMDb.
	 */
	public boolean isTitleDefault();
	
}
