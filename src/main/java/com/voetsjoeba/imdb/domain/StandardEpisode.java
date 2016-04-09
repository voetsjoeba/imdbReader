package com.voetsjoeba.imdb.domain;

import java.io.Serializable;
import java.util.Calendar;

import com.voetsjoeba.imdb.domain.api.Episode;
import com.voetsjoeba.imdb.domain.api.Season;

/**
 * Default {@link Episode} implementation.
 * 
 * @author Jeroen De Ridder
 */
public class StandardEpisode extends AbstractEpisode implements Serializable
{
	private static final long serialVersionUID = 1;
	protected Season season;
	
	protected int number;
	protected String title;
	protected Calendar airDate;
	protected String plot;
	
	public StandardEpisode(Season season)
	{
		this.season = season;
	}
	
	public StandardEpisode(Season season, int number, String title)
	{
		this(season);
		this.number = number;
		this.title = title;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public void setNumber(int number)
	{
		this.number = number;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String name)
	{
		this.title = name;
	}
	
	public Calendar getAirDate()
	{
		return airDate;
	}
	
	public void setAirDate(Calendar airDate)
	{
		this.airDate = airDate;
	}
	
	public String getPlot()
	{
		return plot;
	}
	
	public void setPlot(String plot)
	{
		this.plot = plot;
	}
	
	public Season getSeason()
	{
		return season;
	}
	
	public void setSeason(Season season)
	{
		this.season = season;
	}
}
