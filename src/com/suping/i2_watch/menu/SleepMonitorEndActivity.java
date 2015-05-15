package com.suping.i2_watch.menu;

import android.content.Intent;

public class SleepMonitorEndActivity extends AbstractSetTimeActivity {
	public final static int RESULT_ENDTIME = 66;

	public void confirm() {
		Intent intent = new Intent(SleepMonitorEndActivity.this, SleepMonitorActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}

}
