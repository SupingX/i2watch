package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.List;

import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class ConnectEvolveActivity extends BaseActivity implements OnClickListener {

	private ListView mListView;
	private List<BluetoothDevice> devices;
	private BleDevicesAdapter mBleDevicesAdapter;
	private ImageView imgClose;
	private ImageView imgRefresh;
	private TextView tvState;
	private ProgressBar mProgressBar;
	private AbstractSimpleBlueService mSimpleBlueService;
	private ProgressDialog connectDialog;

	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	private BroadcastReceiver mLiteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ((AbstractSimpleBlueService.ACTION_DEVICE_FOUND).equals(action)) {
				// device found
				Bundle extras = intent.getExtras();
				final BluetoothDevice device = extras.getParcelable(AbstractSimpleBlueService.EXTRA_DEVICE);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						addDevice(device);
						mBleDevicesAdapter.notifyDataSetChanged();
					}
				});
			} else if ((AbstractSimpleBlueService.ACTION_CONNECTION_STATE).equals(action)) {

			} else if ((AbstractSimpleBlueService.ACTION_SERVICE_DISCOVERED_WRITE_DEVICE).equals(action)) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (connectDialog != null) {
							connectDialog.dismiss();
						}
						returnToMain();
					}
				});
				;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_connect);

		initViews();
		setListener();
		devices = new ArrayList<>();
		mBleDevicesAdapter = new BleDevicesAdapter();
		mListView.setAdapter(mBleDevicesAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mSimpleBlueService = getSimpleBlueService();
		registerReceiver(mLiteReceiver, SimpleBlueService.getIntentFilter());
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkBlue();
		updateCurrentDevice();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onceEnter = true;
		unregisterReceiver(mLiteReceiver);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_close:
			mSimpleBlueService.scanDevice(false);
			finish();
			break;
		case R.id.img_refresh:
			devices.clear();
			mSimpleBlueService.scanDevice(true);
			mBleDevicesAdapter.notifyDataSetChanged();
			updateScanning();
			break;
		case R.id.tv_state:
			// 当蓝牙是连接状态时，就可以关闭蓝牙连接。
			if (isConnected()) {
				tvState.setText("关闭连接中...");

				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// 1.先清除本地设备
						mSimpleBlueService.unSaveDevice();
						mSimpleBlueService.close();
						mSimpleBlueService.setConnectState(BluetoothProfile.STATE_DISCONNECTED);
						Intent intent = new Intent(AbstractSimpleBlueService.ACTION_CONNECTION_STATE);
						intent.putExtra(AbstractSimpleBlueService.EXTRA_CONNECT_STATE, BluetoothProfile.STATE_DISCONNECTED);
						sendBroadcast(intent);
						updateCurrentDevice();
						// stopScan();
						// updateScanning();
						// mLiteBlueService.closeAll();
						// updateCurrentDevice();
					}
				}, 1000);
			}
			break;
		case R.id.progressbar:
			mSimpleBlueService.scanDevice(false);
			updateScanning();
			break;
		default:
			break;
		}

	}

	public void initViews() {
		mListView = (ListView) findViewById(R.id.listV);
		imgClose = (ImageView) findViewById(R.id.img_close);
		imgRefresh = (ImageView) findViewById(R.id.img_refresh);
		tvState = (TextView) findViewById(R.id.tv_state);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
	}

	public void setListener() {
		imgClose.setOnClickListener(this);
		imgRefresh.setOnClickListener(this);
		tvState.setOnClickListener(this);
		mProgressBar.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				final BluetoothDevice device = devices.get(position);
				connectDialog = showProgressDialog("正在连接", true);
				if (mSimpleBlueService.getConnectState() == BluetoothProfile.STATE_CONNECTED) {
					mSimpleBlueService.close();
				}
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mSimpleBlueService.connect(device);
					}
				}, 1 * 000);
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
		if (mSimpleBlueService.isScanning()) {
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
		// if
		// (null!=mSimpleBlueService&&mSimpleBlueService.isBinded()&&mSimpleBlueService.getConnectState()==BluetoothProfile.STATE_CONNECTED)
		// {
		if (isConnected()) {
			String name = mSimpleBlueService.getBindedDeviceName();
			String address = mSimpleBlueService.getBindedDeviceAddress();
			tvState.setText(address.equals("") ? "[未连接]" : address);
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

	private boolean onceEnter = true;

	/**
	 * 检查蓝牙
	 */
	private void checkBlue() {
		Log.e("", "-----检查蓝牙-----");
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mSimpleBlueService != null) {
					// 确认蓝牙

					if (!mSimpleBlueService.isEnable()) {
						if (onceEnter) {
							Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
							startActivityForResult(enableBtIntent, 1);
							// showIosDialog();
							onceEnter = false;
						}
					} else {
						if (!(null != mSimpleBlueService && mSimpleBlueService.isBinded() && mSimpleBlueService.getConnectState() == BluetoothProfile.STATE_CONNECTED)) {
							if (mSimpleBlueService.isScanning()) {
								mSimpleBlueService.scanDevice(false);
							}
							mSimpleBlueService.scanDevice(true);
						}
					}
				} else {
					Log.e("", "-----检查蓝牙-----service为空");
				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		switch (requestCode) {
		case 1:

			if (resultCode == RESULT_OK) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mSimpleBlueService.isEnable()) {
							// 蓝牙打开
							mSimpleBlueService.scanDevice(true);
						} else {
							// 未打开
						}
					}
				}, 5000);
			}

			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, arg2);
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
			return devices.size();
		}

		@Override
		public Object getItem(int position) {
			return devices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_ble_devices, parent, false);
				holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
				holder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			BluetoothDevice device = (BluetoothDevice) devices.get(position);
			holder.deviceName.setText(device.getName());
			holder.deviceAddress.setText(device.getAddress());
			return convertView;
		}

		class ViewHolder {
			TextView deviceName;
			TextView deviceAddress;
		}
	}

}
