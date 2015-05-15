package com.suping.i2_watch.menu;

import android.content.Intent;

public class ClockSetActivity_1 extends AbstractSetTimeActivity {
	private static final int RESULT_1 = 0x11;
	@Override
	public void confirm() {
		Intent intent = new Intent(ClockSetActivity_1.this, ClockActivity.class);
		intent.putExtras(b);
		setResult(RESULT_1, intent);
		finish();
	}

}
