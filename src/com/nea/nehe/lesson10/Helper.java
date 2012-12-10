package com.nea.nehe.lesson10;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Helper {
	private static DecimalFormat df = new DecimalFormat("####.##");
	private static Random random = new Random();
	
	public static String roundFloat(float f) {
		return df.format(f);
	}
	
	public static boolean collisionDetect(Object3D candidate, LinkedList<Object3D> others) {
		float x_lo = candidate.get_xPos() - candidate.get_xSize();
		float x_hi = candidate.get_xPos() + candidate.get_xSize();
		float y_lo = candidate.get_yPos() - candidate.get_ySize();
		float y_hi = candidate.get_yPos() + candidate.get_ySize();
		float z_lo = candidate.get_zPos() - candidate.get_zSize();
		float z_hi = candidate.get_zPos() + candidate.get_zSize();
		
		ListIterator<Object3D> itr = others.listIterator();
		while(itr.hasNext()) {
			Object3D cur = (Object3D)itr.next();
			float x_lo_cur = cur.get_xPos() - cur.get_xSize();
			float x_hi_cur = cur.get_xPos() + cur.get_xSize();
			float y_lo_cur = cur.get_yPos() - cur.get_ySize();
			float y_hi_cur = cur.get_yPos() + cur.get_ySize();
			float z_lo_cur = cur.get_zPos() - cur.get_zSize();
			float z_hi_cur = cur.get_zPos() + cur.get_zSize();
			
			if(!(x_lo > x_hi_cur || x_hi < x_lo_cur 
					|| y_lo > y_hi_cur || y_hi < y_lo_cur
					|| z_lo > z_hi_cur || z_hi < z_lo_cur)) {
				return true;
			}
		}
		return false;
	}
	
	public static void collisionResponse(Object3D candidate, LinkedList<Object3D> others) {
		float x_lo = candidate.get_xPos() - candidate.get_xSize();
		float x_hi = candidate.get_xPos() + candidate.get_xSize();
		float y_lo = candidate.get_yPos() - candidate.get_ySize();
		float y_hi = candidate.get_yPos() + candidate.get_ySize();
		float z_lo = candidate.get_zPos() - candidate.get_zSize();
		float z_hi = candidate.get_zPos() + candidate.get_zSize();
		
		ListIterator<Object3D> itr = others.listIterator();
		while(itr.hasNext()) {
			Object3D cur = (Object3D)itr.next();
			float x_lo_cur = cur.get_xPos() - cur.get_xSize();
			float x_hi_cur = cur.get_xPos() + cur.get_xSize();
			float y_lo_cur = cur.get_yPos() - cur.get_ySize();
			float y_hi_cur = cur.get_yPos() + cur.get_ySize();
			float z_lo_cur = cur.get_zPos() - cur.get_zSize();
			float z_hi_cur = cur.get_zPos() + cur.get_zSize();
			
			if(!(x_lo > x_hi_cur || x_hi < x_lo_cur 
					|| y_lo > y_hi_cur || y_hi < y_lo_cur
					|| z_lo > z_hi_cur || z_hi < z_lo_cur)) {
				candidate.revert_pos();
			}
		}
	}
	
	public static float nextRandFloat() {
		return (random.nextFloat() - 0.5f) / 10f;
	}
		}
