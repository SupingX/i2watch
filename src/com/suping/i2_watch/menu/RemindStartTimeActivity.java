package com.suping.i2_watch.menu;

import android.content.Intent;

public class RemindStartTimeActivity extends AbstractSetTimeActivity {
	public final static int RESULT_STARTTIME = 22;
	public void confirm() {
				Intent intent = new Intent(RemindStartTimeActivity.this,
						ReminderActivity.class);
				intent.putExtras(b);
				setResult(RESULT_STARTTIME, intent);
				finish();
	}
}
