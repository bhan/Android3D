package com.nea.nehe.lesson10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;


/**
 * This class is our World representation and loads
 * the world from the textual representation, and 
 * draws the World according to the read.
 * 
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class World {
	
	/** Our texture pointer */
	private int[] textures = new int[3];
	
	/** The Activity Context */
	private Context context;
	
	/** The World sector */
	private Sector sector1;


	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	/** The buffer holding the texture coordinates */
	private FloatBuffer textureBuffer;
	
	/** The initial vertex definition */
	private float[] vertices;
	
	/** The texture coordinates (u, v) */	
	private float[] texture;
	
	private Person viewer;
	
	/**
	 * The World constructor
	 * 
	 * @param context - The Activity Context
	 */
	public World(Context context, Person viewer) {
		this.context = context;
		this.viewer = viewer;
	}
	
	public Person get_viewer() { return viewer; }
	public void set_viewer(Person person) { viewer = person; }
	
	/**
	 * This method is a representation of the original SetupWorld(). 
	 * Here, we load the world into the structure from the
	 * original world.txt file, but in a Java way.
	 * 
	 * Please note that this is not necessarily the way to got,
	 * and definitely not the nicest way to do the reading and
	 * loading from a file. But it is near to the original
	 * and a quick solution for this example. It does not check
	 * for anything NOT part of the original file and can easily 
	 * be corrupted by a changed file.
	 * 
	 * @param fileName - The file name to load from the Asset directory
	 */
	public void loadWorld(String fileName) {
		try {
			//Some temporary variables
			int numtriangles = 0;
			int counter = 0;
			sector1 = new Sector();
			
			List<String> lines = null;
			StringTokenizer tokenizer;
			
			//Quick Reader for the input file
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.context.getAssets().open(fileName)));
			
			//Iterate over all lines
			String line = null;
			while((line = reader.readLine()) != null) {
				//Skip comments and empty lines
				if(line.startsWith("//") || line.trim().equals("")) {
					continue;
				}
				
				//Read how many polygons this file contains
				if(line.startsWith("NUMPOLLIES")) {
					numtriangles = Integer.valueOf(line.split(" ")[1]);					
					sector1.numtriangles = numtriangles;
					sector1.triangle = new Triangle[sector1.numtriangles];
				
				//Read every other line
				} else {
					if(lines == null) {
						lines = new ArrayList<String>();
					}
					
					lines.add(line);
				}
			}
			
			//Clean up!
			reader.close();
			
			//Now iterate over all read lines...
			for(int loop = 0; loop < numtriangles; loop++) {
				//...define triangles...
				Triangle triangle = new Triangle();
				
				//...and fill these triangles with the five read data 
				for(int vert = 0; vert < 3; vert++) {
					//
					line = lines.get(loop * 3 + vert);
					tokenizer = new StringTokenizer(line);
					
					//
					triangle.vertex[vert] = new Vertex();
					//
					triangle.vertex[vert].x = Float.valueOf(tokenizer.nextToken());
					triangle.vertex[vert].y = Float.valueOf(tokenizer.nextToken());
					triangle.vertex[vert].z = Float.valueOf(tokenizer.nextToken());
					triangle.vertex[vert].u = Float.valueOf(tokenizer.nextToken());
					triangle.vertex[vert].v = Float.valueOf(tokenizer.nextToken());
				}
				
				//Finally, add the triangle to the sector
				sector1.triangle[counter++] = triangle;
			}
			
		//If anything should happen, write a log and return
		} catch(Exception e) {
			Log.e("World", "Could not load the World file!", e);
			return;
		}
		
		/*
		 * Now, convert the original structure of the NeHe 
		 * lesson to our classic buffer structure. 
		 * Could/Should be done in one step above. Kept
		 * separated to stick near and clear to the original.
		 * 
		 * This is a quick and not recommended solution.
		 * Just made to quickly present the tutorial.
		 */
		vertices = new float[sector1.numtriangles * 3 * 3];
		texture = new float[sector1.numtriangles * 3 * 2];
		
		int vertCounter = 0;
		int texCounter = 0;
				
		//
		for(Triangle triangle : sector1.triangle) {
			//
			for(Vertex vertex : triangle.vertex) {
				//
				vertices[vertCounter++] = vertex.x;
				vertices[vertCounter++] = vertex.y;
				vertices[vertCounter++] = vertex.z;
				//
				texture[texCounter++] = vertex.u;
				texture[texCounter++] = vertex.v;
			}
		}		
		
		//Build the buffers
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		//
		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}
		
	/**
	 * The world drawing function.
	 * 
	 * @param gl - The GL Context
	 * @param filter - Which texture filter to use
	 */
	public void draw(GL10 gl, int filter) {
		//Bind the texture based on the given filter
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[filter]);

		float xtrans = -viewer.get_xPos();						//Used For Player Translation On The X Axis
		float ztrans = -viewer.get_zPos();						//Used For Player Translation On The Z Axis
		float ytrans = -viewer.get_walkBounce() - 0.25f;			//Used For Bouncing Motion Up And Down
		//360 Degree Angle For Player Direction
		float sceneroty = 360.0f - viewer.get_sideDegrees();
		
		
		//View
		gl.glRotatef(viewer.get_upDownDegrees(), 1.0f, 0, 0);		//Rotate Up And Down To Look Up And Down
		gl.glRotatef(viewer.get_bodyAngle() + sceneroty, 0, 1.0f, 0);		//Rotate Depending On Direction Player Is Facing
		gl.glTranslatef(xtrans, ytrans, ztrans);	//Translate The Scene Based On Player Position
					
		//Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				
		//Point to our buffers
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		
		//Draw the vertices as triangles
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertices.length / 3);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
		
	/**
	 * Load the textures
	 * 
	 * @param gl - The GL Context
	 * @param context - The Activity context
	 */
	public void loadGLTexture(GL10 gl, Context context) {
		//Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(R.drawable.grass);
		Bitmap bitmap = null;
		try {
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);

		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}

		//Generate there texture pointer
		gl.glGenTextures(3, textures, 0);

		//Create Nearest Filtered Texture and bind it to texture 0
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create Linear Filtered Texture and bind it to texture 1
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create mipmapped textures and bind it to texture 2
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
		/*
		 * This is a change to the original tutorial, as buildMipMap does not exist anymore
		 * in the Android SDK.
		 * 
		 * We check if the GL context is version 1.1 and generate MipMaps by flag.
		 * Otherwise we call our own buildMipMap implementation
		 */
		if(gl instanceof GL11) {
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);			
		} 
		
		//Clean up
		bitmap.recycle();
	}

	
/* ***** Structure classes for the "World" ***** */	
	/**
	 * A classic Vertex definition with
	 * texture coordinates.
	 */
	public class Vertex {
		//
		public float x, y, z;
		//
		public float u, v;
	}
	
	/**
	 * The Triangle class, containing
	 * all Vertices for the Triangle
	 */
	public class Triangle {
		//
		public Vertex[] vertex = new Vertex[3];
	}

	/**
	 * The Sector class holding the number and
	 * all Triangles.
	 */
	public class Sector {
		//
		public int numtriangles;
		//
		public Triangle[] triangle;
	}


}