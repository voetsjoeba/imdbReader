package com.voetsjoeba.imdb.domain;

import java.awt.image.BufferedImage;
import java.util.List;

import com.voetsjoeba.imdb.domain.api.Name;
import com.voetsjoeba.imdb.domain.api.Title;

/**
 * Default abstract {@link Title} implementation.
 * 
 * @author Jeroen De Ridder
 */
public abstract class AbstractTitle extends AbstractBaseTitle implements Title {
	
	static final long serialVersionUID = 1L;
	
	protected Integer rating; // on a scale of 0 to 100
	protected String tagline;
	protected String plot;
	protected List<String> genres;
	protected List<Name> stars;
	
	protected transient BufferedImage thumbnail;
	protected String thumbnailUrl;
	
	public AbstractTitle(String id) {
		super(id);
	}
	
	public Integer getRating() {
		return rating;
	}
	
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	
	public List<String> getGenres() {
		return genres;
	}
	
	public List<Name> getStars(){
		return stars;
	}
	
	public void setStars(List<Name> stars) {
		this.stars = stars;
	}
	
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}
	
	public String getTagline() {
		return tagline;
	}
	
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}
	
	public String getPlot() {
		return plot;
	}
	
	public void setPlot(String plot) {
		this.plot = plot;
	}
	
	public BufferedImage getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(BufferedImage image) {
		this.thumbnail = image;
	}
	
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
}
