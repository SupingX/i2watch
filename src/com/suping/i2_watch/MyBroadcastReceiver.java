package com.suping.i2_watch;

import com.suping.i2_watch.service.SimpleBlueService;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public  class MyBroadcastReceiver extends AbstractBroadcastReceiver{
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
	
	

}
