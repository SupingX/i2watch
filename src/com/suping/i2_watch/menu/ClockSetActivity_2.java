package com.suping.i2_watch.menu;

import com.suping.i2_watch.enerty.I2WatchProtocolData;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Intent;

public class ClockSetActivity_2 extends AbstractSetTimeActivity {
	private static final int RESULT_2 = 0x21;
	@Override
	public void confirm() {
		Intent intent = new Intent(ClockSetActivity_2.this, ClockActivity.class);
		intent.putExtras(b);
		setResult(RESULT_2, intent);
		finish();
	}
	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(ClockSetActivity_2.this, I2WatchProtocolData.SHARE_CLOCK_SETTIME_HOUR_2,
				"07");
		npHour.setValue(Integer.valueOf(hour));

		String min = (String) SharedPreferenceUtil.get(ClockSetActivity_2.this, I2WatchProtocolData.SHARE_CLOCK_SETTIME_MIN_2,
				"00");
		if (min.equals(values[0])) {
			npMin.setValue(0);
		} else if (min.equals(values[1])) {
			npMin.setValue(1);
		}
		
	}
}
