package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.suping.i2_watch.XtremApplication;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.xtremeprog.sdk.ble.BleManager;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

public class SportReminderActivity extends Activity implements OnClickListener {
	// requestcode & resultCode
	private final static int REQ_INTERVAL = 1;
	private final static int REQ_STARTTIME = 2;
	private final static int REQ_ENDTIME = 3;
	private final static int RESULT_INTERVAL = 11;
	private final static int RESULT_STARTTIME = 22;
	private final static int RESULT_ENDTIME = 33;

	// 存贮数据
	public final static String SHARE_INTERVAL = "share_interval";
	public final static String SHARE_START_HOUR = "share_start_hour";
	public final static String SHARE_START_MIN = "share_start_min";
	public final static String SHARE_END_HOUR = "share_end_hour";
	public final static String SHARE_END_MIN = "share_end_min";
	public final static String SHARE_REPEAT = "share_reminder_repeatValue";

	// wedgetS
	private ImageView imgBack;
	private TextView tvTitle, tvMon, tvTue, tvWen, tvThu, tvSat, tvFri, tvSun, tvIntervalValue, tvStartValue,
			tvEndValue;
	private RelativeLayout rlStart, rlEnd, rlInterval;
	// 双击间隔时间
	private long exitTime = 0;
	// 重复
	private int repeatValue;

