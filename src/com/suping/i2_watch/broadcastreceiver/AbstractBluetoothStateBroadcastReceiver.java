package com.suping.i2_watch.broadcastreceiver;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

public abstract class AbstractBluetoothStateBroadcastReceiver extends BroadcastReceiver {
	private boolean isDebug = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			if (isDebug) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				System.out.println("=======BluetoothDevice.ACTION_ACL_CONNECTED============");
				System.out.println("== 设备:" + device.getAddress() + " ==");
				System.out.println("== 连接状态:" + "已连接" + " ==");
				System.out.println("=======================================================");
			}
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			onBluetoothDisconnect(device);
			if (isDebug) {
				System.out.println("=======BluetoothDevice.ACTION_ACL_DISCONNECTED=======");
				System.out.println("== 设备:" + device.getAddress() + " ==");
				System.out.println("== 连接状态:" + "已断开连接" + " ==");
				System.out.println("=====================================================");
			}
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
			if (isDebug) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				System.out.println("=======BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED=======");
				System.out.println("== 设备:" + device.getAddress() + " ==");
				System.out.println("== 连接状态:" + "正准备断开连接" + " ==");
				System.out.println("=============================================================");
			}
		} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int state = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE);
				int previousState = intent.getExtras().getInt(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE);
				Log.e("","=======BluetoothDevice.ACTION_BOND_STATE_CHANGED=======");
				Log.e("","== 设备:" + device.getAddress() + " ==");
				Log.e("","== 绑定状态:" + getBondedState(state) + " ==");
				Log.e("","== 上次状态:" + getBondedState(previousState) + " ==");
				Log.e("","=======================================================");
			onBondStateChanged(device,state);
		} else if (BluetoothDevice.ACTION_CLASS_CHANGED.equals(action)) {
			if (isDebug) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				BluetoothClass deviceClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
				System.out.println("=======BluetoothDevice.ACTION_CLASS_CHANGED==========");
				System.out.println("== 设备:" + device.getAddress() + " ==");
				System.out.println("== BluetoothClass:" + deviceClass + " ==");
				System.out.println("=====================================================");
			}
		} else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
			if (isDebug) {
				System.out.println("=======ACTION_PAIRING_REQUEST=======");
			}
			
		} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			if (isDebug) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				BluetoothClass deviceClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
				short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
				System.out.println("=======BluetoothDevice.ACTION_FOUND========");
				System.out.println("== 发现设备:" + device.getAddress() + " ==");
				System.out.println("== BluetoothClass:" + deviceClass + " ==");
				System.out.println("== 设备名字:" + name + " ==");
				System.out.println("== 设备信号:" + rssi + " ==");
				System.out.println("===========================================");
			}
		} else if (BluetoothDevice.ACTION_UUID.equals(action)) {
			if (isDebug) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
				System.out.println("=======BluetoothDevice.ACTION_UUID======");
				System.out.println("== 设备:" + device.getAddress() + " ==");
				System.out.println("== 设备UUID:" + uuids + " ==");
				System.out.println("=========================================");
			}
		}

		//
		else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
			int previousState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);
			if (isDebug) {
				System.out.println("=======BluetoothAdapter.ACTION_STATE_CHANGED======");
				System.out.println("== 蓝牙状态:" + state + " ==");
				System.out.println("== 上次状态:" + previousState + " ==");
				System.out.println("==================================================");
			}
			onBluetoothChange(state, previousState);
		} else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
			if (isDebug) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_CONNECTION_STATE);
				int previousState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE);

				System.out.println("=======BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED======");
				System.out.println("== 蓝牙地址:" + device.getAddress() + " ==");
				System.out.println("== 蓝牙状态(4种状态):" + state + " ==");
				System.out.println("== 上次状态(4种状态):" + previousState + " ==");
				System.out.println("==================================================");
			}
		} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
			if (isDebug) {
				System.out.println("=======BluetoothAdapter.ACTION_DISCOVERY_STARTED======");
			}
		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			System.out.println("=======BluetoothAdapter.ACTION_DISCOVERY_FINISHED======");
		} else if (BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED.equals(action)) {
			if (isDebug) {
				String name = intent.getStringExtra(BluetoothAdapter.EXTRA_LOCAL_NAME);
				System.out.println("=======BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED======");
				System.out.println("== 名字改变:" + name + " ==");
				System.out.println("==================================================");
			}
		} else if (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE.equals(action)) {
			if (isDebug) {
				int duration = intent.getExtras().getInt(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				System.out.println("=======BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE======");
				System.out.println("== 可被发现时间(会启动ACTION_REQUEST_ENABLE？):" + duration + " ==");
				System.out.println("==================================================");
			}
		} else if (BluetoothAdapter.ACTION_REQUEST_ENABLE.equals(action)) {
			if (isDebug) {
				System.out.println("=======BluetoothAdapter.ACTION_REQUEST_ENABLE======");
			}
		} else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
			if (isDebug) {
				int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_SCAN_MODE);
				int preiousState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE);
				System.out.println("=======BluetoothAdapter.ACTION_SCAN_MODE_CHANGED======");
				System.out.println("==蓝牙扫描模式已更改");
				System.out.println("==state:" + state);
				System.out.println("==preiousState:" + preiousState);
				System.out.println("======================================================");
			}
		}
	}

		public abstract void onBondStateChanged(BluetoothDevice device, int state);

	private String getBondedState(int state) {
		if (state == BluetoothDevice.BOND_BONDED) {
			return "已绑定";
		}else if(state == BluetoothDevice.BOND_BONDING){
			return "绑定中";
		}
		return "未绑定";
	}

	public abstract void onBluetoothChange(int state, int previousState);

	public abstract void onBluetoothDisconnect(BluetoothDevice device);

	public static int getAndroidOSVersion() {
		int osVersion;
		try {
			osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			osVersion = 0;
		}

		return osVersion;
	}

	/**
	 * 监听蓝牙变化情况
	 */
	public void registerBoradcastReceiverForCheckBlueToothState(Context context) {
		IntentFilter stateChangeFilter = new IntentFilter();
		stateChangeFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		stateChangeFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		stateChangeFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		stateChangeFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		stateChangeFilter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
		stateChangeFilter.addAction(BluetoothDevice.ACTION_FOUND);
		// stateChangeFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			stateChangeFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);// Api
																				// 19才有用
		}
		stateChangeFilter.addAction(BluetoothDevice.ACTION_UUID);
		//
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		context.registerReceiver(this, stateChangeFilter);
	}

}
