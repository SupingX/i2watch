package com.suping.i2_watch.menu;

import android.content.Intent;

public class SleepMonitorStartActivity extends AbstractSetTimeActivity {

	public final static int RESULT_ENDTIME = 55;

	public void confirm() {
		Intent intent = new Intent(SleepMonitorStartActivity.this, SleepMonitorActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}
}
