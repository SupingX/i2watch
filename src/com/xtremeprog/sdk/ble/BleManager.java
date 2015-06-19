package com.xtremeprog.sdk.ble;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.suping.i2_watch.enerty.AbstractProtocolWrite;
import com.suping.i2_watch.enerty.SportRemindProtocol;
import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.R.mipmap;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * 
 * 连接蓝牙辅助s
 * 
 * @author Administrator
 *
 */
public class BleManager {

	public static final String BLE_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
	public static final String BLE_CHARACTERISTIC_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";;
	public static final String BLE_CHARACTERISTIC_READ = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
	private IBle mIble;
	private static BleManager mBlemanager;
	
	/**
	 * service
	 */
	private BleGattService currentService;
	/**
	 *characteristic写入特性
	 */
	private BleGattCharacteristic currentCharacteristicWrite;
	/**
	 *characteristic读取特性
	 */
	private BleGattCharacteristic currentCharacteristicRead;
	/**
	 * 是否搜索中
	 */
	private boolean isScanning;
	/**
	 * 是否连接中
	 */
	private boolean isConnectted;
	/**
	 * 蓝牙是否打开
	 */
	private boolean isEnable;
	/**
	 * 当前连接地址
	 */
	private String currentAddress;
	private int reConnectCount  = 1;
	private int MAX_RECONNECT_TIMES=10;
	private BleManager(IBle mIble) {
		this.mIble = mIble;
	}
	public void setReConnectCount(int reConnectCount) {
		this.reConnectCount = reConnectCount;
	}
	public static BleManager instance(IBle mIble) {
		if (mBlemanager != null) {
			return mBlemanager;
		}
		return new BleManager(mIble);
	}

	/**
	 * 初始化特性
	 * @param address
	 */
	public void initCharacteristics() {
		if (mIble != null) {
			if(currentAddress==null || currentAddress.equals("")){
				Log.e("BleManager", "refreshCharcateristics() -- 地址为空");
				return;
			}
			ArrayList<BleGattService> services = mIble.getServices(currentAddress);
			if(services == null){
				Log.e("BleManager", "refreshCharcateristics()-- services为空");
				return;
			}
			for (BleGattService s : services) {
				if (s.getUuid().equals(UUID.fromString(BLE_SERVICE))) {
					currentService = s;
					List<BleGattCharacteristic> listBleGattCharacteristics = s.getCharacteristics();
					for (BleGattCharacteristic c : listBleGattCharacteristics) {
						if (c.getUuid().equals(UUID.fromString(BLE_CHARACTERISTIC_WRITE))) {
							currentCharacteristicWrite = c;
						}
						if (c.getUuid().equals(UUID.fromString(BLE_CHARACTERISTIC_READ))) {
							currentCharacteristicRead = c;
							//通知
							mIble.requestCharacteristicNotification(currentAddress, currentCharacteristicRead);
						}
					}
				}
			}
		}
	}

	/**
	 * 写入数据
	 * @param address
	 * @param value
	 */
	public synchronized void writeCharactics(String value) {
		this.writeCharactics(DataUtil.getBytesByString(value));
	}
	
	/**
	 * 写入数据
	 * @param address
	 * @param value
	 */
	public synchronized void writeCharactics(AbstractProtocolWrite pro) {
		this.writeCharactics(pro.toByte());
	}
	
	/**
	 * 写入数据
	 * @param address
	 * @param value
	 */
	public synchronized void writeCharactics(byte [] value) {
		try {
			if (currentAddress == null || currentAddress.equals("")) {
				Log.e("sportReminderActivity", "地址为空");
				return;
			}
			initCharacteristics();
			if (currentCharacteristicWrite == null) {
				Log.e("currentCharacteristicWrite", "没有找到write属性");
				return;
			}
			currentCharacteristicWrite.setValue(value);
			mIble.requestWriteCharacteristic(currentAddress, currentCharacteristicWrite, "");
		} catch (Exception e) {
			Log.e("BleManagerActivity", "写入数据失败...");
			e.printStackTrace();
		}
	}
	
	/**
	 * 开始搜索
	 */
	public void startScan() {
		if (mIble != null) {
			mIble.startScan();
			isScanning = true;
		}

	}

	/**
	 * 结束搜索
	 */
	public void stopScan() {
		if (mIble != null) {
			mIble.stopScan();
			isScanning = false;
		}
	}

	/**
	 * 连接
	 */
	public synchronized void connect() {
		if (mIble != null) {
				mIble.requestConnect(currentAddress);
		}
	}
	
	/**
	 * 重连接
	 */
	public synchronized void  reconnect(String address) {
		Log.e("BleManager", "重连接至 : " + currentAddress);
		if(address.equals(currentAddress)){
			connect();
		}else {
			Log.e("BleManager", "搜索设备 : " + address + "不匹配...");
		}
	}

	public void disconnect() {
		if (mIble != null) {
			mIble.disconnect(currentAddress);
			Log.e("BleManager", "退出连接" + currentAddress);
		}
	}
	
	public void clear(){
		isConnectted = false;
		isScanning =false;
		currentAddress = null;
	}
	
	
	/**
	 * 蓝牙是否可用
	 * @return
	 */
	public boolean isEnabled(){
	
	    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter .getDefaultAdapter(); 
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Log.e("BleManager", "蓝牙不可用");
			return false;
		}else {
			Log.e("BleManager", "蓝牙可用");
			return true;
		}
	}
	
	/**
	 * 打开蓝牙
	 * @param ac
	 */
	public void open(Activity ac){
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		ac.startActivityForResult(enableBtIntent,1);
	}

	/**
	 * 存贮当前地址，下次开机自动重连
	 * @return
	 */
	public void shareAddress(Context context){
		SharedPreferenceUtil.put(context, "SHARE_ADDRESS", currentAddress);
	}
	
	
	/**
     * 发起重连
     */
    public int reConnectDevice(String address) {
    // 最大重连次数之内直接重连，否则跳回至设备选择界面
    	  reConnectCount++;
    	Log.e("BleManager", "重连次数 ：" + reConnectCount);
        if (reConnectCount <= MAX_RECONNECT_TIMES) {
//            Toast.makeText(this, "正在进行蓝牙重连", Toast.LENGTH_LONG).show();
            // 尝试连接该机器
            reconnect(address);
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(isConnectted){
            	Log.e("BleManager", "已连接");
            	return 1;
            }else{
             	Log.e("BleManager", "依旧未连接");
            	reConnectDevice(address);
           
            	return 2;
            }
          
        } else {
        	Log.e("BleManager", "重连次数 大于10");
        	return 3;
//            Intent intent = new Intent(main, connect.getClass());
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("flag", 1);           
//            startActivity(intent);
        }
    
    }
    
    
	
	public boolean isConnectted() {
		return isConnectted;
	}
	public void setConnectted(boolean isConnectted) {
		this.isConnectted = isConnectted;
	}
	public boolean isScanning() {
		return isScanning;
	}
	public void setScanning(boolean isScanning) {
		this.isScanning = isScanning;
	}
	public String getCurrentAddress() {
		return currentAddress;
	}
	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
}
