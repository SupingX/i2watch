package com.suping.i2_watch.menu;

import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
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
		String hour = (String) SharedPreferenceUtil.get(ClockSetActivity_2.this, I2WatchProtocolDataForWrite.SHARE_CLOCK_SETTIME_HOUR_2,
				"12");
		npHour.setValue(Integer.valueOf(hour));

		String min = (String) SharedPreferenceUtil.get(ClockSetActivity_2.this, I2WatchProtocolDataForWrite.SHARE_CLOCK_SETTIME_MIN_2,
				"00");
//		if (min.equals(values[0])) {
//			npMin.setValue(0);
//		} else if (min.equals(values[1])) {
//			npMin.setValue(1);
//		}
		npMin.setValue(Integer.valueOf(min));
	}
	@Override
	public boolean checkTime() {
		// TODO Auto-generated method stub
		return true;
	}
}
