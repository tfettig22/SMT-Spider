package com.fettig.spider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/****

<b>Title:</b> ReaderWriter.java<br>
<b>Project:</b> spider<br>
<b>Description:</b>
	Writes characters to a new file, taking in a String and writing each character of the String one by one
<br>
<b>Copyright:</b> Copyright (c) 2023<br>
<b>Company:</b> Silicon Mountain Technologies<br>
@author Tom Fettig
@version 1.0
@since Feb 02 2023
@updates:

****/

public class ContentWriter {
	
	/**
	 * Takes in a String and writes the content to a file of a specified name
	 * @param fileName name of the file to be written
	 * @param content the String content to be written to the file
	 * @return the File that was written
	 * @throws IOException
	 */
	public File writeCharsToFile(String fileName, String content) throws IOException {
		try (FileWriter writer = new FileWriter(fileName)) {
			for (int i = 0; i < content.length(); i++) {
				writer.write(content.charAt(i));
			}
		}
		return new File(fileName);
	}
}
