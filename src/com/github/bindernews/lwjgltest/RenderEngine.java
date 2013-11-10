package com.github.bindernews.lwjgltest;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class RenderEngine {
	
	public final MGL mgl;
	
	public VCamera cam;
	public float triangleRotationAngle = 0f;
	public Vector3f trianglePos = new Vector3f(-1.5f, 0f, -6f);
	public Vector3f triangleRotation = new Vector3f(10f, 5f, 15f);
	public ArrayList<BoxObject> blocks = new ArrayList<BoxObject>();

	private MainLoop mainLoop;
	private FloatBuffer triangleVertBuf;
	private FloatBuffer triangleColorBuf;

	public RenderEngine(MainLoop loop) throws IOException {
		mgl = new MGL();
		mgl.setDisplaySize(640, 480);
		this.mainLoop = loop;
		triangleVertBuf = BufferUtils.createFloatBuffer(3 * 5);
		triangleVertBuf.put(new float[] {
				0f, 1f, 0f, // vert
				-1f, -1f, 0f, // vert
				1f, -1f, 0f, // vert
				1f, 2f, 1f, // vert
				0f, 1f, 0f, // vert
		});
		triangleVertBuf.flip();
		triangleColorBuf = BufferUtils.createFloatBuffer(3 * 5);
		triangleColorBuf.put(new float[] {
				1f, 0f, 0f, // red
				0f, 1f, 0f, // green
				1f, 0f, 1f, // ???
				0f, 0f, 1f, // blue
				0f, 1f, 1f, // ???
		});
		triangleColorBuf.flip();
		cam = new VCamera(new Vector3f(0f, 0f, 0f), // position
				new Vector3f(0.1f, 0.1f, 0.1f)); // scale (zooms out)
	}

	public void render() {
		if (Display.wasResized() || mainLoop.needsResize) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			mainLoop.needsResize = false;
		}
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		mgl.setupProjection();
		MGL.loadIdentity();
		cam.apply();
		MGL.pushMatrix();
		MGL.translate(trianglePos);
		MGL.rotate3D(triangleRotation);
		GL11.glVertexPointer(3, 0, triangleVertBuf);
		GL11.glColorPointer(3, 0, triangleColorBuf);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 5);
		MGL.popMatrix();
		for (BoxObject bb : blocks) {
			bb.render();
		}
		mgl.setup2D();
		if (mainLoop.camControl.fastMode) {
			Color.green.bind();
		}
		else {
			Color.red.bind();
		}
		MGL.renderQuad(2f, 2f, 0f, 16f, 16f, 0f);
		Color.white.bind();
		if (mainLoop.hasWon()) {
			mgl.drawText(mgl.getWidth() / 2, 10, 1, new String[] {
					"Congratulations! You WIN!!!", "(Press P)"
			});
		}
		else if (mainLoop.isPaused()) {
			mgl.drawText(mgl.getWidth() / 2, 10, 1, new String[] {
				"Paused",
			});
			mgl.drawText(mgl.getWidth() / 2, 40, 3,
					GameHelpDialog.CONTROLS_HELP);
		}
		mgl.setupProjection();
	}

	private FloatBuffer RENDER_TEXTURE_2D_BUFFER = BufferUtils
			.createFloatBuffer(20);

	public void renderTexture2D(Texture tex, float x, float y, float w, float h) {
		final FloatBuffer fb = RENDER_TEXTURE_2D_BUFFER;
		fb.clear();
		// vert 1
		fb.put(0f);
		fb.put(0f);
		fb.put(x);
		fb.put(y);
		fb.put(0f);
		// vert 2
		fb.put(tex.getWidth());
		fb.put(0f);
		fb.put(x + w);
		fb.put(y);
		fb.put(0f);
		// vert 3
		fb.put(tex.getWidth());
		fb.put(tex.getHeight());
		fb.put(x + w);
		fb.put(y + h);
		fb.put(0f);
		// vert 4
		fb.put(0f);
		fb.put(tex.getHeight());
		fb.put(x);
		fb.put(y + h);
		fb.put(0f);
		fb.flip();

		tex.bind();
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glInterleavedArrays(GL11.GL_T2F_V3F, 0, fb);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}

	public void rotateGoal() {
		triangleRotationAngle += 5f;
		if (triangleRotationAngle > 360f)
			triangleRotationAngle -= 360f;
		triangleRotation.x++;
		triangleRotation.y += 2;
		triangleRotation.z++;
	}
	
	public boolean checkWin(double distance) {
		double distX = Math.abs(cam.pos.x - trianglePos.x);
		double distZ = Math.abs(cam.pos.z - trianglePos.z);
		double distY = Math.abs(cam.pos.y - trianglePos.y);
		return (distX < distance && distY < distance && distZ < distance);
	}

	public static void setupOpenGL() {
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		GL11.glClearDepth(1.0f);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

}
