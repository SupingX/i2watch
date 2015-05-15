package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class ReminderActivity extends Activity implements OnClickListener {
	private ImageView img_back;
	private TextView tv_title, tv_mon, tv_tue, tv_wen, tv_thu, tv_sat, tv_fri,
			tv_sun, tv_interval_value, tv_start_value, tv_end_value;
	private RelativeLayout rl_start, rl_end, rl_interval;
	private final static int REQ_INTERVAL = 1;
	private final static int REQ_STARTTIME = 2;
	private final static int REQ_ENDTIME = 3;
	private final static int RESULT_INTERVAL = 11;
	private final static int RESULT_STARTTIME = 22;
	private final static int RESULT_ENDTIME = 33;
	private long exitTime = 0;
	private int repeatValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		initViews();
		setClick();
		tv_title.setText("Activity reminder");
	}

	private void initViews() {
		img_back = (ImageView) findViewById(R.id.img_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_mon = (TextView) findViewById(R.id.tv_mon);
		tv_tue = (TextView) findViewById(R.id.tv_tue);
		tv_wen = (TextView) findViewById(R.id.tv_wen);
		tv_thu = (TextView) findViewById(R.id.tv_thu);
		tv_sat = (TextView) findViewById(R.id.tv_sat);
		tv_fri = (TextView) findViewById(R.id.tv_fri);
		tv_sun = (TextView) findViewById(R.id.tv_sun);
		tv_interval_value = (TextView) findViewById(R.id.tv_interval_value);
		tv_start_value = (TextView) findViewById(R.id.tv_start_value);
		tv_end_value = (TextView) findViewById(R.id.tv_end_value);
		rl_start = (RelativeLayout) findViewById(R.id.rl_start);
		rl_end = (RelativeLayout) findViewById(R.id.rl_end);
		rl_interval = (RelativeLayout) findViewById(R.id.rl_interval);

		String reminder_start = (String) SharedPreferenceUtil.get(
				getApplicationContext(), "reminder_start", "7:00");
		tv_start_value.setText(reminder_start);

		String reminder_end = (String) SharedPreferenceUtil.get(
				getApplicationContext(), "reminder_end", "22:00");
		tv_end_value.setText(reminder_end);

		String reminder_interval = (String) SharedPreferenceUtil.get(
				getApplicationContext(), "reminder_interval", "30");
		tv_interval_value.setText(reminder_interval);

		initWeekday();
	}

	private void initWeekday() {
		repeatValue = (int) SharedPreferenceUtil.get(getApplicationContext(),
				"reminder_repeatValue", 0b00000000);
		Log.d("OB", "初始化repeatValue-->" + Integer.toBinaryString(repeatValue));
		validateTextview(tv_sun,WeekdayEnum.SUN.getDay());
		validateTextview(tv_mon,WeekdayEnum.MON.getDay());
		validateTextview(tv_tue,WeekdayEnum.TUE.getDay());
		validateTextview(tv_wen,WeekdayEnum.WED.getDay());
		validateTextview(tv_thu,WeekdayEnum.THU.getDay());
		validateTextview(tv_fri,WeekdayEnum.FRI.getDay());
		validateTextview(tv_sat,WeekdayEnum.SAT.getDay());
	}

	private void setClick() {
		img_back.setOnClickListener(this);
		tv_title.setOnClickListener(this);
		tv_mon.setOnClickListener(this);
		tv_tue.setOnClickListener(this);
		tv_wen.setOnClickListener(this);
		tv_thu.setOnClickListener(this);
		tv_fri.setOnClickListener(this);
		tv_sat.setOnClickListener(this);
		tv_sun.setOnClickListener(this);
		rl_start.setOnClickListener(this);
		rl_interval.setOnClickListener(this);
		rl_end.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent retrunToMain = new Intent(ReminderActivity.this,
					MenuActivity.class);
			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
			break;
		/**
		 * 
		 * 1111 1111 & 0000 0001 0000 0001 两个操作数中位都为1，结果才为1，否则结果为0 1111 1111 |
		 * 0000 0001 1111 1111 两个位只要有一个为1，那么结果就是1，否则就为0 1111 1111 ^ 0000 0001
		 * 1111 1110 两个操作数的位中，相同则结果为0，不同则结果为1
		 * 
		 */
		case R.id.tv_sun:
			repeatWeekday(tv_sun,WeekdayEnum.SUN.getDay());
			break;
		case R.id.tv_mon:
			repeatWeekday(tv_mon,WeekdayEnum.MON.getDay());
			break;
		case R.id.tv_tue:
			repeatWeekday(tv_tue,WeekdayEnum.TUE.getDay());
			break;
		case R.id.tv_wen:
			repeatWeekday(tv_wen,WeekdayEnum.WED.getDay());
			break;
		case R.id.tv_thu:
			repeatWeekday(tv_thu,WeekdayEnum.THU.getDay());
			break;
		case R.id.tv_fri:
			repeatWeekday(tv_fri,WeekdayEnum.FRI.getDay());
			break;
		case R.id.tv_sat:
			repeatWeekday(tv_sat,WeekdayEnum.SAT.getDay());
			break;
		case R.id.rl_interval:
			Intent intervalIntent = new Intent(ReminderActivity.this,
					ReminderIntervalActivity.class);
			startActivityForResult(intervalIntent, REQ_INTERVAL);
			break;
		case R.id.rl_start:
			Intent startIntent = new Intent(ReminderActivity.this,
					RemindStartTimeActivity.class);
			startActivityForResult(startIntent, REQ_STARTTIME);
			break;
		case R.id.rl_end:
			Intent endIntent = new Intent(ReminderActivity.this,
					ReminderEndTimeActivity.class);
			startActivityForResult(endIntent, REQ_ENDTIME);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_INTERVAL:
			switch (resultCode) {
			case RESULT_INTERVAL:
				Bundle b = data.getExtras();
				String time = b.getString("time");
				time = time == null ? "100" : time;
				tv_interval_value.setText(time);
				SharedPreferenceUtil.put(getApplicationContext(),
						"reminder_interval", time);
				break;
			default:
				break;
			}
			break;
		case REQ_STARTTIME:
			switch (resultCode) {
			case RESULT_STARTTIME:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min + ":" + sec;
				tv_start_value.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(),
						"reminder_start", value);
				break;
			default:
				break;
			}
			break;
		case REQ_ENDTIME:
			switch (resultCode) {
			case RESULT_ENDTIME:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min + ":" + sec;
				tv_end_value.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(),
						"reminder_start", value);
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}

	}

	private void repeatWeekday(TextView tv,int weekday) {
		repeatValue ^= weekday;
		Log.d("OB", tv.getId() + "-->" + Integer.toBinaryString(repeatValue));
		validateTextview(tv, weekday);
		SharedPreferenceUtil.put(getApplicationContext(),
				"reminder_repeatValue", repeatValue);
	}
	
	private void validateTextview(TextView tv,int weekday){
		//例如星期天：
		// 1111 1111 & 0000 0001 = 0000 0001; 开
		// 1111 1110 & 0000 0001 = 0000 0000; 关
		int color = (repeatValue & weekday) == weekday ? getResources().getColor(
				R.color.color_top_text_pressed) : getResources().getColor(
				R.color.grey);
				tv.setTextColor(color);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
