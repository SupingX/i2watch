package com.suping.i2_watch.logic;

import java.util.Date;

public abstract class AbstractProtoclForWrite {
	/**
	 * 时间同步 0x11
	 * 18位
	 * 2015/8/5 9:41		
	 * 0x11 00 DF07 0805 092900
	 * @param type 0x00:24小时  0x01:12小时
	 * @param date 日期
	 * @return
	 */
	public abstract byte[] getByteForSyncTime(int type,Date date);
	public abstract byte[] getByteForClock(int state,int weekday_1,int hour_1,int clock_1);
	
	
}
