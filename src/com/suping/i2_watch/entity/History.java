package com.suping.i2_watch.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.litepal.crud.DataSupport;

import android.util.Log;

public class History extends DataSupport {
	
	private int id;
	/** 记录日期 **/
	private String historyDate;
	/** 类型 : 运动0x01 / 睡眠0x00 **/
	private int type;
	/** 时间点 **/
	private int timeIndex;
	/** 记录的值, 若为 pedo 则是计步数, 若为 sleep 则为翻身次数 **/
	private int historyValue;

	public History() {
	}

	public History(String historyDate, int type, int timeIndex, int historyValue) {
		super();
		this.historyDate = historyDate;
		this.type = type;
		this.timeIndex = timeIndex;
		this.historyValue = historyValue;
	}

	public History(int value, int tag) {
		super();
		//数据 ：0x0001 8 001,
			//时间点前16位 00 00 00 00 00 00 00 00 （31-16）
		this.timeIndex = (value >> 16) & 0xFFFF;
			//num 1 001  1是type 001是num 一起为 8
		int num = (value >> 12) & 0b111;
		this.type = (value >> 15) & 0x01;
			//值 ：00 00 00 00 00 00 
		this.historyValue = value & 0xFFF;
		//时间差 0 1 2 3 4   当前日期位置tag 存贮日期位置num 
		int diff = -1 * (tag + 5 - num) % 5;
		
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();// 得到日历
		calendar.setTime(date);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, diff);// 设置为前diff天 diff为负数
		this.historyDate = new SimpleDateFormat("yyMMdd").format(calendar.getTime());
		
		Log.v("History", "timeIndex : " + timeIndex);
		Log.v("History", "num : " + num);
		Log.v("History", "historyDate : " + historyDate);
		Log.v("History", "type : " + type);
		Log.v("History", "historyValue : " + historyValue);
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTimeIndex() {
		return timeIndex;
	}

	public void setTimeIndex(int timeIndex) {
		this.timeIndex = timeIndex;
	}

	public int getHistoryValue() {
		return historyValue;
	}

	public void setHistoryValue(int historyValue) {
		this.historyValue = historyValue;
	}

	@Override
	public String toString() {
		return this.id + "," + this.historyDate + "," + this.historyValue + "," + this.timeIndex + ","
				+ (this.type);
	}

}
