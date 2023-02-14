package com.fettig.spider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/****

<b>Title:</b> SocketManager.java<br>
<b>Project:</b> spider<br>
<b>Description:</b>
	Creates a socket connection to a website, and return the html response as a string
<br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Feb 06 2023
@updates:

****/

public class ConnectionManager {
	
	private Map<String, Set<String>> cookies = new HashMap<>();
	
	/**
	 * Creates a socket connection to a server.  Can be used for either a post or get request, and stores the cookies from any response into cookies map
	 * @param server the server to connect to
	 * @param port the port at which the server is listening
	 * @param path the file path of the page to get/post
	 * @param requestType the type of request to be sent
	 * @param postBody the body of the post, if applicable
	 * @return the html response in a String
	 * @throws IOException
	 */
	public String createSocket(String server, int port, String path, String requestType, String postBody) throws IOException {
		// new StringBuilder to append eventual output String
		StringBuilder builder = new StringBuilder();
		// change requestType to uppercase to format for header
		requestType = requestType.toUpperCase();
		// create an SSLSocket via SSLSocketFactory
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try (SSLSocket socket = (SSLSocket) factory.createSocket(server, port)) {

			// open an output stream and send a request of specified type, to specified server and file path
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(requestType + " " + path + " HTTP/1.0\r\n");
			out.writeBytes("Host: " + server + "\r\n");
			// if there are cookies stored, send them with the request header
			if (!cookies.isEmpty()) {
				out.writeBytes("Cookie: " + formatCookies(server) + "\r\n");
			}
			// if the request type is POST, send content type and length
			if (requestType.equals("POST")) {
				out.writeBytes("Content-Length: " + postBody.length() + "\r\n");				
				out.writeBytes("Content-Type: application/x-www-form-urlencoded\r\n");
			}
			out.writeBytes("\r\n");
			// if the request type is POST, send body of the request
			if (requestType.equals("POST")) {
				out.writeBytes(postBody + "\r\n");				
			}
			out.flush();
			
			// open input stream and append input to string builder by line
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String input = "";
			while ((input = in.readLine()) != null) {
				builder.append(input);
				builder.append("\r\n");
				// if cookies map doesn't contain the server in the keyset, add new key and set value to empty set
				if (!cookies.keySet().contains(server)) {
					cookies.put(server, new HashSet<>());
				}
				// if a line contains cookie, add that line to the set in cookies map
				if (input.contains("Cookie")) {
					cookies.get(server).add(input + "\r\n");
				}
			}
			out.close();
			in.close();
		}
		// if the request type is a GET, return the html response as a substring with the headers removed
		return (requestType.equals("GET")) ? builder.substring(builder.indexOf("<"), builder.length()) : "";
	}
	
	/**
	 * Helper method to format the saved cookies into a string that can be sent in the request header
	 * @param server
	 * @return
	 */
	public String formatCookies(String server) {
		StringBuilder builder = new StringBuilder();
		for (String cookie : cookies.get(server)) {
			// each cookie begins with "Set-Cookie: " - always 12 characters to remove. Make substring of the cookie starting at index 12 and ending at the next space
			String str = cookie.substring(12, cookie.indexOf(" ", 12));
			builder.append(str);
		}
		// returns a single string that is a chain of all the cookies with extra characters removed
		return builder.toString();
	}
}
