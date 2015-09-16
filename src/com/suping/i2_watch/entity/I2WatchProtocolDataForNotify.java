package com.suping.i2_watch.entity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.DateUtil;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class I2WatchProtocolDataForNotify {
	// -------------------------------------------------------
	public static final int NOTIFY_AA = 0xAA;
	public static final int NOTIFY_SYNC_TIME = 0X11;
	public static final int NOTIFY_CLOCK = 0X15;
	public static final int NOTIFY_REMIND_SPORT = 0X19;
	public static final int NOTIFY_SLEEP_TIME = 0X21;
	public static final int NOTIFY_REMIND_INCOMING = 0X23;
	public static final int NOTIFY_CAMERA_ENTER = 0X31;
	public static final int NOTIFY_CAMERA_EXIT = 0X32;
	public static final int NOTIFY_REMIND_ON = 0X41;
	public static final int NOTIFY_REMIND_OFF = 0X00;
	public static final int NOTIFY_REMIND_CONNECT = 0X43;
	public static final int NOTIFY_SEARCH = 0X45;
	public static final int NOTIFY_BRIGHT = 0X61;
	public static final int NOTIFY_BRIGHT_ON = 0X65;
	public static final int NOTIFY_BRIGHT_OFF = 0X66;
	public static final int NOTIFY_INCOMING = 0X81;
	public static final int NOTIFY_INCOMING_CALLED = 0X82;// 来电已接听
	public static final int NOTIFY_MSG = 0X83;// 短信
	public static final int NOTIFY_MSG_READED = 0X84;// 短信
	public static final int NOTIFY_SIGN = 0X91;// 个性签名
	// -------------------------------------------------------
	public static final int NOTIFY_VIR = 0X05; // 虚拟来电
	public static final int NOTIFY_HISTORY = 0x25; // 历史记录
	public static final int NOTIFY_SYNC_HISTORY = 0x26;//同步开始 同步结束
	public static final int NOTIFY_PHOTO = 0x33;
	public static final int NOTIFY_FIND_PHOTO = 0x43;
	public static final int NOTIFY_STEP_AND_CAL = 0x71;
	public static final int NOTIFY_INCOMING_SILENCE = 0x80;
	public static final int NOTIFY_INCOMING_REJECT = 0x81;

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
		Log.v("", dataStr);
		if (pro.equals("AA") && dataStr.length() == 4) {
			return notifyOrder(dataStr);
		} else if (pro.equals("25") && dataStr.length() == 30) {
			return NOTIFY_HISTORY;
		} else if (pro.equals("26") && dataStr.length() == 4) {
			return NOTIFY_SYNC_HISTORY;
		} else if (pro.equals("33") && dataStr.length() == 2) {
			return NOTIFY_PHOTO;
		} else if (pro.equals("43") && dataStr.length() == 2) {
			return NOTIFY_FIND_PHOTO;
		} else if (pro.equals("71") && dataStr.length() == 24) {
			return NOTIFY_STEP_AND_CAL;
		} else if (pro.equals("80") && dataStr.length() == 2) {
			return NOTIFY_STEP_AND_CAL;
		} else {
			Log.v("", "错误的数据");
			return -1;
		}
	}

	/**
	 * 得到各种操作命令 手环 --> 手机
	 * 
	 * @return
	 */
	public int notifyOrder(String dataStr) {
		String substring = dataStr.substring(2, 4);
		int order = Integer.valueOf(substring, 16);
		return order;
	}

	public Object notifyHistory(byte[] data) {

		if (getTypeFromData(data) == NOTIFY_HISTORY) {
			String dataStr = DataUtil.byteToHexString(data);
			// 协议［30位］ 0x 25 xx xx xxxxxxxx xxxxxxxx xxxxxxxx 　
			// 表示: 0x [协议头]　[历史天数0~4] [小时0~23] [dat0-19] [dat20-39] [dat40-59]
			// 1.解析日期
			int dateDiff = Integer.valueOf(dataStr.substring(2, 4), 16);
			Date date = DateUtil.getDateOfDiffDay(new Date(),-(dateDiff));
			String dateStr = DateUtil.dateToString(date,"yyyyMMdd");
		
			// 2.解析时间
			int hour = Integer.valueOf(dataStr.substring(4, 6), 16);
	
			// 3.数据1 
//			long data_1 = getIntegerValue(new byte[] { data[3], data[4], data[5], data[6] });
			long data_1 = Long.valueOf(dataStr.substring(6, 14),16);
//			long data_1_long = DataUtil.getUnsignedIntt(data_1);
			long type_1 = data_1 >> 31;
			// 3.数据2
//			long data_2 = getIntegerValue(new byte[] { data[7], data[8], data[9], data[10] });
			long data_2 = Long.valueOf(dataStr.substring(14, 22),16);
//			long data_2_long = DataUtil.getUnsignedIntt(data_2);
			long type_2 = data_2 >> 31;
			// 3.数据3
//			long data_3 = getIntegerValue(new byte[] { data[11], data[12], data[13], data[14] });
			long data_3 = Long.valueOf(dataStr.substring(22, 30),16);
//			long data_3_long = DataUtil.getUnsignedIntt(data_3);
			long type_3 = data_3 >> 31;
			
			if (type_1 == 0 && type_2 == 0 &&type_3 == 0) {//运动模式
				L.v("解析-->运动数据");
				HistorySport historySport = new HistorySport();
				historySport.setYear(dateStr.substring(0,4));
				historySport.setMonth(dateStr.substring(4,6));
				historySport.setDay(dateStr.substring(6,8));
				historySport.setHour(String.valueOf(hour));
				//   0     10001        00001        10011       0010 0010 0001 0001
				long sportStep_1 = data_1&0b1111_1111_1111_1111;
				long sportTime_1 = (data_1>>16)&0b11111;
				historySport.setSportStep_1(Integer.valueOf(String.valueOf(sportStep_1)));
				historySport.setSportTime_1(Integer.valueOf(String.valueOf(sportTime_1)));
				
				long sportStep_2 = data_2&0b0111111111111111L;
				long sportTime_2 = (data_2>>16)&0b11111L;
				historySport.setSportStep_2(Integer.valueOf(String.valueOf(sportStep_2)));
				historySport.setSportTime_2(Integer.valueOf(String.valueOf(sportTime_2)));
		
				long sportStep_3 = data_3&0b0111111111111111L;
				long sportTime_3 = (data_3>>16)&0b11111L;
				historySport.setSportStep_3(Integer.valueOf(String.valueOf(sportStep_3)));
				historySport.setSportTime_3(Integer.valueOf(String.valueOf(sportTime_3)));
				
				return historySport;
				
			}else if (type_1 == 1 && type_2 == 1 &&type_3 == 1)  { //睡眠模式
				L.v("解析-->睡眠数据");
				HistorySleep historySleep = new HistorySleep();
				historySleep.setYear(dateStr.substring(0,4));
				historySleep.setMonth(dateStr.substring(4,6));
				historySleep.setDay(dateStr.substring(6,8));
				historySleep.setHour(String.valueOf(hour));
				
				long sleepOnself_1 = data_1&0b111111111111111;
				long sleepAwak_1 = (data_1>>16)&0b11111;
				long sleepLight_1 = (data_1>>21)&0b11111;
				long sleepDeep_1 = (data_1>>26)&0b11111;
				historySleep.setSleepOneself_1(Integer.valueOf(String.valueOf(sleepOnself_1)));
				historySleep.setSleepAwak_1(Integer.valueOf(String.valueOf(sleepAwak_1)));
				historySleep.setSleepLight_1(Integer.valueOf(String.valueOf(sleepLight_1)));
				historySleep.setSleepDeep_1(Integer.valueOf(String.valueOf(sleepDeep_1)));
				
				long sleepOnself_2 = data_2&0b111111111111111;
				long sleepAwak_2 = (data_2>>16)&0b11111;
				long sleepLight_2 = (data_2>>21)&0b11111;
				long sleepDeep_2 = (data_2>>26)&0b11111;
				historySleep.setSleepOneself_2(Integer.valueOf(String.valueOf(sleepOnself_2)));
				historySleep.setSleepAwak_2(Integer.valueOf(String.valueOf(sleepAwak_2)));
				historySleep.setSleepLight_2(Integer.valueOf(String.valueOf(sleepLight_2)));
				historySleep.setSleepDeep_2(Integer.valueOf(String.valueOf(sleepDeep_2)));
				
				long sleepOnself_3 = data_3&0b111111111111111;
				long sleepAwak_3 = (data_3>>16)&0b11111;
				long sleepLight_3 = (data_3>>21)&0b11111;
				long sleepDeep_3 = (data_3>>26)&0b11111;
				historySleep.setSleepOneself_1(Integer.valueOf(String.valueOf(sleepOnself_3)));
				historySleep.setSleepAwak_1(Integer.valueOf(String.valueOf(sleepAwak_3)));
				historySleep.setSleepLight_1(Integer.valueOf(String.valueOf(sleepLight_3)));
				historySleep.setSleepDeep_1(Integer.valueOf(String.valueOf(sleepDeep_3)));
				
				return historySleep;
			}
		}
		return null;

	}
	
	public int notifySyncState(byte[] data){
		if (getTypeFromData(data)==NOTIFY_SYNC_HISTORY) {
			String dataStr = DataUtil.byteToHexString(data);
			return Integer.valueOf(dataStr.substring(2, 4), 16);
		}else{
			return -1;
		}
	}
	
	public int notifyPhoto(byte[] data){
		L.i(getClass(),"拍照");
		if (getTypeFromData(data)==NOTIFY_PHOTO) {
			return 1;
		}else{
			return -1;
		}
	}
	
	public int notifyFindPhone(byte[] data){
		if (getTypeFromData(data)==NOTIFY_FIND_PHOTO) {
			return 1;
		}else{
			return -1;
		}
	}
	
	/**
	 * 计步和卡洛里
	 * 71 00000055 0000000C 0004 00
	 * @param data
	 * @return
	 */
	public long[] notifyStepAndCal(byte[] data){
		if (getTypeFromData(data)==NOTIFY_STEP_AND_CAL) {
			String dataStr = DataUtil.byteToHexString(data);
//			long step = getIntegerValue(new byte[]{data[1],data[2],data[3],data[4]});
//			long cal = getIntegerValue(new byte[]{data[5],data[6],data[7],data[8]});
//			long  time = getIntegerValue(new byte[]{data[9],data[10]});
			long step = Long.valueOf(dataStr.substring(2,10),16);
			long cal = Long.valueOf(dataStr.substring(10,18),16);
			long time = Long.valueOf(dataStr.substring(18,22),16);
			return new long[]{step,cal,time};
		}else{
			return null;
		}
	}
	
	public int notifyIncomingSilence(byte[] data){
		if (getTypeFromData(data)==NOTIFY_INCOMING_SILENCE) {
			return 1;
		}else{
			return -1;
		}
	}
	
	public int notifyIncomingReject(byte[] data){
		if (getTypeFromData(data)==NOTIFY_INCOMING_REJECT) {
			L.v("来点拒绝");
			return 1;
		}else{
			return -1;
		}
	}
	
	
	
/**
 * 高低字节排序
 * @param data
 * @return
 */
	private static int getIntegerValue(byte[] data) {
//		if (data == null || data.length != 4) {
//			return 0;
//		}
//		;
//
//		byte[] temp = new byte[4];
//		for (int i = 0; i < data.length; i++) {
//			temp[3 - i] = data[i];
//		}

		String tempStr = DataUtil.byteToHexString(data);

		return Integer.valueOf(tempStr, 16);
	}

}
