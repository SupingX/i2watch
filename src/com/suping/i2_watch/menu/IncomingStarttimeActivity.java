package com.suping.i2_watch.menu;

import android.content.Intent;


public class IncomingStarttimeActivity extends AbstractSet2Activity {

	@Override
	public void confirm() {
		Intent intent = new Intent(IncomingStarttimeActivity.this,IncomingActivity.class);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}
}
