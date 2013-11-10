package com.github.bindernews.lwjgltest;

import org.lwjgl.input.Keyboard;

public class CameraController
{
	private VCamera cam;
	
	public boolean floating = false;
	public boolean fastMode = false;
	public boolean canJump = true;
	public int speedKeyRepeat = 0;
	
	public float jumpSpeed = 0.5f;
	public float gravity = 0.05f;
	public float speedY = 0f;
	
	private int jumpDelay = 0;
	private boolean jumpDidRelease = true;
	
	public void update()
	{
		cam.moveLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		cam.moveRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		cam.moveFront = Keyboard.isKeyDown(Keyboard.KEY_W);
		cam.moveBack = Keyboard.isKeyDown(Keyboard.KEY_S);
		cam.rotateLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT);
		cam.rotateRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
		cam.rotateUp = Keyboard.isKeyDown(Keyboard.KEY_UP);
		cam.rotateDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN);
		floating = Keyboard.isKeyDown(Keyboard.KEY_F);
		
		//decrement timers
		if (jumpDelay > 0) jumpDelay--;
		if (speedKeyRepeat > 0) speedKeyRepeat--;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			if (jumpDelay == 0 && jumpDidRelease && cam.hasLanded)
			{
				speedY = jumpSpeed;
				jumpDelay = 20;
				cam.hasLanded = false;
			}
		}
		else
		{
			jumpDidRelease = true;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && speedKeyRepeat <= 0)
		{
			fastMode = !fastMode;
			if (fastMode)
				cam.speed = 2.0f;
			else
				cam.speed = 1.0f; 
			speedKeyRepeat = 10;
		}
		
		if (floating)
		{
			cam.speedY = 0f;
		}
		else
		{
			cam.speedY = speedY;
		}
		
	}
	
	public void postCollide()
	{
		if (cam.collideY)
		{
			speedY = 0;
		}
		else
		{
			speedY -= gravity;
		}
		if (cam.speedY <= 0 && cam.collideY)
		{
			jumpDelay = 0;
		}
	}
	
	public void setCamera(VCamera c)
	{
		cam = c;
	}
	
	public VCamera getCamera()
	{
		return cam;
	}
}
