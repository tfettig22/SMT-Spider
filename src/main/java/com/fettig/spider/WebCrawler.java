package com.fettig.spider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/****

<b>Title:</b> WebCrawler.java<br>
<b>Project:</b> spider<br>
<b>Description:</b>
	This class can be used to facilitate the workflow of creating a socket and retrieving the html of a website, writing
	that html to a new file, and parsing the html to get all of the links that exist in the page's html. A socket will
	be created for each link/path, and a corresponding html file will be written. Uses the ConnectionManager, ContentWriter,
	and Parser classes.
<br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Feb 02 2023
@updates:

****/

public class WebCrawler {
	
	private static final String HOMEPAGE = "smt-stage.qa.siliconmtn.com";
	private static final String ADMIN = "/sb/admintool?cPage=index&actionId=WEB_SOCKET&organizationId=SMT";
	private static final String LOGIN_INFO = "requestType=reqBuild&pmid=ADMIN_LOGIN&emailAddress={USER_EMAIL}&password={USER_PASSWORD}&l=";

	private Map<String, Boolean> urlExtensions = new HashMap<>();
	Logger logger = Logger.getLogger(WebCrawler.class.getName());
	
	/**
	 * Main method to instantiate the WebCrawler and enter into the crawling process
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		WebCrawler spider = new WebCrawler();
		spider.beginCrawling(HOMEPAGE, 443);
	}
	
	/**
	 * First logs in to the admin site, and then begins the 'crawl' process by adding / to the map so that the default page is home page
	 * @param server the server to connect to via socket
	 * @param port the port number of the server
	 * @throws IOException
	 */
	public void beginCrawling(String server, int port) throws IOException {
		ConnectionManager connection = new ConnectionManager();
		loginAndGetAdminPage(connection);
		urlExtensions.put("/", false);
		while (urlExtensions.values().contains(false)) {
			crawlWebsite(connection, server, port);
		}
	}
	
	/**
	 * Instantiates ContentWriter and Parser, then loops through each urlExtension and creates a socket connection to each
	 * Creates a new html file for each url path and then parses the path for any additional paths/links and adds them to the map
	 * @param server the server to connect to via socket
	 * @param port the port number of the server
	 * @throws IOException
	 */
	public void crawlWebsite(ConnectionManager connection, String server, int port) throws IOException {
		// instantiate other classes
		ContentWriter writer = new ContentWriter();
		Parser parse = new Parser();
		
		// iterate through all urls in map, creating a file for each one
		for (Map.Entry<String, Boolean> entry : urlExtensions.entrySet()) {
			if (Boolean.FALSE.equals(entry.getValue())) {
				String html = connection.createSocket(server, port, entry.getKey(), "get", null);
				File htmlFile = writer.writeCharsToFile("src/main/resources/smt-" + formatPath(entry.getKey()) + ".html", html);
				
				// log to console that a file has been created
				String fileCreatedMsg = formatPath(formatPath(entry.getKey()) + " file has been created");
				logger.log(Level.INFO, fileCreatedMsg);
				
				// parse the file and add any new links found to the urlExtensions map
				for (String link : parse.parseFile(htmlFile)) {
					if (!urlExtensions.keySet().contains(link)) {
						urlExtensions.put(link, false);						
					}
				}
				
				// change the boolean to true once the file has been created/parsed
				urlExtensions.put(entry.getKey(), true);
			}
		}
	}
	
	/**
	 * Logs in to the requested site using a post request and then gets the html of the website and writes it to a new file
	 * @param connection the connection manager, used to create a socket
	 * @throws IOException
	 */
	public void loginAndGetAdminPage(ConnectionManager connection) throws IOException {
		ContentWriter writer = new ContentWriter();
		// send the post request
		connection.createSocket(HOMEPAGE, 443, ADMIN, "post", LOGIN_INFO);
		logger.log(Level.INFO, "login complete");
		
		// after post request is sent and login is complete, send get request for the admin page
		String html = connection.createSocket(HOMEPAGE, 443, ADMIN, "get", null);
		writer.writeCharsToFile("src/main/resources/smt-admin.html", html);
		logger.log(Level.INFO, "admin file has been created");
	}
	
	/**
	 * Takes in a string and formats it by removing the first character
	 * @param path the string to be formatted
	 * @return
	 */
	public String formatPath(String path) {
		return (path.equals("/")) ? "home" : path.replace("/", "");
	}
	
	/**
	 * Getter method for the urlExtensions map
	 * @return
	 */
	public Map<String, Boolean> getUrlExtensions() {
		return this.urlExtensions;
	}
}
