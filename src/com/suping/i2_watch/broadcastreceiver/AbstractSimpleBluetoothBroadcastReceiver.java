package com.suping.i2_watch.broadcastreceiver;

import com.suping.i2_watch.service.SimpleBlueService;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public abstract class AbstractSimpleBluetoothBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(SimpleBlueService.ACTION_DEVICE_FOUND)) {
			BluetoothDevice device = intent.getParcelableExtra(SimpleBlueService.EXTRA_DEVICE);
			doDeviceFound(device);
		} else if (action.equals(SimpleBlueService.ACTION_SERVICE_DISCOVERED_WRITE_DEVICE)) {
			Toast.makeText(context, "已连接手环", Toast.LENGTH_SHORT).show();
			doDiscoveredWriteService();
		} else if (action.equals(SimpleBlueService.ACTION_CONNECTION_STATE)) {
			int state = intent.getExtras().getInt(SimpleBlueService.EXTRA_CONNECT_STATE);
			switch (state) {
			case BluetoothProfile.STATE_CONNECTED:
				doBlueConnect(state);
				break;
			case BluetoothProfile.STATE_DISCONNECTED:
				doBlueDisconnect(state);
				break;

			default:
				break;
			}

		} else if (action.equals(SimpleBlueService.ACTION_SERVICE_DISCONNECTED_FOR_REMIND)) {
			doDisconnectRemind();
		} else if (action.equals(SimpleBlueService.ACTION_CAMERA)) {
			doCamera();
		} else if (action.equals(SimpleBlueService.ACTION_DATA_STEP_AND_CAL)) {
			long[] data = intent.getExtras().getLongArray(SimpleBlueService.EXTRA_STEP_AND_CAL);
			doStepAndCalReceiver(data);

		} else if (action.equals(SimpleBlueService.ACTION_SYNC_START)) {
			doSyncStart();
		}else if (action.equals(SimpleBlueService.ACTION_SYNC_END)) {
			doSyncEnd();
		}else if (action.equals(SimpleBlueService.ACTION_SERVICE_DISCOVERED_WRONG_DEVICE)) {
			Toast.makeText(context, "连接手环失败", Toast.LENGTH_SHORT).show();
			doDiscoveredWrongService();
		}
	}

	public abstract void doStepAndCalReceiver(long[] data);

	/**
	 * 拍照
	 */
	public abstract void doCamera();

	/**
	 * 断线提醒（重连5秒后）
	 */
	public abstract void doDisconnectRemind();

	/**
	 * 蓝牙连接
	 * 
	 * @param state
	 */
	public abstract void doBlueConnect(int state);

	/**
	 * 蓝牙断开
	 * 
	 * @param state
	 */
	public abstract void doBlueDisconnect(int state);

	/**
	 * 找到匹配设备
	 */
	public abstract void doDiscoveredWriteService();
	public abstract void doDiscoveredWrongService();

	/**
	 * 搜索到设备
	 * 
	 * @param device
	 */
	public abstract void doDeviceFound(BluetoothDevice device);
	public abstract void doSyncStart();
	public abstract void doSyncEnd();
	

}
