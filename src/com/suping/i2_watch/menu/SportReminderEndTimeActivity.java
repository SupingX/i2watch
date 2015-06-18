package com.suping.i2_watch.menu;

import com.suping.i2_watch.util.SharedPreferenceUtil;
import android.content.Intent;

public class SportReminderEndTimeActivity extends AbstractSetTimeActivity {
	public final static int RESULT_ENDTIME = 33;

	public void confirm() {
		Intent intent = new Intent(SportReminderEndTimeActivity.this, SportReminderActivity.class);
		intent.putExtras(b);
		setResult(RESULT_ENDTIME, intent);
		finish();
	}

	@Override
	public void initValue() {
		String hour = (String) SharedPreferenceUtil.get(SportReminderEndTimeActivity.this, SportReminderActivity.SHARE_END_HOUR,
				"22");
		npHour.setValue(Integer.valueOf(hour));

		String min = (String) SharedPreferenceUtil.get(SportReminderEndTimeActivity.this, SportReminderActivity.SHARE_END_MIN,
				"00");
		if (min.equals(values[0])) {
			npMin.setValue(0);
		} else if (min.equals(values[1])) {
			npMin.setValue(1);
		}

	}

}
