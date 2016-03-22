package com.suping.i2_watch.setting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.suping.i2_watch.BaseActivity;
import com.suping.i2_watch.Main;
import com.suping.i2_watch.R;
import com.suping.i2_watch.broadcastreceiver.ImpXplBroadcastReceiver;
import com.suping.i2_watch.service.AddressSaved;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.xblue.XBlueBroadcastReceiver;
import com.suping.i2_watch.service.xblue.XBlueBroadcastUtils;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.service.xblue.XBlueUtils;
import com.suping.i2_watch.view.ActionSheetDialog;
import com.suping.i2_watch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.suping.i2_watch.view.ActionSheetDialog.SheetItemColor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	private List<BluetoothDevice> deviceList;
	private BleDevicesAdapter mBleDevicesAdapter;
	// private XplBluetoothService xplBluetoothService;

	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	private XBlueBroadcastReceiver receiver = new XBlueBroadcastReceiver() {
		public void doDeviceFound(final java.util.ArrayList<BluetoothDevice> devices) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					deviceList.clear();
					if (devices!=null) {
						deviceList.addAll(devices);
					}
					
					mBleDevicesAdapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void doServiceDiscovered(BluetoothDevice device) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					tvState.setText(getString(R.string.info_connect));
					// updateBlueState();
					setCurrentDevice();
					if (connectingDialog != null && connectingDialog.isShowing()) {
						connectingDialog.dismiss();
					}
				}
			});
		}

		@Override
		public void doStepAndCalReceiver(long[] data) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doSyncStart() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doSyncEnd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doCamera() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void doConnectStateChange(BluetoothDevice device, final int state) {
			mHandler.post(new Runnable() {
				public void run() {
					if (state == BluetoothGatt.STATE_DISCONNECTED) {
						// tvState.setText(getString(R.string.info_unconnected));
						tvState.setText(getString(R.string.info_unconnected));
					}
					// updateBlueState();
				}
			});
			
		};
	};

	private ImpXplBroadcastReceiver xplReceiver = new ImpXplBroadcastReceiver() {
		public void doDeviceFound(final BluetoothDevice device, int rssi) {
			mHandler.post(new Runnable() {
				public void run() {
					addDevice(device);
					mBleDevicesAdapter.notifyDataSetChanged();
				}
			});
		};

		public void doConnectStateChange(BluetoothDevice device, final int state) {
			mHandler.post(new Runnable() {
				public void run() {
					if (state == BluetoothGatt.STATE_DISCONNECTED) {
						// tvState.setText(getString(R.string.info_unconnected));
						tvState.setText(getString(R.string.info_unconnected));
					}
					// updateBlueState();
				}
			});
		};

		public void doServiceDisCovered(BluetoothDevice device) {
			mHandler.post(new Runnable() {
				public void run() {
					tvState.setText(getString(R.string.info_connect));
					// updateBlueState();
					setCurrentDevice();
					if (connectingDialog != null && connectingDialog.isShowing()) {
						connectingDialog.dismiss();
					}
					// returnToMain();

				}
			});
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_device);

		initViews();
		setListener();
		deviceList = new ArrayList<>();
		mBleDevicesAdapter = new BleDevicesAdapter();
		mListView.setAdapter(mBleDevicesAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// xplBluetoothService = getXplBluetoothService();
		xBlueService = getXBlueService();
		Log.e("", "xBlueService : " + xBlueService);
		// registerReceiver(xplReceiver, XplBluetoothService.getIntentFilter());
		registerReceiver(receiver, XBlueBroadcastUtils.instance().getIntentFilter());
	}

	@Override
	protected void onResume() {
		super.onResume();
		setCurrentDevice();
		checkBlue();
		updateBlueState();

		xBlueService.startScan();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onceEnter = true;
		// unregisterReceiver(xplReceiver);
		unregisterReceiver(receiver);
		if (connectingDialog != null) {
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
						// if (mSimpleBlueService.isEnable()) {
						// // 蓝牙打开
						// // mSimpleBlueService.scanDevice(true);
						// } else {
						// // 未打开
						// }
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
			finish();
			break;
		case R.id.img_refresh:
			// Log.v("", "刷新搜索");
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					pBarRefresh.setVisibility(View.VISIBLE);
					img_refresh.setVisibility(View.GONE);
					// deviceList.clear();
					// mBleDevicesAdapter.notifyDataSetChanged();
					// if (xplBluetoothService!=null) {
					// xplBluetoothService.scanDevice(true);
					// }
					deviceList.clear();
					 mBleDevicesAdapter.notifyDataSetChanged();
					if (xBlueService != null) {
						xBlueService.startScan();
					}
				}
			});

			break;
		case R.id.img_exit:
			// 当蓝牙是连接状态时，就可以关闭蓝牙连接。
			exitBinded();
			break;
		case R.id.progressbar:
			// Log.v("", "停止搜索");
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					pBarRefresh.setVisibility(View.GONE);
					img_refresh.setVisibility(View.VISIBLE);
					/*
					 * if (xplBluetoothService!=null) {
					 * xplBluetoothService.scanDevice(false); }
					 */
					if (xBlueService != null) {
						xBlueService.stopScan();
					}
				}
			});

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
		textViewTitle.setText(getString(R.string.connect_device));
		mListView = (ListView) findViewById(R.id.lv_device);
		imgExit = (ImageView) findViewById(R.id.img_exit);
		img_refresh = (ImageView) findViewById(R.id.img_refresh);
		pBarRefresh = (ProgressBar) findViewById(R.id.progressbar);

	}

	private android.app.AlertDialog connectingDialog;
	private XBlueService xBlueService;

	public void setListener() {
		imgBack.setOnClickListener(this);
		pBarRefresh.setOnClickListener(this);
		img_refresh.setOnClickListener(this);
		imgExit.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						connectingDialog = showLoadingDialog(getString(R.string.connectting), false);
						/*
						 * xplBluetoothService.scanDevice(false); //
						 * AddressSaved.disBindDevice(getApplicationContext());
						 * // xplBluetoothService.setCurrentAddressEmpty(); //
						 * setCurrentDevice(); xplBluetoothService.close();
						 * updateBlueState(); mHandler.postDelayed(new
						 * Runnable() {
						 * 
						 * @Override public void run() {
						 * xplBluetoothService.connect(device.getAddress()); }
						 * }, 2 * 1000);
						 */

						if (xBlueService!=null) {
						xBlueService.stopScan();
							xBlueService.closeAll();
					
						BluetoothDevice device = deviceList.get(position);
						XBlueUtils.clear(DeviceActivity.this);
						XBlueUtils.saveBlue(DeviceActivity.this, device.getAddress());
						setCurrentDevice();
//						device.createBond();
						xBlueService.connect(device);
						}
					}
				});
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						
							if (connectingDialog != null&& connectingDialog.isShowing()) {
								connectingDialog.dismiss();
							}
							
							if (xBlueService==null || !xBlueService.isAllConnected()) {
								showIosDialog(DeviceActivity.this, "连接超时，请重新连接");
							}
						}
				}, 12 * 1000);
			}
		});
	}

	/**
	 * 解除绑定设备
	 */
	private void exitBinded() {
//		if (!AddressSaved.getBindedAddress(this).equals("")) {
		if (!XBlueUtils.getBlue(this).equals("")) {
			final ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
			dialog.setTitle(getString(R.string.unbind));
			dialog.addSheetItem(getResources().getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {

					mHandler.post(new Runnable() {

						@Override
						public void run() {
							
							XBlueUtils.clear(DeviceActivity.this);
							
							if (xBlueService!=null) {
								xBlueService.closeAll();
							}
							setCurrentDevice();
							updateBlueState();
							
							/*
							 * // 1.先清除本地设备
							 * AddressSaved.disBindDevice(getApplicationContext
							 * ());
							 * xplBluetoothService.setCurrentAddressEmpty(); //
							 * 2.关闭连接（当时连接状态时） // mSimpleBlueService.close(); //
							 * mSimpleBlueService
							 * .setConnectState(BluetoothProfile
							 * .STATE_DISCONNECTED); // Intent intent = new
							 * Intent
							 * (AbstractSimpleBlueService.ACTION_CONNECTION_STATE
							 * ); // intent.putExtra(AbstractSimpleBlueService.
							 * EXTRA_CONNECT_STATE,
							 * BluetoothProfile.STATE_DISCONNECTED); //
							 * sendBroadcast(intent);
							 * xplBluetoothService.close();
							 * 
							 * // } // 3.更新UI updateBlueState();
							 * setCurrentDevice(); //
							 * setDeviceConnectState(BluetoothProfile
							 * .STATE_DISCONNECTED);
							 */}
					});
				}
			}).show();
		} else {
			showShortToast(R.string.no_binded_device);
		}
	}

	/**
	 * list中添加设备
	 * 
	 * @param device
	 */
	private void addDevice(BluetoothDevice device) {
		if (!deviceList.contains(device)) {
			deviceList.add(device);
		}
	}

	/**
	 * 在线蓝牙
	 * 
	 * @param device
	 */
	private void updateBlueState() {
		if (xBlueService.isAllConnected()) {
			tvState.setText(getString(R.string.info_connect));
		} else {
			tvState.setText(getString(R.string.info_unconnected));
		}

	}

	/**
	 * 设置本地绑定设备
	 */
	private void setCurrentDevice() {
		// String address = AddressSaved.getBindedAddress(this);
		// if (address.equals("")) {
		// tvDeviceAddress.setText(getString(R.string.un_binded));
		// address="--";
		// }
		// tvDeviceAddress.setText("[" + address + "]");
//		HashSet<String> blues = XBlueUtils.getBlues(this);
		String blue = XBlueUtils.getBlue(this);
		if (blue .equals("")) {
			tvDeviceAddress.setText(getString(R.string.un_binded));
		} else {
			tvDeviceAddress.setText(blue);
		}
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
		// Log.e("", "-----检查蓝牙-----");
		/*
		 * mHandler.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { if (xplBluetoothService != null) { if
		 * (!xplBluetoothService.isBluetoothEnable()) { if (onceEnter) { Intent
		 * enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		 * // startActivityForResult(enableBtIntent, 1);
		 * startActivity(enableBtIntent); // showIosDialog(); onceEnter = false;
		 * } } else { if (!(null != mSimpleBlueService &&
		 * mSimpleBlueService.isBinded() && mSimpleBlueService.getConnectState()
		 * == BluetoothProfile.STATE_CONNECTED)) { if
		 * (mSimpleBlueService.isScanning()) {
		 * mSimpleBlueService.scanDevice(false); }
		 * mSimpleBlueService.scanDevice(true); }
		 * 
		 * // if (isXplConnected()) { // xplBluetoothService.scanDevice(true);
		 * // } } } else { // Log.e("", "-----检查蓝牙-----service为空"); }
		 * 
		 * } },1000);
		 */

		if (!xBlueService.isAllConnected()) {
			xBlueService.startScan();
		}
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
			return deviceList.size();
		}

		@Override
		public Object getItem(int position) {
			return deviceList.get(position);
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
			BluetoothDevice device = (BluetoothDevice) deviceList.get(position);
			String name = device.getName();
			if (name==null || name.equals("")) {
				name= "WATCH";
			}
			holder.deviceName.setText(name);
			holder.deviceAddress.setText(device.getAddress());
			return convertView;
		}

		class ViewHolder {
			TextView deviceName;
			TextView deviceAddress;
		}
	}
}
