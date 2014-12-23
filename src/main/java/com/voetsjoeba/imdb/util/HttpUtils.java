package com.voetsjoeba.imdb.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
public class HttpUtils {
	
	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
	
	private static final Set<String> imageContentTypes = Collections.synchronizedSet(new HashSet<String>(Arrays.asList("image/jpg", "image/jpeg", "image/png", "image/gif")));
	
	/**
	 * Fetches an image from a URL.
	 * 
	 * @param url the URL to fetch. Must not be null.
	 */
	public static BufferedImage fetchImage(String url) throws IOException, HttpException {
		
		if(url == null) throw new IllegalArgumentException("URL argument must not be null");
		if(!url.startsWith("http://")) url = "http://" + url; // throws a weird "Target host must not be null" exception if used without starting "http://" 
		
		BufferedImage result = null;
		HttpClient httpClient = getNoCookiesHttpClient();
		
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		
		StatusLine responseStatusLine = response.getStatusLine();
		if(responseStatusLine.getStatusCode() != HttpStatus.SC_OK){
			throw new HttpException("Response returned status code that is not 200 OK (returned: " + responseStatusLine.getStatusCode() + " " + responseStatusLine.getReasonPhrase() + ")");
		}
		
		HttpEntity responseContent = response.getEntity();
		String responseContentType = responseContent.getContentType().getValue();
		
		log.debug("Image fetch request " + url + " returned result of MIME type " + responseContentType);
		
		/*synchronized(imageContentTypes){ // probably not necessary, but let's play it safe anyway
			if(!imageContentTypes.contains(responseContentType.toLowerCase())){
				throw new HttpException("Response returned unknown MIME type content (returned: " + responseContentType + ", expected: one of " + imageContentTypes + ")");
			}
		}*/
		
		if(!imageContentTypes.contains(responseContentType.toLowerCase())) throw new HttpException("Response returned unknown MIME type content (returned: " + responseContentType + ", expected: one of " + imageContentTypes + ")");
			
		InputStream imageStream = responseContent.getContent();
		result = ImageIO.read(imageStream);
		
		return result;
		
	}
	
	/**
	 * Fetches a web page over HTTP via GET and returns it as a {@link String}.
	 * 
	 * @param url The URL of the page to fetch.
	 */
	public static String getPage(String url) throws IOException, HttpException {
		
		if(url == null) throw new IllegalArgumentException("URL argument must not be null");
		if(!url.startsWith("http://")) url = "http://" + url; // throws a weird "Target host must not be null" exception if used without starting "http://"
		
		// create new default http client and disable cookies
		HttpClient httpClient = getNoCookiesHttpClient();
		
		HttpGet request = new HttpGet(url);
		HttpResponse response = httpClient.execute(request);
		
		// make sure we got a 200 OK response
		StatusLine responseStatusLine = response.getStatusLine();
		if(responseStatusLine.getStatusCode() != HttpStatus.SC_OK){
			throw new HttpException("Response returned status code that is not 200 OK (returned: " + responseStatusLine.getStatusCode() + " " + responseStatusLine.getReasonPhrase() + ")");
		}
		
		// get the response as a properly character-decoded string
		HttpEntity responseContent = response.getEntity();
		String responseString = EntityUtils.toString(responseContent);
		log.trace(responseString);
		
		responseContent.consumeContent();
		return responseString;
		
	}
	
	/**
	 * Cleans up HTML and parses it into a {@link Document}.
	 * @param page The HTML to parse.
	 * @param cleanUp Whether to first clean up the HTML before parsing it.
	 */
	public static Document parsePage(String page){
		return Jsoup.parse(page);
	}
	
	/**
	 * Creates and returns an HttpClient instance that does not bother with cookies.
	 */
	public static HttpClient getNoCookiesHttpClient(){
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.removeRequestInterceptorByClass(RequestAddCookies.class);
		httpClient.removeResponseInterceptorByClass(ResponseProcessCookies.class);
		return httpClient;
		
	}
	
	/**
	 * Exception-safe UTF-8 URLEncoder call. Stops you from having to deal with annoying UnsupportedEncodingExceptions
	 * that will never occur anyway because UTF-8 is supported everywhere.
	 */
	public static String urlEncodeUtf8(String str){
		try {
			return URLEncoder.encode(str, "UTF-8");
		}
		catch(UnsupportedEncodingException e) {
			throw new Error("The UTF-8 character encoding does not appear to be supported. This is required to use this application.", e); // won't happen, UTF-8 is supported
		}
	}
	
	public static List<String> getNonEmptyTextNodeStrings(Element e)
	{
		List<String> result = new ArrayList<String>();
		for (TextNode textNode : e.textNodes())
		{
			String text = StringUtils.trimToNull(textNode.text());
			if (text != null)
				result.add(text);
		}
		return result;
	}
	
}
