package com.suping.i2_watch.enerty;

import android.content.Context;
import android.util.Log;

import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class I2WatchProtocolData {

	// 运动提醒
	public final static String SHARE_INTERVAL = "share_interval";
	public final static String SHARE_START_HOUR = "share_start_hour";
	public final static String SHARE_START_MIN = "share_start_min";
	public final static String SHARE_END_HOUR = "share_end_hour";
	public final static String SHARE_END_MIN = "share_end_min";
	public final static String SHARE_REPEAT = "share_reminder_repeatValue";

	// 睡眠设置
	public final static String SHARE_MONITOR_TARGET_HOUR = "share_motitor_target_hour";
	public final static String SHARE_MONITOR_TARGET_MIN = "share_motitor_target_min";
	public final static String SHARE_MONITOR_START_HOUR = "share_motitor_start_hour";
	public final static String SHARE_MONITOR_START_MIN = "share_motitor_start_min";
	public final static String SHARE_MONITOR_END_HOUR = "share_motitor_end_hour";
	public final static String SHARE_MONITOR_END_MIN = "share_motitor_end_min";

	// 闹钟设置
	// 周期 : 存储的是二进制数字0b1111111
	public final static String SHARE_WEEKDAY_1 = "share_weekday_1";
	public final static String SHARE_WEEKDAY_2 = "share_weekday_2";
	public final static String SHARE_WEEKDAY_3 = "share_weekday_3";
	// 时刻 ：
	public final static String SHARE_CLOCK_SETTIME_HOUR_1 = "share_clockSet_hour_1";
	public final static String SHARE_CLOCK_SETTIME_MIN_1 = "share_clockSet_min_1";
	public final static String SHARE_CLOCK_SETTIME_HOUR_2 = "share_clockSet_hour_2";
	public final static String SHARE_CLOCK_SETTIME_MIN_2 = "share_clockSet_min_2";
	public final static String SHARE_CLOCK_SETTIME_HOUR_3 = "share_clockSet_hour_3";
	public final static String SHARE_CLOCK_SETTIME_MIN_3 = "share_clockSet_min_3";
	// 开关：checkboxS
	public final static String SHARE_CLOCK_CHECKBOX_1 = "share_clock_cb_item_1";
	public final static String SHARE_CLOCK_CHECKBOX_2 = "share_clock_cb_item_2";
	public final static String SHARE_CLOCK_CHECKBOX_3 = "share_clock_cb_item_3";

	// 存贮地址
	/** 运动提醒开关 **/
	public static final String SHARE_ACTIVITY = "share_activity";
	/** 防丢开关 **/
	public static final String SHARE_NON = "share_non";
	/** 来电提醒开关 **/
	public static final String SHARE_INCOMING = "share_incoming";
	/** SignSet **/
	public static final String SHARE_SIGN_SET = "share_signset";
	/** 亮度 **/
	public static final String SHARE_BRIGHT = "share_bright";

	/** 默认间隔 **/
	public static final String DEFAULT_INTERVAL = "100";
	public static final String DEFAULT_START_HOUR = "07";
	public static final String DEFAULT_START_MIN = "00";
	public static final String DEFAULT_END_HOUR = "22";
	public static final String DEFAULT_END_MIN = "00";
	public static final int DEFAULT_REPEAT = 0b00000000;

	/** 写出协议 **/
	public static enum ProtocolWrite {
		/** 时间同步 **/
		UPDATE_TIME(0x11)
		/** 闹钟设置 **/
		, CLOCK(0x15)
		/** 运动提醒设置 **/
		, SPORT(0x19)
		/** 睡眠时段设置 **/
		, SLEEP(0x21)
		/** 来电提醒时段设置 **/
		, INCOMING(0x23)
		/** 读取睡眠检测设置 **/
		, RECORD_SLEEP(0x25)
		/** 拍照进入 **/
		, CAMERA_ENTER(0x31)
		/** 拍照退出 **/
		, CAMERA_EXIT(0x32)
		/** 防丢设置 开 **/
		, REMIND_LOST_ON(0x41)
		/** 防丢设置 关 **/
		, REMIND_LOST_OFF(0x42)
		/** 防丢设置 连接丢失 **/
		, REMIND_LOST_UNCONNECT(0x43)
		/** 防丢设置 恢复连接 **/
		, REMIND_LOST_RECONNECT(0x44)
		/** 查找手环 **/
		, SEARCH(0x45)
		/** 亮度设置 开 **/
		, BRIHT_ON(0x61)
		/** 亮度设置 关 **/
		, BRIHT_OFF(0x61)
		/** 读当天步数 **/
		, READ_STEP(0x71)
		/** 来电 **/
		, READ_INCOMMING(0x81)
		/** 已接来电 **/
		, READ_CALLED(0x82)
		/** 已接来电 **/
		, SIGNATURE(0x91)
		/** 关闭手环 **/
		, SHUTDOWN(0xFC);

		private int value;

		private ProtocolWrite(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/** 睡眠历史数据 **/
	public static enum RecordSleep {
		CANCEL(0x00), RECORD_TODAY(0x01), RECORD_ALL(0x02), RECORD_DELETE(0x5a);
		private int value;

		private RecordSleep(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/** 读入协议 **/
	public static enum ProtocolRead {
		CALLFAKER(0x05)
		/** 历史数据 **/
		, HISTORTY(0x25), SLEEP(0x26), PHONE(0x33), SERACH(0x43), STEP(0x71), INCOMMING_SLIENCE(0x80), INCOMMING_REJECT(
				0x81);
		private int value;

		private ProtocolRead(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	/**
	 * 取出存在本地的运动提醒数据, 转换成发送协议的 Protocol
	 * 
	 * @return
	 */
	public static SportRemindProtocol protocolDataForActivityRemindSync(Context context) {
		// 获取本地数据
		String interval = (String) SharedPreferenceUtil.get(context, SHARE_INTERVAL, DEFAULT_INTERVAL);
		String startHour = (String) SharedPreferenceUtil.get(context, SHARE_START_HOUR, DEFAULT_START_HOUR);
		String startMin = (String) SharedPreferenceUtil.get(context, SHARE_START_MIN, DEFAULT_START_MIN);
		String endHour = (String) SharedPreferenceUtil.get(context, SHARE_END_HOUR, DEFAULT_END_HOUR);
		String endMin = (String) SharedPreferenceUtil.get(context, SHARE_END_MIN, DEFAULT_END_MIN);
		int repeat = (int) SharedPreferenceUtil.get(context, SHARE_REPEAT, DEFAULT_REPEAT);
		String onoff = (boolean) SharedPreferenceUtil.get(context, SHARE_ACTIVITY, false) ? "01" : "00";
		// 初始化SportProtocol
		SportRemindProtocol sp = new SportRemindProtocol();
		sp.setEndHour(DataUtil.getStringByString(endHour));
		sp.setEndMin(DataUtil.getStringByString(endMin));
		sp.setInterval(DataUtil.getStringByString(interval));
		sp.setOnoff(onoff);
		sp.setRepeat(getRepeatValue(repeat));
		sp.setStartHour(DataUtil.getStringByString(startHour));
		sp.setStartMin(DataUtil.getStringByString(startMin));
		// log
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.i("I2WatchProtocol", "SportProtocol.protocol 	   ：" + SportRemindProtocol.protocol);
		Log.i("I2WatchProtocol", "SportProtocol.getOnoff 	   ：" + sp.getOnoff());
		Log.i("I2WatchProtocol", "SportProtocol.getInterval  ：" + sp.getInterval());
		Log.i("I2WatchProtocol", "SportProtocol.getRepeat 	   ：" + sp.getRepeat());
		Log.i("I2WatchProtocol", "SportProtocol.getStartHour ：" + sp.getStartHour());
		Log.i("I2WatchProtocol", "SportProtocol.getStartMin  ：" + sp.getStartMin());
		Log.i("I2WatchProtocol", "SportProtocol.getEndHour   ：" + sp.getEndHour());
		Log.i("I2WatchProtocol", "SportProtocol.getEndMin    ：" + sp.getEndMin());
		Log.v("I2WatchProtocol", "hexDataForActivityRemindSync : 本地运动提醒数据 ：" + sp.toString());
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return sp;
	};

	/**
	 * 取睡眠时段设置协议的 protocolData
	 * 
	 * @return
	 */
	public static SleepMonitorProtocol protocolDataForSleepPeriodSync(Context context) {
		// 获取本地数据
		String startHour = (String) SharedPreferenceUtil.get(context, SHARE_MONITOR_START_HOUR, DEFAULT_START_HOUR);
		String startMin = (String) SharedPreferenceUtil.get(context, SHARE_MONITOR_START_MIN, DEFAULT_START_MIN);
		String endHour = (String) SharedPreferenceUtil.get(context, SHARE_MONITOR_END_HOUR, DEFAULT_END_HOUR);
		String endMin = (String) SharedPreferenceUtil.get(context, SHARE_MONITOR_END_MIN, DEFAULT_END_MIN);
		// 初始化SportProtocol
		SleepMonitorProtocol sp = new SleepMonitorProtocol();
		sp.setEndHour(DataUtil.getStringByString(endHour));
		sp.setEndMin(DataUtil.getStringByString(endMin));
		sp.setStartHour(DataUtil.getStringByString(startHour));
		sp.setStartMin(DataUtil.getStringByString(startMin));
		// log
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.i("I2WatchProtocol", "SleepMonitorProtocol.protocol 	   ：" + SleepMonitorProtocol.protocol);
		Log.i("I2WatchProtocol", "SleepMonitorProtocol.getStartHour ：" + sp.getStartHour());
		Log.i("I2WatchProtocol", "SleepMonitorProtocol.getStartMin  ：" + sp.getStartMin());
		Log.i("I2WatchProtocol", "SleepMonitorProtocol.getEndHour   ：" + sp.getEndHour());
		Log.i("I2WatchProtocol", "SleepMonitorProtocol.getEndMin    ：" + sp.getEndMin());
		Log.v("I2WatchProtocol", "hexDataForSleepPeriodSync : 睡眠时段数据 ：" + sp.toString());
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return sp;
	}

	/**
	 * 取出存在本地的闹钟数据, 转换成发送协议的 protocolData
	 * 
	 * @return
	 */
	public static ClockSetProtocol protocolDataForClockSync(Context context) {
		// 获取本地数据
		boolean onoffFirst = (boolean) SharedPreferenceUtil.get(context, SHARE_CLOCK_CHECKBOX_1, false);
		int repeatFirst = (int) SharedPreferenceUtil.get(context, SHARE_WEEKDAY_1, 0b00000000);
		String hourFirst = (String) SharedPreferenceUtil.get(context, SHARE_CLOCK_SETTIME_HOUR_1, DEFAULT_END_HOUR);
		String minFirst = (String) SharedPreferenceUtil.get(context, SHARE_CLOCK_SETTIME_MIN_1, DEFAULT_END_MIN);

		boolean onoffSecond = (boolean) SharedPreferenceUtil.get(context, SHARE_CLOCK_CHECKBOX_2, false);
		int repeatSecond = (int) SharedPreferenceUtil.get(context, SHARE_WEEKDAY_2, 0b00000000);
		String hourSecond = (String) SharedPreferenceUtil.get(context, SHARE_CLOCK_SETTIME_HOUR_2, DEFAULT_END_HOUR);
		String minSecond = (String) SharedPreferenceUtil.get(context, SHARE_CLOCK_SETTIME_MIN_2, DEFAULT_END_MIN);

		boolean onoffThrid = (boolean) SharedPreferenceUtil.get(context, SHARE_CLOCK_CHECKBOX_3, false);
		int repeatThrid = (int) SharedPreferenceUtil.get(context, SHARE_WEEKDAY_3, 0b00000000);
		String hourThrid = (String) SharedPreferenceUtil.get(context, SHARE_CLOCK_SETTIME_HOUR_3, DEFAULT_END_HOUR);
		String minThrid = (String) SharedPreferenceUtil.get(context, SHARE_CLOCK_SETTIME_MIN_3, DEFAULT_END_MIN);

		// 初始化SportProtocol
		ClockSetProtocol sp = new ClockSetProtocol();
		sp.setOnoffFirst(onoffFirst ? "01" : "00");
		sp.setRepeatFirst(getRepeatValue(repeatFirst));
		sp.setHourFirst(DataUtil.getStringByString(hourFirst));
		sp.setMinFirst(DataUtil.getStringByString(minFirst));

		sp.setOnoffSecond(onoffSecond ? "01" : "00");
		sp.setRepeatSecond(getRepeatValue(repeatSecond));
		sp.setHourSecond(DataUtil.getStringByString(hourSecond));
		sp.setMinSecond(DataUtil.getStringByString(minSecond));

		sp.setOnoffThrid(onoffThrid ? "01" : "00");
		sp.setRepeatThrid(getRepeatValue(repeatThrid));
		sp.setHourThrid(DataUtil.getStringByString(hourThrid));
		sp.setMinThrid(DataUtil.getStringByString(minThrid));

		// log
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.i("I2WatchProtocol", "ClockSetProtocol.protocol 	   ：" + ClockSetProtocol.protocol);
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getOnoffFirst ：" + sp.getOnoffFirst());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getRepeatFirst  ：" + sp.getRepeatFirst());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getHourFirst   ：" + sp.getHourFirst());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getMinFirst    ：" + sp.getMinFirst());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getOnoffSecond ：" + sp.getOnoffSecond());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getRepeatSecond  ：" + sp.getRepeatSecond());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getHourSecond   ：" + sp.getHourSecond());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getMinSecond    ：" + sp.getMinSecond());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getOnoffThrid ：" + sp.getOnoffThrid());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getRepeatThrid  ：" + sp.getRepeatThrid());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getHourThrid   ：" + sp.getHourThrid());
		Log.i("ClockSetProtocol", "SleepMonitorProtocol.getMinThrid    ：" + sp.getMinThrid());
		Log.v("I2WatchProtocol", "hexDataForClockSync : 闹钟设置数据 ：" + sp.toString());
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return sp;
	}

	/**
	 * 取出存在本地的运动提醒数据, 转换成发送协议的 hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForActivityRemindSync(Context context) {
		return protocolDataForActivityRemindSync(context).toByte();
	}

	/**
	 * 取出存在本地的闹钟数据, 转换成发送协议的 hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForClockSync(Context context) {
		return protocolDataForClockSync(context).toByte();
	}

	/**
	 * 取睡眠时段设置协议的 hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForSleepPeriodSync(Context context) {
		return protocolDataForSleepPeriodSync(context).toByte();
	}

	/**
	 * 取更新拍照状态的 hexData
	 * 
	 * @param 拍照的状态
	 *            , PhotographStateEnter 与 PhotographStateOut (0:退出 1：进入)
	 * @return
	 */
	public static byte[] hexDataForUpdatePhotographState(int state) {
		String hex = "00";
		if (state == 0) {
			hex = "32";
		} else if (state == 1) {
			hex = "31";
		}
		byte[] hexDate = DataUtil.getBytesByString(hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.v("I2WatchProtocol", "hexDataForUpdatePhotographState : 相机数据 ：" + hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return hexDate;
	}

	/**
	 * 防丢的状态改变时, 发送的数据, peripheral.RSSI 超过限定时 ON, 然后恢复时 OFF
	 * 
	 * @param
	 * @return
	 */
	public static byte[] hexDataForAntiLostSetSync(int state) {
		String hex = "00";
		if (state == 0) {// 关
			hex = "43";
		} else if (state == 1) {// 恢复
			hex = "44";
		}
		byte[] hexDate = DataUtil.getBytesByString(hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.v("I2WatchProtocol", "hexDataForAntiLostSetSync : 丢失状态改变数据 ：" + hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return hexDate;
	}

	/**
	 * 防丢开关的协议 hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForLostOnoffI2Watch(Context context) {
		boolean nf = (boolean) SharedPreferenceUtil.get(context, I2WatchProtocolData.SHARE_NON, false);
		String hex = nf ? "41" : "42";
		byte[] hexDate = DataUtil.getBytesByString(hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.v("I2WatchProtocol", "hexDataForLostOnoffI2Watch : 防丢开光数据 ：" + hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return hexDate;
	}

	/**
	 * 开始查找手环, 在关闭前应每 6 秒执行一次, 建议用 NSTimer 来实现 hexDataForSearchI2Watch
	 * 
	 * @return
	 */
	public static byte[] hexDataForSearchI2Watch() {
		String hex = "45";
		byte[] hexDate = DataUtil.getBytesByString(hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.v("I2WatchProtocol", "hexDataForSearchI2Watch : 开始查找手环数据 ：" + hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return  hexDate;
	}

	/**
	 * 同步亮度的设置, 在每次改变量度的时候发送即可
	 * 
	 * @return
	 */
	public static byte[] hexDataForUpdateBrightness(Context context) {
		String hex = "61";
		String bright = (String) SharedPreferenceUtil.get(context, SHARE_BRIGHT, "01");
		hex += DataUtil.getStringByString(bright);
		
		byte[] hexDate = DataUtil.getBytesByString(hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.v("I2WatchProtocol", "hexDataForUpdateBrightness : 改变亮度数据 ：" + hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return  hexDate;
	}

	/**
	 * 关闭手环的协议 hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForCloseI2Watch() {
		String hex = "FC";
		byte[] hexDate = DataUtil.getBytesByString(hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		Log.v("I2WatchProtocol", "hexDataForCloseI2Watch : 关闭手环数据 ：" + hex);
		Log.i("I2WatchProtocol", "----------------------------------------------------------");
		return  hexDate;
	}

	/**
	 * 取同步时间发送协议的 hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForTimeSync() {

		return null;
	}

	/**
	 * 来电提醒时段设置协议的 hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForCallingAlarmPeriodSync() {

		return null;
	}

	/**
	 * 取历史数据的 hexData
	 * 
	 * @param historyType
	 *            历史记录类型, 分为 GetHistoryTypeToday 和 GetHistoryTypeAll
	 * @param startIndex
	 *            (仅用于 GetHistoryTypeToday) 历史记录开始时间点的 index, 0 ~ 359 之间
	 * @return
	 */
	public static byte[] hexDataForGetHistoryType(int historyType, int startIndex) {

		return null;
	}

	/**
	 * 取今天的计步和卡路里总计数据
	 * 
	 * @param state
	 * @return
	 */
	public static byte[] hexDataForGetTodayTotalStepAndCal(int state) {

		return null;
	}

	/**
	 * 有新来电, 或者更新未接来电个数
	 * 
	 * @param
	 * @return
	 */
	public static byte[] hexDataForHasCallingCount(int count) {

		return null;
	}

	/**
	 * 从本地取得个性签名并转换成协议需要的hexData
	 * 
	 * @return
	 */
	public static byte[] hexDataForSignatureSync() {

		return null;
	}

	/**
	 * 获取星期重复 十进制int类型 --> 二进制字符串
	 * 
	 * @param repeat
	 * @return
	 */
	private static String getRepeatValue(int repeat) {
		String value = Integer.toBinaryString(repeat);
		switch (value.length()) {
		case 1:
			value = "0000000" + value;
			break;
		case 2:
			value = "000000" + value;
			break;
		case 3:
			value = "00000" + value;
			break;
		case 4:
			value = "0000" + value;
			break;
		case 5:
			value = "000" + value;
			break;
		case 6:
			value = "00" + value;
			break;
		case 7:
			value = "0" + value;
			break;
		default:
			break;
		}
		return value;
	}

}
