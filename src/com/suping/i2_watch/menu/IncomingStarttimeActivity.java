package com.suping.i2_watch.menu;

import com.suping.i2_watch.util.SharedPreferenceUtil;
import android.content.Intent;


public class IncomingStarttimeActivity extends AbstractSet2Activity {

	@Override
	public void confirm() {
		Intent intent = new Intent(IncomingStarttimeActivity.this,IncomingActivity.class);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(IncomingStarttimeActivity.this, IncomingActivity.SHARE_INCOMING_START_HOUR,
				"07");
		np_min.setValue(Integer.valueOf(hour));

		String sec = (String) SharedPreferenceUtil.get(IncomingStarttimeActivity.this, IncomingActivity.SHARE_INCOMING_START_MIN,
				"00");
		np_sec.setValue(Integer.valueOf(sec));
	}
}
