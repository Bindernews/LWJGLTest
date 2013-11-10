package com.github.bindernews.lwjgltest;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
	
	public static void printProp(String prop) {
		System.out.println(prop + "=" + System.getProperty(prop));
	}

	public static void main(String[] args) throws IOException {
		printProp("java.class.path");
		printProp("java.library.path");
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		Thread.UncaughtExceptionHandler teh = new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread arg0, Throwable arg1) {
				System.err.println("Uncaught exception in thread \"" + arg0.getName());
				arg1.printStackTrace();
				System.exit(0);
			}
		};
		Thread.setDefaultUncaughtExceptionHandler(teh);

		MainLoop loop = new MainLoop();
		loop.start();
		while (!loop.exec.isTerminated()) {
			try {
				loop.exec.awaitTermination(999, TimeUnit.DAYS);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
