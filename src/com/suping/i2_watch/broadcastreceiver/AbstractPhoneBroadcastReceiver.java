package com.suping.i2_watch.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AbstractPhoneBroadcastReceiver extends BroadcastReceiver{


	@Override
	public void onReceive(Context context, Intent intent) {
		int state = intent.getExtras().getInt(TelephonyManager.EXTRA_STATE);
		String intcomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
		Log.e("", ""+state);
		Log.e("", ""+intcomingNumber);
		
	}
	
	/**
	 * 监听来电变化情况
	 */
	public void registerBoradcastReceiverForPhoneState(Context context) {
		
		IntentFilter stateChangeFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		context.registerReceiver(this, stateChangeFilter);
	}
	
}
