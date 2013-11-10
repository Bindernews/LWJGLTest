package com.github.bindernews.lwjgltest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.FloatBuffer;

public class Util {
	
	public static String stackTraceToString(Throwable t) {
		StringWriter swt = new StringWriter(200);
		t.printStackTrace(new PrintWriter(swt));
		return swt.getBuffer().toString();
	}
	
	public static void printFloatBuffer(FloatBuffer fb) {
		fb.rewind();
		while (fb.hasRemaining()) {
			System.out.print(fb.get() + ", ");
		}
		System.out.println();
	}
}
