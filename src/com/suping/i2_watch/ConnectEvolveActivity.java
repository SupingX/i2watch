package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.List;


import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

	private ListView lv_bleDevicesListView;
	private List<BluetoothDevice> devices;
	private BleDevicesAdapter mBleDevicesAdapter;
	private ImageView img_close;
	private ImageView img_refresh;
	private TextView tv_state;
	private ProgressBar mProgressBar;
	private boolean isScanning;
	private String deviceAddr = "disconnected";
	private boolean isConnected;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
				// runOnUiThread(new Runnable() {
				// @Override
				// public void run() {
				// Toast.makeText(ConnectEvolveActivity.this,
				// "Ble not support", Toast.LENGTH_SHORT).show();
				// finish();
				// }
				// });

			} else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
				// device found
				Bundle extras = intent.getExtras();
				final BluetoothDevice device = extras
						.getParcelable(BleService.EXTRA_DEVICE);
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
						Toast.makeText(ConnectEvolveActivity.this,
								"No bluetooth adapter", Toast.LENGTH_SHORT)
								.show();
						finish();
					}
				});
			} else if (BleService.BLE_GATT_CONNECTED.equals(action)) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						isConnected = true;
						isScanning =false;
						updateDevice();
						updateScanning();
					}
				});
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						isConnected = false;
						updateDevice();
						IBle mIbBle = getIble();
						devices.clear();
						mIbBle.startScan();
						
						isScanning = true;
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
		lv_bleDevicesListView.setAdapter(mBleDevicesAdapter);

	}

	@Override
	protected void onStart() {
		super.onStart();

		IBle mIble = getIble();
		if (mIble != null && !mIble.adapterEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, BleService.getIntentFilter());
		if (getIble() != null && getIble().adapterEnabled()) {
			getIble().startScan();
			mBleDevicesAdapter.notifyDataSetChanged();
		}
		updateDevice();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mReceiver);
		getIble().stopScan();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		IBle mIble = getIble();
		switch (v.getId()) {
		case R.id.img_close:
			if (mIble != null && mIble.adapterEnabled()) {
				getIble().stopScan();
				isScanning = false;
			}
			finish();
			break;
		case R.id.img_refresh:
			if (mIble != null && mIble.adapterEnabled()) {
				devices.clear();
				getIble().startScan();
				mBleDevicesAdapter.notifyDataSetChanged();
				isScanning = true;
				updateScanning();
			}
			break;
		case R.id.tv_state:
			if (mIble != null && mIble.adapterEnabled()) {
				tv_state.setText("关闭连接...");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getIble().stopScan();
						updateScanning();
						getIble().disconnect(deviceAddr);
						isConnected = false;
						updateDevice();
						// finish();
					}
				}, 1000);
			}
			break;
		case R.id.progressbar:
			if (mIble != null && mIble.adapterEnabled()) {
				getIble().stopScan();
				isScanning = false;
				updateScanning();
			}
			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initViews() {
		lv_bleDevicesListView = (ListView) findViewById(R.id.listV);
		img_close = (ImageView) findViewById(R.id.img_close);
		img_refresh = (ImageView) findViewById(R.id.img_refresh);
		tv_state = (TextView) findViewById(R.id.tv_state);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
	}

	private void setClick() {
		img_close.setOnClickListener(this);
		img_refresh.setOnClickListener(this);
		tv_state.setOnClickListener(this);
		mProgressBar.setOnClickListener(this);
		lv_bleDevicesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final IBle mIble = getIble();
				if (mIble != null && mIble.adapterEnabled()) {
					deviceAddr = devices.get(position).getAddress();
					tv_state.setText("正在连接至 ： " + deviceAddr + "...");
					mIble.requestConnect(deviceAddr);
//					mHandler.postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							mIble.requestConnect(deviceAddr);
//								isConnected = false;
//								updateDevice();
//						}
//					}, 10 * 1000);
				}
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
		if (isScanning) {
			img_refresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			img_refresh.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}

	}

	/**
	 * 在线蓝牙
	 * 
	 * @param device
	 */
	private void updateDevice() {
		if (isConnected) {
			tv_state.setText(deviceAddr);

		} else {
			tv_state.setText(deviceAddr + "[未连接]");
		}
	}

	/**
	 * 获取IBle
	 * 
	 * @return
	 */
	private IBle getIble() {
		XtremApplication app = (XtremApplication) getApplication();
		IBle mBle = app.getIBle();
		// Log.e("ble", "IBle: " + mBle);
		return mBle;
	}

	/**
	 * 蓝牙adapter
	 * 
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
			return null;
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
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.item_ble_devices, parent, false);
				holder.deviceName = (TextView) convertView
						.findViewById(R.id.device_name);
				holder.deviceAddress = (TextView) convertView
						.findViewById(R.id.device_address);
				BluetoothDevice device = (BluetoothDevice) devices
						.get(position);
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
