package com.fettig.spider;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/****

<b>Title:</b> Parser.java<br>
<b>Project:</b> spider<br>
<b>Description:</b> <br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Feb 02 2023
@updates:

****/

public class Parser {

	public Set<String> parseFile(File file) throws IOException {
		Document doc = Jsoup.parse(file);
		Set<String> urls = new HashSet<>();
		doc.select("a").forEach(tag -> urls.add(tag.attr("href")));
		
		return urls.stream()
				.filter(url -> url.contains("/"))
				.collect(Collectors.toSet());
	}
}
