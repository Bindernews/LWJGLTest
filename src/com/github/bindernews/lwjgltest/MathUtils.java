package com.github.bindernews.lwjgltest;

public class MathUtils
{
	public static float dirlen_x(double dir, float len)
	{
		return (float)(len*Math.cos(Math.toRadians(dir)));
	}
	
	public static float dirlen_y(double dir, float len)
	{
		return (float)(len*Math.sin(Math.toRadians(dir)));
	}
	
	public static double dirlen_x(double dir, double len)
	{
		return len*Math.cos(Math.toRadians(dir));
	}
	
	public static double dirlen_y(double dir, double len)
	{
		return (len*Math.sin(Math.toRadians(dir)));
	}
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt( square(y2 - y1) + square(x2 - x1) );
	}
	
	public static double square(double v) {
		return v*v;
	}
}
