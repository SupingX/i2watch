package com.suping.i2_watch.menu;

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
}
