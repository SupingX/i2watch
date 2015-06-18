package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.TextView;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class SportReminderIntervalActivity extends Activity {

	private final static int RESULT_INTERVAL = 11;
	private NumberPicker npInterval;
	private TextView tvCancel;
	private TextView tvConfirm;
	private String[] values;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_reminder_interval);
		initViews();
		setClick();

	}

	private void initViews() {
		npInterval = (NumberPicker) findViewById(R.id.np_interval);
		values = new String[] { "15", "30", "45", "60", "75", "90", "105", "120" };
		npInterval.setDisplayedValues(values);
		npInterval.setMaxValue(values.length - 1);
		// npInterval.setMinValue(0);
		initvalue();
		npInterval.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		// ((EditText)(np.getChildAt(0))).setFocusable(false);
		// ((EditText)(np.getChildAt(0))).setFocusableInTouchMode(false);

		tvCancel = (TextView) findViewById(R.id.tv_negative);
		tvConfirm = (TextView) findViewById(R.id.tv_positive);
	}

	private void initvalue() {
		String interval = (String) SharedPreferenceUtil.get(SportReminderIntervalActivity.this,
				SportReminderActivity.SHARE_INTERVAL, "15");
		if (interval.equals(values[0])) {
			npInterval.setValue(0);
		} else if (interval.equals(values[1])) {
			npInterval.setValue(1);
		} else if (interval.equals(values[2])) {
			npInterval.setValue(2);
		} else if (interval.equals(values[3])) {
			npInterval.setValue(3);
		} else if (interval.equals(values[4])) {
			npInterval.setValue(4);
		} else if (interval.equals(values[5])) {
			npInterval.setValue(5);
		} else if (interval.equals(values[6])) {
			npInterval.setValue(6);
		} else if (interval.equals(values[7])) {
			npInterval.setValue(7);
		}
	}

	private void setClick() {
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tvConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String time = values[npInterval.getValue()];
				Bundle b = new Bundle();
				b.putString("time", time);
				Intent intent = new Intent(SportReminderIntervalActivity.this, SportReminderActivity.class);
				intent.putExtras(b);
				setResult(RESULT_INTERVAL, intent);
				finish();
			}
		});

		npInterval.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {

			}
		});
	}
}
