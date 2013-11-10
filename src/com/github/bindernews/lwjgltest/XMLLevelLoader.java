package com.github.bindernews.lwjgltest;

import java.io.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.vector.Vector3f;

public class XMLLevelLoader extends DefaultHandler implements LevelLoader {

	public static final Hashtable<String, ReadableColor> COLOR_MAP = new Hashtable<String, ReadableColor>();

	private float playerDir;
	private ArrayList<BoxObject> boxes;
	private Vector3f playerPos;
	private Vector3f goalPos;
	private String filename;
	private Attributes cattributes;
	private Color ccolor;
	private XMLReader xmlreader;
	private boolean offsetHeight;

	public XMLLevelLoader() throws LevelLoaderException {
		initColorMap();
		boxes = new ArrayList<BoxObject>();
		playerPos = new Vector3f();
		goalPos = new Vector3f();
		ccolor = new Color(Color.WHITE);
		filename = null;
		cattributes = null;

		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser saxparser = spf.newSAXParser();
			xmlreader = saxparser.getXMLReader();
			xmlreader.setContentHandler(this);
		}
		catch (SAXException e) {
			throw new LevelLoaderException(e);
		}
		catch (ParserConfigurationException e) {
			throw new LevelLoaderException(e);
		}

	}

	//
	// SAX Parser Methods
	//

	@Override
	public void startDocument() throws SAXException {
		boxes.clear();
		ccolor.setColor(Color.WHITE);
		playerDir = 0f;
		playerPos.set(0f, 2f, 0f);
		goalPos.set(2f, 5f, 0f);
		offsetHeight = true;
		cattributes = null;
	}

	@Override
	public void endDocument() throws SAXException {
		ccolor = new Color(Color.LTGREY);
		addBox(new BoxObject(-50f, -10f, -50f, 100f, 9.90f, 100f));
		cattributes = null;
	}

	@Override
	public void startElement(String uri, String localname, String qName,
			Attributes attributes) throws SAXException {
		cattributes = attributes;
		if ("box".equals(localname) || "kill".equals(localname)) {
			BoxObject box = new BoxObject(parseFloatAttr("x"),
					parseFloatAttr("y"), parseFloatAttr("z"),
					parseFloatAttr("w"),
					(parseFloatAttr("h") - (offsetHeight ? 0.01f : 0f)),
					parseFloatAttr("d"));
			addBox(box);
			if (localname.equals("kill"))
				box.doesKill = true;
		}
		else if ("slope".equals(localname)) {
			float newx = parseFloatAttr("x");
			float newy = parseFloatAttr("y");
			float newz = parseFloatAttr("z");
			float width = parseFloatAttr("width");
			int dir = parseIntAttr("dir") % 4;
			int steps = parseIntAttr("steps");
			float nwidth = 0.09f;
			float ndepth = 0.09f;
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
				BoxObject box = new BoxObject(newx + nx, newy + (i * 0.1f),
						newz + nz, nwidth, 0.1f, ndepth);
				box.setRamp(true);
				addBox(box);
			}
		}
		else if ("color".equals(localname)) {
			String sName = cattributes.getValue("name");
			String sRGBA = cattributes.getValue("rgba");
			if (sName != null) {
				ReadableColor rc = COLOR_MAP.get(sName.toLowerCase());
				if (rc != null) {
					ccolor = new Color(rc);
				}
				else {
					throw new SAXException("File " + filename + ": bad color");
				}
			}
			else if (sRGBA != null) {
				String[] csplit = sRGBA.split("\\s+");
				if (csplit.length > 1) {
					ccolor = new Color(parseInt(csplit[0].trim()),
							parseInt(csplit[1].trim()),
							parseInt(csplit[2].trim()),
							parseInt(csplit[3].trim()));
				}
				else {
					int ival = parseInt(sRGBA.trim());
					ccolor = new Color((byte) (ival >> 24),
							(byte) (ival >> 16), (byte) (ival >> 8),
							(byte) (ival >> 0));
				}
			}
		}
		else if ("player".equals(localname)) {
			playerPos.set(parseFloatAttr("x"), parseFloatAttr("y"),
					parseFloatAttr("z"));
			playerDir = parseFloatAttr("dir");
		}
		else if ("goal".equals(localname)) {
			goalPos.set(parseFloatAttr("x"), parseFloatAttr("y"),
					parseFloatAttr("z"));
		}
		else if ("level".equals(localname)) {
			// ignore
		}
		else if ("option".equals(localname)) {
			if ("offsetHeight".equals(getAttr("name"))) {
				offsetHeight = Boolean.parseBoolean(getAttr("value"));
			}
		}
		else {
			throw new SAXException("File " + filename + ": unknown object type");
		}
	}

	//
	// Convenience methods
	//

	private void addBox(BoxObject obj) {
		obj.setColor(ccolor);
		obj.setUseTexture(true);
		boxes.add(obj);
	}

	private static int parseInt(String s) {
		if (s.startsWith("0x")) {
			return Integer.parseInt(s.substring(2), 16);
		}
		else {
			return Integer.parseInt(s);
		}
	}

	private float parseFloatAttr(String name) {
		return Float.parseFloat(getAttr(name));
	}

	private int parseIntAttr(String name) {
		return parseInt(getAttr(name));
	}
	
	private String getAttr(String name) {
		return cattributes.getValue(name);
	}

	private static void initColorMap() {
		if (COLOR_MAP.isEmpty()) {
			COLOR_MAP.put("black", Color.BLACK);
			COLOR_MAP.put("blue", Color.BLUE);
			COLOR_MAP.put("cyan", Color.CYAN);
			COLOR_MAP.put("dkgrey", Color.DKGREY);
			COLOR_MAP.put("green", Color.GREEN);
			COLOR_MAP.put("grey", Color.GREY);
			COLOR_MAP.put("ltgrey", Color.LTGREY);
			COLOR_MAP.put("orange", Color.ORANGE);
			COLOR_MAP.put("purple", Color.PURPLE);
			COLOR_MAP.put("red", Color.RED);
			COLOR_MAP.put("white", Color.WHITE);
			COLOR_MAP.put("yellow", Color.YELLOW);
		}
	}

	// /////////////////////////////////
	// Methods from LevelLoader //
	// /////////////////////////////////

	@Override
	public void loadReader(String name, Reader reader)
			throws LevelLoaderException {
		filename = name;
		try {
			xmlreader.parse(new InputSource(reader));
		}
		catch (SAXException e) {
			throw new LevelLoaderException(e);
		}
		catch (IOException e) {
			throw new LevelLoaderException(e);
		}
		catch (NumberFormatException e) {
			throw new LevelLoaderException(e);
		}
	}

	@Override
	public Collection<BoxObject> getBoxes() {
		return boxes;
	}

	@Override
	public Vector3f getGoalPos() {
		return goalPos;
	}

	@Override
	public Vector3f getPlayerPos() {
		return playerPos;
	}

	@Override
	public String getName() {
		return filename;
	}

	@Override
	public float getPlayerDirection() {
		return playerDir;
	}

}
