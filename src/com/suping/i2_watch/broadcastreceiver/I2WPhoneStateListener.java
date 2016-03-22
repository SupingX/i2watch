package com.suping.i2_watch.broadcastreceiver;



public class I2WPhoneStateListener extends AbstractPhoneStateListener{
	@Override
	public void onIncoming(int state, String incomingNumber) {

		if (mOnIncomingListener!=null) {
			mOnIncomingListener.onIncoming(state, incomingNumber);
		}
	}
	
	public interface OnIncomingListener{
		void onIncoming(int state, String incomingNumber);
		void onOffhook(String incomingNumber);
	}
	
	private OnIncomingListener mOnIncomingListener;
	
	public void setOnIncomingListener(OnIncomingListener l){
		this.mOnIncomingListener = l	;
	}

	@Override
	public void onIdle(String incomingNumber) {
		if (mOnIncomingListener!=null) {
			mOnIncomingListener.onOffhook( incomingNumber);
		}
	}

}
