package com.voetsjoeba.imdb.domain;

import com.voetsjoeba.imdb.domain.api.Episode;

/**
 * Abstract superclass for {@link Episode} implementations.
 * 
 * @author Jeroen
 */
public abstract class AbstractEpisode implements Episode {
	
	@Override
	public String toString(){
		return getSeasonEpisodeNumber() + ": " + getTitle();
	}
	
	public SeasonEpisodeNumber getSeasonEpisodeNumber() {
		return new SeasonEpisodeNumber(this);
	}
	
	public boolean isTitleDefault(){
		
		String title = getTitle();
		SeasonEpisodeNumber seNumber = getSeasonEpisodeNumber();
		
		return (title != null && title.equals("Episode #" + seNumber.getSeasonNumber() + "." + seNumber.getEpisodeNumber()));
		
	}
	
}
