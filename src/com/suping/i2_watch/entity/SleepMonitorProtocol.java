package com.suping.i2_watch.entity;

import com.suping.i2_watch.util.DataUtil;

public class SleepMonitorProtocol {
	
	/** 协议头   **/
	public static final String protocol = "21"; 
	
	/** 开始的小时值   ：为一个十六进制的字符串 **/
	private String startHour;
	/** 开始的分钟值   ：为一个十六进制的字符串  **/
	private String startMin;
	/** 结束的小时值    ：为一个十六进制的字符串**/
	private String endHour;
	/** 结束的分钟值    ：为一个十六进制的字符串**/
	private String endMin;
	/** 重复的星期        ：为一个十六进制的字符串  **/
	
	public SleepMonitorProtocol(String startHour, String startMin, String endHour, String endMin) {
		super();
		this.startHour = startHour;
		this.startMin = startMin;
		this.endHour = endHour;
		this.endMin = endMin;
	}
	public SleepMonitorProtocol() {
		// TODO Auto-generated constructor stub
	}
	public String getStartHour() {
		return startHour;
	}
	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}
	public String getStartMin() {
		return startMin;
	}
	public void setStartMin(String startMin) {
		this.startMin = startMin;
	}
	public String getEndHour() {
		return endHour;
	}
	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}
	public String getEndMin() {
		return endMin;
	}
	public void setEndMin(String endMin) {
		this.endMin = endMin;
	}
	public static String getProtocol() {
		return protocol;
	}
	
	@Override
	public String toString() {
		return protocol + startHour + startMin + endHour + endMin;
	}
	
	public byte[] toByte() {
		return DataUtil.getBytesByString(toString());
	}
}
