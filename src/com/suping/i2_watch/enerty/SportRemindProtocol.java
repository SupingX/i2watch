package com.suping.i2_watch.enerty;

import com.suping.i2_watch.util.DataUtil;

/**
 * 运动提醒 协议
 * @author Administrator
 * 
 */
public class SportRemindProtocol extends AbstractProtocolWrite {
	/** 协议头   **/
	public static final String protocol = "19"; 
	/** 重复                   ：为一个十六进制的字符串**/
	private String interval;
	/** 开始的小时值   ：为一个十六进制的字符串 **/
	private String startHour;
	/** 开始的分钟值   ：为一个十六进制的字符串  **/
	private String startMin;
	/** 结束的小时值    ：为一个十六进制的字符串**/
	private String endHour;
	/** 结束的分钟值    ：为一个十六进制的字符串**/
	private String endMin;
	/** 重复的星期        ：为一个十六进制的字符串  **/
	private String repeat;
	/** 运动提醒开关     ：为一个十六进制的字符串。（开：01 ；关：00）   **/
	private String onoff;
	
	
	public SportRemindProtocol() {
	}

	public SportRemindProtocol(String interval, String startHour, String startMin, String endHour, String endMin,
			String repeat, String onoff) {
		super();
		this.interval = interval;
		this.startHour = startHour;
		this.startMin = startMin;
		this.endHour = endHour;
		this.endMin = endMin;
		this.repeat = repeat;
		this.onoff = onoff;
	}
	
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
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
	public String getRepeat() {
		return repeat;
	}
	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}
	public String getOnoff() {
		return onoff;
	}
	public void setOnoff(String onoff) {
		this.onoff = onoff;
	}
	
	@Override
	public String toString() {
		return protocol + onoff + interval + repeat + startHour + startMin + endHour + endMin;
	}
	
	/**
	 * 协议byte[]形式
	 * @return
	 */
	public byte[] toByte(){
		return DataUtil.getBytesByString(toString());
	}
}
