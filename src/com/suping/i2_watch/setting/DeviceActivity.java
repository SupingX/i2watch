package com.suping.i2_watch.setting;

import java.util.ArrayList;
import java.util.List;

import com.lee.pullrefresh.ui.PullToRefreshScrollView;
import com.suping.i2_watch.BaseActivity;
import com.suping.i2_watch.Main;
import com.suping.i2_watch.R;
import com.suping.i2_watch.R.id;
import com.suping.i2_watch.R.layout;
import com.suping.i2_watch.broadcastreceiver.SimpleBluetoothBroadcastReceiverBroadcastReceiver;
import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.view.ActionSheetDialog;
import com.suping.i2_watch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.suping.i2_watch.view.ActionSheetDialog.SheetItemColor;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
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
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceActivity extends BaseActivity implements OnClickListener {
	private boolean onceEnter = true;
	private ImageView imgExit;
	private TextView textViewTitle;
	private ImageView imgBack;
	private ProgressBar pBarRefresh;
	private TextView tvDeviceName;
	private TextView tvDeviceAddress;
	private ImageView img_refresh;
	private TextView tvState;
	private ListView mListView;
	private List<BluetoothDevice> devices;
	private BleDevicesAdapter mBleDevicesAdapter;
	private AbstractSimpleBlueService mSimpleBlueService;

	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	private SimpleBluetoothBroadcastReceiverBroadcastReceiver mReceiver = new SimpleBluetoothBroadcastReceiverBroadcastReceiver() {
		@Override
		public void doBlueDisconnect(int state) {
			super.doBlueDisconnect(state);
			runOnUiThread(new Runnable() {
				public void run() {
					updateBlueState();
				}
			});
		}

		@Override
		public void doDeviceFound(final BluetoothDevice device) {
			super.doDeviceFound(device);

			runOnUiThread(new Runnable() {
				public void run() {
					addDevice(device);
					mBleDevicesAdapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void doDiscoveredWriteService() {
			super.doDiscoveredWriteService();
			runOnUiThread(new Runnable() {
				public void run() {
					if (connectingDialog!=null && connectingDialog.isShowing()) {
						connectingDialog.dismiss();
					}
					returnToMain();
				}
			});
		}
		
		public void doDiscoveredWrongService() {
			if (connectingDialog!=null && connectingDialog.isShowing()) {
				connectingDialog.dismiss();
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_device);

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
		registerReceiver(mReceiver, SimpleBlueService.getIntentFilter());
	}

	@Override
	protected void onResume() {
		super.onResume();
		setCurrentDevice();
		checkBlue();
		updateBlueState();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onceEnter = true;
		unregisterReceiver(mReceiver);
		if (connectingDialog!=null && connectingDialog.isShowing()) {
			connectingDialog.dismiss();
		}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			mSimpleBlueService.scanDevice(false);
			finish();
			break;
		case R.id.img_refresh:
			Log.v("", "刷新搜索");
			pBarRefresh.setVisibility(View.VISIBLE);
			img_refresh.setVisibility(View.GONE);
			devices.clear();
			mSimpleBlueService.scanDevice(true);
			mBleDevicesAdapter.notifyDataSetChanged();
			break;
		case R.id.img_exit:
			// 当蓝牙是连接状态时，就可以关闭蓝牙连接。
			exitBinded();
			break;
		case R.id.progressbar:
			Log.v("", "停止搜索");
			pBarRefresh.setVisibility(View.GONE);
			img_refresh.setVisibility(View.VISIBLE);
			mSimpleBlueService.scanDevice(false);
			break;
		default:
			break;
		}

	}

	public void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		textViewTitle = (TextView) findViewById(R.id.tv_title);
		tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
		tvState = (TextView) findViewById(R.id.tv_state);
		tvDeviceAddress = (TextView) findViewById(R.id.tv_device_address);
		textViewTitle.setText("连接设备");
		mListView = (ListView) findViewById(R.id.lv_device);
		imgExit = (ImageView) findViewById(R.id.img_exit);
		img_refresh = (ImageView) findViewById(R.id.img_refresh);
		pBarRefresh = (ProgressBar) findViewById(R.id.progressbar);
	}

	private ProgressDialog connectingDialog;
	public void setListener() {
		imgBack.setOnClickListener(this);
		pBarRefresh.setOnClickListener(this);
		img_refresh.setOnClickListener(this);
		imgExit.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				final BluetoothDevice device = devices.get(position);
				connectingDialog = showProgressDialog("正在连接...",true);
				mSimpleBlueService.scanDevice(false);
					mSimpleBlueService.close();
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
	 * 解除绑定设备
	 */
	private void exitBinded() {
		if (mSimpleBlueService.isBinded()) {
			final ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
			dialog.setTitle("解除绑定");
			dialog.addSheetItem(getResources().getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
	
					mHandler.post(new Runnable() {
	
						@Override
						public void run() {
	
							// 1.先清除本地设备
							mSimpleBlueService.unSaveDevice();
							// 2.关闭连接（当时连接状态时）
							// if (mSimpleBlueService.getConnectState() ==
							// BluetoothProfile.STATE_CONNECTED) {
							mSimpleBlueService.close();
							mSimpleBlueService.setConnectState(BluetoothProfile.STATE_DISCONNECTED);
							Intent intent = new Intent(AbstractSimpleBlueService.ACTION_CONNECTION_STATE);
							intent.putExtra(AbstractSimpleBlueService.EXTRA_CONNECT_STATE, BluetoothProfile.STATE_DISCONNECTED);
							sendBroadcast(intent);
	
							// }
							// 3.更新UI
							updateBlueState();
							setCurrentDevice();
							// setDeviceConnectState(BluetoothProfile.STATE_DISCONNECTED);
						}
					});
				}
			}).show();
		} else {
			showShortToast("未绑定设备");
		}
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
	 * 在线蓝牙
	 * 
	 * @param device
	 */
	private void updateBlueState() {
		// if
		// (null!=mSimpleBlueService&&mSimpleBlueService.isBinded()&&mSimpleBlueService.getConnectState()==BluetoothProfile.STATE_CONNECTED)
		// {
		if (isConnected()) {
			tvState.setText("[已连接]");
		} else {
			tvState.setText("[未连接]");
		}

	}

	/**
	 * 设置本地绑定设备
	 */
	private void setCurrentDevice() {
		String name = mSimpleBlueService.getBindedDeviceName();
		String address = mSimpleBlueService.getBindedDeviceAddress();
		if (address.equals("")) {
			tvDeviceAddress.setText("[未绑定]");
			tvDeviceName.setText("");
			
			return ;
		}
		tvDeviceName.setText(name.equals("--") ? "" : "["+name+"]");
		tvDeviceAddress.setText(name.equals("--") ? "[未绑定]" : "["+address+"]");
	}

	/**
	 * 连接成功 返回主页面
	 * 
	 * @param address
	 */
	private void returnToMain() {
		Intent intent = new Intent(this, Main.class);
		startActivity(intent);
		finish();
	}

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
//							startActivityForResult(enableBtIntent, 1);
							startActivity(enableBtIntent);
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
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_device, parent, false);
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
