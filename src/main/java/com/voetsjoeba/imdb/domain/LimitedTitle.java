package com.voetsjoeba.imdb.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.voetsjoeba.imdb.domain.api.Title;


/**
 * Holds only the basic information about a title as returned on the search pages.
 * 
 * @author Jeroen De Ridder
 */
public class LimitedTitle extends AbstractBaseTitle {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Optional list of extra info strings. Can be null.
	 */
	protected List<String> extraInfo;
	
	public LimitedTitle(String id) {
		super(id);
	}
	
	/**
	 * Constructs a LimitedTitle instance for a larger Title instance.
	 * @param title the larger Title instance to create this LimitedTitle from. Must not be null.
	 */
	public LimitedTitle(Title title){
		super(title.getId());
		
		this.year = title.getYear();
		this.title = title.getTitle();
	}

	public List<String> getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(List<String> extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	@Override
	public String toString() {
		String result = super.toString();
		if(extraInfo != null) result += " " + StringUtils.join(extraInfo, ", ");
		return result;
	}
	
}
