package com.suping.i2_watch.bluetooth;

import java.util.*;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.*;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("InlinedApi")
public class BleService extends Service {
	private static final String TAG = "BleService";
	private static final int SCAN_PERIOD = 10000;

	/** Intent for broadcast */
	public static final String BLE_NOT_SUPPORTED = "com.maginawin.bleguide.BLE_NOT_SUPPORTED";
	public static final String BLE_NOT_BT_ADAPTER = "com.maginawin.bleguide.BLE_NOT_BT_ADAPTER";
	public static final String BLE_STATUS_ABNORMAL = "com.maginawin.bleguide.BLE_STATUS_ABNORMAL";
	public static final String BLE_DEVICE_FOUND = "com.maginawin.bleguide.BLE_DEVICE_FOUND";
	public static final String BLE_DEVICE_SCANING = "com.maginawin.bleguide.BLE_DEVICE_SCAN";
	public static final String BLE_DEVICE_STOP_SCAN = "com.maginawin.bleguide.BLE_DEVICE_STOP_SCAN";
	public static final String BLE_GATT_CONNECTED = "com.maginawin.bleguide.BEL_GATT_CONNECTED";
	public static final String BLE_GATT_DISCONNECTED = "com.maginawin.bleguide.BLE_GATT_DISCONNECTED";
	public static final String BLE_SERVICE_DISCOVERED = "com.maginawin.bleguide.BLE_SERVICE_DISCOVERED";
	public static final String BLE_CHARACTERISTIC_READ = "com.maginawin.bleguide.BLE_CHARACTERISTIC_READ";
	public static final String BLE_CHARACTERISTIC_NOTIFICATION = "com.maginawin.bleguide.BLE_CHARACTERISTIC_NOTIFICATION";
	public static final String BLE_CHARACTERISTIC_WRITE = "com.maginawin.bleguide.BLE_CHARACTERISTIC_WRITE";
	public static final String BLE_CHARACTERISTIC_CHANGED = "com.maginawin.bleguide.BLE_CHARACTERISTIC_CHANGED";
	public static final String BLE_RSSI_READ = "com.maginawin.bleguide.BLE_RSSI_READ";

	/** Intent extras */
	public static final String EXTRA_DEVICE = "DEVICE";
	public static final String EXTRA_RSSI = "RSSI";
	public static final String EXTRA_SCAN_RECORD = "SCAN_RECORD";
	public static final String EXTRA_SOURCE = "SOURCE";
	public static final String EXTRA_ADDR = "ADDRESS";
	public static final String EXTRA_CONNECTED = "CONNECTED";
	public static final String EXTRA_STATUS = "STATUS";
	public static final String EXTRA_UUID = "UUID";
	public static final String EXTRA_VALUE = "VALUE";
	public static final String EXTRA_REQUEST = "REQUEST";
	public static final String EXTRA_REASON = "REASON";

	private final IBinder mBinder = new LocalBinder();
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothDevice mBluetoothDevice;
	private boolean isScanning;
	private Handler mHandler;
	private boolean isConnected = false;

	// private BluetoothGattCharacteristic mWriteChar;
	// private BluetoothGattCharacteristic mNotiChar;
	//

