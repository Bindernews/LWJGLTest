package com.github.bindernews.lwjgltest;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Convenience class to load resources.
 * 
 * @author Bindernews
 */
public class Res {

	public static URL getResource(String name) {
		return ClassLoader.getSystemClassLoader().getResource(name);
	}
	
	public static InputStream getResourceAsStream(String name) {
		return ClassLoader.getSystemClassLoader().getResourceAsStream(name);
	}
	
	public static Font getFont(String name) throws FontFormatException, IOException {
		return Font.createFont(Font.TRUETYPE_FONT, getResourceAsStream(name));
	}
	
	public static Font getFont(String name, float ptsize) throws FontFormatException, IOException {
		return getFont(name).deriveFont(ptsize);
	}
	
	public static String getText(String name) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[100];
		int charsRead = buffer.length;
		BufferedReader br = new BufferedReader(new InputStreamReader(getResourceAsStream(name)));
		do {
			charsRead = br.read(buffer);
			sb.append(buffer, 0, charsRead);
		}
		while(charsRead == buffer.length);
		br.close();
		return sb.toString();
	}
}
