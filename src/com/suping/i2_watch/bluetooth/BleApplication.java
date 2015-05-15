package com.suping.i2_watch.bluetooth;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.widget.Toast;

public class BleApplication extends Application {
	private BleService mService;
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mService = ((BleService.LocalBinder) service).getBleService();
		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		
		super.onCreate();
		
		Intent intent = new Intent(this, BleService.class);
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
		} else {
			bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		mService.disconnectBleDevice();
		unbindService(mServiceConnection);
	}

	public BleService getBleService() {
		return mService;
	}

}
