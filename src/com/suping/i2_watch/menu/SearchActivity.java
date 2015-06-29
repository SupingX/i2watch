package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.suping.i2_watch.R;
import com.suping.i2_watch.XtremApplication;
import com.suping.i2_watch.entity.I2WatchProtocolData;
import com.suping.i2_watch.view.RadarView;
import com.xtremeprog.sdk.ble.BleManager;
import com.xtremeprog.sdk.ble.BleService;

public class SearchActivity extends Activity implements OnClickListener {
	private String TAG = this.getClass().getSimpleName();
//	private ImageView imgRomation;
//	private Animation operatingAnim;
	private TextView tvCancel;
	private TextView tvSearchInfo;
//	private boolean isConnected;
	private RadarView radar;
	
//确保类内部的handler不含有对外部类的隐式引用 
	private  Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mHandler.postDelayed(search, 6000);
				
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	private BleManager mBleManager;
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
			} else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
			} else if (BleService.BLE_NO_BT_ADAPTER.equals(action)) {
			} else if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBleManager.setConnectted(true);
						updateConnectedState();
					}
				});
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBleManager.setConnectted(false);
						updateConnectedState();
					}
				});
			}
		}
	};
	
	/**
	 * 开始旋转
	 */
	private Runnable start  = new Runnable() {
		@Override
		public void run() {
			radar.start();
		}
	};

	/**
	 * 停止旋转
	 */
	private Runnable stop  = new Runnable() {
		@Override
		public void run() {
			radar.stop();
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(new SearchView(SearchActivity.this));
		setContentView(R.layout.activity_search);
//		FrameLayout fl = (FrameLayout) findViewById(R.id.fl);
//		imgBack = new ImageView(SearchActivity.this);
//		imgBack.setImageResource(R.drawable.ic_search_bg);
//		FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
//				FrameLayout.LayoutParams.WRAP_CONTENT,
//				FrameLayout.LayoutParams.WRAP_CONTENT);
//		params.gravity = Gravity.CENTER;  
//		imgBack.setLayoutParams(params);
//		
//		imgRomation= new ImageView(SearchActivity.this);
//		imgBack.setImageResource(R.drawable.ic_search_rotation);
//		imgRomation.setLayoutParams(params);
//		fl.addView(imgBack);
//		fl.addView(imgRomation);
//		initAnimation();
		initViews();
		setLinstener();
		mBleManager = ((XtremApplication)getApplication()).getBleManager();
		Log.d(TAG, "mBleManager : " + mBleManager);
		mHandler.post(search);
	}
	
	private  Runnable search = new Runnable() {
		
		@Override
		public void run() {
			byte [] hexData = I2WatchProtocolData.hexDataForSearchI2Watch();
			mBleManager.writeCharactics(hexData);
			mHandler.sendEmptyMessage(1);
		}
		
	};
	
	@Override
	protected void onResume() {
		//for test
//		isConnected = true;
//		mHandler.post(start);
//		updateConnectedState();
		
		registerReceiver(mBroadcastReceiver, BleService.getIntentFilter());
		updateConnectedState();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mBroadcastReceiver);
		radar.stop();
		super.onPause();
	}
	
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		if (operatingAnim != null && imgRomation != null && operatingAnim.hasStarted()) {  
//			imgRomation.clearAnimation();  
//			imgRomation.startAnimation(operatingAnim);  
//	    }   
//		super.onConfigurationChanged(newConfig);
//	}
	@Override
	protected void onDestroy() {
		mHandler.removeCallbacks(search);
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_negative:
			finish();
			break;
		case R.id.radar:
			if (mBleManager.isConnectted()) {
				mHandler.post(stop);
//				isConnected = !isConnected;
				updateConnectedState();
			} else {
				mHandler.post(start);
//				isConnected = !isConnected;
				updateConnectedState();
			}
			break;
		default:
			break;
		}
	}

	private void initViews() {
		tvCancel =(TextView) findViewById(R.id.tv_negative);
		tvSearchInfo =(TextView) findViewById(R.id.tv_info);
		radar = (RadarView) findViewById(R.id.radar);
	}
	
	private void setLinstener(){
		tvCancel.setOnClickListener(this);
		radar.setOnClickListener(this);
	}
	
//	/**
//	 * 初始化旋转动画
//	 */
//	private void initAnimation(){
//		//加载布局文件
//		operatingAnim = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.retate);
//		//设置旋转速率 ： 
//		//LinearInterpolator ：匀速效果 
//		//AccelerateinterpolatoR ： 加速效果
//		//DecelerateInterpolator ： 减速效果
//		LinearInterpolator lin = new LinearInterpolator();
//		operatingAnim.setInterpolator(lin);
//	}
	
	/**
	 * 更新搜素状态：
	 *  旋转 表示 已连接手环
	 *  不转 表示 未连接手环
	 * @param isConnected
	 */
	private void updateConnectedState(){
		if(mBleManager.isConnectted()){
			tvSearchInfo.setVisibility(View.GONE);
			radar.start();
		} else {
			tvSearchInfo.setVisibility(View.VISIBLE);
			radar.stop();
		}
	}
}
