package com.suping.i2_watch;


import com.suping.i2_watch.broadcastreceiver.BluetoothStateBroadcastReceiver;
import com.suping.i2_watch.broadcastreceiver.I2WPhoneStateListener;
import com.suping.i2_watch.broadcastreceiver.I2WPhoneStateListener.OnIncomingListener;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.util.MessageUtil;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
/**
 * 全局application 绑定蓝牙Service
 * @author Administrator
 *
 */
public class XtremApplication extends Application implements OnIncomingListener{
	

	
	private AbstractSimpleBlueService mSimpleBlueService;
	
	private ServiceConnection mSimpleBlueServiceConn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSimpleBlueService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mSimpleBlueService =  (AbstractSimpleBlueService) (((AbstractSimpleBlueService.MyBinder)service).getService());
			BluetoothStateBroadcastReceiver mBluetoothStateBroadcastReceiver = new BluetoothStateBroadcastReceiver(getApplicationContext(), mSimpleBlueService);
			mBluetoothStateBroadcastReceiver.registerBoradcastReceiverForCheckBlueToothState(getApplicationContext());
			if (!mSimpleBlueService.isEnable()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(enableBtIntent);
			// showIosDialog();
			}else{
				mSimpleBlueService.scanDevice(true);
			}
			
		}
	};

	private TelephonyManager telephonyManager;
	
	public AbstractSimpleBlueService getSimpleBlueService(){
		return this.mSimpleBlueService;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Intent liteIntent = new Intent(this, SimpleBlueService.class);
		bindService(liteIntent, mSimpleBlueServiceConn, Context.BIND_AUTO_CREATE);
		
		//来电拒接
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		I2WPhoneStateListener i2wPhoneStateListener = new I2WPhoneStateListener();
		i2wPhoneStateListener.setOnIncomingListener(this);
		i2wPhoneStateListener.registerListenerForPhoneState(telephonyManager);
//		telephonyManager.listen(new I2WPhoneStateListener(mSimpleBlueService, getApplicationContext()) , PhoneStateListener.LISTEN_CALL_STATE);
	
	}
	
	@Override
	public void onTerminate() {
		mSimpleBlueService.close();
		unbindService(mSimpleBlueServiceConn);
		onTerminate();
	}

	@Override
	public void onIncoming(int state, String incomingNumber) {
		//通知手环有来电
		L.e("-----------------电话来了  发给手环 手环链接了吗？---------------"+mSimpleBlueService.isBinded() + "  " + mSimpleBlueService.getConnectState() );
		if (null!=mSimpleBlueService&& mSimpleBlueService.isBinded()&&mSimpleBlueService.getConnectState()==BluetoothProfile.STATE_CONNECTED) {
			L.e("-----------------手环链接了 ---------------");
			mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForHasCallingCount(MessageUtil.readMissCall(getApplicationContext())));
		}else{
			L.e("-----------------手环未连接 ---------------");
		}
	}


	
	
	
}
