package com.nea.nehe.lesson10;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * This is a port of the {@link http://nehe.gamedev.net} OpenGL 
 * tutorials to the Android 1.5 OpenGL ES platform. Thanks to 
 * NeHe and all contributors for their great tutorials and great 
 * documentation. This source should be used together with the
 * textual explanations made at {@link http://nehe.gamedev.net}.
 * The code is based on the original Visual C++ code with all
 * comments made. It has been altered and extended to meet the
 * Android requirements. The Java code has according comments.
 * 
 * If you use this code or find it helpful, please visit and send
 * a shout to the author under {@link http://www.insanitydesign.com/}
 * 
 * @DISCLAIMER
 * This source and the whole package comes without warranty. It may or may
 * not harm your computer or cell phone. Please use with care. Any damage
 * cannot be related back to the author. The source has been tested on a
 * virtual environment and scanned for viruses and has passed all tests.
 * 
 * 
 * This is an interpretation of "Lesson 10: Loading And Moving Through A 3D World"
 * for the Google Android platform.
 * 
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class Lesson10 extends GLSurfaceView implements Renderer, OnTouchListener {
		
	/** Our World */
	private World world;
	private Cube[] cubes;
	private int numCubes = 3;
	
	private LinkedList<Object3D> nonViewers;
	
	private long lastTime = System.currentTimeMillis();
	
	private int filter = 0;				//Which texture filter?
	
	/** Is blending enabled */
	private boolean blend = false;
	
	/** The Activity Context */
	private Context context;
	
	private float prevTouchX, prevTouchY;

	
	/**
	 * Set this class as renderer for this GLSurfaceView.
	 * Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen
	 * 
	 * @param context - The Activity Context
	 */
	public Lesson10(Context context) {
		super(context);
		init(context);
	}
	
	public Lesson10(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		//Set this as Renderer
		this.setRenderer(this);
		//Request focus
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		
		this.context = context;
		
		//Instance our World
		world = new World(this.context, new Person(0f, 0f, 0f, .01f, .01f, .01f));
		cubes = new Cube[numCubes];
		for(int i = 0; i < numCubes; ++i) {
			cubes[i] = new Cube(0f, 0.1f, -(float)i-1, 0.1f, 0.1f, 0.1f);
		}
		nonViewers = new LinkedList<Object3D>(Arrays.asList(cubes));
		
		//cube = new Cube();
		//Set the world as listener to this view
		//this.setOnKeyListener(world);
		//this.setOnTouchListener(this);
		this.setOnTouchListener(this);
	}
	
	public World get_world() { return world; }
	
	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//Settings
		gl.glDisable(GL10.GL_DITHER);						//Disable dithering
		gl.glEnable(GL10.GL_TEXTURE_2D);					//Enable Texture Mapping
		gl.glShadeModel(GL10.GL_SMOOTH); 					//Enable Smooth Shading
		// black background
		gl.glClearColor(0.0f, 0.0f, 1.0f, 0.5f);
		gl.glClearDepthf(1.0f); 							//Depth Buffer Setup
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);		//Set The Blending Function For Translucency
		gl.glDepthFunc(GL10.GL_LEQUAL); 					//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
				
		//Load our world from the textual description
		world.loadWorld("test.txt");
		//Load the texture for our world once during Surface creation
		world.loadGLTexture(gl, this.context);
		//gl.glPushMatrix();
		for(int i = 0; i < numCubes; ++i)
			cubes[i].loadGLTexture(gl, this.context);
	}
	
	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		//Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		
		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
		//Check if the blend flag has been set to enable/disable blending
		if(blend) {
			gl.glEnable(GL10.GL_BLEND);			//Turn Blending On
			gl.glDisable(GL10.GL_DEPTH_TEST);	//Turn Depth Testing Off
		} else {
			gl.glDisable(GL10.GL_BLEND);		//Turn Blending On
			gl.glEnable(GL10.GL_DEPTH_TEST);	//Turn Depth Testing Off
		}
		
		world.draw(gl, filter);
		
		long curTime = System.currentTimeMillis();
		if(curTime - lastTime > 10) {
			ListIterator<Object3D> itr = nonViewers.listIterator();
			while(itr.hasNext()) {
				Object3D cur = itr.next();
				gl.glPushMatrix();
				cur.randomMove();
				cur.draw(gl, filter);
				gl.glPopMatrix();
			}
			lastTime = curTime;
		}
	}
		

	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	
	/**
	 * Override the touch screen listener.
	 * 
	 * React to moves and presses on the touchscreen.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//
		/*float x = event.getX();
        float y = event.getY();
        
        //A press on the screen
        if(event.getAction() == MotionEvent.ACTION_UP) {
        	//Define an upper area of 10% to define a lower area
        	int upperArea = this.getHeight() / 10;
        	int lowerArea = this.getHeight() - upperArea;
        	
        	//
        	if(y > lowerArea) {
        		//Change the blend setting if the lower area left has been pressed
        		if(x < (this.getWidth() / 2)) {
        			if(blend) {
        				blend = false;
            		} else {
            			blend = true;
            		} 
        		}
        	}
        }
        
        */ 
		return true;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean handled = false;
		
		float x = event.getX();
        float y = event.getY();
        
        //If a touch is moved on the screen
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
        	Person viewer = world.get_viewer();
        	//Calculate the change
        	float dx = x - prevTouchX;
	        float dy = y - prevTouchY;
        	
    	    viewer.add_bodyAngle(dx * Globals.touch_scale);
    	    double bodyAngleRads = Math.toRadians(viewer.get_bodyAngle());
    	    
    		//Move On The X-Plane Based On Player Direction
			viewer.add_xPos(dy * (float)Math.sin(bodyAngleRads) * 0.005f);
			//Move On The Z-Plane Based On Player Direction
			viewer.add_zPos(-dy * (float)Math.cos(bodyAngleRads) * 0.005f);
			
			Helper.collisionResponse(viewer, nonViewers);
			if(viewer.get_xPos() > Globals.max_xpos) viewer.set_xPos(Globals.max_xpos);
			if(viewer.get_xPos() < Globals.min_xpos) viewer.set_xPos(Globals.min_xpos);
			if(viewer.get_zPos() > Globals.max_zpos) viewer.set_zPos(Globals.max_zpos);
			if(viewer.get_zPos() < Globals.min_zpos) viewer.set_zPos(Globals.min_zpos);
			
			if(viewer.get_walkBounceAngle() > 360f) {							//Is walkbiasangle>=359?
				viewer.set_walkBounceAngle(0f);								//Make walkbiasangle Equal 0
			} else {
				viewer.add_walkBounceAngle(10f);								//If walkbiasangle < 359 Increase It By 10
			}
			viewer.set_walkBounce(
					(float)Math.sin(Math.toRadians(viewer.get_walkBounceAngle())) / 20.0f);	//Causes The Player To Bounce
			
			/*Run.textView_pos.setText("body_angle:"+Helper.roundFloat(viewer.get_bodyAngle())+
					" xpos:"+Helper.roundFloat(viewer.get_xPos())+
					" zpos:"+Helper.roundFloat(viewer.get_zPos())+
					"\ndx:"+Helper.roundFloat(dx)+
					" dy:"+Helper.roundFloat(dy)+
					" walkbias:"+Helper.roundFloat(viewer.get_walkBounce())
					);
    	    */
			Run.textView_pos.setText("collide?" + Helper.collisionDetect(viewer, nonViewers));
			//We handled the event
            handled = true;
        }
        
        //Remember the values
        prevTouchX = x;
        prevTouchY = y;
        
		return handled;
	}
}