	// broadcastReceiver
	private SportReminderBroadcastReceiver mReceiver;
	//当前连接的蓝牙
	private String currentAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		initViews();
		setClick();
		mReceiver = new SportReminderBroadcastReceiver();
		tvTitle.setText("Activity reminder");
	}

	@Override
	protected void onResume() {
		registerReceiver(mReceiver, BleService.getIntentFilter());
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		// from ReminderIintervalActivity
		case REQ_INTERVAL:
			switch (resultCode) {
			case RESULT_INTERVAL:
				Bundle b = data.getExtras();
				String time = b.getString("time");
				time = time == null ? "100" : time;
				tvIntervalValue.setText(time);
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_INTERVAL, time);
				//写入数据 设置interval（间隔）
//				getIble().requestWriteCharacteristic(address, characteristic, remark)
//				getSer
				break;
			default:
				break;
			}
			break;
		// from ReminderStartTimeActivity
		case REQ_STARTTIME:
			switch (resultCode) {
			case RESULT_STARTTIME:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min + ":" + sec;
				tvStartValue.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_START_HOUR, min);
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_START_MIN, sec);
				//写入数据 设置starttime（开始时间）
				
				break;
			default:
				break;
			}
			break;
		// from ReminderEndTimeActivity
		case REQ_ENDTIME:
			switch (resultCode) {
			case RESULT_ENDTIME:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min + ":" + sec;
				tvEndValue.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_END_HOUR, min);
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_END_MIN, sec);
				//写入数据 设置endtime（结束时间）
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent retrunToMain = new Intent(SportReminderActivity.this, MenuActivity.class);
			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter, R.anim.activity_from_left_to_right_exit);
			break;
		/**
		 * 
		 * 1111 1111 & 0000 0001 0000 0001 两个操作数中位都为1，结果才为1，否则结果为0 1111 1111 |
		 * 0000 0001 1111 1111 两个位只要有一个为1，那么结果就是1，否则就为0 1111 1111 ^ 0000 0001
		 * 1111 1110 两个操作数的位中，相同则结果为0，不同则结果为1
		 * 
		 */
		case R.id.tv_sun:
			repeatWeekday(tvSun, WeekdayEnum.SUN.getDay());
			break;
		case R.id.tv_mon:
			repeatWeekday(tvMon, WeekdayEnum.MON.getDay());
			break;
		case R.id.tv_tue:
			repeatWeekday(tvTue, WeekdayEnum.TUE.getDay());
			break;
		case R.id.tv_wen:
			repeatWeekday(tvWen, WeekdayEnum.WED.getDay());
			break;
		case R.id.tv_thu:
			repeatWeekday(tvThu, WeekdayEnum.THU.getDay());
			break;
		case R.id.tv_fri:
			repeatWeekday(tvFri, WeekdayEnum.FRI.getDay());
			break;
		case R.id.tv_sat:
			repeatWeekday(tvSat, WeekdayEnum.SAT.getDay());
			break;
		case R.id.rl_interval:
			Intent intervalIntent = new Intent(SportReminderActivity.this, SportReminderIntervalActivity.class);
			startActivityForResult(intervalIntent, REQ_INTERVAL);
			break;
		case R.id.rl_start:
			Intent startIntent = new Intent(SportReminderActivity.this, SportRemindStartTimeActivity.class);
			startActivityForResult(startIntent, REQ_STARTTIME);
			break;
		case R.id.rl_end:
			Intent endIntent = new Intent(SportReminderActivity.this, SportReminderEndTimeActivity.class);
			startActivityForResult(endIntent, REQ_ENDTIME);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvMon = (TextView) findViewById(R.id.tv_mon);
		tvTue = (TextView) findViewById(R.id.tv_tue);
		tvWen = (TextView) findViewById(R.id.tv_wen);
		tvThu = (TextView) findViewById(R.id.tv_thu);
		tvSat = (TextView) findViewById(R.id.tv_sat);
		tvFri = (TextView) findViewById(R.id.tv_fri);
		tvSun = (TextView) findViewById(R.id.tv_sun);
		tvIntervalValue = (TextView) findViewById(R.id.tv_interval_value);
		tvStartValue = (TextView) findViewById(R.id.tv_start_value);
		tvEndValue = (TextView) findViewById(R.id.tv_end_value);
		rlStart = (RelativeLayout) findViewById(R.id.rl_start);
		rlEnd = (RelativeLayout) findViewById(R.id.rl_end);
		rlInterval = (RelativeLayout) findViewById(R.id.rl_interval);

		initSetting();

		initWeekday();
	}

	/**
	 * 初始化设置
	 */
	private void initSetting() {
		// interval
		String reminder_interval = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_INTERVAL, "30");
		tvIntervalValue.setText(reminder_interval);
		// start
		String reminder_start_hour = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_START_HOUR, "07");
		String reminder_start_min = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_START_MIN, "00");
		tvStartValue.setText(reminder_start_hour + ":" + reminder_start_min);
		// end
		String reminder_end_hour = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_END_HOUR, "22");
		String reminder_end_min = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_END_MIN, "00");
		tvEndValue.setText(reminder_end_hour + ":" + reminder_end_min);

	}

	/**
	 * 初始化周期
	 */
	private void initWeekday() {
		repeatValue = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_REPEAT, 0b00000000);
		Log.d("OB", "初始化repeatValue-->" + Integer.toBinaryString(repeatValue));
		updateTextview(tvSun, WeekdayEnum.SUN.getDay());
		updateTextview(tvMon, WeekdayEnum.MON.getDay());
		updateTextview(tvTue, WeekdayEnum.TUE.getDay());
		updateTextview(tvWen, WeekdayEnum.WED.getDay());
		updateTextview(tvThu, WeekdayEnum.THU.getDay());
		updateTextview(tvFri, WeekdayEnum.FRI.getDay());
		updateTextview(tvSat, WeekdayEnum.SAT.getDay());
	}

	private void setClick() {
		imgBack.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		tvMon.setOnClickListener(this);
		tvTue.setOnClickListener(this);
		tvWen.setOnClickListener(this);
		tvThu.setOnClickListener(this);
		tvFri.setOnClickListener(this);
		tvSat.setOnClickListener(this);
		tvSun.setOnClickListener(this);
		rlStart.setOnClickListener(this);
		rlInterval.setOnClickListener(this);
		rlEnd.setOnClickListener(this);
	}

	/**
	 * 选择一个星期的重复
	 * 
	 * @param tv
	 * @param weekday
	 */
	private void repeatWeekday(TextView tv, int weekday) {
		repeatValue ^= weekday;
		Log.d("OB", tv.getId() + "-->" + Integer.toBinaryString(repeatValue));
		updateTextview(tv, weekday);
		SharedPreferenceUtil.put(getApplicationContext(), SHARE_REPEAT, repeatValue);
		//写入下位机  repeatValue ：01001111
		
	}

	/**
	 * 更新星期的状态（文本颜色） 选中时：橙色
	 * 
	 * @param tv
	 * @param weekday
	 */
	private void updateTextview(TextView tv, int weekday) {
		// 例如星期天：
		// 1111 1111 & 0000 0001 = 0000 0001; 开
		// 1111 1110 & 0000 0001 = 0000 0000; 关
		int color = (repeatValue & weekday) == weekday ? getResources().getColor(R.color.color_top_text_pressed)
				: getResources().getColor(R.color.grey);
		tv.setTextColor(color);
	}


	/**
	 * broadcastReceiver
	 * 
	 * @author Administrator
	 * 
	 */
	private class SportReminderBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BleService.BLE_GATT_CONNECTED)) {
				
				currentAddress = intent.getExtras().getString(BleService.EXTRA_ADDR);
				
			} else if (action.equals(BleService.BLE_GATT_DISCONNECTED)) {

			} else if (action.equals(BleService.BLE_CHARACTERISTIC_WRITE)) {

			} else if (action.equals(BleService.BLE_CHARACTERISTIC_READ)) {

			} else if (action.equals(BleService.BLE_CHARACTERISTIC_NOTIFICATION)) {

			}
		}
	}
}
