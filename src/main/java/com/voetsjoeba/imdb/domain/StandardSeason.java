package com.voetsjoeba.imdb.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.voetsjoeba.imdb.domain.api.Episode;
import com.voetsjoeba.imdb.domain.api.Season;

/**
 * Default {@link Season} implementation.
 * 
 * @author Jeroen De Ridder
 */
public class StandardSeason implements Season, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Integer number;
	protected List<Episode> episodes;
	
	public StandardSeason(){
		episodes = new ArrayList<Episode>();
	}
	
	public StandardSeason(int number){
		this();
		this.number = Integer.valueOf(number);
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public List<Episode> getEpisodes() {
		return Collections.unmodifiableList(episodes);
	}
	
	public Episode getEpisode(int number){
		
		// TODO: this is inefficient, but we'll allow it since the amount of episodes in an IMDb title is usually very low
		for(Episode episode : episodes){
			if(episode.getNumber() == number) return episode;
		}
		
		return null;
	}
	
	public void setEpisodes(List<Episode> episodes) {
		this.episodes = episodes;
	}
	
	public boolean hasEpisode(int number){
		
		// TODO: this is inefficient, but we'll allow it since the amount of episodes in an IMDb title is usually very low
		Episode episode = getEpisode(number);
		return (episode != null);
		//return ((number - 1) >= 0 && (number - 1) < episodes.size());
		
	}
	
}
