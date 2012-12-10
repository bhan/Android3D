package com.nea.nehe.lesson10;

import javax.microedition.khronos.opengles.GL10;

public class Object3D {
	private float xPos, yPos, zPos;
	private float xSize, ySize, zSize;
	
	private float prev_xPos, prev_yPos, prev_zPos;
	
	public Object3D() {
		this.xPos = 0f; this.yPos = 0f; this.zPos = 0f; 
		this.xSize = 1f; this.ySize = 1f; this.zSize = 1f;
	}
	
	public Object3D(float xPos, float yPos, float zPos, 
			float xSize, float ySize, float zSize) {
		this.xPos = xPos; this.yPos = yPos; this.zPos = zPos; 
		this.xSize = xSize; this.ySize = ySize; this.zSize = zSize;
	}
	
	public void add_xPos(float d) { prev_xPos = xPos; xPos += d; }
	public float get_xPos() { return xPos; }
	public void set_xPos(float xPos) { prev_xPos = this.xPos; this.xPos = xPos; }
	public void add_yPos(float d) { prev_yPos = yPos; yPos += d; }
	public float get_yPos() { return yPos; }
	public void set_yPos(float yPos) { prev_yPos = this.yPos; this.yPos = yPos; }
	public void add_zPos(float d) { prev_zPos = zPos; zPos += d;	}
	public float get_zPos() { return zPos; }
	public void set_zPos(float zPos) { prev_zPos = this.zPos; this.zPos = zPos; }
	
	public float get_xSize() { return xSize; }
	public void set_xSize(float xSize) { this.xSize = xSize; }
	public float get_ySize() { return ySize; }
	public void set_ySize(float ySize) { this.ySize = ySize; }
	public float get_zSize() { return xSize; }
	public void set_zSize(float zSize) { this.zSize = zSize; }
	
	public void revert_pos() {
		xPos = prev_xPos;
		yPos = prev_yPos;
		zPos = prev_zPos;
	}
	
	public void draw(GL10 gl, int filter) {
		gl.glTranslatef(xPos, yPos, zPos);
		gl.glScalef(xSize, ySize, zSize);
	}
	
	public void randomMove() {
		float xRand = Helper.nextRandFloat();
		float zRand = Helper.nextRandFloat();
		add_xPos(xRand);
		add_zPos(zRand);
	}
}