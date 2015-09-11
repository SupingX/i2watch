package com.suping.i2_watch.menu;

import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Intent;

public class SleepMonitorTargetActivity extends AbstractSetTimeActivity{
	public final static int RESULT_ENDTIME = 44;
	
	
	
	
	
	public void confirm() {
		Intent intent = new Intent(SleepMonitorTargetActivity.this, SleepMonitorActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(SleepMonitorTargetActivity.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_TARGET_HOUR,
				"07");
		npHour.setValue(Integer.valueOf(hour));

		String min = (String) SharedPreferenceUtil.get(SleepMonitorTargetActivity.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_TARGET_MIN,
				"00");
		npMin.setValue(Integer.valueOf(min));
//		if (min.equals(values[0])) {
//			npMin.setValue(0);
//		} else if (min.equals(values[1])) {
//			npMin.setValue(1);
//		}
		
	}

	@Override
	public boolean checkTime() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
