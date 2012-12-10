package com.nea.nehe.lesson10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OrientationIndicator extends View {
	
	private float head_x;
	private float head_y;
	private float body_x;
	private float body_y;
	
	private int radius;
	private Paint head_paint = new Paint();
	private Paint body_paint = new Paint();
	
	public OrientationIndicator(Context context) {
		super(context);
		init();
	}
	
	public OrientationIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		radius = (Run.display_height > Run.display_width) 
				? Run.display_width : Run.display_height;
		radius /= 10;
		
		head_paint.setColor(0xFF0000);
		head_paint.setAlpha(255);
		head_paint.setStrokeWidth(2.0f);
		head_paint.setStyle(Paint.Style.STROKE);
		
		body_paint.setColor(0x00FF00);
		body_paint.setAlpha(255);
		body_paint.setStrokeWidth(2.0f);
		body_paint.setStyle(Paint.Style.STROKE);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Person viewer = Run.lesson10.get_world().get_viewer();
				
		canvas.drawCircle(radius, radius, radius, head_paint);

		double bodyAngle = Math.toRadians(viewer.get_bodyAngle());
    	body_x = (float)Math.sin(bodyAngle);
    	body_y = (float)Math.cos(bodyAngle);
		canvas.drawLine(radius, radius, 
				radius+radius*body_x, radius-radius*body_y, body_paint);
		
		double sideDegrees = Math.toRadians(viewer.get_sideDegrees());
		head_x = (float)Math.sin(bodyAngle + sideDegrees);
		head_y = (float)Math.cos(bodyAngle + sideDegrees);
		canvas.drawLine(radius, radius, 
				radius+radius*head_x, radius-radius*head_y, head_paint);
		invalidate();
	}
}
