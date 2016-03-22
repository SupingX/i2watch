package com.suping.i2_watch;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.suping.i2_watch.broadcastreceiver.I2WPhoneStateListener;
import com.suping.i2_watch.broadcastreceiver.I2WPhoneStateListener.OnIncomingListener;
import com.suping.i2_watch.menu.CallfakerFromActivity;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.suping.i2_watch.view.SlipView;

import android.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VirtualActivity extends Activity implements OnIncomingListener {

	private AudioManager audioManager;
	private RingtoneManager ringtoneManager;
	private DisplayMetrics dm;
	private Ringtone myRingtone;

	/** 滑动接听挂断组件 */
	private SlipView slipView;
	private Chronometer chronometer;
	private RelativeLayout rl_dial_content;
	/** 来电username */
	private TextView tv_incoming_username;
	/** 来电号码 */
	private TextView tv_incoming_number;
	/** 挂断按钮 */
	private Button btn_hang_up;
	private TelephonyManager telephonyManager;
	private I2WPhoneStateListener i2wPhoneStateListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		// 使屏幕在 锁屏状态下 ，还可以弹出。
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		// 设置屏幕处于敞亮状态
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_virtual);

		findViews();

		Display defaultDisplay = getWindowManager().getDefaultDisplay();
		dm = new DisplayMetrics();
		defaultDisplay.getMetrics(dm);
		initRing();

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		i2wPhoneStateListener = new I2WPhoneStateListener();
		i2wPhoneStateListener.setOnIncomingListener(this);
		i2wPhoneStateListener.registerListenerForPhoneState(telephonyManager);
		
		// Intent intent = getIntent();
				String incoming_name = (String) SharedPreferenceUtil.get(getApplicationContext(), CallfakerFromActivity.SHARE_PHONE_NAME, "王经理");
				// = intent.getStringExtra("");
				String incoming_number = (String) SharedPreferenceUtil.get(getApplicationContext(), CallfakerFromActivity.SHARE_PHONE_NUMBER, "13047618255");
				// = intent.getStringExtra("");

				Log.e("", "incoming_name : " + incoming_name);
				Log.e("", "incoming_number : " + incoming_number);
				if (incoming_name == null || incoming_name.equals("")) {
					incoming_name = "王经理";
				}
				if (incoming_number == null || incoming_number.equals("")) {
					incoming_number = "15626570445";
				}
				tv_incoming_number.setText(incoming_number);
				tv_incoming_username.setText(incoming_name);
				btn_hang_up.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						VirtualActivity.this.finish();
					}
				});
				slipView.setOnPhoneSlipListener(new SlipView.OnPhoneSlipListener() {

					@Override
					public void onPhoneSliped(String type, float x) {
						// TODO Auto-generated method stub
						if (type.equals(SlipView.SLIP_LEFT)) {
							if (x > dm.widthPixels * 2 / 3) {
								if (myRingtone!=null && myRingtone.isPlaying()) {
									myRingtone.stop();
									myRingtone=null;
								}
								chronometer.setVisibility(View.VISIBLE);
								slipView.setVisibility(View.GONE);
								rl_dial_content.setVisibility(View.VISIBLE);
								chronometer.setFormat("%s");
								// SystemClock.elapsedRealtime() 启动之后经过的时间
								// 从0开始计时
								chronometer.setBase(SystemClock.elapsedRealtime());
								mHandler.removeCallbacks(ringR);
								chronometer.start();
							}
						} else if (type.equals(SlipView.SLIP_RIGHT)) {
							if (x < dm.widthPixels / 3) {
								VirtualActivity.this.finish();
							}
						}
					}
				});
		

	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		telephonyManager.listen(i2wPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		chronometer.stop();
		if (myRingtone!=null && myRingtone.isPlaying()) {
			myRingtone.stop();
			mHandler.removeCallbacks(ringR);
		}
		super.onDestroy();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:

				mHandler.postDelayed(ringR, 6000);
				break;

			default:
				break;
			}
		};
	};

	private Runnable ringR = new Runnable() {

		@Override
		public void run() {
			if (myRingtone != null) {
				myRingtone.play();
				mHandler.sendEmptyMessage(1);
			}
		}
	};

	/**
	 * 设置铃声
	 */
	private void initRing() {
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 获取手机铃声音量
//		int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
		ringtoneManager = new RingtoneManager(this);
		ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
		Uri uri = getSystemDefultRingtoneUri();
		Log.e("", "uri :" + uri);
		if (uri != null) {
			myRingtone = RingtoneManager.getRingtone(this, getSystemDefultRingtoneUri());
		}
		mHandler.removeCallbacks(ringR);
		mHandler.post(ringR);

	}

	private void findViews() {
		// TODO Auto-generated method stub
		slipView = (SlipView) this.findViewById(R.id.slipview);
		chronometer = (Chronometer) this.findViewById(R.id.call_time);
		rl_dial_content = (RelativeLayout) this.findViewById(R.id.rl_dial_content);
		tv_incoming_username = (TextView) this.findViewById(R.id.tv_incoming_username);
		tv_incoming_number = (TextView) this.findViewById(R.id.tv_incoming_phone_number);
		btn_hang_up = (Button) this.findViewById(R.id.btn_hang_up_phone);

	}

	/**
	 * 获取系统当前铃音的URI
	 * 
	 * @return
	 */
	private Uri getSystemDefultRingtoneUri() {
		return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
	}

	@Override
	public void onIncoming(int state, String incomingNumber) {
		// endCall();
	}

	/**
	 * 挂电话
	 */
	private void endCall() {
		Class<TelephonyManager> c = TelephonyManager.class;
		try {
			Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = null;
			iTelephony = (ITelephony) getITelephonyMethod.invoke((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE), (Object[]) null);
			iTelephony.endCall();
		} catch (Exception e) {
		}

	}

	@Override
	public void onOffhook(String incomingNumber) {
//		Log.e("xpl", "挂电话啦~回复正常音量！！！");
//		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		am.setRingerMode(AudioManager.MODE_NORMAL);
//		am.setRingerMode(AudioManager.MODE_RINGTONE);
	}
}
