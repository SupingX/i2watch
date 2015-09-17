package com.suping.i2_watch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.internal.telephony.ITelephony;
import com.suping.i2_watch.broadcastreceiver.BluetoothStateBroadcastReceiver;
import com.suping.i2_watch.broadcastreceiver.I2WPhoneStateListener;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.util.MessageUtil;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
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
		telephonyManager.listen(new I2WPhoneStateListener(mSimpleBlueService, getApplicationContext()) , PhoneStateListener.LISTEN_CALL_STATE);
	
	}
	
//	private I2WPhoneStateListener 
//	String num;//存储来电号码 
//	  HashMap<String, String> numMap = new HashMap<>();//用来存储来电号码  
//	private class MyPhoneStateListener  extends PhoneStateListener {
//		@Override
//		public void onCallStateChanged(int state, String incomingNumber) {
//			super.onCallStateChanged(state, incomingNumber);
//			switch (state) {
//			case TelephonyManager.CALL_STATE_IDLE://空闲
//			
//				break;
//			case TelephonyManager.CALL_STATE_RINGING://来电
//				//通知手环有来电
//				if (null!=mSimpleBlueService&& mSimpleBlueService.isBinded()&&mSimpleBlueService.getConnectState()==BluetoothProfile.STATE_CONNECTED) {
//					mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForHasCallingCount(	MessageUtil.readMissCall(getApplicationContext())));
//				}
//				break;
//			 case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）  
//	                break; 
//
//			default:
//				break;
//			}
//			
//			
//		}
//
//	
//	}
//	

	private void sendMes() {
	    //直接调用短信接口发短信  
//        SmsManager smsManager = SmsManager.getDefault();  
//        List<String> divideContents = smsManager.divideMessage("正在有事，稍后");   
//        for (String text : divideContents) {      
//            smsManager.sendTextMessage(num, null, text, null, null);    
//        }  
		
	}
	
	@Override
	public void onTerminate() {
		mSimpleBlueService.close();
		unbindService(mSimpleBlueServiceConn);
		onTerminate();
	}


	
	
	
}
