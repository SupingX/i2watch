package com.suping.i2_watch;

import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
/**
 * 全局application 绑定蓝牙Service
 * @author Administrator
 *
 */
public class XtremApplication extends Application {
	private BleService mService;
	private IBle mBle;

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((BleService.LocalBinder) rawBinder).getService();
			mBle = mService.getBle();
		}

		@Override
		public void onServiceDisconnected(ComponentName classname) {
			mService = null;
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		Intent bindIntent = new Intent(this, BleService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	public IBle getIBle() {
//		if (mBle != null && !mBle.adapterEnabled()) {
//			Toast.makeText(getApplicationContext(), "蓝牙无效", Toast.LENGTH_SHORT).show();
//			return null;
//		}
		return mBle;
	}

}
