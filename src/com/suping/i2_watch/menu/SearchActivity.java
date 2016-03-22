package com.suping.i2_watch.menu;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.suping.i2_watch.BaseActivity;
import com.suping.i2_watch.R;
import com.suping.i2_watch.broadcastreceiver.ImpXplBroadcastReceiver;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.xblue.XBlueBroadcastReceiver;
import com.suping.i2_watch.service.xblue.XBlueBroadcastUtils;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.view.RadarView;

public class SearchActivity extends BaseActivity implements OnClickListener {
	private String TAG = this.getClass().getSimpleName();
	// private ImageView imgRomation;
	// private Animation operatingAnim;
	private TextView tvCancel;
	private TextView tvSearchInfo;
	// private boolean isConnected;
	private RadarView radar;

	// 确保类内部的handler不含有对外部类的隐式引用
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mHandler.postDelayed(search, 2500);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	private XBlueBroadcastReceiver receiver = new XBlueBroadcastReceiver() {
		public void doDeviceFound(final java.util.ArrayList<BluetoothDevice> devices) {
		}

		@Override
		public void doServiceDiscovered(BluetoothDevice device) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					updateConnectedState();
				}
			});
		}

		@Override
		public void doStepAndCalReceiver(final long[] data) {
			
		}

		@Override
		public void doSyncStart() {
		}

		@Override
		public void doSyncEnd() {
			
		}

		@Override
		public void doCamera() {
			
		}

		@Override
		public void doConnectStateChange(BluetoothDevice device, int state) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					updateConnectedState();
				}
			});
		};
	};
//	private XplBluetoothService xplBluetoothService;
	private ImpXplBroadcastReceiver xplReceiver = new ImpXplBroadcastReceiver() {
		public void doConnectStateChange(
				android.bluetooth.BluetoothDevice device, int state) {

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					updateConnectedState();
				}
			});

		};

		public void doServiceDisCovered(android.bluetooth.BluetoothDevice device) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					updateConnectedState();
				}
			});
		};

	};


	/**
	 * 开始旋转
	 */
	private Runnable start = new Runnable() {
		@Override
		public void run() {
			radar.start();
		}
	};

	/**
	 * 停止旋转
	 */
	private Runnable stop = new Runnable() {
		@Override
		public void run() {
			radar.stop();
		}
	};
	private XBlueService xBlueService;

//	private AbstractSimpleBlueService mSimpleBlueService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(new SearchView(SearchActivity.this));
		setContentView(R.layout.activity_search);
		// FrameLayout fl = (FrameLayout) findViewById(R.id.fl);
		// imgBack = new ImageView(SearchActivity.this);
		// imgBack.setImageResource(R.drawable.ic_search_bg);
		// FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
		// FrameLayout.LayoutParams.WRAP_CONTENT,
		// FrameLayout.LayoutParams.WRAP_CONTENT);
		// params.gravity = Gravity.CENTER;
		// imgBack.setLayoutParams(params);
		//
		// imgRomation= new ImageView(SearchActivity.this);
		// imgBack.setImageResource(R.drawable.ic_search_rotation);
		// imgRomation.setLayoutParams(params);
		// fl.addView(imgBack);
		// fl.addView(imgRomation);
		// initAnimation();
		initViews();
		setLinstener();
//		registerReceiver(xplReceiver, XplBluetoothService.getIntentFilter());
		xBlueService = getXBlueService();
		registerReceiver(receiver, XBlueBroadcastUtils.instance().getIntentFilter());

	}

	private Runnable search = new Runnable() {
		@Override
		public void run() {
//			if (isXplConnected()) {
			if (xBlueService!=null && xBlueService.isAllConnected()) {
				byte[] hexData = I2WatchProtocolDataForWrite
						.hexDataForSearchI2Watch();
//				xplBluetoothService.writeCharacteristic(hexData);
				xBlueService.write(hexData);
		
			}
			mHandler.sendEmptyMessage(1);
		}

	};

	@Override
	protected void onStart() {
		super.onStart();
//		xplBluetoothService = getXplBluetoothService();
	}

	@Override
	protected void onResume() {
		// for test
		// isConnected = true;
		mHandler.post(start);
		// updateConnectedState();
		mHandler.post(search);
		updateConnectedState();
		super.onResume();
	}

	@Override
	protected void onPause() {
		radar.stop();
		super.onPause();
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// if (operatingAnim != null && imgRomation != null &&
	// operatingAnim.hasStarted()) {
	// imgRomation.clearAnimation();
	// imgRomation.startAnimation(operatingAnim);
	// }
	// super.onConfigurationChanged(newConfig);
	// }
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
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
			// if (mBleManager.isConnectted()) {
			// mHandler.post(stop);
			// // isConnected = !isConnected;
			// updateConnectedState();
			// } else {
			// mHandler.post(start);
			// // isConnected = !isConnected;
			// updateConnectedState();
			// }
			break;
		default:
			break;
		}
	}

	private void initViews() {
		tvCancel = (TextView) findViewById(R.id.tv_negative);
		tvSearchInfo = (TextView) findViewById(R.id.tv_info);
		radar = (RadarView) findViewById(R.id.radar);
	}

	private void setLinstener() {
		tvCancel.setOnClickListener(this);
		radar.setOnClickListener(this);
	}

	// /**
	// * 初始化旋转动画
	// */
	// private void initAnimation(){
	// //加载布局文件
	// operatingAnim = AnimationUtils.loadAnimation(SearchActivity.this,
	// R.anim.retate);
	// //设置旋转速率 ：
	// //LinearInterpolator ：匀速效果
	// //AccelerateinterpolatoR ： 加速效果
	// //DecelerateInterpolator ： 减速效果
	// LinearInterpolator lin = new LinearInterpolator();
	// operatingAnim.setInterpolator(lin);
	// }

	/**
	 * 更新搜素状态： 旋转 表示 已连接手环 不转 表示 未连接手环
	 * 
	 * @param isConnected
	 */
	private void updateConnectedState() {
//		if (isXplConnected()) {
		if (xBlueService!=null && xBlueService.isAllConnected()) {
			tvSearchInfo.setVisibility(View.GONE);
		} else {
			tvSearchInfo.setVisibility(View.VISIBLE);
		}
	}
}