	private final LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			bleDeviceFound(device, rssi, scanRecord);
		}
	};

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			// TODO Auto-generated method stub
			super.onConnectionStateChange(gatt, status, newState);
			if (newState == BluetoothGatt.STATE_CONNECTED) {
				mBluetoothDevice = gatt.getDevice();
				isConnected = true;
				if (!gatt.discoverServices()) {
					Log.d(TAG, "discover services failure.");
				}
				bleGattConnected(mBluetoothDevice);
			} else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
				bleGattConnected(gatt.getDevice());
				isConnected = false;
				mBluetoothDevice = null;
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// TODO Auto-generated method stub
			super.onServicesDiscovered(gatt, status);
			bleServiceDiscovered(gatt.getDevice());
			List<BluetoothGattService> services = gatt.getServices();
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicRead(gatt, characteristic, status);
			bleCharacteristicRead(gatt.getDevice(), characteristic.getUuid().toString(), status,
					characteristic.getValue());
			;
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicWrite(gatt, characteristic, status);
			bleCharacteristicWrite(gatt.getDevice(), characteristic.getUuid().toString(), status);
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			// TODO Auto-generated method stub
			super.onCharacteristicChanged(gatt, characteristic);
			bleCharacteristicChange(gatt.getDevice(), characteristic.getUuid().toString(), characteristic.getValue());
			;
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// TODO Auto-generated method stub
			super.onReadRemoteRssi(gatt, rssi, status);
			bleReadRemoteRssi(gatt.getDevice(), rssi, status);
		}

	};

	public class LocalBinder extends Binder {
		public BleService getBleService() {
			return BleService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "on create");
		mHandler = new Handler();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		disconnectBleDevice();
		return super.onUnbind(intent);
	}

	public static IntentFilter getIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BLE_NOT_SUPPORTED);
		filter.addAction(BLE_NOT_BT_ADAPTER);
		filter.addAction(BLE_STATUS_ABNORMAL);
		filter.addAction(BLE_DEVICE_FOUND);
		filter.addAction(BLE_DEVICE_SCANING);
		filter.addAction(BLE_DEVICE_STOP_SCAN);
		filter.addAction(BLE_GATT_CONNECTED);
		filter.addAction(BLE_GATT_DISCONNECTED);
		filter.addAction(BLE_SERVICE_DISCOVERED);
		filter.addAction(BLE_CHARACTERISTIC_READ);
		filter.addAction(BLE_CHARACTERISTIC_NOTIFICATION);
		filter.addAction(BLE_CHARACTERISTIC_WRITE);
		filter.addAction(BLE_CHARACTERISTIC_CHANGED);
		return filter;
	}

	public BluetoothGatt getBluetoothGatt() {
		return mBluetoothGatt;
	}

	public void scanBleDevices(boolean isScan) {
		if (isBleEnabled()) {
			if (isScan) {
				if (!isScanning) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							isScanning = false;
							scanBleDevices(false);
						}
					}, SCAN_PERIOD);
					isScanning = true;
					mBluetoothAdapter.startLeScan(mLeScanCallback);
					bleDeviceScanning();
				}
			} else {
				isScanning = false;
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				bleDeviceStopScan();
			}
		}
	}

	public void connectBleDevice(BluetoothDevice device) {
		if (mBluetoothGatt != null
		// && mBluetoothDevice.getAddress().equals(device.getAddress())) {
				&& mBluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
			// if (isConnected) {
			if (mBluetoothGatt.connect()) {
				return;
			}
		}
		mBluetoothDevice = device;
		mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback);
	}

	public void connectBleDevice(String address) {
		if (mBluetoothGatt != null && mBluetoothDevice.getAddress().equals(address)) {
			if (mBluetoothGatt.connect()) {
				return;
			}
		}
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mBluetoothDevice = device;
		mBluetoothGatt = device.connectGatt(getApplicationContext(), true, mGattCallback);
	}

	public void disconnectBleDevice() {
		if (mBluetoothGatt != null) {
			// if (mBluetoothGatt.connect()) {
			mBluetoothGatt.disconnect();
			// }
			// mBluetoothGatt.close();
			// mBluetoothGatt = null;
		}
		bleGattDisconnected(mBluetoothDevice);
	}

	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
					Log.d(TAG, "write characteristic value : " + characteristic.getValue().toString() + " failure.");
				}
			}
		}
	}

	public void updateNotificaiton(BluetoothGattCharacteristic characteristic, boolean enable) {
		mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
		if (enable) {
			BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
					.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	protected boolean isBleEnabled() {
		boolean isEnabled = false;
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			bleStatusAbnormal();
		} else {
			if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
				if (mBluetoothAdapter != null) {
					isEnabled = true;
				}
			} else {
				Toast.makeText(getApplicationContext(), "ble not support", Toast.LENGTH_SHORT).show();
				bleNotSupported();
			}
		}
		return isEnabled;
	}

	protected void bleNotSupported() {
		Log.d(TAG, "ble not supported.");
		Intent intent = new Intent(BLE_NOT_SUPPORTED);
		sendBroadcast(intent);
	}

	protected void bleNotBTAdapter() {
		Log.d(TAG, "ble not bt adapter.");
		Intent intent = new Intent(BLE_NOT_BT_ADAPTER);
		sendBroadcast(intent);
	}

	protected void bleStatusAbnormal() {
		Log.d(TAG, "ble status abnormal.");
		Intent intent = new Intent(BLE_STATUS_ABNORMAL);
		sendBroadcast(intent);
	}

	protected void bleDeviceFound(BluetoothDevice device, int rssi, byte[] scanRecord) {
		Log.d(TAG, "device found " + device.getAddress());
		Intent intent = new Intent(BleService.BLE_DEVICE_FOUND);
		intent.putExtra(BleService.EXTRA_DEVICE, device);
		intent.putExtra(BleService.EXTRA_RSSI, rssi);
		intent.putExtra(BleService.EXTRA_SCAN_RECORD, scanRecord);
		sendBroadcast(intent);
	}

	protected void bleDeviceScanning() {
		Log.d(TAG, "ble device scanning.");
		Intent intent = new Intent(BleService.BLE_DEVICE_SCANING);
		sendBroadcast(intent);
	}

	protected void bleDeviceStopScan() {
		Log.d(TAG, "ble device stop scan.");
		Intent intent = new Intent(BleService.BLE_DEVICE_STOP_SCAN);
		sendBroadcast(intent);
	}

	protected void bleGattConnected(BluetoothDevice device) {
		Log.d(TAG, "ble gatt connected.");
		Intent intent = new Intent(BLE_GATT_CONNECTED);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		sendBroadcast(intent);
	}

	protected void bleGattDisconnected(BluetoothDevice device) {
		Log.d(TAG, "ble gatt disconnted.");
		Intent intent = new Intent(BLE_GATT_DISCONNECTED);
		// intent.putExtra(EXTRA_DEVICE, device);
		// intent.putExtra(EXTRA_ADDR, device.getAddress());
		sendBroadcast(intent);
	}

	protected void bleServiceDiscovered(BluetoothDevice device) {
		Log.d(TAG, "ble service discovered.");
		Intent intent = new Intent(BLE_SERVICE_DISCOVERED);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		sendBroadcast(intent);
	}

	protected void bleCharacteristicRead(BluetoothDevice device, String uuid, int status, byte[] value) {
		Log.d(TAG, "ble characteristic : " + uuid + "; value : " + value);
		Intent intent = new Intent(BLE_CHARACTERISTIC_READ);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		intent.putExtra(EXTRA_VALUE, value);
		sendBroadcast(intent);
	}

	protected void bleCharacteristicNotification(BluetoothDevice device, String uuid, boolean isEnabled, int status) {
		Log.d(TAG, "ble characteristic : " + uuid + " notification status : " + status);
		Intent intent = new Intent(BLE_CHARACTERISTIC_NOTIFICATION);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		intent.putExtra(EXTRA_VALUE, isEnabled);
		sendBroadcast(intent);
	}

	protected void bleCharacteristicWrite(BluetoothDevice device, String uuid, int status) {
		Log.d(TAG, "ble characteristic : " + uuid + " status : " + status);
		Intent intent = new Intent(BLE_CHARACTERISTIC_WRITE);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
	}

	protected void bleCharacteristicChange(BluetoothDevice device, String uuid, byte[] value) {
		Log.d(TAG, "ble characteristic : " + uuid + " value : " + value);
		Intent intent = new Intent(BLE_CHARACTERISTIC_CHANGED);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_ADDR, device.getAddress());
		intent.putExtra(EXTRA_UUID, uuid);
		intent.putExtra(EXTRA_VALUE, value);
		sendBroadcast(intent);
	}

	protected void bleReadRemoteRssi(BluetoothDevice device, int rssi, int status) {
		Log.d(TAG, "ble read remote rssi : " + rssi);
		Intent intent = new Intent(BLE_RSSI_READ);
		intent.putExtra(EXTRA_DEVICE, device);
		intent.putExtra(EXTRA_RSSI, rssi);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
}
