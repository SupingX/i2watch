package com.suping.i2_watch;

import java.util.ArrayList;

import org.litepal.LitePalApplication;

import com.xtremeprog.sdk.ble.BleManager;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
/**
 * 全局application 绑定蓝牙Service
 * @author Administrator
 *
 */
public class XtremApplication extends Application {
	private BleService mService;
	private IBle mBle;
	private BleManager mbleManager;
	
	private static ArrayList<Activity> activities = new ArrayList<Activity>();

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((BleService.LocalBinder) rawBinder).getService();
			mBle = mService.getBle();
			mbleManager = BleManager.instance(mBle);
			Log.i("APP", "mBle : " + mBle);
			Log.i("APP", "mbleManager : " + mbleManager);
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
	
	public BleManager getBleManager(){
		return mbleManager;
	}
	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	public static void finishActivity() {
		for (Activity activity : activities) {
			if (activity != null) {
				activity.finish();
			}
		}
		Process.killProcess(Process.myPid());
		System.exit(0);
	}
	
	@Override
	public void onTerminate() {
		mbleManager.disconnect();
		mbleManager.clear();
		mbleManager = null;
		unbindService(mServiceConnection);
		onTerminate();
	}

}
