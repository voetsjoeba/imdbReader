package com.voetsjoeba.imdb.domain.api;

import java.util.List;

/**
 * Represents a season within a {@link Series}.
 * 
 * @author Jeroen De Ridder
 */
public interface Season {
	
	/**
	 * Returns the number of this season (starting at 1).
	 */
	public Integer getNumber();
	
	/**
	 * Returns an unmodifiable list of {@link Episode}s in this season.
	 */
	public List<Episode> getEpisodes();
	
	/**
	 * Returns the episode with the provided number (starting at 1). If no such episode exists, null is returned.
	 */
	public Episode getEpisode(int number);
	
	/**
	 * Returns true if this season contains an episode with the provided number (starting at 1), false otherwise.
	 */
	public boolean hasEpisode(int number);
	
	public void setNumber(Integer setNumber);
	public void setEpisodes(List<Episode> episode);
	
}