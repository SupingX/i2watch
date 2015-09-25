package com.suping.i2_watch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

public class DateUtil {

	public static final int SECONDS_IN_DAY = 60 * 60 * 24;
	public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

	public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY && toDay(ms1) == toDay(ms2);
	}

	/**
	 * 获取当前手机的时间格式
	 * 
	 * @param context
	 * @return 24小时制/12小时制
	 */
	public static int getTimeFormat(Context context) {
//		String timeFormat = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.TIME_12_24);
//		Log.e("DateUtil", "timeFormat :" + timeFormat);
//		if (timeFormat.equals("24")) {
//			return 0;
//		} else if (timeFormat.equals("12")) {
//			return 1;
//		}
//		return -1;
		boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
		if (is24HourFormat) {
			return 0;
		}else{
			return 1;
		}
	}

	private static long toDay(long millis) {
		return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public static boolean isSameDay(String date1, Date date2) {
		String dateStr2 = sdf.format(date2);
		return date1.equals(dateStr2);
	}

	public static boolean isSameMonth(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		return c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

	}

	public static String dateToString(Date date) {
		return sdf.format(date);
	}

	public static String dateToHourStr(long date) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date);
		SimpleDateFormat s = new SimpleDateFormat("mm:ss");
		return s.format(c.getTime());
	}

	public static Date stringToDate(String date) {
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static int getDayOfMonth(Date date) {
		// 获取一个月的天
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		;
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static int getDayOfMonth(String date) {
		// 获取一个月的天
		return getDayOfMonth(stringToDate(date));
	}

	// 当前的前DIFF天
	public static Date getDateOfDiffDay(Date date, int diff) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		;
		c.add(Calendar.DATE, diff);
		return c.getTime();
	}

	// 当前的前DIFF月
	public static Date getDateOfDiffMonth(Date date, int diff) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		;
		c.add(Calendar.MONTH, diff);
		Log.v("", "偏移后的月份 ：" + c.get(Calendar.MONTH));
		return c.getTime();
	}

	// 当前月的所有天数
	public static int getMonthMaxDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getMaximum(Calendar.MONTH);

	}

	public static String dateToString(Date date, String sdf) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(sdf);
		return sdf1.format(date);
	}

	public static Date stringToDate(String date, String sdf) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(sdf);
		try {
			return sdf1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据传入的毫秒数，转换成MM:SS时间串形式返回
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String getMMSS(long timeMillis) {
		StringBuffer sb = new StringBuffer();
		long mm = (timeMillis % (1000 * 60 * 60)) / 1000 / 60;
		long ss = ((timeMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

		if (mm < 0) {
			sb.append("00:");
		} else if (mm < 10) {
			sb.append("0" + mm + ":");
		} else {
			sb.append(mm + ":");
		}
		if (ss < 0) {
			sb.append("00");
		} else if (ss < 10) {
			sb.append("0" + ss);
		} else {
			sb.append(ss);
		}
		return sb.toString();
	}

	/**
	 * 根据传入的耗秒数, 转换成为HH:MM:SS的字符串返回
	 */
	public static String getHHMMSS(long time) {
		String hhmmss = "00:00:00";
		StringBuffer bf = new StringBuffer();
		if (time == 0) {
			return hhmmss;
		}
		long hh = time / 1000 / 60 / 60;
		long mm = (time % (1000 * 60 * 60)) / 1000 / 60;
		long ss = ((time % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

		if (hh < 0) {
			bf.append("00:");
		} else if (hh < 10) {
			bf.append("0" + hh + ":");
		} else {
			bf.append(hh + ":");
		}
		if (mm < 0) {
			bf.append("00:");
		} else if (mm < 10) {
			bf.append("0" + mm + ":");
		} else {
			bf.append(mm + ":");
		}
		if (ss < 0) {
			bf.append("00");
		} else if (ss < 10) {
			bf.append("0" + ss);
		} else {
			bf.append(ss);
		}
		hhmmss = bf.toString();
		System.out.println(hhmmss);
		return hhmmss;
	}

	/**
	 * 比较2个时间的大小
	 */
	public static boolean compareTime(Date time1, Date time2) {
		return time1.before(time2);
	}

	public static int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH);
	}

	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}

}
