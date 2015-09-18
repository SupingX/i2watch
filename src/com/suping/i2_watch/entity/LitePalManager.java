package com.suping.i2_watch.entity;

import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.suping.i2_watch.util.DateUtil;
import com.suping.i2_watch.util.L;

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
				exitHistory.updateAll();
			} else {
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
		L.i(year);
		L.i(month);
		L.i(day);
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
				L.e("数据库无睡眠数据");
			}
		}
		return dataSleep;
	}
	
	/**
	 * 获得每个时刻每个的点的计步数量 72个数据
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
				L.e("数据库无运动数据");
			}
		}
		return steps;
	}
	
	/**
	 * 获得每个时刻每个的点的睡眠数量 72个数据
	 * @param date
	 * @return
	 */
	public int[] getDataForSleep(String date) {
		String[] split = date.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		L.i(year);
		L.i(month);
		L.i(day);
		int sleeps[] = new int[72];
		for (int i = 0; i < 24; i++) {
			String hour = String.valueOf(i).trim();
			List<History> sleepList = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(History.class);
			if (sleepList != null && sleepList.size() > 0) {
				// 获得每个小时的数据
				History sleep = sleepList.get(0);
				// 0-20分钟
				int sleepDeep_1 = sleep.getSleepDeep_1();
				int sleepLight_1 = sleep.getSleepLight_1();
				int sleepAwak_1 = sleep.getSleepAwak_1();
				sleeps[3 * i] = getValue(sleepDeep_1, sleepLight_1, sleepAwak_1);
				// 20-40分钟
				int sleepDeep_2 = sleep.getSleepDeep_2();
				int sleepLight_2 = sleep.getSleepLight_2();
				int sleepAwak_2 = sleep.getSleepAwak_2();
				sleeps[3 * i + 1] = getValue(sleepDeep_2, sleepLight_2, sleepAwak_2);
				// 40-60分钟
				int sleepDeep_3 = sleep.getSleepDeep_3();
				int sleepLight_3 = sleep.getSleepLight_3();
				int sleepAwak_3 = sleep.getSleepAwak_3();
				sleeps[3 * i + 2] = getValue(sleepDeep_3, sleepLight_3, sleepAwak_3);
				// 统计数据
			} else {
				L.e("数据库无睡眠数据");
			}
		}
		return sleeps;
	}
	
	/**
	 * 0--深睡 1--浅睡 2--清醒 默认0
	 */
	private int getValue(int deep, int light, int awak) {
		// 比较3个点确定显示的值
		int value = 0;
		if (deep >= light && deep >= awak) {// 深睡
			value = 0;
		} else if (light > deep && light >= awak) {// 浅睡
			value = 1;
		} else if (awak > light && awak > deep) {// 清醒
			value = 2;
		}
		return value;
	}
}
