package com.suping.i2_watch.broadcastreceiver;


import android.bluetooth.BluetoothDevice;

public  class SimpleBluetoothBroadcastReceiverBroadcastReceiver extends AbstractSimpleBluetoothBroadcastReceiver{
	/**
	 * 蓝牙连接
	 * @param state
	 */
	@Override
	public void doBlueConnect(int state) {
		
	}
	
	/**
	 * 蓝牙断开
	 * @param state
	 */
	@Override
	public void doBlueDisconnect(int state) {
		
	}
	
	/**
	 * 找到匹配设备
	 */
	@Override
	public void doDiscoveredWriteService() {
		
	}
	
	/**
	 * 搜索到设备
	 * @param device
	 */
	@Override
	public void doDeviceFound(BluetoothDevice device) {
		
	}
	
	/**
	 * 断线提醒（重连5秒后）
	 */
	@Override
	public void doDisconnectRemind() {
		
	}
	
	/**
	 * 拍照
	 */
	@Override
	public void doCamera() {
		
	}
	
	/**
	 * 计步 0卡路里1 时间2	 
	 */
	@Override
	public void doStepAndCalReceiver(long[] data) {
		
	}

	/**
	 * 同步开始
	 */
	@Override
	public void doSyncStart() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 同步结束
	 */
	@Override
	public void doSyncEnd() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 链接失败
	 */
	@Override
	public void doDiscoveredWrongService() {
		
	}
	
	

}
