package com.suping.i2_watch.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.litepal.crud.DataSupport;

import android.util.Log;

/**
 　* 协议［30位］ ：0x 25 xx xx xxxxxxxx xxxxxxxx xxxxxxxx
 　* 		表示:0x [协议头]　[历史天数0~4] [小时0~23] [dat0-19] [dat20-39] [dat40-59] 
 　* 		其中每个时段dat包含32位
 　* 			[bit0-bit15] 　　[翻身次数|运动步数]
 　* 			[bit16-bit20]  　[清醒时间|运动时间]
 　* 　　　　　[bit21-bit25]　　[浅睡时间] 
 　*  　　　　　[bit26-bit30] 　　　[深睡时间]
 　* 　　　　　 [bit31]　　　　　　[模式 1:sleep | 0:step]
 　* 		  
 * @author Administrator
 *
 */
public class HistorySleep extends DataSupport {
	
	private int id;
	/** 记录日期 **/
	private String year;
	private String month;
	private String day;
	/**0~23**/
	private String hour;
	/** 1个小时内3个值  翻身次数**/
	private int sleepOneself_1;
	private int sleepOneself_2;
	private int sleepOneself_3;
	/** 1个小时内3个值  清醒时间　**/
	private int sleepAwak_1;
	private int sleepAwak_2;
	private int sleepAwak_3;
	/** 1个小时内3个值  浅水时间　**/
	private int sleepLight_1;
	private int sleepLight_2;
	private int sleepLight_3;
	/** 1个小时内3个值  深睡时间　**/
	private int sleepDeep_1;
	private int sleepDeep_2;
	private int sleepDeep_3;
	
	


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}




	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}


	public String getMonth() {
		return month;
	}


	public void setMonth(String month) {
		this.month = month;
	}


	public String getDay() {
		return day;
	}


	public void setDay(String day) {
		this.day = day;
	}


	public String getHour() {
		return hour;
	}


	public void setHour(String hour) {
		this.hour = hour;
	}


	public int getSleepOneself_1() {
		return sleepOneself_1;
	}


	public void setSleepOneself_1(int sleepOneself_1) {
		this.sleepOneself_1 = sleepOneself_1;
	}


	public int getSleepOneself_2() {
		return sleepOneself_2;
	}


	public void setSleepOneself_2(int sleepOneself_2) {
		this.sleepOneself_2 = sleepOneself_2;
	}


	public int getSleepOneself_3() {
		return sleepOneself_3;
	}


	public void setSleepOneself_3(int sleepOneself_3) {
		this.sleepOneself_3 = sleepOneself_3;
	}


	public int getSleepAwak_1() {
		return sleepAwak_1;
	}


	public void setSleepAwak_1(int sleepAwak_1) {
		this.sleepAwak_1 = sleepAwak_1;
	}


	public int getSleepAwak_2() {
		return sleepAwak_2;
	}


	public void setSleepAwak_2(int sleepAwak_2) {
		this.sleepAwak_2 = sleepAwak_2;
	}


	public int getSleepAwak_3() {
		return sleepAwak_3;
	}


	public void setSleepAwak_3(int sleepAwak_3) {
		this.sleepAwak_3 = sleepAwak_3;
	}


	public int getSleepLight_1() {
		return sleepLight_1;
	}


	public void setSleepLight_1(int sleepLight_1) {
		this.sleepLight_1 = sleepLight_1;
	}


	public int getSleepLight_2() {
		return sleepLight_2;
	}


	public void setSleepLight_2(int sleepLight_2) {
		this.sleepLight_2 = sleepLight_2;
	}


	public int getSleepLight_3() {
		return sleepLight_3;
	}


	public void setSleepLight_3(int sleepLight_3) {
		this.sleepLight_3 = sleepLight_3;
	}


	public int getSleepDeep_1() {
		return sleepDeep_1;
	}


	public void setSleepDeep_1(int sleepDeep_1) {
		this.sleepDeep_1 = sleepDeep_1;
	}


	public int getSleepDeep_2() {
		return sleepDeep_2;
	}


	public void setSleepDeep_2(int sleepDeep_2) {
		this.sleepDeep_2 = sleepDeep_2;
	}


	public int getSleepDeep_3() {
		return sleepDeep_3;
	}


	public void setSleepDeep_3(int sleepDeep_3) {
		this.sleepDeep_3 = sleepDeep_3;
	}





	public HistorySleep() {
	}


	public HistorySleep(int value, int tag) {
		super();
//		//数据 ：0x0001 8 001,
//			//时间点前16位 00 00 00 00 00 00 00 00 （31-16）
//		this.timeIndex = (value >> 16) & 0xFFFF;
//			//num 1 001  1是type 001是num 一起为 8
//		int num = (value >> 12) & 0b111;
//		this.type = (value >> 15) & 0x01;
//			//值 ：00 00 00 00 00 00 
//		this.historyValue = value & 0xFFF;
//		//时间差 0 1 2 3 4   当前日期位置tag 存贮日期位置num 
//		int diff = -1 * (tag + 5 - num) % 5;
//		
//		Date date = new Date();
//		Calendar calendar = Calendar.getInstance();// 得到日历
//		calendar.setTime(date);// 把当前时间赋给日历
//		calendar.add(Calendar.DAY_OF_MONTH, diff);// 设置为前diff天 diff为负数
//		this.historyDate = new SimpleDateFormat("yyMMdd").format(calendar.getTime());
//		
//		Log.v("History", "timeIndex : " + timeIndex);
//		Log.v("History", "num : " + num);
//		Log.v("History", "historyDate : " + historyDate);
//		Log.v("History", "type : " + type);
//		Log.v("History", "historyValue : " + historyValue);
	}

	
	@Override
	public String toString() {
		return "["+this.year
				+"/"  + month
				+"/"  + day
				+"/"  + hour
				+"/"  + sleepOneself_1
				+"/"  + sleepOneself_2
				+"/"  + sleepOneself_3
				+"/"  + sleepAwak_1
				+"/"  + sleepAwak_2
				+"/"  + sleepAwak_3
				+"/"  + sleepLight_1
				+"/"  + sleepLight_2
				+"/"  + sleepLight_3
				+"/"  + sleepDeep_1
				+"/"  + sleepDeep_2
				+"/"  + sleepDeep_3 +"]"
				;
	}
}
