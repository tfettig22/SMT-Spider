package com.fettig.spiderwarmup;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/****

<b>Title:</b> SmtSocket.java<br>
<b>Project:</b> spider<br>
<b>Description:</b> <br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Jan 31 2023
@updates:

****/

public class SmtSocket {

	private static final String SMT_HTML = "src/main/resources/smt-html.html";
		
	public static void main(String[] args) throws IOException {
		SmtSocket sock = new SmtSocket();
		sock.getHtml("smt-stage.qa.siliconmtn.com", 443, SMT_HTML);
	}
	
	public void getHtml(String server, int port, String file) throws IOException {
		StringBuilder builder = new StringBuilder();
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try (SSLSocket socket = (SSLSocket) factory.createSocket(server, port)) {
			
			socket.startHandshake();
			
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes("GET / HTTP/1.0 \r\n");
			out.writeBytes("Host: smt-stage.qa.siliconmtn.com \r\n");
			out.writeBytes("\r\n");
			out.flush();
			
			InputStreamReader in = new InputStreamReader(socket.getInputStream());
			int input = 0;
			while ((input = in.read()) != -1) {
				builder.append((char) input);
			}
			out.close();
			in.close();
			writeCharsToFile(file, builder.toString());
			
			File page = new File(file);
			Document doc = Jsoup.parse(page);
			Set<String> urls = new HashSet<>();
			doc.select("a").forEach(tag -> urls.add(tag.attr("href")));
			
			Set<String> links = urls.stream()
					.filter(url -> url.contains("/"))
					.collect(Collectors.toSet());

			System.out.println(links);
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
