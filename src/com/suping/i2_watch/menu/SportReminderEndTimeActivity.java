package com.suping.i2_watch.menu;

import com.suping.i2_watch.enerty.I2WatchProtocolData;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Intent;
import android.util.Log;

public class SportReminderEndTimeActivity extends AbstractSetTimeActivity {
	public final static int RESULT_ENDTIME = 33;

	public void confirm() {
		Intent intent = new Intent(SportReminderEndTimeActivity.this, SportReminderActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(SportReminderEndTimeActivity.this, I2WatchProtocolData.SHARE_END_HOUR,
				I2WatchProtocolData.DEFAULT_END_HOUR);
		npHour.setValue(Integer.valueOf(hour));

		String min = (String) SharedPreferenceUtil.get(SportReminderEndTimeActivity.this, I2WatchProtocolData.SHARE_END_MIN,
				I2WatchProtocolData.DEFAULT_END_MIN);
		if (min.equals(values[0])) {
			npMin.setValue(0);
		} else if (min.equals(values[1])) {
			npMin.setValue(1);
		}

	}
	@Override
	public boolean checkTime() {
		int hour = npHour.getValue();
		
		String hourStart = (String) SharedPreferenceUtil.get(SportReminderEndTimeActivity.this,
				I2WatchProtocolData.SHARE_START_HOUR, I2WatchProtocolData.DEFAULT_START_HOUR);
		
		if(hour <= Integer.parseInt(hourStart)){ //开始时间大于结束时间
			Log.e("SportReminderEndTimeActivity", "开始时间大于结束时间，设置错误。");
			return false;
		}
		return true;
	}

}
