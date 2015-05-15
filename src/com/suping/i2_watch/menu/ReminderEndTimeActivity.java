package com.suping.i2_watch.menu;

import android.content.Intent;

public class ReminderEndTimeActivity extends AbstractSetTimeActivity {
	public final static int RESULT_ENDTIME = 33;

	public void confirm() {
		Intent intent = new Intent(ReminderEndTimeActivity.this, ReminderActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}

}
