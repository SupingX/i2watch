package com.suping.i2_watch.util;

import android.util.Log;

public class L {
	private static String DEFAULT_TAG = "laputa";
	
	public static void v(String tag ,String msg){
		Log.v(tag, format(msg));
	}
	
	public static void v(String msg){
		Log.v(DEFAULT_TAG, format(msg));
	}
	
	public static void v(Class<?> cls ,String msg){
		Log.v(cls.getSimpleName(), format(msg));
	}

	public static void e(String tag ,String msg){
		Log.e(tag, format(msg));
	}
	
	public static void e(String msg){
		Log.e(DEFAULT_TAG, format(msg));
	}
	public static void e(Class<?> cls ,String msg){
		Log.e(cls.getSimpleName(), format(msg));
	}
	
	public static void i(String tag ,String msg){
		Log.i(tag, format(msg));
	}
	
	public static void i(String msg){
		Log.i(DEFAULT_TAG,format(msg));
	}
	public static void i(Class<?> cls ,String msg){
		Log.i(cls.getSimpleName(), format(msg));
	}
	
	
	private static String  format(String msg){
		return "____"+msg;
	}
}
