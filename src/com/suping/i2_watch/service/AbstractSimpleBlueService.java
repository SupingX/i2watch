package com.suping.i2_watch.service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public abstract class AbstractSimpleBlueService extends Service {
	private final String TAG = "SimpleBlueService";
	public final static String ACTION_DEVICE_FOUND = "ACTION_DEVICE_FOUND";
	public final static String ACTION_SERVICE_DISCOVERED_WRITE_DEVICE = "ACTION_SERVICE_DISCOVERED_WRITE_DEVICE";
	public final static String ACTION_REMOTE_RSSI = "ACTION_REMOTE_RSSI";
	public final static String ACTION_CONNECTION_STATE = "ACTION_CONNECTION_STATE";
	public final static String ACTION_SERVICE_DISCONNECTED_FOR_REMIND = "ACTION_SERVICE_DISCONNECTED_FOR_REMIND";
	public final static String ACTION_SERVICE_DISCOVERED_WRONG_DEVICE = "ACTION_SERVICE_DISCOVERED_WRONG_DEVICE";

	public final static String EXTRA_DEVICE = "EXTRA_DEVICE";
	public final static String EXTRA_RSSI = "EXTRA_RSSI";
	public final static String EXTRA_CONNECT_STATE = "EXTRA_CONNECT_STATE";
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private AbstractSimpleBluetooth mSimpleBluetooth;
	private MyBinder myBinder = new MyBinder();
	private boolean isScanning;
	private BluetoothGattService mBluetoothGattService;
	public Handler mHander = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0xabc:
				index++;
				if (index >= values.size()) {
					onWriteOver();
					index = 0;
					values.clear();
					mHander.removeCallbacks(runWrite);
				} else {
					mHander.postDelayed(runWrite, 3000);
				}
				break;

			default:
				break;
			}
		};
	};
	private int connectState;

	public void setConnectState(int state) {
		this.connectState = state;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public class MyBinder extends Binder {
		public AbstractSimpleBlueService getService() {
			return AbstractSimpleBlueService.this;
		}
	}

	public BluetoothGattService getBluetoothGattService() {
		return this.mBluetoothGattService;
	}

	public void scanDevice(boolean scan) {
		if (scan) {
			mHander.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (isScanning) {
						isScanning = false;
						mSimpleBluetooth.stopScan();
					}
						
				}
			}, 30 * 1000);
			isScanning = true;
			mSimpleBluetooth.startScan();
		} else {
			isScanning = false;
			mSimpleBluetooth.stopScan();
		}
	}

	/**
	 * 是否找到匹配的Service
	 */
	private boolean isWrieteServiceFound;
	private BluetoothGattCharacteristic characteristicWrite;

	public void writeCharacteristic(byte[] order) {
		try {
			logE("写数据");
			if (characteristicWrite != null) {
				characteristicWrite.setValue(order);
				mSimpleBluetooth.write(characteristicWrite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int index = 0;
	private List<byte[]> values = new ArrayList<>();
	/**
	 * 
	 * @param value
	 */
	private Runnable runWrite = new Runnable() {
		@Override
		public void run() {
			if (isBinded() && getConnectState() == BluetoothProfile.STATE_CONNECTED) {
				writeCharacteristic(values.get(index));
				L.i("蓝牙连接正常，继续写数据中 序号：" + index);
			} else {
				L.i("蓝牙连接失败，跳过 序号：" + index);
			}
			mHander.sendEmptyMessage(0xabc);
		}
	};

	/**
	 * 写一串数据
	 * 
	 * @param values
	 */
	public void writeValueToDevice(final List<byte[]> values) {
		if (values == null) {
			return;
		}
		if (values.size() <= 0) {
			return;
		}
		L.i("开始写一串数据 ，数据个数：" + values.size());
		// //清空数据
		// index = 0;
		// this.values.clear();
		// 获取数据
		this.values = values;
		mHander.post(runWrite);
	}

	public int getConnectState() {
		return connectState;
	}

	public boolean isScanning() {
		return this.isScanning;
	}

	public void close() {
		mSimpleBluetooth.close();
	}

	public void disconnect() {
		mSimpleBluetooth.disconnect();
	}

	public void connect(BluetoothDevice device) {
		mSimpleBluetooth.connect(device);
	}

	public void readRemoteRssi() {
		mSimpleBluetooth.readRemoteRssi();
	}

	public void unSaveDevice() {
		mSimpleBluetooth.unSaveDevice();
	};

	public boolean isBinded() {
		return mSimpleBluetooth.isBinded();
	}

	public boolean isEnable() {
		return mSimpleBluetooth.isEnable();
	}

	public String getBindedDeviceName() {
		return mSimpleBluetooth.getBindedName();
	}

	public String getBindedDeviceAddress() {
		return mSimpleBluetooth.getBindedAddress();
	}

	@Override
	public void onCreate() {
		logV("onCreate");
		if (this.mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		}
		if (mBluetoothAdapter == null) {
			// mBluetoothAdapter = mBluetoothManager.getAdapter();
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		mSimpleBluetooth = new SimpleBluetooth(getApplicationContext(), mBluetoothAdapter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		logE("service销毁！");
		mSimpleBluetooth.close();
		connectState = BluetoothProfile.STATE_DISCONNECTED;
		super.onDestroy();
	}

	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_DEVICE_FOUND);
		intentFilter.addAction(ACTION_SERVICE_DISCOVERED_WRITE_DEVICE);
		intentFilter.addAction(ACTION_SERVICE_DISCOVERED_WRONG_DEVICE);
		intentFilter.addAction(ACTION_REMOTE_RSSI);
		intentFilter.addAction(ACTION_CONNECTION_STATE);
		intentFilter.addAction(ACTION_SERVICE_DISCONNECTED_FOR_REMIND);
		return intentFilter;
	}

	private void logV(String msg) {
		Log.v(TAG, "**   " + msg + "  **");
	}

	private void logE(String msg) {
		Log.e(TAG, "**   " + msg + "  **");
	};

	private boolean isPrint = false;
	
	private long lastDiscoveredServiceTime = 0L;
	
	private class SimpleBluetooth extends AbstractSimpleBluetooth {

		public SimpleBluetooth(Context context, BluetoothAdapter mBluetoothAdapter) {
			super(context, mBluetoothAdapter);
		}

		@Override
		public void onLeScanCallBack(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			sendBroadcastForDeviceFound(device, rssi);
			String bindedName = getBindedName();
			String bindedAddress = getBindedAddress();
			// && device.getName().equals(bindedName)
			if (isBinded() && device.getAddress().equals(bindedAddress)) {
				Log.e("AbstractSimpleBluetooth", "bindedName,bindedAddress = " + bindedName + "," + bindedAddress);
				mHander.postDelayed(new Runnable() {
					@Override
					public void run() {
						connect(device);
					}
				}, 1000);

			}
		}

		@Override
		public void onServicesDiscoveredCallBack(BluetoothGatt gatt, int status) {

			doPrintDescoveredServices(gatt.getServices());
			mBluetoothGattService = gatt.getService(UUID.fromString(BLE_SERVICE));
			// gatt.setCharacteristicNotification(characteristicWrite, true);
			if (mBluetoothGattService != null) {
				Log.e("", "_______________匹配service______________");
				characteristicWrite = mBluetoothGattService.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_WRITE));
				BluetoothGattCharacteristic characteristicNotify = mBluetoothGattService.getCharacteristic(UUID.fromString(BLE_CHARACTERISTIC_NOTIFY));
				if (characteristicWrite != null && characteristicNotify != null) {
					characteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
					Log.e("", "_______________匹配characteristicWrite_________________________");
					Log.e("", "_______________匹配characteristicNotify_________________________");
					gatt.setCharacteristicNotification(characteristicNotify, true);
					BluetoothGattDescriptor descriptor = characteristicNotify.getDescriptor(UUID.fromString(DESC_CCC));
					descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					gatt.writeDescriptor(descriptor);
					saveDevice(gatt.getDevice());// 绑定蓝牙
					isWrieteServiceFound = true;
					
					//发送通知
					sendBroadcastForServiceDiscoveredWriteDevice(gatt, status);
					long currentTimeMillis = System.currentTimeMillis();
					//如果本次链接服务 和上次链接服务的时间差大于5秒 就做通知
					if (currentTimeMillis-lastDiscoveredServiceTime>15000) {
						L.i("服务距离上次链接超过5秒，所以需要重新同步手机设置");
						lastDiscoveredServiceTime = currentTimeMillis;
						onServicediscoveredSuccess();
					}else{
						L.i("服务距离上次链接没有超过5秒，所以不需要重新同步手机设置");
						
					}
					
					// 停止搜索？
					scanDevice(false);
				} else {
					// 没有连接匹配的服务,断开连接.防止连接不匹配的蓝牙?
					sendBroadcastForServiceDiscoveredWrongDevice();
					onServicediscoveredFail();
				}
			}else{
				sendBroadcastForServiceDiscoveredWrongDevice();
				onServicediscoveredFail();
			}
		}

		@Override
		public void onReadRemoteRssiCallBack(BluetoothGatt gatt, int rssi, int status) {
			sendBroadcastForRemoteRssi(gatt, rssi, status);
		}

		@Override
		public void onConnectionStateChangeCallBack(BluetoothGatt gatt, int status, int newState) {
			logE("-->  onConnectionStateChangeCallBack : [ newState : " + newState + "]");
			sendBroadcastForConnectionState(gatt, newState);
			connectState = newState;
			switch (newState) {
			case BluetoothProfile.STATE_CONNECTED:
				logV("已连接");

				gatt.discoverServices();
				break;
			case BluetoothProfile.STATE_DISCONNECTING:
				logV("断开中");
				connectState = newState;
				break;
			case BluetoothProfile.STATE_CONNECTING:
				logV("连接中");
				connectState = newState;
				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				logV("已断开");

				connectState = newState;
				
				onDisconnect();
				
				// 首先，清空一些数据
				isWrieteServiceFound = false; //
				mBluetoothGattService = null;// 清空Service
				characteristicWrite = null;// 清空 characteristic

				// 掉线时， 当设备绑定，就尝试重连
				if (mSimpleBluetooth.isBinded()) {
					BluetoothGatt bluetoothGatt = mSimpleBluetooth.getBluetoothGatt();
					if (bluetoothGatt != null) {
						bluetoothGatt.connect();
						// 5秒后再次检查是否连接并且找到服务成功，如果未成功则，关闭GATT。开启搜索
						mHander.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (!isWrieteServiceFound) {
									mSimpleBluetooth.close();// 清空Gatt
									scanDevice(true); // 开始搜索
									boolean isRemindOpen = (Boolean) SharedPreferenceUtil.get(getApplicationContext(), I2WatchProtocolDataForWrite.SHARE_NON, false);
									if (isRemindOpen) {
										sendBroadcastForRemind();
										onReconnectedOverTimeOut();
									}
								}

							}
						}, 5000);

					}
				}

				break;
			default:
				break;
			}

		}

		@Override
		public void onCharacteristicWriteCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

		}

		@Override
		public void onCharacteristicChangedCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			sendBroadcastForCharacteristicChanged(gatt, characteristic);
		}

	}

	public void sendBroadcastForDeviceFound(BluetoothDevice device, int rssi) {
		Intent intent = new Intent(ACTION_DEVICE_FOUND);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}

	public void doPrintDescoveredServices(List<BluetoothGattService> services) {
		if (isPrint) {
			if (services != null && services.size() > 0) {
				Log.i("", "_____________________service________________________");
				for (BluetoothGattService service : services) {
					Log.i("", "__service : " + service.getUuid());
					List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
					if (characteristics != null && characteristics.size() > 0) {
						Log.i("", "____________characteristic_____________");
						for (BluetoothGattCharacteristic characteristic : characteristics) {
							Log.i("", "_____characteristic : " + characteristic.getUuid());
						}
					}
				}
			}
		}
	}

	public void setIsPrint(boolean ip) {
		this.isPrint = ip;
	}

	public void sendBroadcastForCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		byte[] value = characteristic.getValue();
		parseData(value);
	}

	/**
	 * 抽象方法 子类来实现 解析数据 并分发通知
	 * 
	 * @param value
	 */
	public abstract void parseData(byte[] value);

	public void sendBroadcastForConnectionState(BluetoothGatt gatt, int newState) {
		Intent intent = new Intent(ACTION_CONNECTION_STATE);
		intent.putExtra(EXTRA_CONNECT_STATE, newState);
		sendBroadcast(intent);

	}

	public void sendBroadcastForRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
		Intent intent = new Intent(ACTION_REMOTE_RSSI);
		intent.putExtra(EXTRA_RSSI, rssi);
		sendBroadcast(intent);
	}

	public void sendBroadcastForServiceDiscoveredWriteDevice(BluetoothGatt gatt, int status) {
		Intent intent = new Intent(ACTION_SERVICE_DISCOVERED_WRITE_DEVICE);
		intent.putExtra(EXTRA_DEVICE, gatt.getDevice());
		sendBroadcast(intent);
	}

	/**
	 * 连接了错误的设备 不匹配service
	 */
	private void sendBroadcastForServiceDiscoveredWrongDevice() {
		Intent intent = new Intent(ACTION_SERVICE_DISCOVERED_WRONG_DEVICE);
		sendBroadcast(intent);
	}

	/**
	 * 断连连接超过5秒 ， 发送通知
	 */
	protected void sendBroadcastForRemind() {
		Intent intent = new Intent(ACTION_SERVICE_DISCONNECTED_FOR_REMIND);
		sendBroadcast(intent);
	}

	/** 找到匹配的设备 **/
	public abstract void onServicediscoveredSuccess();

	/** 重新连接超时 **/
	public abstract void onReconnectedOverTimeOut();

	/** 写数据结束 **/
	public abstract void onWriteOver();
	public abstract void onServicediscoveredFail();
	public abstract void onDisconnect();

}
