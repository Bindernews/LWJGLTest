package com.github.bindernews.lwjgltest;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class BoxObject {
	public static final IntBuffer TRIANGLE_INDEX_BUFFER = BufferUtils
			.createIntBuffer(16);
	public static final IntBuffer LINE_INDEX_BUFFER = BufferUtils
			.createIntBuffer(16);
	static {
		TRIANGLE_INDEX_BUFFER.put(new int[] {
				0, 1, 3, 2, 6, 7, 4, 5, 6, 2, 1, 5, 0, 4, 3, 7,
		});
		TRIANGLE_INDEX_BUFFER.position(0);
		LINE_INDEX_BUFFER.put(new int[] {
				0, 1, 5, 1, 2, 6, 2, 3, 7, 3, 0, 4, 5, 6, 7, 4,
		});
		LINE_INDEX_BUFFER.position(0);
	}

	private boolean useTexture = false;
	private boolean ramp = false;
	public boolean useColorVerts = false;
	public boolean doesKill = false;

	private Color color;
	private FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(24);

	// private Vector3f rotation = new Vector3f();

	public BoxObject(float x, float y, float z, float w, float h, float d) {
		setDimensions(x, y, z, w, h, d);
		color = new Color(Color.WHITE);
	}

	public void setDimensions(float x, float y, float z, float w, float h,
			float d) {
		vertexBuffer.clear();
		put3f(x, y, z);
		put3f(x + w, y, z);
		put3f(x + w, y, z + d);
		put3f(x, y, z + d);
		put3f(x, y + h, z);
		put3f(x + w, y + h, z);
		put3f(x + w, y + h, z + d);
		put3f(x, y + h, z + d);
	}
	
	public void destroy() {
	}

	public void setColor(Color col) {
		color = col;
	}

	public void render() {
		vertexBuffer.position(0);
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glLineWidth(2f);
		GL11.glColor3ub((byte) 0, (byte) 0, (byte) 0);
		GL11.glVertexPointer(3, 0, vertexBuffer);
		GL11.glDrawElements(GL11.GL_LINE_STRIP, LINE_INDEX_BUFFER);
		GL11.glColor3ub(color.getRedByte(), color.getGreenByte(),
				color.getBlueByte());
		GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, TRIANGLE_INDEX_BUFFER);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
	}

	public float x() {
		return vertexBuffer.get(0);
	}

	public float y() {
		return vertexBuffer.get(1);
	}

	public float z() {
		return vertexBuffer.get(2);
	}

	public float width() {
		return vertexBuffer.get(3) - x();
	}

	public float height() {
		return vertexBuffer.get(13) - y();
	}

	public float depth() {
		return vertexBuffer.get(8) - z();
	}

	private void put3f(float x, float y, float z) {
		vertexBuffer.put(x);
		vertexBuffer.put(y);
		vertexBuffer.put(z);
	}

	public boolean getUseTexture() {
		return useTexture;
	}

	public void setUseTexture(boolean useTexture) {
		this.useTexture = useTexture;
	}

	public boolean isRamp() {
		return ramp;
	}

	public void setRamp(boolean ramp) {
		this.ramp = ramp;
	}
}
