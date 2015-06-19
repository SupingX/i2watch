package com.suping.i2_watch.menu;

import com.suping.i2_watch.enerty.I2WatchProtocolData;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Intent;

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
		String hour = (String) SharedPreferenceUtil.get(SportRemindStartTimeActivity.this,
				I2WatchProtocolData.SHARE_START_HOUR, I2WatchProtocolData.DEFAULT_START_HOUR);
		npHour.setValue(Integer.valueOf(hour));

		String min = (String) SharedPreferenceUtil.get(SportRemindStartTimeActivity.this,
				I2WatchProtocolData.SHARE_START_MIN, I2WatchProtocolData.DEFAULT_START_MIN);
		if (min.equals(values[0])) {
			npMin.setValue(0);
		} else if (min.equals(values[1])) {
			npMin.setValue(1);
		}
	}
}
