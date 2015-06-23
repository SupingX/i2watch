package com.suping.i2_watch.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.Main;
import com.suping.i2_watch.R;
import com.suping.i2_watch.XtremApplication;
import com.suping.i2_watch.enerty.I2WatchProtocolData;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.xtremeprog.sdk.ble.BleManager;

public class MenuActivity extends Activity implements OnClickListener {
	// 转发code
	/** SignSet requestCode **/
	private static final int REQ_SIGNSET = 0x200200;
	/** camera requestCode **/
	private static final int REQ_CAMERA = 0x20023300;
	// WedgetS
	private ImageView imgBack;
	private TextView tvTitle, tvSignset, tvBright;
	private Button btnIncrease, btnDecrease;
	private CheckBox cbActivityReminder, cbReminderOnOff, cbIncoming;
	private RelativeLayout rlActivityReminder, rlSleep, rlClock, rlCamera, rlSignset, rlIncoming, rlCallfaker,
			rlSearch, rlShutDown, rlRecord;

	// 连续按退出间隔时间
	private long exitTime = 0;
	/** 蓝牙  **/ 
	private BleManager mBleBleManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initViews();
		setClick();
		mBleBleManager = ((XtremApplication)getApplication()).getBleManager();
		Log.i("MenuActivity", "--mbleBleManager : " + mBleBleManager);
	}

	@Override
	protected void onResume() {
		init();
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		// from SignatureSetActivity
		case REQ_SIGNSET:
			if (resultCode == RESULT_OK) {
				String value = data.getExtras().getString("ed");
				tvSignset.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_SIGN_SET, value);
			} else if (resultCode == RESULT_CANCELED) {
			} else {
			}
			break;
		// from CameraActivity
		case REQ_CAMERA:
			if (resultCode == RESULT_OK) {
				//
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "相机权限被禁止使用！", Toast.LENGTH_SHORT).show();
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
			Intent retrunToMain = new Intent(MenuActivity.this, Main.class);
			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		// 活动
		case R.id.rl_reminder:
			Intent reminderIntent = new Intent(MenuActivity.this, SportReminderActivity.class);
			startActivity(reminderIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		// 睡眠
		case R.id.rl_sleep:
			Intent sleepIntent = new Intent(MenuActivity.this, SleepMonitorActivity.class);
			startActivity(sleepIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		// 闹钟
		case R.id.rl_clock:
			Intent clockIntent = new Intent(MenuActivity.this, ClockActivity.class);
			startActivity(clockIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		// 相机
		case R.id.rl_camera:
			
			Intent intent = new Intent(MenuActivity.this, CameraActivity.class);
			startActivity(intent);
			break;

		// 查找设备
		case R.id.rl_search:
			Intent intentSearch = new Intent(MenuActivity.this, SearchActivity.class);
			startActivity(intentSearch);
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		// 记录
		case R.id.rl_record:
			Intent intentRecord = new Intent(MenuActivity.this, RecordActivity.class);
			Bundle b1 = new Bundle();
			b1.putInt("flag", 0);
			intentRecord.putExtras(b1);
			startActivity(intentRecord);
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		//
		case R.id.rl_signset:
			Intent signsetIntent = new Intent(MenuActivity.this, SignatureSetActivity.class);
			String value = tvSignset.getText().toString().trim();
			Bundle b = new Bundle();
			b.putString("value", value);
			signsetIntent.putExtra("bundle", b);
			startActivityForResult(signsetIntent, REQ_SIGNSET);

			break;

		// 来电提醒
		case R.id.rl_incoming:
			Intent incomingIntent = new Intent(MenuActivity.this, IncomingActivity.class);
			startActivity(incomingIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		// 虚拟来电
		case R.id.rl_callfaker:
			Intent callfakerIntent = new Intent(MenuActivity.this, CallfakerActivity.class);
			startActivity(callfakerIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;

		// 增加
		case R.id.btn_decrease:
			int bright1 = Integer.valueOf(tvBright.getText().toString().trim());
			if (bright1 == 1) {
				tvBright.setText(String.valueOf(10));
			} else {
				tvBright.setText(String.valueOf(bright1 - 1));
			}
			SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_BRIGHT, tvBright.getText()
					.toString().trim());
			byte[] hexData1 = I2WatchProtocolData.hexDataForUpdateBrightness(this);
			mBleBleManager.writeCharactics(hexData1);
			break;

		// 减少
		case R.id.btn_increase:
			int bright2 = Integer.valueOf(tvBright.getText().toString().trim());
			if (bright2 == 10) {
				tvBright.setText(String.valueOf(1));
			} else {
				tvBright.setText(String.valueOf(bright2 + 1));
			}
			SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_BRIGHT, tvBright.getText()
					.toString().trim());
			byte[] hexData2 = I2WatchProtocolData.hexDataForUpdateBrightness(this);
			mBleBleManager.writeCharactics(hexData2);
			break;

		case R.id.rl_shutdown:
			
			Resources resources = getResources();
			String message = resources.getString(R.string.is_close);
			String confirm = resources.getString(R.string.confirm);
			String cancel = resources.getString(R.string.cancel);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(message).setPositiveButton(confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					byte[] hexData = I2WatchProtocolData.hexDataForCloseI2Watch();
					// XtremApplication.finishActivity();
					mBleBleManager.writeCharactics(hexData);
				}
			}).setNegativeButton(cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).create().show();
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
		tvSignset = (TextView) findViewById(R.id.tv_signset_value);
		tvBright = (TextView) findViewById(R.id.tv_bright_value);
		btnIncrease = (Button) findViewById(R.id.btn_increase);
		btnDecrease = (Button) findViewById(R.id.btn_decrease);
		rlActivityReminder = (RelativeLayout) findViewById(R.id.rl_reminder);
		rlSleep = (RelativeLayout) findViewById(R.id.rl_sleep);
		rlClock = (RelativeLayout) findViewById(R.id.rl_clock);
		rlCamera = (RelativeLayout) findViewById(R.id.rl_camera);
		rlSignset = (RelativeLayout) findViewById(R.id.rl_signset);
		rlIncoming = (RelativeLayout) findViewById(R.id.rl_incoming);
		rlCallfaker = (RelativeLayout) findViewById(R.id.rl_callfaker);
		rlSearch = (RelativeLayout) findViewById(R.id.rl_search);
		rlShutDown = (RelativeLayout) findViewById(R.id.rl_shutdown);
		rlRecord = (RelativeLayout) findViewById(R.id.rl_record);
		cbActivityReminder = (CheckBox) findViewById(R.id.cb_reminder);
		cbReminderOnOff = (CheckBox) findViewById(R.id.cb_reminder_nf);
		cbIncoming = (CheckBox) findViewById(R.id.cb_incoming);

	}

	/**
	 * 初始化值
	 */
	private void init() {
		// 标题
		tvTitle.setText(getResources().getString(R.string.menu));

		// signature
		String signature = (String) SharedPreferenceUtil.get(getApplicationContext(),
				I2WatchProtocolData.SHARE_SIGN_SET, getResources().getString(R.string.signature_is_not_set));
		tvSignset.setText(signature);

		// 亮度
		String bright = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolData.SHARE_BRIGHT,
				"5");
		tvBright.setText(bright);

		boolean reminder = (boolean) SharedPreferenceUtil.get(getApplicationContext(),
				I2WatchProtocolData.SHARE_ACTIVITY, false);
		cbActivityReminder.setChecked(reminder);
		boolean reminder_nf = (boolean) SharedPreferenceUtil.get(getApplicationContext(),
				I2WatchProtocolData.SHARE_NON, false);
		cbReminderOnOff.setChecked(reminder_nf);
		boolean reminder_incoming = (boolean) SharedPreferenceUtil.get(getApplicationContext(),
				I2WatchProtocolData.SHARE_INCOMING, false);
		// Log.e("menu", "incoming : " + String.valueOf(reminder_incoming));
		cbIncoming.setChecked(reminder_incoming);
	}

	private void setClick() {
		imgBack.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		rlActivityReminder.setOnClickListener(this);
		rlSleep.setOnClickListener(this);
		rlClock.setOnClickListener(this);
		rlCamera.setOnClickListener(this);
		rlSignset.setOnClickListener(this);
		btnIncrease.setOnClickListener(this);
		btnDecrease.setOnClickListener(this);
		rlIncoming.setOnClickListener(this);
		rlShutDown.setOnClickListener(this);
		rlCallfaker.setOnClickListener(this);
		rlSearch.setOnClickListener(this);
		rlRecord.setOnClickListener(this);

		// 活动开关
		cbActivityReminder.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_ACTIVITY, true);
				} else {
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_ACTIVITY, false);
				}
			}
		});

		// 防丢提醒开关
		cbReminderOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_NON, true);
				} else {
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_NON, false);
				}
				// 写入属性
				byte[] hexData = I2WatchProtocolData.hexDataForLostOnoffI2Watch(MenuActivity.this);
				mBleBleManager.writeCharactics(hexData);
			}
		});

		// 来电提醒开关
		cbIncoming.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_INCOMING, true);
				} else {
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolData.SHARE_INCOMING, false);
				}
			}
		});
	}

}
