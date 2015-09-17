package com.suping.i2_watch.broadcastreceiver;

import java.util.HashMap;

import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.util.MessageUtil;

public class I2WPhoneStateListener extends AbstractPhoneStateListener{
	private AbstractSimpleBlueService mSimpleBlueService;
	private Context mContext;
	public I2WPhoneStateListener(AbstractSimpleBlueService mSimpleBlueService,Context context){
		this.mContext = context;
		this.mSimpleBlueService = mSimpleBlueService;
	}

	@Override
	public void onIncoming(int state, String incomingNumber) {
		//通知手环有来电
		if (null!=mSimpleBlueService&& mSimpleBlueService.isBinded()&&mSimpleBlueService.getConnectState()==BluetoothProfile.STATE_CONNECTED) {
			mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForHasCallingCount(MessageUtil.readMissCall(mContext)));
		}
	}

}
