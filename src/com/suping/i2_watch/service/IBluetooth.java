package com.suping.i2_watch.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

public interface IBluetooth {

	/**
	 * 蓝牙是否可用
	 * @return
	 */
	public boolean isEnable();
	/**
	 * 开始搜索
	 */
	public void startScan();
	
	/**
	 * 结束搜索
	 */
	public void stopScan();
	/**
	 * 连接蓝牙
	 * @param device
	 */
	public boolean connect(BluetoothDevice device);
	/**
	 * 连接蓝牙
	 * @param address
	 */
	public boolean connect(String address);
	
	/**
	 * 断开连接
	 */
	public void disconnect();
	/**
	 * 关闭
	 */
	public void close();
	
	/**
	 * 
	 */
	public void write(BluetoothGattCharacteristic characteristic);
	
	/**
	 * 
	 */
	public void readRemoteRssi();
	
	/**
	 * save
	 */
	public void saveDevice(BluetoothDevice device);
	public void unSaveDevice();
	
	public boolean isBinded();
	public String getBindedName();
	public String getBindedAddress();
	
	
}
