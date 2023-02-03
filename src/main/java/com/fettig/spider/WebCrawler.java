package com.fettig.spider;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/****

<b>Title:</b> WebCrawler.java<br>
<b>Project:</b> spider<br>
<b>Description:</b>
	This class can be used to create a socket connection to a website server and 'crawl' the site, creating new
	html files for each unique link on the page.  It instantiates the ReaderWriter and Parser classes to do this.
	The crawHomePage method will start by getting all the links on from the home page of the site, and
	then the crawlOtherPages will hit the other url extensions across the site.
<br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Feb 02 2023
@updates:

****/

public class WebCrawler {
	
	private Set<String> urlExtensions = new TreeSet<>();
	
	public static void main(String[] args) throws IOException {
		WebCrawler spider = new WebCrawler();
		spider.process("smt-stage.qa.siliconmtn.com", "443");
	}
	
	public void process(String server, String port) throws IOException {
		crawlHomePage(server, port);
		crawlOtherPages(server, port);
	}
	
	public String createGETSocket(String server, int port, String path) throws IOException {
		StringBuilder builder = new StringBuilder();
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try (SSLSocket socket = (SSLSocket) factory.createSocket(server, port)) {
			
			socket.startHandshake();
			
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes("GET " + path + " HTTP/1.0 \r\n");
			out.writeBytes("Host: " + server + "\r\n");
			out.writeBytes("\r\n");
			out.flush();
			
			InputStreamReader in = new InputStreamReader(socket.getInputStream());
			int input = 0;
			while ((input = in.read()) != -1) {
				builder.append((char) input);
			}
			out.close();
			in.close();
		}
		return builder.toString();
	}
	
	public String createPOSTSocket() {
		return "";
	}
	
	public void crawlHomePage(String server, String port) throws IOException {
		ReaderWriter writer = new ReaderWriter();
		Parser parse = new Parser();
		urlExtensions.add("/");
		String homeHtml = createGETSocket(server, Integer.parseInt(port), "/");
		File homeFile = writer.writeCharsToFile("src/main/resources/smt-home.html", homeHtml);
		for (String link : parse.parseFile(homeFile)) {
			urlExtensions.add(link);
		}
		System.out.println(urlExtensions);
	}
	
	public void crawlOtherPages(String server, String port) throws IOException {
		ReaderWriter writer = new ReaderWriter();
		Parser parse = new Parser();
		for (String path : urlExtensions) {
			if (!path.equals("/")) {
				String formatPath = new StringBuilder(path).deleteCharAt(0).toString();
				String pathHtml = createGETSocket(server, Integer.parseInt(port), path);
				File pathFile = writer.writeCharsToFile("src/main/resources/smt-" + formatPath + ".html", pathHtml);
			}
		}
	}
	
	public Set<String> getUrlExtensions() {
		return this.urlExtensions;
	}

}
