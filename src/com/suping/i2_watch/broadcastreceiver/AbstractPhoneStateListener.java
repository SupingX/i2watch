package com.suping.i2_watch.broadcastreceiver;

import java.util.HashMap;



import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public abstract class AbstractPhoneStateListener extends PhoneStateListener {

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:// 空闲
//			Log.e("xpl", "手机空闲起来了");
			onIdle(incomingNumber);
			break;
		case TelephonyManager.CALL_STATE_RINGING:// 来电
			onIncoming(state, incomingNumber);
//			Log.e("xpl", "来电话了");
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机（正在通话中）
//			Log.e("xpl", "电话被挂起了");
			break;

		default:
			break;
		}

	}

	public abstract void onIncoming(int state, String incomingNumber);
	public abstract void onIdle( String incomingNumber);

	/**
	 * 监听来电变化情况
	 */
	public void registerListenerForPhoneState(TelephonyManager telephonyManager) {
		telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
	}


}
