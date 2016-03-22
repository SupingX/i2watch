package com.suping.i2_watch.entity;

import org.litepal.crud.DataSupport;

public class History extends DataSupport{
	private int id;
	/** 记录日期 **/
	private String year;
	private String month;
	private String day;
	/**时段0~23**/
	private String hour;
	/** 1个小时内3个值  运动步数**/
	private int sportStep_1;
	private int sportStep_2;
	private int sportStep_3;
	/** 1个小时内3个值  运动时间　**/
	private int sportTime_1;
	private int sportTime_2;
	private int sportTime_3;
	/** 1个小时内3个值  翻身次数**/
	private int sleepOneself_1;
	private int sleepOneself_2;
	private int sleepOneself_3;
	public History(String year, String month, String day, String hour, int sportStep_1, int sportStep_2, int sportStep_3, int sportTime_1, int sportTime_2, int sportTime_3, int sleepOneself_1,
			int sleepOneself_2, int sleepOneself_3, int sleepAwak_1, int sleepAwak_2, int sleepAwak_3, int sleepLight_1, int sleepLight_2, int sleepLight_3, int sleepDeep_1, int sleepDeep_2,
			int sleepDeep_3) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.sportStep_1 = sportStep_1;
		this.sportStep_2 = sportStep_2;
		this.sportStep_3 = sportStep_3;
		this.sportTime_1 = sportTime_1;
		this.sportTime_2 = sportTime_2;
		this.sportTime_3 = sportTime_3;
		this.sleepOneself_1 = sleepOneself_1;
		this.sleepOneself_2 = sleepOneself_2;
		this.sleepOneself_3 = sleepOneself_3;
		this.sleepAwak_1 = sleepAwak_1;
		this.sleepAwak_2 = sleepAwak_2;
		this.sleepAwak_3 = sleepAwak_3;
		this.sleepLight_1 = sleepLight_1;
		this.sleepLight_2 = sleepLight_2;
		this.sleepLight_3 = sleepLight_3;
		this.sleepDeep_1 = sleepDeep_1;
		this.sleepDeep_2 = sleepDeep_2;
		this.sleepDeep_3 = sleepDeep_3;
	}
	/** 1个小时内3个值  清醒时间　**/
	private int sleepAwak_1;
	private int sleepAwak_2;
	private int sleepAwak_3;
	/** 1个小时内3个值  浅睡时间　**/
	private int sleepLight_1;
	private int sleepLight_2;
	private int sleepLight_3;
	/** 1个小时内3个值  深睡时间　**/
	private int sleepDeep_1;
	private int sleepDeep_2;
	private int sleepDeep_3;
	
	
	public History() {
		super();
	}
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
	public int getSportStep_1() {
		return sportStep_1;
	}
	public void setSportStep_1(int sportStep_1) {
		this.sportStep_1 = sportStep_1;
	}
	public int getSportStep_2() {
		return sportStep_2;
	}
	public void setSportStep_2(int sportStep_2) {
		this.sportStep_2 = sportStep_2;
	}
	public int getSportStep_3() {
		return sportStep_3;
	}
	public void setSportStep_3(int sportStep_3) {
		this.sportStep_3 = sportStep_3;
	}
	public int getSportTime_1() {
		return sportTime_1;
	}
	public void setSportTime_1(int sportTime_1) {
		this.sportTime_1 = sportTime_1;
	}
	public int getSportTime_2() {
		return sportTime_2;
	}
	public void setSportTime_2(int sportTime_2) {
		this.sportTime_2 = sportTime_2;
	}
	public int getSportTime_3() {
		return sportTime_3;
	}
	public void setSportTime_3(int sportTime_3) {
		this.sportTime_3 = sportTime_3;
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
	@Override
	public String toString() {
		return "History [id=" + id + ", year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", sportStep_1=" + sportStep_1 + ", sportStep_2=" + sportStep_2 + ", sportStep_3="
				+ sportStep_3 + ", sportTime_1=" + sportTime_1 + ", sportTime_2=" + sportTime_2 + ", sportTime_3=" + sportTime_3 + ", sleepOneself_1=" + sleepOneself_1 + ", sleepOneself_2="
				+ sleepOneself_2 + ", sleepOneself_3=" + sleepOneself_3 + ", sleepAwak_1=" + sleepAwak_1 + ", sleepAwak_2=" + sleepAwak_2 + ", sleepAwak_3=" + sleepAwak_3 + ", sleepLight_1="
				+ sleepLight_1 + ", sleepLight_2=" + sleepLight_2 + ", sleepLight_3=" + sleepLight_3 + ", sleepDeep_1=" + sleepDeep_1 + ", sleepDeep_2=" + sleepDeep_2 + ", sleepDeep_3=" + sleepDeep_3
				+ "]";
	}
	
//	@Override
//	public String toString() {
//		return "[ 日期："+this.year+
//				"/"+this.month
//				+"/"+this.day
//				+"小时："+this.hour+""+"\n"
//				+"时段1 :"+sportStep_1
//				+"/"+sportTime_1
//				+"/"+sleepOneself_1
//				+"/"+sleepAwak_1
//				+"/"+sleepLight_1
//				+"/"+sleepDeep_1+"\n"
//				+"时段2 :"+sportStep_2
//				+"/"+sportTime_2
//				+"/"+sleepOneself_2
//				+"/"+sleepAwak_2
//				+"/"+sleepLight_2
//				+"/"+sleepDeep_2+"\n"
//				+"时段3 :"+sportStep_3
//				+"/"+sportTime_3
//				+"/"+sleepOneself_3
//				+"/"+sleepAwak_3
//				+"/"+sleepLight_3
//				+"/"+sleepDeep_3+"]";
//			
	
//	}
	
}
