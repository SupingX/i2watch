package com.suping.i2_watch.test;

import java.util.List;

import org.litepal.crud.DataSupport;

import com.suping.i2_watch.entity.HistorySleep;
import com.suping.i2_watch.entity.HistorySport;
import com.suping.i2_watch.entity.I2WatchProtocolDataForNotify;
import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.L;

public class Test {
	private static Test test;
	public static Test instance(){
		if (test==null) {
			test = new Test();
		}
		return test;
	}
	
	private Test(){
		
	}
	
	/**
	 * 创建历史数据
	 */
	public void CreateHistoryData() {
		for (int i = 0; i < 10; i++) {

			String data1 = "25000" + i + "965099129765432191234567";
//			String data1 = "25000" + i + "7AB6AD557AB6AD537AB6AD52";

			final Object notifyHistory = I2WatchProtocolDataForNotify.instance().notifyHistory(DataUtil.getBytesByString(data1));
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (notifyHistory != null) {
						if (notifyHistory instanceof HistorySport) {
							HistorySport sport = isExitSportHistory((HistorySport) notifyHistory);// 数据库有
							if (sport != null) {
								L.e("已存在");
								sport.setSportStep_1(((HistorySport) notifyHistory).getSportStep_1());
								sport.setSportStep_2(((HistorySport) notifyHistory).getSportStep_2());
								sport.setSportStep_3(((HistorySport) notifyHistory).getSportStep_3());
								sport.setSportTime_1(((HistorySport) notifyHistory).getSportTime_1());
								sport.setSportTime_2(((HistorySport) notifyHistory).getSportTime_2());
								sport.setSportTime_3(((HistorySport) notifyHistory).getSportTime_3());
								sport.save();
							} else {
								((HistorySport) notifyHistory).save();
							}
						} else if (notifyHistory instanceof HistorySleep) {
							HistorySleep sleep = isExitSleepHistory((HistorySleep) notifyHistory);
							if (sleep != null) {// 数据库有
								L.e("已存在");
								sleep.setSleepOneself_1(((HistorySleep) notifyHistory).getSleepOneself_1());
								sleep.setSleepOneself_2(((HistorySleep) notifyHistory).getSleepOneself_2());
								sleep.setSleepOneself_3(((HistorySleep) notifyHistory).getSleepOneself_3());
								sleep.setSleepAwak_1(((HistorySleep) notifyHistory).getSleepAwak_1());
								sleep.setSleepAwak_2(((HistorySleep) notifyHistory).getSleepAwak_2());
								sleep.setSleepAwak_3(((HistorySleep) notifyHistory).getSleepAwak_3());
								sleep.setSleepLight_1(((HistorySleep) notifyHistory).getSleepLight_1());
								sleep.setSleepLight_2(((HistorySleep) notifyHistory).getSleepLight_2());
								sleep.setSleepLight_3(((HistorySleep) notifyHistory).getSleepLight_3());
								sleep.setSleepDeep_1(((HistorySleep) notifyHistory).getSleepDeep_1());
								sleep.setSleepDeep_2(((HistorySleep) notifyHistory).getSleepDeep_2());
								sleep.setSleepDeep_3(((HistorySleep) notifyHistory).getSleepDeep_3());
								sleep.save();
							} else {
								((HistorySleep) notifyHistory).save();
							}
						}
					} else {
						L.e("数据null");
					}
				}
			}).start();
		}
		List<HistorySleep> sleeps = DataSupport.findAll(HistorySleep.class);
		List<HistorySport> sports = DataSupport.findAll(HistorySport.class);
		for (HistorySleep sleep : sleeps) {
			L.e(sleep.toString());
		}
		for (HistorySport sport : sports) {
			L.e(sport.toString());
		}
	}

	private HistorySport isExitSportHistory(HistorySport sport) {
		List<HistorySport> sports = DataSupport.where("year=? and month =? and day = ? and hour = ? ", sport.getYear(), sport.getMonth(), sport.getDay(), sport.getHour()).find(HistorySport.class);
		if (sports != null && sports.size() > 0) {
			return sports.get(0);
		} else {
			return null;
		}
	}

	private HistorySleep isExitSleepHistory(HistorySleep sleep) {
		List<HistorySleep> sleeps = DataSupport.where("year=? and month =? and day = ? and hour = ? ", sleep.getYear(), sleep.getMonth(), sleep.getDay(), sleep.getHour()).find(HistorySleep.class);
		if (sleeps != null && sleeps.size() > 0) {
			return sleeps.get(0);
		} else {
			return null;
		}
	}
}
