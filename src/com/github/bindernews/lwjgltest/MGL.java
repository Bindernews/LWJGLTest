package com.github.bindernews.lwjgltest;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

public class MGL {
	
	private int displayWidth;
	private int displayHeight;
	private Font drawFont;
	
	public MGL() {
		drawFont = new TrueTypeFont(java.awt.Font.decode("Arial-PLAIN-20"), true);
	}
	
	public void resizeGLView() {
		GL11.glViewport(0, 0, displayWidth, displayHeight);
		GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public void setupProjection() {
		GL11.glMatrixMode(GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(65.0f, displayWidth / displayHeight, 0.01f, 100f);
		GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void setup2D() {
		GL11.glMatrixMode(GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, displayWidth, displayHeight, 0, -1, 1);
		GL11.glMatrixMode(GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public void setDisplaySize(int w, int h) {
		displayWidth = w;
		displayHeight = h;
	}
	
	public int getWidth() {
		return displayWidth;
	}
	
	public int getHeight() {
		return displayHeight;
	}
	
	public void drawText(float x, float y, int align, String[] lines) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		float dx, dy;
		switch (align) {
		case 3: {
			float maxWidth = 0f;
			for (String s : lines) {
				int w = drawFont.getWidth(s);
				if (w > maxWidth)
					maxWidth = w;
			}
			dx = x - (maxWidth / 2f);
			break;
		}
		default:
			dx = x;
			break;
		}
		for (int i = 0; i < lines.length; i++) {
			dy = y + (drawFont.getLineHeight() * i);
			switch (align) {
			case 0:
				dx = x;
				break;
			case 1:
				dx = x - (drawFont.getWidth(lines[i]) / 2f);
				break;
			case 2:
				dx = x - drawFont.getWidth(lines[i]);
				break;
			case 3:
				break;
			default:
				dx = x;
			}
			drawFont.drawString(dx, dy, lines[i]);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	void setFont(Font f) {
		drawFont = f;
	}
	
	Font getFont() {
		return drawFont;
	}
	
	public static void pushMatrix() {
		GL11.glPushMatrix();
	}
	
	public static void popMatrix() {
		GL11.glPopMatrix();
	}
	
	public static void loadIdentity() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public static void renderQuad(float x, float y, float z, float w, float h, float d) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(x + w, y, z);
		GL11.glVertex3f(x + w, y + h, z + d);
		GL11.glVertex3f(x, y + h, z + d);
		GL11.glEnd();
	}
	
	public static void translate(Vector3f v) {
		GL11.glTranslatef(v.x, v.y, v.z);
	}
	
	public static void rotate3D(Vector3f v) {
		rotate3D(v.x, v.y, v.z);
	}
	
	public static void rotate3D(float x, float y, float z) {
		GL11.glRotatef(x, 1f, 0f, 0f);
		GL11.glRotatef(y, 0f, 1f, 0f);
		GL11.glRotatef(z, 0f, 0f, 1f);
	}
}
