package com.suping.i2_watch.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.litepal.crud.DataSupport;

import android.util.Log;

/**
 * * 协议［30位］ ：0x 25 xx xx xxxxxxxx xxxxxxxx xxxxxxxx 　
 * * 表示:0x [协议头]　[历史天数0~4]
 * * [小时0~23] [dat0-19] [dat20-39] [dat40-59] 　
 * * 其中每个时段dat包含32位 　
 * * [bit0-bit15]   [翻身次数|运动步数] 　
 * * [bit16-bit20] 　[清醒时间|运动时间] 　
 * * [bit21-bit25]　[浅睡时间] 　
 * * [bit26-bit30] 　[深睡时间] 　
 * * [bit31]　　　　 [模式 1:sleep | 0:step] 　
 * *
 * 
 * @author Administrator
 *
 */
public class HistorySport extends DataSupport {

	private int id;
	/** 记录日期 **/
	private String historyDate;
	/**
	 * 运动步数
	 */
	private int sportStep;
	/**
	 * 运动时间
	 */
	private int sportTime;

	public HistorySport() {
	}

	public HistorySport(String historyDate, int sportStep, int sportTime) {
		super();
		this.historyDate = historyDate;
		this.sportStep = sportStep;
		this.sportTime = sportTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(String historyDate) {
		this.historyDate = historyDate;
	}

	public int getSportStep() {
		return sportStep;
	}

	public void setSportStep(int sportStep) {
		this.sportStep = sportStep;
	}

	public int getSportTime() {
		return sportTime;
	}

	public void setSportTime(int sportTime) {
		this.sportTime = sportTime;
	}

//	public HistorySport(int value, int tag) {
//		super();
//		 //数据 ：0x0001 8 001,
//		 //时间点前16位 00 00 00 00 00 00 00 00 （31-16）
//	
//		
//			int hour = (value >> 16) & 0xFFFF;
//		 //num 1 001 1是type 001是num 一起为 8
//		 int num = (value >> 12) & 0b111;
////		 this.type = (value >> 15) & 0x01;
//		 //值 ：00 00 00 00 00 00
//		 this.sportStep = value & 0xFFF;
//		 //时间差 0 1 2 3 4 当前日期位置tag 存贮日期位置num
//		 int diff = -1 * (tag + 5 - num) % 5;
//		
//		 Date date = new Date();
//		 Calendar calendar = Calendar.getInstance();// 得到日历
//		 calendar.setTime(date);// 把当前时间赋给日历
//		 calendar.add(Calendar.DAY_OF_MONTH, diff);// 设置为前diff天 diff为负数
//		 this.historyDate = new
//		 SimpleDateFormat("yyMMdd").format(calendar.getTime());
//		
//		 Log.v("History", "timeIndex : " + timeIndex);
//		 Log.v("History", "num : " + num);
//		 Log.v("History", "historyDate : " + historyDate);
//		 Log.v("History", "type : " + type);
//		 Log.v("History", "historyValue : " + historyValue);
//	}

}
