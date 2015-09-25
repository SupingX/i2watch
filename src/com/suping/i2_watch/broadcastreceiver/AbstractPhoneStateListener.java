package com.suping.i2_watch.broadcastreceiver;

import java.util.HashMap;

import com.suping.i2_watch.util.L;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public abstract class AbstractPhoneStateListener extends PhoneStateListener {

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:// 空闲

			break;
		case TelephonyManager.CALL_STATE_RINGING:// 来电
			onIncoming(state, incomingNumber);
			L.e("-----------------电话来了 ---------------");
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机（正在通话中）
			break;

		default:
			break;
		}

	}

	public abstract void onIncoming(int state, String incomingNumber);

	/**
	 * 监听来电变化情况
	 */
	public void registerListenerForPhoneState(TelephonyManager telephonyManager) {
		telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
	}


}
