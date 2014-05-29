package com.voetsjoeba.imdb.domain;

import java.io.Serializable;

import com.voetsjoeba.imdb.domain.api.Episode;

/**
 * Aggregates a season and an episode number.
 * 
 * @author Jeroen De Ridder
 */
public class SeasonEpisodeNumber implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected Integer seasonNumber;
	protected Integer episodeNumber;
	
	public SeasonEpisodeNumber(Integer seasonNumber, Integer episodeNumber){
		this.seasonNumber = seasonNumber;
		this.episodeNumber = episodeNumber;
	}
	
	public SeasonEpisodeNumber(Episode episode){
		this(episode.getSeason().getNumber(), episode.getNumber());
	}
	
	public Integer getSeasonNumber() {
		return seasonNumber;
	}
	
	public void setSeasonNumber(Integer seasonNumber) {
		this.seasonNumber = seasonNumber;
	}
	
	public Integer getEpisodeNumber() {
		return episodeNumber;
	}
	
	public void setEpisodeNumber(Integer episodeNumber) {
		this.episodeNumber = episodeNumber;
	}
	
	@Override
	public String toString(){
		return String.format("S%02dE%02d", seasonNumber, episodeNumber);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (episodeNumber == null ? 0 : episodeNumber.hashCode());
		result = prime * result + (seasonNumber == null ? 0 : seasonNumber.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) return false;
		if(obj == this) return true;
		
		if(getClass() != obj.getClass()) return false;
		SeasonEpisodeNumber other = (SeasonEpisodeNumber) obj;
		
		Integer otherEpisodeNumber = other.getEpisodeNumber();
		if(episodeNumber == null && otherEpisodeNumber != null) return false;
		if(!episodeNumber.equals(otherEpisodeNumber)) return false;
		
		Integer otherSeasonNumber = other.getSeasonNumber();
		if(seasonNumber == null && otherSeasonNumber != null) return false;
		if(!seasonNumber.equals(otherSeasonNumber)) return false;
		
		return true;
		
	}
	
}
