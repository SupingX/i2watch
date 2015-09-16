package com.suping.i2_watch.entity;

import com.suping.i2_watch.util.DataUtil;
/**
 * 闹钟设置协议
 * @author Administrator
 *
 */
public class ClockSetProtocol extends AbstractProtocolWrite {
	/** 协议头 **/
	public static final String protocol = "15";

	private String onoffFirst;
	private String repeatFirst;
	private String hourFirst;
	private String minFirst;
	private String onoffSecond;
	private String repeatSecond;
	private String hourSecond;
	private String minSecond;
	private String onoffThrid;
	private String repeatThrid;
	private String hourThrid;
	private String minThrid;
	
	
	public ClockSetProtocol() {
	}

	public ClockSetProtocol(String onoffFirst, String repeatFirst, String hourFirst, String minFirst,
			String onoffSecond, String repeatSecond, String hourSecond, String minSecond, String onoffThrid,
			String repeatThrid, String hourThrid, String minThrid) {
		super();
		this.onoffFirst = onoffFirst;
		this.repeatFirst = repeatFirst;
		this.hourFirst = hourFirst;
		this.minFirst = minFirst;
		this.onoffSecond = onoffSecond;
		this.repeatSecond = repeatSecond;
		this.hourSecond = hourSecond;
		this.minSecond = minSecond;
		this.onoffThrid = onoffThrid;
		this.repeatThrid = repeatThrid;
		this.hourThrid = hourThrid;
		this.minThrid = minThrid;
	}

	public String getOnoffFirst() {
		return onoffFirst;
	}

	public void setOnoffFirst(String onoffFirst) {
		this.onoffFirst = onoffFirst;
	}

	public String getRepeatFirst() {
		return repeatFirst;
	}

	public void setRepeatFirst(String repeatFirst) {
		this.repeatFirst = repeatFirst;
	}

	public String getHourFirst() {
		return hourFirst;
	}

	public void setHourFirst(String hourFirst) {
		this.hourFirst = hourFirst;
	}

	public String getMinFirst() {
		return minFirst;
	}

	public void setMinFirst(String minFirst) {
		this.minFirst = minFirst;
	}

	public String getOnoffSecond() {
		return onoffSecond;
	}

	public void setOnoffSecond(String onoffSecond) {
		this.onoffSecond = onoffSecond;
	}

	public String getRepeatSecond() {
		return repeatSecond;
	}

	public void setRepeatSecond(String repeatSecond) {
		this.repeatSecond = repeatSecond;
	}

	public String getHourSecond() {
		return hourSecond;
	}

	public void setHourSecond(String hourSecond) {
		this.hourSecond = hourSecond;
	}

	public String getMinSecond() {
		return minSecond;
	}

	public void setMinSecond(String minSecond) {
		this.minSecond = minSecond;
	}

	public String getOnoffThrid() {
		return onoffThrid;
	}

	public void setOnoffThrid(String onoffThrid) {
		this.onoffThrid = onoffThrid;
	}

	public String getRepeatThrid() {
		return repeatThrid;
	}

	public void setRepeatThrid(String repeatThrid) {
		this.repeatThrid = repeatThrid;
	}

	public String getHourThrid() {
		return hourThrid;
	}

	public void setHourThrid(String hourThrid) {
		this.hourThrid = hourThrid;
	}

	public String getMinThrid() {
		return minThrid;
	}

	public void setMinThrid(String minThrid) {
		this.minThrid = minThrid;
	}

	public static String getProtocol() {
		return protocol;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return protocol + onoffFirst +repeatFirst + hourFirst + minFirst
				+ onoffSecond + repeatSecond + hourSecond + minSecond
				+ onoffThrid + repeatThrid + hourThrid + minThrid;
	}
	@Override
	public byte[] toByte() {
		// TODO Auto-generated method stub
		return DataUtil.getBytesByString(this.toString());
	}

}
