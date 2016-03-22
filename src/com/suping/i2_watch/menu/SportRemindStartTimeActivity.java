package com.suping.i2_watch.menu;

import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Intent;
import android.util.Log;

public class SportRemindStartTimeActivity extends AbstractSetTimeActivity {
	public final static int RESULT_STARTTIME = 22;

	public void confirm() {
		Intent intent = new Intent(SportRemindStartTimeActivity.this, SportReminderActivity.class);
		intent.putExtras(b);
		setResult(RESULT_STARTTIME, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hourStart = (String) SharedPreferenceUtil.get(SportRemindStartTimeActivity.this,
				I2WatchProtocolDataForWrite.SHARE_WATER_START_HOUR, I2WatchProtocolDataForWrite.DEFAULT_START_HOUR);
		npHour.setValue(Integer.valueOf(hourStart));
		
		String min = (String) SharedPreferenceUtil.get(SportRemindStartTimeActivity.this,
				I2WatchProtocolDataForWrite.SHARE_WATER_START_MIN, I2WatchProtocolDataForWrite.DEFAULT_START_MIN);
		npMin.setValue(Integer.valueOf(min));
//		if (min.equals(values[0])) {
//			npMin.setValue(0);
//		} else if (min.equals(values[1])) {
//			npMin.setValue(1);
//		}
	}

	@Override
	public boolean checkTime() {
		int hour = npHour.getValue();
		
		String hourEnd = (String) SharedPreferenceUtil.get(SportRemindStartTimeActivity.this,
				I2WatchProtocolDataForWrite.SHARE_WATER_END_HOUR, I2WatchProtocolDataForWrite.DEFAULT_END_HOUR);
		
		if(hour >= Integer.parseInt(hourEnd)){ //开始时间大于结束时间
			//Log.e("SportRemindStartTimeActivcity", "开始时间大于结束时间，设置错误。");
			return false;
		}
		return true;
	}
}
