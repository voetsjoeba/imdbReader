package com.voetsjoeba.imdb.domain;

import java.io.Serializable;

import com.voetsjoeba.imdb.domain.api.BaseTitle;
import com.voetsjoeba.imdb.exception.UnknownIdException;

/**
 * Default abstract {@link BaseTitle} implementation.
 * 
 * @author Jeroen De Ridder
 */
public abstract class AbstractBaseTitle implements BaseTitle, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final String id;
	protected String title;
	protected Integer year;
	
	public AbstractBaseTitle(String id){
		if(id == null) throw new IllegalArgumentException("ID must be non-null");
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getId() {
		return id;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getUrl() {
		if(id == null) throw new UnknownIdException("Cannot build URL to IMDb title: ID not known");
		return "http://www.imdb.com/title/" + id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) return true;
		
		/*
		 * Using a getClass() comparison ensures the symmetry requirement, but makes trivial subclasses unequal (ie. subclasses that eg. only overwrite methods).
		 * This violates the Liskov substitution principle, and also the principle of least astonishment.
		 */
		//if(obj == null || obj.getClass() != this.getClass()) return false; // 
		
		/*
		 * Technically, using instanceof does not enforce the symmetry requirement because a subclass implementation of equals() may not be symmetric with this equals() method,
		 * ie. it's possible that at one point a subclass is written for which passing a superclass object to the subclass equals() does not return the same value as passing the 
		 * subclass to the superclass. However, we leave the responsibility of ensuring symmetry with the author of the subclass.
		 */
		if(!(obj instanceof AbstractBaseTitle)) return false;
		
		BaseTitle other = (BaseTitle) obj;
		return id.equals(other.getId()); // id cannot be null
		
	}
	
	@Override
	public int hashCode() {
		return id.hashCode(); // id cannot be null
	}
	
	@Override
	public String toString() {
		return title + (year != null ? " ("+year.toString()+")" : "");
	}
	
	public String getSearchTerm(){
		return id;
	}
	
}
