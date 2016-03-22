package com.suping.i2_watch;

import java.util.Date;

import com.suping.i2_watch.broadcastreceiver.I2WPhoneStateListener;
import com.suping.i2_watch.broadcastreceiver.I2WPhoneStateListener.OnIncomingListener;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.service.AddressSaved;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.XplBluetoothService.XplBinder;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.service.xblue.XBlueService.XBlueBinder;
import com.suping.i2_watch.util.DataUtil;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 全局application 绑定蓝牙Service
 * 
 * @author Administrator
 *
 */
public class XtremApplication extends Application implements OnIncomingListener {
	private TelephonyManager telephonyManager;
//	private XplBluetoothService xplBluetoothService;
	

	private XBlueService xBlueService;
	private ServiceConnection xBluetoothConntion = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			XBlueService.XBlueBinder binder = (XBlueBinder) service;
			xBlueService = binder.getXBlueService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	};
	
	
	
//	private ServiceConnection mXplBluetoothConn = new ServiceConnection() {
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			xplBluetoothService = null;
//		}
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			XplBinder xplBinder = (XplBinder) service;
//			xplBluetoothService = xplBinder.getXplBluetoothService();
//			if (AddressSaved.isBinded(getApplicationContext())) {
//				xplBluetoothService.scanDevice(true);
//			}
//		}
//	};

	@Override
	public void onCreate() {
		super.onCreate();
		// 绑定service
//		Intent xplIntent = new Intent(this, XplBluetoothService.class);
//		bindService(xplIntent, mXplBluetoothConn, Context.BIND_AUTO_CREATE);
		Intent xplIntent = new Intent(this, XBlueService.class);
		bindService(xplIntent, xBluetoothConntion, Context.BIND_AUTO_CREATE);
		// 来电拒接
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		I2WPhoneStateListener i2wPhoneStateListener = new I2WPhoneStateListener();
		i2wPhoneStateListener.setOnIncomingListener(this);
		i2wPhoneStateListener.registerListenerForPhoneState(telephonyManager);
		IntentFilter filter = new IntentFilter();
		filter.addAction(SMS_RECEIVED_ACTION);
		registerReceiver(mPhoneReceiver, filter);
		
		
		timeSettingChangedReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (Intent.ACTION_TIME_CHANGED.equals(action)) {
//					Log.e("xpl", "时间改变了！！！！！！！");
					// 8.时间同步
					byte[] hexDataForTimeSync = I2WatchProtocolDataForWrite.hexDataForTimeSync(new Date(), context);
					if (xBlueService!=null && xBlueService.isAllConnected()) {
						xBlueService.write(hexDataForTimeSync);
					}
				}
			}
		};
		registerReceiver(timeSettingChangedReceiver, new IntentFilter(Intent.ACTION_TIME_CHANGED));
	}
	
	
	public XBlueService getXBlueService(){
		return this.xBlueService;
	}
	
	@Override
	public void onTerminate() {
		Log.e("", "Watch 关闭");
//		unbindService(mXplBluetoothConn);
		
		if (xBlueService!=null) {
			xBlueService.closeAll();
		}
		unbindService(xBluetoothConntion);
		unregisterReceiver(mPhoneReceiver);
		unregisterReceiver(timeSettingChangedReceiver);
		super.onTerminate();
	}

	@Override
	public void onIncoming(int state, String incomingNumber) {
		Log.e("", "incomingNumber :" + incomingNumber);
		// 通知手环有来电
//		if (xplBluetoothService!=null && xplBluetoothService.isBluetoothConnected()) {
//			xplBluetoothService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForHasCallingCount(1, incomingNumber));
//		}
		if (xBlueService!=null && xBlueService.isAllConnected()) {
			xBlueService.write(I2WatchProtocolDataForWrite.hexDataForHasCallingCount(1, incomingNumber));
		}
	}

	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	/**
	 * 新来短信 监听
	 */
	private BroadcastReceiver mPhoneReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
				// SmsMessage[] messages = getMessagesFromIntent(intent);
			/*	if (null != mSimpleBlueService && mSimpleBlueService.isBinded() && mSimpleBlueService.getConnectState() == BluetoothProfile.STATE_CONNECTED
						&& mSimpleBlueService.isWrieteServiceFound()) {
					mSimpleBlueService.writeCharacteristic(DataUtil.getBytesByString("83"));
				}*/
//				if (xplBluetoothService!=null && xplBluetoothService.isBluetoothConnected()) {
//					xplBluetoothService.writeCharacteristic(DataUtil.getBytesByString("83"));
//				}
				if (xBlueService!=null && xBlueService.isAllConnected()) {
					xBlueService.write(DataUtil.getBytesByString("83"));
				}
				// for (SmsMessage message : messages) {
				// Log.e("", "来信的信息了！！！！！！");
				// Log.i("", message.getOriginatingAddress() + " : " +
				// message.getDisplayOriginatingAddress() + " : " +
				// message.getDisplayMessageBody() + " : " +
				// message.getTimestampMillis());
				// else {
				// }
				// }

			}
		}

	};


	private BroadcastReceiver timeSettingChangedReceiver;

	public final SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		byte[][] pduObjs = new byte[messages.length][];
		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}


	@Override
	public void onOffhook(String incomingNumber) {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.MODE_NORMAL);
		int max = am.getStreamMaxVolume( AudioManager.STREAM_RING );
		am.setStreamVolume(AudioManager.STREAM_RING, max, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}

	
//	public XplBluetoothService getXplBluetoothService(){
//		return this.xplBluetoothService;
//	}

}
