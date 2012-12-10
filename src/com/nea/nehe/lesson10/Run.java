package com.nea.nehe.lesson10;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The initial Android Activity, setting and initiating
 * the OpenGL ES Renderer Class @see Lesson10.java
 * 
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class Run extends Activity {

	private SensorManager mSensorManager;
	
	private Sensor accelSensor;
	private SensorEventListener accelListener;
	
	private Sensor magSensor;
	private SensorEventListener magListener;

	private Queue<float[]> prev_accelValues = new LinkedList<float[]>();
	private int num_accelValues = 25;
	private float[] running_sum = {0f, 0f, 0f};
			
	private float[] last_accelValues = new float[3];
	private float[] accelValues = new float[3];
	private float[] magValues = new float[3];
	private float[] orientationValues = new float[3];
	private float[] rotMatrix = new float[9];
	
	public static int display_width;
	public static int display_height;
	
	public static Lesson10 lesson10;
	public static TextView textView_data;
	public static TextView textView_pos;
	public static ImageView head_indicator;
	
	/**
	 * Initiate our @see Lesson10.java,
	 * which is GLSurfaceView and Renderer
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Globals.head_toggle) {
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			initSensorListeners();
			mSensorManager.registerListener(accelListener, accelSensor, SensorManager.SENSOR_DELAY_GAME);
			mSensorManager.registerListener(magListener, magSensor, SensorManager.SENSOR_DELAY_GAME);			
		}
		
		for(int i = 0; i < num_accelValues; ++i) {
			float[] arr = {0.0f, 0.0f, 0.0f};
			prev_accelValues.add(arr);
		}
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		display_height = metrics.heightPixels;
		display_width = metrics.widthPixels;
		
		setContentView(R.layout.activity_layout);
		lesson10 = (Lesson10)findViewById(R.id.custom_glSurfaceView);
		textView_data = (TextView)findViewById(R.id.textView_data);
		textView_pos = (TextView)findViewById(R.id.textView_pos);
	}
	
	private void initSensorListeners() {
		accelListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				System.arraycopy(accelValues, 0, last_accelValues, 0, 3);
				System.arraycopy(event.values, 0, accelValues, 0, 3);
				accelFilter();
				updateRotation();
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		};
		
		magListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				System.arraycopy(event.values, 0, magValues, 0, 3);
				updateRotation();
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		};
	}
	
	private void updateRotation() {
		SensorManager.getRotationMatrix(rotMatrix, null, accelValues, magValues);
		SensorManager.getOrientation(rotMatrix, orientationValues);
		float new_lookupdown = (float)Math.toDegrees(orientationValues[1]);
		Person viewer = lesson10.get_world().get_viewer();
		
		viewer.set_upDownDegrees(new_lookupdown);
		float new_yrot_rads = orientationValues[2];
	    float new_yrot = (float)Math.toDegrees(new_yrot_rads);
	    viewer.set_sideDegrees(new_yrot);
	    textView_data.setText("head_up:"+Helper.roundFloat(viewer.get_upDownDegrees()) + 
	    		"\nhead_side:"+Helper.roundFloat(viewer.get_sideDegrees()));
	}
	
	private void accelFilter() {
		float[] copy = {0f, 0f, 0f};
		System.arraycopy(accelValues, 0, copy, 0, 3);
		prev_accelValues.add(copy);
		float[] values = prev_accelValues.remove();
		running_sum[0] = running_sum[0] + accelValues[0] - values[0];
		running_sum[1] = running_sum[1] + accelValues[1] - values[1];
		running_sum[2] = running_sum[2] + accelValues[2] - values[2];
		accelValues[0] = running_sum[0] / num_accelValues;
		accelValues[1] = running_sum[1] / num_accelValues;
		accelValues[2] = running_sum[2] / num_accelValues;
	}
	
	/**
	 * Remember to resume our Lesson
	 */
	@Override
	protected void onResume() {
		super.onResume();
		lesson10.onResume();
	}

	/**
	 * Also pause our Lesson
	 */
	@Override
	protected void onPause() {
		super.onPause();
		lesson10.onPause();
	}

}