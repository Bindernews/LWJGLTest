package com.github.bindernews.lwjgltest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;

public class BasicLevelLoader implements LevelLoader {
	public ArrayList<BoxObject> boxes;
	public Vector3f playerNewPos;
	public Vector3f goalNewPos;
	public String fileName = null;

	public BasicLevelLoader() {
		boxes = new ArrayList<BoxObject>();
		playerNewPos = new Vector3f(0f, 4f, 0f);
		goalNewPos = new Vector3f(0f, 15f, 0f);
	}

	@Override
	public void loadReader(String name, Reader reader)
			throws LevelLoaderException {
		fileName = name;
		int lineNum = 0;
		try {
			BufferedReader bread = new BufferedReader(reader);
			Color color = new Color(Color.WHITE);
			while (true) {
				String lineIn = bread.readLine();
				lineNum++;

				if (lineIn == null)
					break;

				int commentPos = lineIn.indexOf("#");
				if (commentPos != -1)
					lineIn = lineIn.substring(0, commentPos);
				lineIn = lineIn.trim();

				if (lineIn.equals(""))
					continue;

				String[] sln = lineIn.split(",");

				if (sln[0].equals("box") || sln[0].equals("kill")) {
					float nx = Float.parseFloat(sln[1]);
					float ny = Float.parseFloat(sln[2]);
					float nz = Float.parseFloat(sln[3]);
					float nw = Float.parseFloat(sln[4]);
					float nh = Float.parseFloat(sln[5]);
					float nd = Float.parseFloat(sln[6]);

					BoxObject box = new BoxObject(nx, ny, nz, nw, nh, nd);
					box.setColor(color);
					box.setUseTexture(true);
					if (sln[0].equals("kill"))
						box.doesKill = true;
					boxes.add(box);
				} else if (sln[0].equals("slope")) {
					float newx = Float.parseFloat(sln[1]);
					float newy = Float.parseFloat(sln[2]);
					float newz = Float.parseFloat(sln[3]);
					float width = Float.parseFloat(sln[4]);
					int dir = Integer.parseInt(sln[5]) % 4;
					int steps = Integer.parseInt(sln[6]);

					float nwidth = 0.1f;
					float ndepth = 0.1f;
					if (dir % 2 == 0)
						ndepth = width;
					else
						nwidth = width;

					for (int i = 0; i < steps; i++) {

						float nx = 0;
						float nz = 0;

						switch (dir) {
						case 0:
							nx = i * 0.1f;
							break;
						case 1:
							nz = i * 0.1f;
							break;
						case 2:
							nx = i * -0.1f;
							break;
						case 3:
							nz = i * -0.1f;
							break;
						}
						BoxObject box = new BoxObject(newx + nx, newy
								+ (i * 0.1f), newz + nz, nwidth, 0.1f, ndepth);
						box.setColor(color);
						box.setUseTexture(true);
						boxes.add(box);
					}
				} else if (sln[0].equals("color")) {
					int red = Integer.parseInt(sln[1]);
					int green = Integer.parseInt(sln[2]);
					int blue = Integer.parseInt(sln[3]);
					color = new Color(red, green, blue);
				} else if (sln[0].equals("player")) {
					float nx = Float.parseFloat(sln[1]);
					float ny = Float.parseFloat(sln[2]);
					float nz = Float.parseFloat(sln[3]);
					playerNewPos.set(nx, ny, nz);
				} else if (sln[0].equals("goal")) {
					float nx = Float.parseFloat(sln[1]);
					float ny = Float.parseFloat(sln[2]);
					float nz = Float.parseFloat(sln[3]);
					goalNewPos.set(nx, ny, nz);
				} else {
					throw new LevelLoaderException(fileName, "line " + lineNum,
							"Unknown object type");
				}
			}
		} catch (NumberFormatException nfe) {
			throw new LevelLoaderException(fileName, "line " + lineNum,
					"Invalid number");
		} catch (IndexOutOfBoundsException iob) {
			throw new LevelLoaderException(fileName, "line " + lineNum,
					"Object data error");
		} catch (IOException ioe) {
			throw new LevelLoaderException(fileName, "line " + lineNum, ioe);
		}

		BoxObject box = new BoxObject(-50f, -10f, -50f, 100f, 9.90f, 100f);
		box.setUseTexture(true);
		boxes.add(box);
	}

	@Override
	public Collection<BoxObject> getBoxes() {
		return boxes;
	}

	@Override
	public Vector3f getGoalPos() {
		return goalNewPos;
	}

	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public Vector3f getPlayerPos() {
		return playerNewPos;
	}

	@Override
	public float getPlayerDirection() {
		// TODO Auto-generated method stub
		return 0;
	}
}
