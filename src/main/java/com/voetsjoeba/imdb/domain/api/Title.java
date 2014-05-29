package com.voetsjoeba.imdb.domain.api;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Represents a generic IMDb title. Serves as the base class for more specialized titles, primarily {@link Movie} and {@link Series}.
 * 
 * @author Jeroen De Ridder
 */
public interface Title extends BaseTitle {
	
	
	public Integer getRating();
	public List<String> getGenres();
	public List<Name> getStars();
	public String getTagline();
	public String getPlot();
	public String getThumbnailUrl();
	public BufferedImage getThumbnail();
	
	
	public void setRating(Integer rating);
	public void setGenres(List<String> genres);
	public void setStars(List<Name> stars);
	public void setTagline(String tagline);
	public void setPlot(String plot);
	public void setThumbnailUrl(String url);
	public void setThumbnail(BufferedImage image);
	
	/**
	 * Returns a string representation of the type of title represented by this instance.
	 */
	public String getTypeString();
	
}
