package com.suping.i2_watch.menu;

import com.suping.i2_watch.BaseActivity;
import com.suping.i2_watch.R;
import com.suping.i2_watch.entity.AbstractProtocolWrite;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.entity.SportRemindProtocol;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.util.SharedPreferenceUtil;

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

/**
 * 来电提醒 设置： 开始时间 结束时间
 * 
 * @author Administrator
 *
 */
public class IncomingActivity extends BaseActivity implements OnClickListener{
	private ImageView imgBack;
	private TextView textViewTitle;
	private TextView textViewStart,textViewEnd;
	private RelativeLayout rlStart,rlEnd;
	private long exitTime = 0;
	private XBlueService xBlueService;
//	private XplBluetoothService xplBluetoothService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incoming);
		initViews();
		setClick();
		textViewTitle.setText(getResources().getString(R.string.incoming_reminder));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
//		xplBluetoothService = getXplBluetoothService();
		
		xBlueService = getXBlueService();
	}
	
	@Override
	protected void onDestroy() {
	
		super.onDestroy();
	}
	
	private void doWriteToWatch(){
		AbstractProtocolWrite protocolForCallingAlarmPeriodSync = I2WatchProtocolDataForWrite.protocolForCallingAlarmPeriodSync(this);
//		if (isXplConnected()) {
		if (xBlueService!=null && xBlueService.isAllConnected()) {
			
			
			byte[] byte1 = protocolForCallingAlarmPeriodSync.toByte();
//			xplBluetoothService.writeCharacteristic(byte1);
			xBlueService.write(byte1);
		}else{
			showShortToast(getString(R.string.no_binded_device));
		}
	}

	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			
			switch (requestCode) {
			case 1:
				switch (resultCode) {
				case RESULT_OK:
					Bundle b = data.getExtras();
					String min = b.getString("min");
					String sec = b.getString("sec");
					String value = min+":"+sec;
					//Log.e("incoming", "value : " + value);
					textViewStart.setText(value);
	//				SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_START, value);
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_START_HOUR, min);
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_START_MIN, sec);
					break;
				default:
					break;
				}
				break;
				//from IncomingEndtimeActivity
			case 2:
				switch (resultCode) {
				case RESULT_OK:
					Bundle b = data.getExtras();
					String min = b.getString("min");
					String sec = b.getString("sec");
					String value = min+":"+sec;
					textViewEnd.setText(value);
	//				SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_END, value);
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_END_HOUR, min);
					SharedPreferenceUtil.put(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_END_MIN, sec);
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
//			Intent retrunToMain = new Intent(IncomingActivity.this,MenuActivity.class);
//			startActivity(retrunToMain);
			doWriteToWatch();
			this.finish();
//			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
//					R.anim.activity_from_left_to_right_exit);
			break;
			
		case R.id.rl_start:
			Intent startIntent = new Intent(IncomingActivity.this,IncomingStarttimeActivity.class);
			startActivityForResult(startIntent, 1);
			break;
		case R.id.rl_end:
			Intent endIntent = new Intent(IncomingActivity.this,IncomingEndtimeActivity.class);
			startActivityForResult(endIntent, 2);
			break;
		}
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
//	        if((System.currentTimeMillis()-exitTime) > 2000){  
//	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
//	            exitTime = System.currentTimeMillis();   
//	        } else {
//	            finish();
//	            System.exit(0);
//	        }
//	        return true;   
//	    }
//	    return super.onKeyDown(keyCode, event);
//	}

	private void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		textViewTitle = (TextView) findViewById(R.id.tv_title);
		textViewStart = (TextView) findViewById(R.id.tv_start_value);
		textViewEnd = (TextView) findViewById(R.id.tv_end_value);
		rlStart = (RelativeLayout) findViewById(R.id.rl_start);
		rlEnd = (RelativeLayout) findViewById(R.id.rl_end);
		
		String incomingStartHour = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_START_HOUR, I2WatchProtocolDataForWrite.DEFAULT_START_HOUR);
		String incomingStartMin = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_START_MIN, I2WatchProtocolDataForWrite.DEFAULT_START_MIN);
		textViewStart.setText(incomingStartHour + ":" +incomingStartMin);
		
		String incomingEndHour = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_END_HOUR, I2WatchProtocolDataForWrite.DEFAULT_END_HOUR);
		String incomingEndMin = (String) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_INCOMING_END_MIN, I2WatchProtocolDataForWrite.DEFAULT_END_MIN);
		textViewEnd.setText(incomingEndHour + ":" + incomingEndMin);
		
	}
	
	
	@Override
	public void onBackPressed() {
		doWriteToWatch();
		super.onBackPressed();
	}
	private void setClick() {
		imgBack.setOnClickListener(this);
		textViewTitle.setOnClickListener(this);
		rlStart.setOnClickListener(this);
		rlEnd.setOnClickListener(this);
		
	}
}
