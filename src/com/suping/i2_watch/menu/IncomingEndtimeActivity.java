package com.suping.i2_watch.menu;

import android.content.Intent;
public class IncomingEndtimeActivity extends AbstractSet2Activity {

	@Override
	public void confirm() {
		Intent intent = new Intent(IncomingEndtimeActivity.this,IncomingActivity.class);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}
}
