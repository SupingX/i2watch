package com.suping.i2_watch.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.DateUtil;

public class LitePalManager {
	private static LitePalManager litePal;

	public static LitePalManager instance() {
		if (litePal == null) {
			litePal = new LitePalManager();
		}
		return litePal;
	}

	private LitePalManager() {

	}

	public History isExitHistory(History history) {
		List<History> historyFind = DataSupport.where("year=? and month =? and day = ? and hour = ? ", history.getYear(), history.getMonth(), history.getDay(), history.getHour()).find(History.class);
		if (historyFind != null && historyFind.size() > 0) {
			return historyFind.get(0);
		} else {
			return null;
		}
	}

	public void saveHistory(History history) {
		if (history != null) {
			History exitHistory = isExitHistory(history);
			if (exitHistory != null) {
				//Log.e("", "============================存在");
				ContentValues values = new ContentValues();
				values.put("sleepAwak_1", history.getSleepAwak_1());
				values.put("sleepAwak_2", history.getSleepAwak_2());
				values.put("sleepAwak_3", history.getSleepAwak_3());

				values.put("sleepDeep_1", history.getSleepDeep_1());
				values.put("sleepDeep_2", history.getSleepDeep_2());
				values.put("sleepDeep_3", history.getSleepDeep_3());

				values.put("sleepLight_1", history.getSleepLight_1());
				values.put("sleepLight_2", history.getSleepLight_2());
				values.put("sleepLight_3", history.getSleepLight_3());

				values.put("sleepOneself_1", history.getSleepOneself_1());
				values.put("sleepOneself_2", history.getSleepOneself_2());
				values.put("sleepOneself_3", history.getSleepOneself_3());

				values.put("sportTime_1", history.getSportTime_1());
				values.put("sportTime_2", history.getSportTime_2());
				values.put("sportTime_3", history.getSportTime_3());

				values.put("sportStep_1", history.getSportStep_1());
				values.put("sportStep_2", history.getSportStep_2());
				values.put("sportStep_3", history.getSportStep_3());

				DataSupport.update(History.class, values, exitHistory.getId());
			} else {
				//Log.e("", "============================bu存在");
				history.save();
			}
		}
	}

	/**
	 * 
	 * @param date
	 * @return [步数，时间，深睡，浅睡，清醒]
	 */
	public int[] getHistoryForCount(Date date) {
		int dataSleep[] = new int[5];
		String dateToString = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateToString.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];

