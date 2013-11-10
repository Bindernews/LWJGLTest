package com.github.bindernews.lwjgltest;

import java.io.Reader;
import java.util.Collection;

import org.lwjgl.util.vector.Vector3f;

public interface LevelLoader
{
	void loadReader(String name, Reader reader) throws LevelLoaderException;
	
	Collection<BoxObject> getBoxes();
	
	Vector3f getGoalPos();
	Vector3f getPlayerPos();
	float getPlayerDirection();
	
	String getName();
}
