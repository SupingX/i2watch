package com.suping.i2_watch.service;

import java.util.UUID;





import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

public abstract class AbstractSimpleBluetooth implements IBluetooth {
	public final static String DEVICE_NAME = "DEVICE_NAME";
	public final static String DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public final static String BLE_SERVICE = "00001523-1212-efde-1523-785feabcd123";
	public final static String BLE_CHARACTERISTIC_NOTIFY = "00001524-1212-efde-1523-785feabcd123";
	public final static String BLE_CHARACTERISTIC_WRITE = "00001525-1212-efde-1523-785feabcd123";
	public final static String DESC_CCC = "00002902-0000-1000-8000-00805f9b34fb";
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	private Context context;
	private SimpleBluetoothGattCallBack gattCallBack = new SimpleBluetoothGattCallBack();
	private SimpleLeScanCallback scanCallBack = new SimpleLeScanCallback();

	public AbstractSimpleBluetooth(Context context,BluetoothAdapter mBluetoothAdapter) {
		this.mBluetoothAdapter = mBluetoothAdapter;
		this.context = context;
	}

	@Override
	public boolean isEnable() {
		if (mBluetoothAdapter != null) {
			return mBluetoothAdapter.isEnabled();
		} else {
			return false;
		}
	}
	
	public BluetoothGatt getBluetoothGatt(){
		return this.mBluetoothGatt;
	}
	
	@Override
	public void startScan() {
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.startLeScan(scanCallBack);
		}
	}

	@Override
	public void stopScan() {
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.stopLeScan(scanCallBack);
		}
	}
	@Override
	public void readRemoteRssi() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.readRemoteRssi();
		}
	}
	
	@Override
	public boolean connect(BluetoothDevice device) {
		Log.e("", "连接开始");
		if (device == null || mBluetoothAdapter == null) {
			Log.e("", "device空");
			return false;
		}
		if (mBluetoothGatt != null) {
			Log.e("", "mBluetoothGatt不空，连接以前的device");
			if (this.mBluetoothGatt.connect()) {
				return true;
			}
			return false;
		}
		Log.e("", "mBluetoothGatt空，连接新的device");
		mBluetoothGatt = device.connectGatt(context, false, new SimpleBluetoothGattCallBack());
		return true;
		
	}

	@Override
	public boolean connect(String address) {
		return false;
	}

	@Override
	public void disconnect() {
		if (mBluetoothGatt!=null) {
			mBluetoothGatt.disconnect();
		}
	}

	@Override
	public void close() {
		if (mBluetoothGatt!=null) {
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt=null;
		}
		
	}
	public boolean isBinded(){
//		String name = (String) SharedPreferenceUtil.get(context, DEVICE_NAME, "");
		String address = (String) SharedPreferenceUtil.get(context, DEVICE_ADDRESS, "no_address");
		if (!address.equals("no_address") ) {//地址 空则 返回false
			return true;
		}else{
			return false;
		}
	};
	
	public String getBindedName(){
		return (String) SharedPreferenceUtil.get(context, DEVICE_NAME, "");
	};
	public String getBindedAddress(){
		return (String) SharedPreferenceUtil.get(context, DEVICE_ADDRESS, "");
	};

	@Override
	public void write(BluetoothGattCharacteristic characteristic) {
		if (characteristic==null) {
			Log.e("AbstractSimpleBluetooth", "写数据characteristic 为空");
			return ;
		}
		if (mBluetoothGatt!=null ) {
			Log.e("AbstractSimpleBluetooth", "写数据write");
			mBluetoothGatt.writeCharacteristic(characteristic);
		}
	}
	
	@Override
	public void saveDevice(BluetoothDevice device){
		if (device!=null) {
			SharedPreferenceUtil.put(context, DEVICE_NAME,device.getName() );
			SharedPreferenceUtil.put(context, DEVICE_ADDRESS,device.getAddress() );
		}
	};
	
	
	

	@Override
	public void unSaveDevice() {
			SharedPreferenceUtil.put(context, DEVICE_NAME,"no_name" );
			SharedPreferenceUtil.put(context, DEVICE_ADDRESS,"no_address");
	}



	/**
	 * 连接CallBack
	 * @author Administrator
	 *
	 */
	private class SimpleBluetoothGattCallBack extends BluetoothGattCallback{

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			onCharacteristicChangedCallBack(gatt,characteristic);
			Log.e("AbstractSimpleBluetooth", ">>>>>>>> onCharacteristicChanged <<<<<<<<<<<");
			super.onCharacteristicChanged(gatt, characteristic);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			
			super.onCharacteristicRead(gatt, characteristic, status);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			Log.e("AbstractSimpleBluetooth", ">>>>>>>> onCharacteristicWrite <<<<<<<<<<<");
			onCharacteristicWriteCallBack(gatt,characteristic,status);
			super.onCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			Log.e("AbstractSimpleBluetooth", ">>>>>>>> onConnectionStateChange <<<<<<<<<<< newState:" + newState);
			
//			if (!gatt.equals(mBluetoothGatt)) {
//				return;
//			}
//			
			onConnectionStateChangeCallBack(gatt,status,newState);
			super.onConnectionStateChange(gatt, status, newState);
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorWrite(gatt, descriptor, status);
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.e("AbstractSimpleBluetooth", ">>>>>>>> onReadRemoteRssi <<<<<<<<<<<  rssi" + rssi);
			onReadRemoteRssiCallBack(gatt,rssi,status);
			super.onReadRemoteRssi(gatt, rssi, status);
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
			Log.e("AbstractSimpleBluetooth", ">>>>>>>> onReliableWriteCompleted <<<<<<<<<<<");
			Log.e("", "写成功了？onReliableWriteCompleted()");
			super.onReliableWriteCompleted(gatt, status);
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			Log.e("AbstractSimpleBluetooth", ">>>>>>>> onServicesDiscovered <<<<<<<<<<<");
	           if (status == BluetoothGatt.GATT_SUCCESS) {
	        	   onServicesDiscoveredCallBack(gatt,status);
	           }
			super.onServicesDiscovered(gatt, status);
		}
		
	}
	/**
	 * 搜索CallBack
	 * @author Administrator
	 *
	 */
	private class SimpleLeScanCallback implements LeScanCallback {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Log.e("AbstractSimpleBluetooth", ">>>>>>>> onLeScan <<<<<<<<<<<" );
			onLeScanCallBack(device, rssi, scanRecord);
		}
	}
	
	
	
	// some callbacks
	public abstract void onLeScanCallBack(BluetoothDevice device, int rssi, byte[] scanRecord);

	public abstract void onServicesDiscoveredCallBack(BluetoothGatt gatt, int status) ;

	public abstract void onReadRemoteRssiCallBack(BluetoothGatt gatt, int rssi, int status) ;

	public abstract void onConnectionStateChangeCallBack(BluetoothGatt gatt, int status, int newState) ;

	public abstract void onCharacteristicWriteCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) ;

	public abstract void onCharacteristicChangedCallBack(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) ;
}
