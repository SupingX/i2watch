package com.suping.i2_watch.menu;

import android.content.Intent;

public class ClockSetActivity_3 extends AbstractSetTimeActivity {
	private static final int RESULT_3 = 0x31;
	@Override
	public void confirm() {
		Intent intent = new Intent(ClockSetActivity_3.this, ClockActivity.class);
		intent.putExtras(b);
		setResult(RESULT_3, intent);
		finish();
	}
}
