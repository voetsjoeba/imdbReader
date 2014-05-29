package com.voetsjoeba.imdb.domain.api;

import java.util.List;

import com.voetsjoeba.imdb.domain.SeasonEpisodeNumber;

/**
 * Represents an IMDb TV series.
 * 
 * @author Jeroen De Ridder
 */
public interface Series extends Title {
	
	/**
	 * Returns a list of all seasons in this series.
	 */
	public List<Season> getSeasons();
	
	/**
	 * Returns the season with the specified number (starting at 1). If no such season exists, null is returned.
	 */
	public Season getSeason(int number);
	
	/**
	 * Returns true if this series has a season with the specified number (starting at 1), false otherwise.
	 */
	public boolean hasSeason(int number);
	
	/**
	 * Returns true if this series contains an episode with the provided season and episode numbers, false otherwise.
	 */
	public boolean hasSeasonEpisode(SeasonEpisodeNumber seNumber);
	
	public void setSeasons(List<Season> seasons);
	
}