package com.voetsjoeba.imdb.util;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtilsTest extends TestCase {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(HttpUtilsTest.class);
	
	@Override @Before
	public void setUp() throws Exception {
		
	}
	
	@Override @After
	public void tearDown() throws Exception {
		
	}
	
	public void testFetchPageContent() throws Exception {
		String response = HttpUtils.getPage("http://www.google.com");
		assertTrue(response.contains("Google"));
	}
	
}
