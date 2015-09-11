package com.suping.i2_watch;

import java.util.ArrayList;

import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;

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
	

	
	private AbstractSimpleBlueService mSimpleBlueService;
	
	private ServiceConnection mSimpleBlueServiceConn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSimpleBlueService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSimpleBlueService =  (AbstractSimpleBlueService) (((AbstractSimpleBlueService.MyBinder)service).getService());
		
		}
	};
	
	public AbstractSimpleBlueService getSimpleBlueService(){
		return this.mSimpleBlueService;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Intent liteIntent = new Intent(this, SimpleBlueService.class);
		bindService(liteIntent, mSimpleBlueServiceConn, Context.BIND_AUTO_CREATE);
	
	}
	
	
	@Override
	public void onTerminate() {
		mSimpleBlueService.close();
		unbindService(mSimpleBlueServiceConn);
		onTerminate();
	}

}
