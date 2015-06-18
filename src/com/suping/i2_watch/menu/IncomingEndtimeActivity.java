package com.suping.i2_watch.menu;

import com.suping.i2_watch.util.SharedPreferenceUtil;
import android.content.Intent;
public class IncomingEndtimeActivity extends AbstractSet2Activity {

	@Override
	public void confirm() {
		Intent intent = new Intent(IncomingEndtimeActivity.this,IncomingActivity.class);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(IncomingEndtimeActivity.this, IncomingActivity.SHARE_INCOMING_END_HOUR,
				"07");
		np_min.setValue(Integer.valueOf(hour));

		String sec = (String) SharedPreferenceUtil.get(IncomingEndtimeActivity.this, IncomingActivity.SHARE_INCOMING_END_MIN,
				"00");
		np_sec.setValue(Integer.valueOf(sec));
	}
}
