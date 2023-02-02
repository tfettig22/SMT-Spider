package com.fettig.spiderwarmup;

import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/****

<b>Title:</b> JsoupTest.java<br>
<b>Project:</b> spider<br>
<b>Description:</b> <br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Jan 31 2023
@updates:

****/

public class JsoupTest {

	private static final String SMT_FILE = "src/main/resources/smtSite.html";
	
	public static void main(String[] args) throws IOException {
		JsoupTest soup = new JsoupTest();
		soup.getSMTLinks();
	}
	
	public void getSMTLinks() throws IOException {
		Document doc = Jsoup.connect("https://www.siliconmtn.com").get();
		Elements links = doc.select("a[href]");
		writeCharsToFile(SMT_FILE, links.toString());
	}
	
	public void writeCharsToFile(String fileName, String content) throws IOException {
		try (FileWriter writer = new FileWriter(fileName)) {
			for (int i = 0; i < content.length(); i++) {
				writer.write(content.charAt(i));
			}
		}
	}
}
