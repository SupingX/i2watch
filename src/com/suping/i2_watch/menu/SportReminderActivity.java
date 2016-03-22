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

import com.suping.i2_watch.BaseActivity;
import com.suping.i2_watch.R;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.entity.SportRemindProtocol;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class SportReminderActivity extends BaseActivity implements OnClickListener {
	// requestcode & resultCode
	private final static int REQ_INTERVAL = 1;
	private final static int REQ_STARTTIME = 2;
	private final static int REQ_ENDTIME = 3;
	private final static int RESULT_INTERVAL = 11;
	private final static int RESULT_STARTTIME = 22;
	private final static int RESULT_ENDTIME = 33;
	/** ImageView 返回    **/
	private ImageView imgBack;
	/** TextView 周一~周日  间隔/开始/结束 时间   **/
	private TextView tvTitle, tvMon, tvTue, tvWen, tvThu, tvSat, tvFri, tvSun, tvIntervalValue, tvStartValue,
			tvEndValue;
	/** RelativeLayout  间隔/开始/结束    **/
	private RelativeLayout rlStart, rlEnd, rlInterval;
	/** 双击间隔时间    **/ 
	private long exitTime = 0;
	/** 重复次数    **/ 
	private int repeatValue;
	private XBlueService xBlueService;
//	private XplBluetoothService xplBluetoothService;
	/** broadcastReceiver  **/ 
	/** 蓝牙  **/ 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		initViews();
		setClick();
		tvTitle.setText(getResources().getString(R.string.sport_reminder));
//		xplBluetoothService = getXplBluetoothService();
		
		xBlueService = getXBlueService();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	
	
	@Override
	protected void onDestroy() {
	
		
		super.onDestroy();
	}
	
	
	private void writeToWatch(){
		SportRemindProtocol sp = I2WatchProtocolDataForWrite.protocolDataForActivityRemindSync(this);
//		Log.e("SportReminderActivity", "待发送的协议-运动提醒  : " + sp.toString());
//		if (isConnected()) {
//		if (isXplConnected()) {
		if (xBlueService!=null && xBlueService.isAllConnected()) {
			
//			Log.e("SportReminderActivity", "待发送的协议-运动提醒  : " + sp.toString());
//			getSimpleBlueService().writeCharacteristic(sp.toByte());
//			xplBluetoothService.writeCharacteristic(sp.toByte());
			xBlueService.write(sp.toByte());
		}else{
			showShortToast(getString(R.string.no_binded_device));
		}
	}
	
	@Override
	public void onBackPressed() {
		writeToWatch();
		super.onBackPressed();
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
				if (time !=null) {
					tvIntervalValue.setText(time);
//					Log.e("", "运动间隔 :"+ time);
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INTERVAL, time);
				}
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
				SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_START_HOUR, min);
				SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_START_MIN, sec);
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
				SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_END_HOUR, min);
				SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_END_MIN, sec);
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
//			Intent retrunToMain = new Intent(SportReminderActivity.this, MenuActivity.class);
//			startActivity(retrunToMain);
			writeToWatch();
			this.finish();
//			overridePendingTransition(R.anim.activity_from_left_to_right_enter, R.anim.activity_from_left_to_right_exit);
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

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//			if ((System.currentTimeMillis() - exitTime) > 2000) {
//				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//				exitTime = System.currentTimeMillis();
//			} else {
//				finish();
//				System.exit(0);
//			}
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

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
		String reminder_interval = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INTERVAL, I2WatchProtocolDataForWrite.DEFAULT_INTERVAL);
		tvIntervalValue.setText(reminder_interval);
		// start
		String reminder_start_hour = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_START_HOUR, I2WatchProtocolDataForWrite.DEFAULT_START_HOUR);
		String reminder_start_min = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_START_MIN, I2WatchProtocolDataForWrite.DEFAULT_START_MIN);
		tvStartValue.setText(reminder_start_hour + ":" + reminder_start_min);
		// end
		String reminder_end_hour = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_END_HOUR, I2WatchProtocolDataForWrite.DEFAULT_END_HOUR);
		String reminder_end_min = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_END_MIN, I2WatchProtocolDataForWrite.DEFAULT_END_MIN);
		tvEndValue.setText(reminder_end_hour + ":" + reminder_end_min);

	}

	/**
	 * 初始化周期
	 */
	private void initWeekday() {
		repeatValue = (int) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_REPEAT, 0b01111111);
		//Log.d("OB", "初始化repeatValue-->" + Integer.toBinaryString(repeatValue));
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
		//Log.d("OB", tv.getId() + "-->" + Integer.toBinaryString(repeatValue));
		updateTextview(tv, weekday);
		SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_REPEAT, repeatValue);
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


}
