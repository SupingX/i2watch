package com.suping.i2_watch.menu;

import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Intent;
import android.util.Log;
public class IncomingEndtimeActivity extends AbstractSet2Activity {

	@Override
	public void confirm() {
		Intent intent = new Intent(IncomingEndtimeActivity.this,IncomingActivity.class);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(IncomingEndtimeActivity.this, I2WatchProtocolDataForWrite.SHARE_INCOMING_END_HOUR,
				I2WatchProtocolDataForWrite.DEFAULT_END_HOUR);
		npHour.setValue(Integer.valueOf(hour));

		String sec = (String) SharedPreferenceUtil.get(IncomingEndtimeActivity.this, I2WatchProtocolDataForWrite.SHARE_INCOMING_END_MIN,
				I2WatchProtocolDataForWrite.DEFAULT_END_MIN);
		npMin.setValue(Integer.valueOf(sec));
	}

	@Override
	public boolean checkTime() {
		int hour = npHour.getValue();
		int min = npMin.getValue();
		
		String hourStart = (String) SharedPreferenceUtil.get(IncomingEndtimeActivity.this,
				I2WatchProtocolDataForWrite.SHARE_INCOMING_START_HOUR, I2WatchProtocolDataForWrite.DEFAULT_START_HOUR);
		String minStart = (String) SharedPreferenceUtil.get(IncomingEndtimeActivity.this,
				I2WatchProtocolDataForWrite.SHARE_INCOMING_START_MIN, I2WatchProtocolDataForWrite.DEFAULT_START_MIN);
		
		if(hour < Integer.parseInt(hourStart)){ //开始时间大于结束时间
			//Log.e("IncomingEndtimeActivity", "开始时间大于结束时间，设置错误。");
			return false;
		} else if (hour == Integer.parseInt(hourStart)){
			if(min<=Integer.parseInt(minStart)){
				//Log.e("IncomingEndtimeActivity", "开始分钟大于结束分钟，设置错误。");
				return false;
			}
		}
		return true;
	}
}
