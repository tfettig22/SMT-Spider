package com.fettig.spiderwarmup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/****

<b>Title:</b> ApacheSocket.java<br>
<b>Project:</b> spider<br>
<b>Description:</b>
	Practice class that creates a socket and attaches to localhost. It will request the html from the default apache website and write the html to a new file
<br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Jan 30 2023
@updates:

****/

public class ApacheSocket {
	
	private static final String APACHE_FILE = "src/main/resources/apacheDefaultSite.html";
	private static final String SMT_FILE = "src/main/resources/smtFile.html";
	
	/**
	 * Instantiates the class and invokes a method to write a new html file
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ApacheSocket sock = new ApacheSocket();
		sock.getHTMLFromSite("localhost", 81, APACHE_FILE);
//		sock.getHTMLFromSite("siliconmtn.com", 80, SMT_FILE);
	}
	
	/**
	 * Creates a socket for a given host and port, gets the html at that site, and write the html to an output file
	 * @param host name of host the socket will attach to
	 * @param port the port at which the socket will attach
	 * @param file the file to which the html will be written
	 * @throws IOException
	 */
	public void getHTMLFromSite(String host, int port, String file) throws IOException {		
	// create a socket that attaches to given host and port number
		StringBuilder builder = new StringBuilder();
		try (Socket echoSocket = new Socket(host, port)) {
	// create input/output streams from the socket
			BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			DataOutputStream out = new DataOutputStream(echoSocket.getOutputStream());
	// write protocol request and headers to output stream
			out.writeBytes("GET / HTTP/1.1\r\n");
			out.writeBytes("Host: " + host + "\r\n");
			out.writeBytes("\r\n");
			
			out.flush();
	// read bytes from input stream and append to string builder
			String inData = null;
			while ((inData = in.readLine()) != null) {
				System.out.println(inData);
				builder.append(inData + "\n");
			}
	// write content from string builder to output file
			writeCharsToFile(file, builder.toString());
		}
	}
	
	/**
	 * Helper method to write content to an output file
	 * @param fileName name of the output file
	 * @param content the content to be written
	 * @throws IOException
	 */
	public void writeCharsToFile(String fileName, String content) throws IOException {
		try (FileWriter writer = new FileWriter(fileName)) {
			for (int i = 0; i < content.length(); i++) {
				writer.write(content.charAt(i));
			}
		}
	}
}
