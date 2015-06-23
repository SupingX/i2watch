package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.List;

import com.xtremeprog.sdk.ble.BleManager;
import com.xtremeprog.sdk.ble.BleService;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectEvolveActivity extends Activity implements OnClickListener {

	private ListView mListView;
	private List<BluetoothDevice> devices;
	private BleDevicesAdapter mBleDevicesAdapter;
	private ImageView imgClose;
	private ImageView imgRefresh;
	private TextView tvState;
	private ProgressBar mProgressBar;
	// private boolean isScanning;
	// private String deviceAddr = "disconnected";
	// private boolean isConnected;

	private BleManager mBleManager;
	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}

	};
	
	private Runnable startOnOpen = new Runnable() {
		@Override
		public void run() {
			mBleManager.startScan();
			updateScanning();
		}
	};
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(ConnectEvolveActivity.this, "BLE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show();
					}
				});
			} else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
				// device found
				Bundle extras = intent.getExtras();
				final BluetoothDevice device = extras.getParcelable(BleService.EXTRA_DEVICE);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						addDevice(device);
						mBleDevicesAdapter.notifyDataSetChanged();
					}
				});
			} else if (BleService.BLE_NO_BT_ADAPTER.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(ConnectEvolveActivity.this, "No bluetooth adapter", Toast.LENGTH_SHORT).show();
					}
				});
			} else if (BleService.BLE_GATT_CONNECTED.equals(action)) { // 连接成功
				Log.e("connectEvolveActivity", "连接成功");
				Bundle extras = intent.getExtras();
				final BluetoothDevice device = extras.getParcelable(BleService.EXTRA_DEVICE);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBleManager.stopScan();
						mBleManager.setConnectted(true);
						mBleManager.setCurrentAddress(device.getAddress());
						updateCurrentDevice();
						updateScanning();
						// 回到主页
						returnToMain();
					}
				});
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBleManager.setConnectted(false);
						updateCurrentDevice();
						devices.clear();
						mBleManager.startScan();
						updateScanning();
					}
				});
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_connect);
		initViews();
		setClick();
		devices = new ArrayList<>();
		mBleDevicesAdapter = new BleDevicesAdapter();
		mListView.setAdapter(mBleDevicesAdapter);
		mBleManager = ((XtremApplication) getApplication()).getBleManager();
		registerReceiver(mReceiver, BleService.getIntentFilter());
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		Log.e("connectEvolveActivity", "获取 mBleManager : " + mBleManager);
//		Log.e("conn", mBleManager + "");
		updateCurrentDevice();
		if(!mBleManager.isEnabled()){
			mBleManager.open(ConnectEvolveActivity.this);
		} else {
			if(!mBleManager.isScanning()){
				Log.d("conn","postDelayed 500  :  " + !mBleManager.isScanning());
				mHandler.postDelayed(startOnOpen,500 );
			}
		}
		updateScanning();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mBleManager.isScanning()){
			mBleManager.stopScan();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mHandler.removeCallbacks(startOnOpen);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_CANCELED) {
				Log.e("connectEvovlveActivity", "蓝牙未打开");
				finish();
			} else if((resultCode == RESULT_OK) ){
				Log.e("connectEvovlveActivity", "蓝牙已打开");
				if(!mBleManager.isScanning()){
//					Log.d("conn","postDelayed 5000  :  " + !mBleManager.isScanning() );
//					mHandler.postDelayed(startOnOpen, 3000);
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_close:
			if (mBleManager.isScanning()) {
				mBleManager.stopScan();
			}
			finish();
			break;
		case R.id.img_refresh:
			devices.clear();
			mBleManager.startScan();
			mBleDevicesAdapter.notifyDataSetChanged();
			updateScanning();
			break;
		case R.id.tv_state:
			tvState.setText("关闭连接中...");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// getIble().stopScan();
					mBleManager.stopScan();
					updateScanning();
					mBleManager.disconnect();
					updateCurrentDevice();
					// finish();
				}
			}, 1000);
			break;
		case R.id.progressbar:
			mBleManager.stopScan();
			updateScanning();
			break;
	
		default:
			break;
		}
	
	}

	private void initViews() {
		mListView = (ListView) findViewById(R.id.listV);
		imgClose = (ImageView) findViewById(R.id.img_close);
		imgRefresh = (ImageView) findViewById(R.id.img_refresh);
		tvState = (TextView) findViewById(R.id.tv_state);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
	}

	private void setClick() {
		imgClose.setOnClickListener(this);
		imgRefresh.setOnClickListener(this);
		tvState.setOnClickListener(this);
		mProgressBar.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				String deviceAddr = devices.get(position).getAddress();
				mBleManager.setCurrentAddress(deviceAddr);
				mBleManager.stopScan();
				updateScanning();
				tvState.setText("正在连接至 ： " + deviceAddr + "...");
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(!mBleManager.isConnectted()){
							tvState.setText("连接失败...");
						}
					}
				}, 8000);
				mBleManager.connect();
			}
		});
	}
	
	/**
	 * list中添加设备
	 * 
	 * @param device
	 */
	private void addDevice(BluetoothDevice device) {
		if (!devices.contains(device)) {
			devices.add(device);
		}
	}

	/**
	 * 搜索状态
	 * 
	 * @param state
	 */
	private void updateScanning() {
		if (mBleManager.isScanning()) {
			imgRefresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			imgRefresh.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}

	}

	/**
	 * 在线蓝牙
	 * 
	 * @param device
	 */
	private void updateCurrentDevice() {
		Log.e("conn", "mBleManager.isConnectted() : " + mBleManager.isConnectted());
		Log.e("conn", "mBleManager.getCurrentAddress() : " + mBleManager.getCurrentAddress());
		if (mBleManager.isConnectted()) {
			tvState.setText(mBleManager.getCurrentAddress());
		} else {
			tvState.setText("[未连接]");
		}
	}

	/**
	 * 连接成功 返回主页面
	 * 
	 * @param address
	 */
	private void returnToMain() {
		Intent intent = new Intent(ConnectEvolveActivity.this, Main.class);
		startActivity(intent);
		finish();
	}


	/**
	 * 蓝牙adapter
	 * @author Administrator
	 * 
	 */
	private class BleDevicesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return devices.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return devices.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (devices.size() > 0) {
				ViewHolder holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_ble_devices, parent,
						false);
				holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
				holder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
				BluetoothDevice device = (BluetoothDevice) devices.get(position);
				holder.deviceName.setText(device.getName());
				;
				holder.deviceAddress.setText(device.getAddress());
				;
				return convertView;
			} else {
				return null;
			}
		}
		private class ViewHolder {
			private TextView deviceName;
			private TextView deviceAddress;
		}
	}

}
