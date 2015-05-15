package com.suping.i2_watch.menu;

import android.content.Intent;

public class SleepMonitorTargetActivity extends AbstractSetTimeActivity{
	public final static int RESULT_ENDTIME = 44;

	public void confirm() {
		Intent intent = new Intent(SleepMonitorTargetActivity.this, SleepMonitorActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}
}
