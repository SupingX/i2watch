package com.suping.i2_watch;

import com.suping.i2_watch.util.L;
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
import android.os.SystemClock;
import android.util.DisplayMetrics;
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

public class VirtualActivity extends Activity {

	private AudioManager audioManager;
	private RingtoneManager ringtoneManager;
	private DisplayMetrics dm;
	private Ringtone ringtone;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		//使屏幕在 锁屏状态下 ，还可以弹出。
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED 
				|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		//设置屏幕处于敞亮状态
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_virtual);
		
		findViews();
		
		Display defaultDisplay = getWindowManager().getDefaultDisplay();
		dm = new DisplayMetrics();
		defaultDisplay.getMetrics(dm);
		initRing();
		
		
		
	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String incoming_name = intent
				.getStringExtra("");
		String incoming_number = intent
				.getStringExtra("");

		if (incoming_name == null || incoming_name.equals("")) {
			incoming_name = "老王";
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
						if (ringtone.isPlaying()) {
							ringtone.stop();
						}
						chronometer.setVisibility(View.VISIBLE);
						slipView.setVisibility(View.GONE);
						rl_dial_content.setVisibility(View.VISIBLE);
						chronometer.setFormat("%s");
						// SystemClock.elapsedRealtime() 启动之后经过的时间
						// 从0开始计时
						chronometer.setBase(SystemClock.elapsedRealtime());
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
	protected void onDestroy() {
		chronometer.stop();
		if (ringtone.isPlaying()) {
			ringtone.stop();
		}
		super.onDestroy();
	}
	/**
	 * 设置铃声
	 */
	private void initRing() {
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//获取手机铃声音量
		int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
		L.v("音量：" +streamVolume);
		ringtoneManager = new RingtoneManager(this);
		ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
		ringtone = RingtoneManager.getRingtone(this, getSystemDefultRingtoneUri());
		ringtone.play();
	}
	
	private void findViews() {
		// TODO Auto-generated method stub
		slipView = (SlipView) this.findViewById(R.id.slipview);
		chronometer = (Chronometer) this.findViewById(R.id.call_time);
		rl_dial_content = (RelativeLayout) this
				.findViewById(R.id.rl_dial_content);
		tv_incoming_username = (TextView) this
				.findViewById(R.id.tv_incoming_username);
		tv_incoming_number = (TextView) this
				.findViewById(R.id.tv_incoming_phone_number);
		btn_hang_up = (Button) this.findViewById(R.id.btn_hang_up_phone);

	}


	/**
	 * 获取系统当前铃音的URI
	 * @return
	 */
	private Uri getSystemDefultRingtoneUri(){
		return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
	}
}
