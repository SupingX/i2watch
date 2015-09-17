package com.suping.i2_watch.broadcastreceiver;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


public abstract  class AbstractBluetoothStateBroadcastReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			Log.e("BaseApp", "ACTION_ACL_CONNECTED");
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			// mSimpleBlueService.closeRing();
			Log.e("BaseApp", "ACTION_ACL_DISCONNECTED");
		} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getExtras().getInt(BluetoothAdapter. EXTRA_STATE);
			int previousState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);
			Log.e("BaseApp", "__ACTION_STATE_CHANGED");
			Log.e("BaseApp", "____state : " + state);
			Log.e("BaseApp", "____state_previous : " + previousState);
			
			onBluetoothChange( state, previousState);
			
		} else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
			Log.e("BaseApp", "ACTION_CONNECTION_STATE_CHANGED");
		}
	}

	public abstract void onBluetoothChange(int state, int previousState) ;
	
	/**
	 * 监听蓝牙变化情况
	 */
	public void registerBoradcastReceiverForCheckBlueToothState(Context context) {
		IntentFilter stateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		stateChangeFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		stateChangeFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		stateChangeFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		context.registerReceiver(this, stateChangeFilter);
	}

}