		for (int i = 0; i < 24; i++) {
			String hour = String.valueOf(i).trim();
			List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(History.class);
			if (sleepList != null && sleepList.size() > 0) {
				// 获得每个小时的数据
				History history = sleepList.get(0);
				// 0-20分钟
				int sleepDeep_1 = history.getSleepDeep_1();
				int sleepLight_1 = history.getSleepLight_1();
				int sleepAwak_1 = history.getSleepAwak_1();
				int sportStep_1 = history.getSportStep_1();
				int sportTime_1 = history.getSportTime_1();

				// 20-40分钟
				int sleepDeep_2 = history.getSleepDeep_2();
				int sleepLight_2 = history.getSleepLight_2();
				int sleepAwak_2 = history.getSleepAwak_2();
				int sportStep_2 = history.getSportStep_2();
				int sportTime_2 = history.getSportTime_2();
				// 40-60分钟
				int sleepDeep_3 = history.getSleepDeep_3();
				int sleepLight_3 = history.getSleepLight_3();
				int sleepAwak_3 = history.getSleepAwak_3();
				int sportStep_3 = history.getSportStep_3();
				int sportTime_3 = history.getSportTime_3();
				// 统计数据
				dataSleep[0] += (sportStep_1 + sportStep_2 + sportStep_3);
				dataSleep[1] += (sportTime_1 + sportTime_2 + sportTime_3);
				dataSleep[2] += (sleepDeep_1 + sleepDeep_2 + sleepDeep_3);
				dataSleep[3] += (sleepLight_1 + sleepLight_2 + sleepLight_3);
				dataSleep[4] += (sleepAwak_1 + sleepAwak_2 + sleepAwak_3);
			} else {
			}
		}
		return dataSleep;
	}

	public float getDeepSleepForDay(Date date) {
		float deep = 0;
		int[] historyForCount = getHistoryForCount(date);
		deep = historyForCount[2];
		return deep;
	}

	public float getLightSleepForDay(Date date) {
		float light = 0;
		int[] historyForCount = getHistoryForCount(date);
		light = historyForCount[2];
		return light;
	}

	/**
	 * 获取每一天的总睡眠数据
	 */
	public float getSleepTotalForDay(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int sum1 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepDeep_1", Integer.class);
		int sum2 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepDeep_2", Integer.class);
		int sum3 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepDeep_3", Integer.class);
		int sum4 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepLight_1", Integer.class);
		int sum5 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepLight_2", Integer.class);
		int sum6 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepLight_3", Integer.class);
		return sum1 + sum2 + sum3 + sum4 + sum5 + sum6;
	}

	/**
	 * 获取每一天的浅睡眠数据
	 */
	public float getSleepLightForDay(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int sum4 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepLight_1", Integer.class);
		int sum5 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepLight_2", Integer.class);
		int sum6 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepLight_3", Integer.class);
		return sum4 + sum5 + sum6;
	}

	/**
	 * 获取每一天的浅睡眠数据
	 */
	public float getSleepAwakForDay(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int sum4 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepAwak_1", Integer.class);
		int sum5 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepAwak_2", Integer.class);
		int sum6 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepAwak_3", Integer.class);
		return sum4 + sum5 + sum6;
	}

	/**
	 * 获取每一天的深睡数据
	 */
	public float getSleepDeepForDay(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int sum1 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepDeep_1", Integer.class);
		int sum2 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepDeep_2", Integer.class);
		int sum3 = DataSupport.where("year=? and month =? and day = ? ", year, month, day).sum(History.class, "sleepDeep_3", Integer.class);
		return sum1 + sum2 + sum3;
	}

	/**
	 * 获取每一周的浅睡数据
	 */
	public float getSleepLightForWeekday(Date date) {
		Date date1 = DateUtil.getDateOfDiffDay(date, -1);
		Date date2 = DateUtil.getDateOfDiffDay(date, -2);
		Date date3 = DateUtil.getDateOfDiffDay(date, -3);
		Date date4 = DateUtil.getDateOfDiffDay(date, -4);
		Date date5 = DateUtil.getDateOfDiffDay(date, -5);
		Date date6 = DateUtil.getDateOfDiffDay(date, -6);
		return getSleepLightForDay(date6) + getSleepLightForDay(date5) + getSleepLightForDay(date4) + getSleepLightForDay(date3) + getSleepLightForDay(date2) + getSleepLightForDay(date1)
				+ getSleepLightForDay(date);
	}

	/**
	 * 获取每一周的深睡眠数据
	 */
	public float getSleepDeepForWeekday(Date date) {
		Date date1 = DateUtil.getDateOfDiffDay(date, -1);
		Date date2 = DateUtil.getDateOfDiffDay(date, -2);
		Date date3 = DateUtil.getDateOfDiffDay(date, -3);
		Date date4 = DateUtil.getDateOfDiffDay(date, -4);
		Date date5 = DateUtil.getDateOfDiffDay(date, -5);
		Date date6 = DateUtil.getDateOfDiffDay(date, -6);
		return getSleepDeepForDay(date6) + getSleepDeepForDay(date5) + getSleepDeepForDay(date4) + getSleepDeepForDay(date3) + getSleepDeepForDay(date2) + getSleepDeepForDay(date1)
				+ getSleepDeepForDay(date);
	}

	/**
	 * 获取每一月的总睡眠数据
	 */
	public float getSleepTotalForMonth(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		int sum1 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepDeep_1", Integer.class);
		int sum2 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepDeep_2", Integer.class);
		int sum3 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepDeep_3", Integer.class);
		int sum4 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepLight_1", Integer.class);
		int sum5 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepLight_2", Integer.class);
		int sum6 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepLight_3", Integer.class);
		return sum1 + sum2 + sum3 + sum4 + sum5 + sum6;
	}

	/**
	 * 获取每一月的深睡眠数据
	 */
	public float getSleepDeepForMonth(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		int sum1 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepDeep_1", Integer.class);
		int sum2 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepDeep_2", Integer.class);
		int sum3 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepDeep_3", Integer.class);
		return sum1 + sum2 + sum3;
	}

	/**
	 * 获取每一月的浅睡眠数据
	 */
	public float getSleepLightForMonth(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		int sum4 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepLight_1", Integer.class);
		int sum5 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepLight_2", Integer.class);
		int sum6 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sleepLight_3", Integer.class);
		return sum4 + sum5 + sum6;
	}

	public float getSleepDeepForHour(Date date, String startHour, String endHour) {
		float deep = 0;
		String[] split = DateUtil.dateToString(date, "yyyy-MM-dd").split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int sum4 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, startHour, endHour + 1).sum(History.class, "sleepDeep_1", Integer.class);
		int sum5 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, startHour, endHour + 1).sum(History.class, "sleepDeep_2", Integer.class);
		int sum6 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, startHour, endHour + 1).sum(History.class, "sleepDeep_3", Integer.class);
		deep = sum4 + sum5 + sum6;
		return deep;
	}

	public float getSleepLightForHour(Date date, String startHour, String endHour) {
		float deep = 0;
		String[] split = DateUtil.dateToString(date, "yyyy-MM-dd").split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int sum4 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, startHour, endHour + 1).sum(History.class, "sleepLight_1", Integer.class);
		int sum5 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, startHour, endHour + 1).sum(History.class, "sleepLight_2", Integer.class);
		int sum6 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, startHour, endHour + 1).sum(History.class, "sleepLight_3", Integer.class);
		deep = sum4 + sum5 + sum6;
		return deep;
	}

	public float getSleepAwakForHour(Date date, String startHour, String endHour) {
		float deep = 0;
		String[] split = DateUtil.dateToString(date, "yyyy-MM-dd").split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int start = Integer.valueOf(startHour);
		int end = Integer.valueOf(endHour);
		int sum4 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, String.valueOf(start), String.valueOf(end)).sum(History.class, "sleepAwak_1",
				Integer.class);
		int sum5 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, String.valueOf(start), String.valueOf(end)).sum(History.class, "sleepAwak_2",
				Integer.class);
		int sum6 = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, String.valueOf(start), String.valueOf(end)).sum(History.class, "sleepAwak_3",
				Integer.class);
		deep = sum4 + sum5 + sum6;
		return deep;
	}

	public List<History> getHistory(Date date, String startHour, String endHour) {
		String[] split = DateUtil.dateToString(date, "yyyy-MM-dd").split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		List<History> find = DataSupport.where("year=? and month =? and day=? and hour>=? and hour<= ?", year, month, day, startHour, endHour + 1).find(History.class);
		return find;
	}

	private String[] getStringDate(Date date) {
		String dataStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dataStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		return new String[] { year, month, day };
	}

	public int getSizeForSetting(String startHour, String endHour) {
		int start = Integer.valueOf(startHour);
		int end = Integer.valueOf(endHour);
		if (start >= end) {
			return 24 - start + end + 1;
		} else {
			return end - start + 1;
		}
	}

	/**
	 * 获取睡眠时间段之间的睡眠数据
	 */
	public float[] getSleepHistoryForSetting(Date date, String startHour, String endHour) {
		String dataStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dataStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int start = Integer.valueOf(startHour);
		int end = Integer.valueOf(endHour);
		if (start >= end) {// 跨天
			Date yesteday = DateUtil.getDateOfDiffDay(date, -1);// 昨天
			int size = 24 - start + end + 1;
			float data[] = new float[size*3];
			String[] yesDate = getStringDate(yesteday);
			for (int i = 0; i < size; i++) {

			
				if (i < (24 - start-1)) {// 昨天
					String hour = String.valueOf(i+start).trim();
					List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour = ? ", yesDate[0], yesDate[1], yesDate[2], hour).find(History.class);
					if (sleepList != null && sleepList.size() > 0) {
						// 获得每个小时的数据
						History sleep = sleepList.get(0);
						//Log.e("", "今天的数据sleep 时刻：" + i + "-->" + sleep);
						// 0-20分钟
						data[3 * i] = getValue(sleep, 1);
						// 20-40分钟
						data[3 * i + 1] = getValue(sleep, 2);
						// 40-60分钟
						data[3 * i + 2] = getValue(sleep, 3);
						// 统计数据
					}
				} else {// 今天
					String hour = String.valueOf(i-(24-start)).trim();
					List<History> todayList = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, String.valueOf(hour)).find(History.class);
					if (todayList != null && todayList.size() > 0) {
						// 获得每个小时的数据
						History sleep = todayList.get(0);
						//Log.e("", "今天的数据sleep 时刻：" + i + "-->" + sleep);
						// 0-20分钟
						data[3 * i] = getValue(sleep, 1);
						// 20-40分钟
						data[3 * i + 1] = getValue(sleep, 2);
						// 40-60分钟
						data[3 * i + 2] = getValue(sleep, 3);
						// 统计数据
					}
				}
			}
			return data;
		} else {// 同一天
			int sizeSameday = end - start + 1;
			float dataSame[] = new float[sizeSameday*3];
			for (int i = 0; i < sizeSameday; i++) {

				String hour = String.valueOf(start+i).trim();
				List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(History.class);
				if (sleepList != null && sleepList.size() > 0) {
					// 获得每个小时的数据
					History sleep = sleepList.get(0);
					//Log.e("", "今天的数据sleep 时刻：" + i + "-->" + sleep);
					// 0-20分钟
					dataSame[3 * i] = getValue(sleep, 1);
					// 20-40分钟
					dataSame[3 * i + 1] = getValue(sleep, 2);
					// 40-60分钟
					dataSame[3 * i + 2] = getValue(sleep, 3);
					// 统计数据
				}
			}
			return dataSame;
		}
	}

	/**
	 * 获取睡眠时间段之间的睡眠数据
	 */
	public float[] getSleepInfoForSetting(Date date, String startHour, String endHour) {
		float deep = 0;
		float light = 0;
		float awak = 0;

		int start = Integer.valueOf(startHour);
		int end = Integer.valueOf(endHour);
		//Log.e("haha", "start :" + start);
		//Log.e("haha", "end :" + end);
		// 21-6dian

		if (start >= end) {// 跨天
			Date yesteday = DateUtil.getDateOfDiffDay(date, -1);// 昨天
			// deep
			float yesterdayDeep = getSleepDeepForHour(yesteday, startHour, String.valueOf(23));
			//Log.e("haha", "yesterdayDeep :" + yesterdayDeep);
			float todayDeep = getSleepDeepForHour(date, String.valueOf(0), String.valueOf(end));
			//Log.e("haha", "todayDeep :" + todayDeep);
			deep = yesterdayDeep + todayDeep;
			// light
			float yesterdayLight = getSleepLightForHour(yesteday, startHour, String.valueOf(23));
			//Log.e("", "yesterdayLight :" + yesterdayLight);
			float todayLight = getSleepLightForHour(date, String.valueOf(0), String.valueOf(end));
			//Log.e("", "todayLight :" + todayLight);
			light = yesterdayLight + todayLight;
			// awak
			float yesterdayAwak = getSleepAwakForHour(yesteday, startHour, String.valueOf(23));
			float todayAwak = getSleepAwakForHour(date, String.valueOf(0), String.valueOf(end));
			awak = yesterdayAwak + todayAwak;

		} else {// 同一天
			deep = getSleepDeepForHour(date, startHour, endHour);
			light = getSleepLightForHour(date, startHour, endHour);
			awak = getSleepAwakForHour(date, startHour, endHour);
		}
		return new float[] { deep, light, awak };
	}

	/**
	 * 一月的数字下标
	 * 
	 * @param date
	 * @return
	 */
	public String[] getSleepMonthTexts(Date date, Context context) {
		String sdf1 = "dd" + context.getString(R.string.unit_day);
		String sdf2 = "MM" + context.getString(R.string.unit_month) + "dd" + context.getString(R.string.unit_day);

		Date week1 = DateUtil.getDateOfDiffDay(date, -6);
		Date week2 = DateUtil.getDateOfDiffDay(week1, -6);
		Date week3 = DateUtil.getDateOfDiffDay(week2, -6);
		Date week4 = DateUtil.getDateOfDiffDay(week3, -6);
		String week0Str = DateUtil.dateToString(date, sdf1);
		String week1Str = DateUtil.dateToString(week1, sdf1);
		String week2Str = DateUtil.dateToString(week2, sdf1);
		String week3Str = DateUtil.dateToString(week3, sdf1);
		String week4Str = DateUtil.dateToString(week4, sdf2);
		return new String[] { week4Str, week3Str, week2Str, week1Str, week0Str };
	}

	/**
	 * 一年的数字下标
	 * 
	 * @param date
	 * @return
	 */
	public String[] getSleepYearTexts(Date date, Context context) {
		String sdf = "MM" + context.getString(R.string.unit_month);
		Date month1 = DateUtil.getDateOfDiffMonth(date, -1);
		Date month2 = DateUtil.getDateOfDiffMonth(date, -2);
		Date month3 = DateUtil.getDateOfDiffMonth(date, -3);
		Date month4 = DateUtil.getDateOfDiffMonth(date, -4);
		Date month5 = DateUtil.getDateOfDiffMonth(date, -5);
		Date month6 = DateUtil.getDateOfDiffMonth(date, -6);
		Date month7 = DateUtil.getDateOfDiffMonth(date, -7);
		Date month8 = DateUtil.getDateOfDiffMonth(date, -8);
		Date month9 = DateUtil.getDateOfDiffMonth(date, -9);
		String month0Str = DateUtil.dateToString(date, sdf);
		String month1Str = DateUtil.dateToString(month1, sdf);
		String month2Str = DateUtil.dateToString(month2, sdf);
		String month3Str = DateUtil.dateToString(month3, sdf);
		String month4Str = DateUtil.dateToString(month4, sdf);
		String month5Str = DateUtil.dateToString(month5, sdf);
		String month6Str = DateUtil.dateToString(month6, sdf);
		String month7Str = DateUtil.dateToString(month7, sdf);
		String month8Str = DateUtil.dateToString(month8, sdf);
		String month9Str = DateUtil.dateToString(month9, sdf);
		return new String[] { month9Str, month8Str, month7Str, month6Str, month5Str, month4Str, month3Str, month2Str, month1Str, month0Str };
	}

	/**
	 * 一周的数字下标
	 * 
	 * @param date
	 * @return
	 */
	public String[] getSleepWeekDayTexts(Date date, Context context) {
		String sdf1 = "dd" + context.getString(R.string.unit_day);
		String sdf2 = "MM" + context.getString(R.string.unit_month) + "dd" + context.getString(R.string.unit_day);
		Date date1 = DateUtil.getDateOfDiffDay(date, -1);
		Date date2 = DateUtil.getDateOfDiffDay(date, -2);
		Date date3 = DateUtil.getDateOfDiffDay(date, -3);
		Date date4 = DateUtil.getDateOfDiffDay(date, -4);
		Date date5 = DateUtil.getDateOfDiffDay(date, -5);
		Date date6 = DateUtil.getDateOfDiffDay(date, -6);
		String dateStr6 = DateUtil.dateToString(date6, sdf2);
		String dateStr5 = DateUtil.dateToString(date5, sdf1);
		String dateStr4 = DateUtil.dateToString(date4, sdf1);
		String dateStr3 = DateUtil.dateToString(date3, sdf1);
		String dateStr2 = DateUtil.dateToString(date2, sdf1);
		String dateStr1 = DateUtil.dateToString(date1, sdf1);
		String dateStr0 = DateUtil.dateToString(date, sdf1);
		return new String[] { dateStr6, dateStr5, dateStr4, dateStr3, dateStr2, dateStr1, dateStr0 };
	}

	private float getTotal(float[] sleepWeekDayDatas) {
		float total = 0;
		for (int i = 0; i < sleepWeekDayDatas.length; i++) {
			total += sleepWeekDayDatas[i];
		}
		return total;
	}

	/**
	 * 获得每个时刻每个的点的计步数量 72个数据
	 * 
	 * @param date
	 * @return
	 */
	public int[] getDataForSport(String date) {
		String[] split = date.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int steps[] = new int[72];
		for (int i = 0; i < 24; i++) {
			String hour = String.valueOf(i).trim();
			List<History> sports = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(History.class);
			if (sports != null && sports.size() > 0) {
				History sport = sports.get(0);
				steps[3 * i] = sport.getSportStep_1();
				steps[3 * i + 1] = sport.getSportStep_2();
				steps[3 * i + 2] = sport.getSportStep_3();
			} else {
//				L.e("数据库无运动数据");
			}
		}
		return steps;
	}

	/**
	 * 获得每个时刻 计步数量
	 * 
	 * @param date
	 * @return
	 */
	public float[] getStepForTime(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		float steps[] = new float[24];
		for (int i = 0; i < 24; i++) {
			String hour = String.valueOf(i).trim();// 得到每个小时点
			List<History> sports = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(History.class);
			if (sports != null && sports.size() > 0) {
				History sport = sports.get(0);
				steps[i] = (float) (sport.getSportStep_1() + sport.getSportStep_2() + sport.getSportStep_3());
			} else {
//				L.e(dateStr + "时刻 :" + hour + "数据库无运动数据");
			}

		}
		return steps;
	}

	/**
	 * 获取每一天的总步数
	 */
	public float getStepForDay(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int sum1 = DataSupport.where("year=? and month =? and day = ?  ", year, month, day).sum(History.class, "sportStep_1", Integer.class);
		int sum2 = DataSupport.where("year=? and month =? and day = ?  ", year, month, day).sum(History.class, "sportStep_2", Integer.class);
		int sum3 = DataSupport.where("year=? and month =? and day = ?  ", year, month, day).sum(History.class, "sportStep_3", Integer.class);
		return sum1 + sum2 + sum3;
	}

	/**
	 * 获得这一天前6天总的步数
	 * 
	 * @param date
	 * @return
	 */
	public float getStepForWeekDay(Date date) {
		float total = 0;

		Date date1 = DateUtil.getDateOfDiffDay(date, -1);
		Date date2 = DateUtil.getDateOfDiffDay(date, -2);
		Date date3 = DateUtil.getDateOfDiffDay(date, -3);
		Date date4 = DateUtil.getDateOfDiffDay(date, -4);
		Date date5 = DateUtil.getDateOfDiffDay(date, -5);
		Date date6 = DateUtil.getDateOfDiffDay(date, -6);

		total += getStepForDay(date6);
		total += getStepForDay(date5);
		total += getStepForDay(date4);
		total += getStepForDay(date3);
		total += getStepForDay(date2);
		total += getStepForDay(date1);
		total += getStepForDay(date);

		return total;
	}

	public float getStepForMonth(Date date) {
		String dateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dateStr.split("-");
		String year = split[0];
		String month = split[1];
		int sum1 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sportStep_1", Integer.class);
		int sum2 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sportStep_2", Integer.class);
		int sum3 = DataSupport.where("year=? and month =?", year, month).sum(History.class, "sportStep_3", Integer.class);
		return sum1 + sum2 + sum3;

	}

	/**
	 * 获得每个时刻每个的点的睡眠数量 72个数据
	 * 
	 * @param date
	 * @return
	 */
	public float[] getDataForSleep(Date date) {
		String dataStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dataStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		float sleeps[] = new float[72];
		for (int i = 0; i < 24; i++) {
			String hour = String.valueOf(i).trim();
			List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(History.class);
			if (sleepList != null && sleepList.size() > 0) {
				// 获得每个小时的数据
				History sleep = sleepList.get(0);
				//Log.e("", "今天的数据sleep 时刻：" + i + "-->" + sleep);
				// 0-20分钟
				sleeps[3 * i] = getValue(sleep, 1);
				// 20-40分钟
				sleeps[3 * i + 1] = getValue(sleep, 2);
				// 40-60分钟
				sleeps[3 * i + 2] = getValue(sleep, 3);
				// 统计数据
			} else {
//				L.e("数据库无睡眠数据");
			}
		}
		return sleeps;
	}

	/**
	 * 获得2个时刻间的数据
	 * 
	 * @param date
	 * @return
	 */
	public float[] getDataForSleep(Date date, String startHour, String endHour) {
		String dataStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String[] split = dataStr.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		float sleeps[] = null;
		int size = 0;
		if (Integer.valueOf(startHour) >= Integer.valueOf(endHour)) {
			String yea = DateUtil.dateToString(DateUtil.getDateOfDiffDay(date, -1));
			String[] yeaData = yea.split("-");
			String yearYea = yeaData[0];
			String monthYea = yeaData[1];
			String dayYea = yeaData[2];
			size = 24 - Integer.valueOf(startHour) + Integer.valueOf(endHour) + 1;
			sleeps = new float[size * 3];// 每一个小时有3条数据
			for (int i = 0; i < size; i++) {
				if (i <= 24 - Integer.valueOf(startHour)) {
					List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour >=? and hour<=? ", yearYea, monthYea, dayYea, startHour, "24").find(History.class);
					if (sleepList != null && sleepList.size() > 0) {
						// 获得每个小时的数据
						History sleep = sleepList.get(0);
						//Log.e("", "今天的数据sleep 时刻：" + i + "-->" + sleep);
						// 0-20分钟
						sleeps[3 * i] = getValue(sleep, 1);
						// 20-40分钟
						sleeps[3 * i + 1] = getValue(sleep, 2);
						// 40-60分钟
						sleeps[3 * i + 2] = getValue(sleep, 3);
						// 统计数据
					} else {
//						L.e("数据库无睡眠数据");
					}
				} else {
					List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour >=? and hour<= ? ", year, month, day, "0", endHour).find(History.class);
					if (sleepList != null && sleepList.size() > 0) {
						// 获得每个小时的数据
						History sleep = sleepList.get(0);
						//Log.e("", "今天的数据sleep 时刻：" + i + "-->" + sleep);
						// 0-20分钟
						sleeps[3 * i] = getValue(sleep, 1);
						// 20-40分钟
						sleeps[3 * i + 1] = getValue(sleep, 2);
						// 40-60分钟
						sleeps[3 * i + 2] = getValue(sleep, 3);
						// 统计数据
					} else {
//						L.e("数据库无睡眠数据");
					}
				}
			}
		} else {
			size = Integer.valueOf(endHour) - Integer.valueOf(startHour) + 1;
			sleeps = new float[size * 3];
			for (int i = 0; i < size; i++) {
				List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour >=? and hour<= ? ", year, month, day, startHour, endHour).find(History.class);
				if (sleepList != null && sleepList.size() > 0) {
					// 获得每个小时的数据
					History sleep = sleepList.get(0);
					//Log.e("", "今天的数据sleep 时刻：" + i + "-->" + sleep);
					// 0-20分钟
					sleeps[3 * i] = getValue(sleep, 1);
					// 20-40分钟
					sleeps[3 * i + 1] = getValue(sleep, 2);
					// 40-60分钟
					sleeps[3 * i + 2] = getValue(sleep, 3);
					// 统计数据
				} else {
				}
			}

		}

		return sleeps;
	}

	/**
	 * 0--深睡 1--浅睡 2--清醒 默认0
	 */
	private float getValue(History sleep, int i) {
		int value = 0;//默认是0深睡
//		int value = 2;
		if (i == 1) {
			if (sleep.getSleepOneself_1() >= 0 && sleep.getSleepOneself_1() < 40) {// 深睡
				value = 0;
			} else if (sleep.getSleepOneself_3() >= 40 && sleep.getSleepOneself_3() < 100) {// 浅睡
				value = 1;
			} else {
				value = 2;// 清醒
			}
		} else if (i == 2) {
			if (sleep.getSleepOneself_2() >= 0 && sleep.getSleepOneself_2() < 40) {// 深睡
				value = 0;
			} else if (sleep.getSleepOneself_3() >= 40 && sleep.getSleepOneself_3() < 100) {// 浅睡
				value = 1;
			} else {
				value = 2;
			}
		} else if (i == 3) {
			if (sleep.getSleepOneself_3() >= 0 && sleep.getSleepOneself_3() < 40) {// 深睡
				value = 0;
			} else if (sleep.getSleepOneself_3() >= 40 && sleep.getSleepOneself_3() < 100) {// 浅睡
				value = 1;
			} else {
				value = 2;
			}
		}

		return value;
	}
}
