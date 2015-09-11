package com.suping.i2_watch.menu;

import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Intent;
import android.util.Log;

public class SleepMonitorStartActivity extends AbstractSetTimeActivity {

	public final static int RESULT_ENDTIME = 55;

	public void confirm() {
		Intent intent = new Intent(SleepMonitorStartActivity.this, SleepMonitorActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(SleepMonitorStartActivity.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_START_HOUR,
				"07");
		npHour.setValue(Integer.valueOf(hour));

		String min = (String) SharedPreferenceUtil.get(SleepMonitorStartActivity.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_START_MIN,
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
		int hour = npHour.getValue();
		
		String hourEnd = (String) SharedPreferenceUtil.get(SleepMonitorStartActivity.this,
				I2WatchProtocolDataForWrite.SHARE_MONITOR_END_HOUR, I2WatchProtocolDataForWrite.DEFAULT_END_HOUR);
		
		if(hour >= Integer.parseInt(hourEnd)){ //开始时间大于结束时间
			Log.e("SleepMonitorStartActivity", "开始时间大于结束时间，设置错误。");
			return false;
		}
		return true;
	}
}
