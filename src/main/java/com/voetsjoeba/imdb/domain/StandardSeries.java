package com.voetsjoeba.imdb.domain;

import java.util.List;

import com.voetsjoeba.imdb.domain.api.Season;
import com.voetsjoeba.imdb.domain.api.Series;

/**
 * Default {@link Series} implementation.
 * 
 * @author Jeroen De Ridder
 */
public class StandardSeries extends AbstractTitle implements Series {
	
	private static final long serialVersionUID = 1L;
	
	// transient to prevent seasons from being serialized upon writing out Titles in the registry. It doesn't make sense to serialize
	// these, as the saved episode list is likely to become obsolete as time progresses.
	protected transient List<Season> seasons;
	
	/**
	 * Creates a new Series with the specified id.
	 */
	public StandardSeries(String id)
	{
		this(id, null);
	}
	
	/**
	 * Creates a new Series with the specified id and title.
	 */
	public StandardSeries(String id, String title)
	{
		super(id);
		setTitle(title);
	}
	
	public List<Season> getSeasons()
	{
		return seasons;
	}
	
	public void setSeasons(List<Season> seasons)
	{
		this.seasons = seasons;
	}
	
	public boolean hasSeason(int number)
	{
		// TODO: this is inefficient, but we'll allow it since the amount of
		// seasons in an IMDb title is usually very low
		return (getSeason(number) != null);
	}
	
	public Season getSeason(int number)
	{
		// TODO: this is inefficient, but we'll allow it since the amount of
		// seasons in an IMDb title is usually very low
		for (Season season : seasons)
		{
			if (season.getNumber() == number)
				return season;
		}
		
		return null;
	}
	
	public boolean hasSeasonEpisode(SeasonEpisodeNumber seNumber)
	{
		Season season = getSeason(seNumber.getSeasonNumber());
		return (season != null && season.hasEpisode(seNumber.getEpisodeNumber()));
	}
	
	@Override
	public String toString()
	{
		return title + (year != null ? " (TV series, " + year.toString() + ")" : "");
	}
	
	public String getTypeString()
	{
		return "TV Series";
	}
}
