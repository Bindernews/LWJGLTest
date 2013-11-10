package com.github.bindernews.lwjgltest;

public class LevelLoaderException extends Exception
{
	private static final long serialVersionUID = -4773010602534457575L;
	
	
	public LevelLoaderException(Throwable src)
	{
		super(src);
	}
	
	public LevelLoaderException(String fname, String context, Throwable src)
	{
		super(makeErrorMsg(fname, context, ""), src);
	}
	
	public LevelLoaderException(String fname, String context, String msg)
	{
		super(makeErrorMsg(fname, context, msg));
	}
	
	public static String makeErrorMsg(String fn, String context, String msg)
	{
		return "Error in " + fn + " " + context + ": " + msg;
	}
	
}
