package com.suping.i2_watch.entity;

import android.content.Context;
import android.util.Log;

import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class I2WatchProtocolDataForNotify {
	
	public static final  int NOTIFY_SYNC_TIME = 0x05;
	public static final  int NOTIFY_HISTORY = 0x25;
	public static final  int NOTIFY_SYNC_HISTORY = 0x26;
	public static final  int NOTIFY_PHOTO = 0x33;
	public static final  int NOTIFY_FIND_PHOTO = 0x43;
	public static final  int NOTIFY_STEP_AND_CAL = 0x71;
	public static final  int NOTIFY_INCOMING_SILENCE = 0x80;
	public static final  int NOTIFY_INCOMING_REJECT = 0x81;
	
	
	private static I2WatchProtocolDataForNotify mProtocolForNotify;

	private I2WatchProtocolDataForNotify() {

	}

	public static I2WatchProtocolDataForNotify instance() {
		if (mProtocolForNotify == null) {
			mProtocolForNotify = new I2WatchProtocolDataForNotify();
		}
		return mProtocolForNotify;
	}
	
	public int getTypeFromData(byte[] data) {
		String dataStr = DataUtil.byteToHexString(data);
		String pro = dataStr.substring(0, 2);
		Log.v("",dataStr);
		if (pro.equals("05") && dataStr.length() ==2) {
			return NOTIFY_SYNC_TIME;
		} else if (pro.equals("25") && dataStr.length() == 32) {
			return NOTIFY_HISTORY;
		}else if (pro.equals("26") && dataStr.length() == 4) {
			return NOTIFY_SYNC_HISTORY;
		}else if (pro.equals("33") && dataStr.length() == 2) {
			return NOTIFY_PHOTO;
		} else if (pro.equals("43") && dataStr.length() == 2) {
			return NOTIFY_FIND_PHOTO;
		} else if (pro.equals("71") && dataStr.length() == 24) {
			return NOTIFY_STEP_AND_CAL;
		} else if (pro.equals("80") && dataStr.length() == 2) {
			return NOTIFY_STEP_AND_CAL;
		}
		
		else {
			Log.v("","错误的数据");
			return -1;
		}
	}
	
	
	/**
	 * 时间同步 0xAA 11
	 * @return
	 */
	public  int notifyForSyncTime(byte []data){
//		String dataStr = DataUtil.byteToHexString(data);
		if (getTypeFromData(data)==NOTIFY_SYNC_TIME) {
			return 0x11; //返回0x11 表示要时间同步
		}else{
			return -1;
		}
	}
	
	
}
