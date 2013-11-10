package com.github.bindernews.lwjgltest;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class VCamera {
	
	public static final float STEP_HEIGHT = 0.11f;
	public static final float ACCEL_MAX = 0.15f;
	
	public boolean moveLeft = false;
	public boolean moveRight = false;
	public boolean jump = false;
	public boolean moveFront = false;
	public boolean moveBack = false;
	public boolean rotateLeft = false;
	public boolean rotateRight = false;
	public boolean rotateUp = false;
	public boolean rotateDown = false;
	public boolean hasLanded = false;

	public boolean collideX, collideXY, collideY, collideZ, collideZY,
			collideXYZ;

	public float stepAmt = 0.0f;
	public float speedY = 0.0f;

	public Accelerator accelLeft = new Accelerator(0, ACCEL_MAX);
	public Accelerator accelRight = new Accelerator(0, ACCEL_MAX);
	public Accelerator accelFront = new Accelerator(0, ACCEL_MAX);
	public Accelerator accelBack = new Accelerator(0, ACCEL_MAX);

	public float speed = 1.0f;
	public float rotSpeed = 3.0f;

	public Vector3f rot = new Vector3f(0, 0, 0);
	public Vector3f pos = new Vector3f(0, 2f, 0);
	public Vector3f scale = new Vector3f(1f, 1f, 1f);
	public Vector3f prevpos = new Vector3f();

	private float width = 1;
	private float height = 1.75f;

	public VCamera(Vector3f pos_, Vector3f scale_) {
		pos.set(pos_);
		scale.set(scale_);
	}

	public void moveXZ(float speed, float dir) {
		double angle = Math.toRadians(dir);
		pos.x += speed * Math.sin(angle);
		pos.z += speed * Math.cos(angle);
	}

	public void translate(float x, float y, float z) {
		pos.x += x;
		pos.y += y;
		pos.z += z;
	}

	public void update(MainLoop loop) {
		prevpos.x = pos.x;
		prevpos.y = pos.y;
		prevpos.z = pos.z;

		// do movement
		float deltax = 0, deltay = 0, deltaz = 0;

		double ACCEL_SPEED = 0.015;
		double DEACCEL_SPEED = ACCEL_SPEED * -2;

		if (moveLeft) {
			accelLeft.accelerateAmount(ACCEL_SPEED);
		} else {
			accelLeft.accelerateAmount(DEACCEL_SPEED);
		}
		if (moveRight) {
			accelRight.accelerateAmount(ACCEL_SPEED);
		} else {
			accelRight.accelerateAmount(DEACCEL_SPEED);
		}
		if (moveFront) {
			accelFront.accelerateAmount(ACCEL_SPEED);
		} else {
			accelFront.accelerateAmount(DEACCEL_SPEED);
		}

		if (moveBack) {
			accelBack.accelerateAmount(ACCEL_SPEED);
		} else {
			accelBack.accelerateAmount(DEACCEL_SPEED);
		}

		double spdLR = (accelRight.getValue() - accelLeft.getValue()) * speed;
		double spdFB = (accelBack.getValue() - accelFront.getValue()) * speed;
//		double angle = Math.toDegrees(Math.atan2(spdFB, spdLR)) - rot.y;
//		double magnitude = MathUtils.distance(0, 0, spdLR, spdFB);
//		deltax += MathUtils.dirlen_x(angle, magnitude);
//		deltaz += MathUtils.dirlen_y(angle, magnitude);
		deltax += MathUtils.dirlen_y(rot.y + 90, spdLR);
		deltaz += MathUtils.dirlen_x(rot.y + 90, spdLR);
		deltax += MathUtils.dirlen_y(rot.y, spdFB);
		deltaz += MathUtils.dirlen_x(rot.y, spdFB);

		float cRotSpeed = rotSpeed * speed;
		if (rotateLeft) {
			rot.y += cRotSpeed;
		}
		if (rotateRight) {
			rot.y -= cRotSpeed;
		}
		if (rotateUp) {
			if (rot.x > -85)
				rot.x -= cRotSpeed;
		}
		if (rotateDown) {
			if (rot.x < 85)
				rot.x += cRotSpeed;
		}

		deltay = speedY;

		collideX = false;
		collideY = false;
		collideZ = false;
		collideXY = false;
		collideZY = false;
		collideXYZ = false;
		stepAmt = 0f;

		float topY = 0f;
		for (BoxObject obj : loop.renderer.blocks) {
			float stepHeight = (obj.y() + obj.height()) - (pos.y - height);
			if (wouldCollideNew(obj, pos.x + deltax, pos.y, pos.z)) {
				if (checkStep(obj, stepHeight)) {
					stepAmt = stepHeight;
				} else {
					collideX = true;
				}
			}
			if (wouldCollideNew(obj, pos.x, pos.y, pos.z + deltaz)) {
				if (checkStep(obj, stepHeight)) {
					stepAmt = stepHeight;
				} else {
					collideZ = true;
				}
			}
			if (wouldCollideNew(obj, pos.x, pos.y + deltay, pos.z)) {
				if (!collideY || obj.y() + obj.height() > topY) {
					topY = obj.y() + obj.height();
				}
				collideY = true;
			}
			if (wouldCollideNew(obj, pos.x + deltax, pos.y + deltay, pos.z)) {
				collideXY = true;
			}

			if (wouldCollideNew(obj, pos.x, pos.y + deltay, pos.z + deltaz)) {
				collideZY = true;
			}

			if (wouldCollideNew(obj, pos.x + deltax, pos.y + deltay, pos.z + deltaz)) {
				collideXYZ = true;
			}

		}

		if (!collideX) {
			pos.x += deltax;
		}
		if (!collideZ)
			pos.z += deltaz;
		if (!collideY) {
			pos.y += deltay;
		}
		else {
			if (speedY < 0) {
				pos.y = topY + height + 0.01f;
				hasLanded = true;
			}
		}
		pos.y += stepAmt;

		/*
		 * if (collideX || collideZ) { accelFront.setValue(0);
		 * accelBack.setValue(0); accelLeft.setValue(0); accelRight.setValue(0);
		 * }
		 */
	}

	public void restorePrevPos() {
		pos.x = prevpos.x;
		pos.y = prevpos.y;
		pos.z = prevpos.z;
	}
	
	public boolean checkStep(BoxObject obj, float dif) {
		return (dif <= STEP_HEIGHT || (obj.isRamp() && dif <= STEP_HEIGHT * 2));
	}

	public boolean wouldCollide(BoxObject obj, float x, float y, float z) {
		float xlow = x - (width / 2);
		float xhigh = x + (width / 2);
		float zlow = z - (width / 2);
		float zhigh = z + (width / 2);
		float ylow = y - height;
		float yhigh = y;

		if (((xlow > obj.x() && xlow < obj.x() + obj.width()) || (xhigh > obj.x() && xhigh < obj.x() + obj.width()))
				&& ((zlow > obj.z() && zlow < obj.z() + obj.depth()) || (zhigh > obj.z() && zhigh < obj.z() + obj.depth()))
				&& ((ylow > obj.y() && ylow < obj.y() + obj.height()) || (yhigh > obj.y() && yhigh < obj.y() + obj.height())))
			return true;
		return false;
	}

	public boolean wouldCollideNew(BoxObject obj, float x, float y, float z) {
		float xlow = x - (width / 2);
		float xhigh = x + (width / 2);
		float zlow = z - (width / 2);
		float zhigh = z + (width / 2);
		float ylow = y - height;
		float yhigh = y + 0.1f;

		if (xlow >= obj.x() + obj.width() || xhigh <= obj.x()
				|| zlow >= obj.z() + obj.depth() || zhigh <= obj.z()
				|| ylow >= obj.y() + obj.height() || yhigh < obj.y())
			return false;
		return true;
	}

	public void apply() {
		GL11.glScalef(scale.x, scale.y, scale.z);
		GL11.glRotatef(rot.x, 1f, 0f, 0f);
		GL11.glRotatef(rot.y, 0f, -1f, 0f);
		GL11.glRotatef(rot.z, 0f, 0f, 1f);
		GL11.glTranslatef(-pos.x, -pos.y, -pos.z);
	}
}
