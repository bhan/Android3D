package com.nea.nehe.lesson10;

public class Person extends Object3D {

	// body orientation angle (degrees)
	private float bodyAngle;
	// head up and down angle 
	private float upDownDegrees;
	// head side to side angle
	private float sideDegrees;

	private float walkBounce;
	private float walkBounceAngle;
	
	public Person() {
		super();
	}
	
	public Person(float xPos, float yPos, float zPos,
			float xSize, float ySize, float zSize) {
		super(xPos, yPos, zPos, xSize, ySize, zSize);		
	}
	
	public float get_bodyAngle() { return bodyAngle; }
	public void set_bodyAngle(float n) { bodyAngle = n; }
	public void add_bodyAngle(float d) { bodyAngle += d; }
	
	public float get_sideDegrees() { return sideDegrees; }
	public void set_sideDegrees(float n) { sideDegrees = n;	}
	public void add_sideDegrees(float d) { sideDegrees += d; }
	
	public float get_upDownDegrees() { return upDownDegrees; }
	public void set_upDownDegrees(float n) { upDownDegrees = n;	}
	public void add_upDownDegrees(float d) { upDownDegrees += d; }
	
	public float get_walkBounce() { return walkBounce; }
	public void set_walkBounce(float n) { walkBounce = n; }
	public void add_walkBounce(float d) { walkBounce += d; }
	
	public float get_walkBounceAngle() { return walkBounceAngle; }
	public void set_walkBounceAngle(float n) { walkBounceAngle = n; }
	public void add_walkBounceAngle(float d) { walkBounceAngle += d; }
}