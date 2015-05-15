package com.suping.i2_watch;


import java.util.ArrayList;
import java.util.List;

import com.suping.i2_watch.bluetooth.BleApplication;
import com.suping.i2_watch.bluetooth.BleService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ConnectActivity extends Activity {

	private ListView lv_bleDevicesListView;
	private List<BluetoothDevice> devices;
	private BleDevicesAdapter mBleDevicesAdapter;
	private BleService mBleService;
	private ImageView img_close;
	private ImageView img_refresh;
	private TextView tv_state;
	private ProgressBar mProgressBar;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BleService.BLE_STATUS_ABNORMAL)){
				Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(mIntent);
			} else if (action.equals(BleService.BLE_DEVICE_SCANING)) {
				validate(1);
				
			} else if (action.equals(BleService.BLE_DEVICE_STOP_SCAN)) {
				validate(2);
			
			}else if (action.equals(BleService.BLE_DEVICE_FOUND)) {
				Bundle b = intent.getExtras();
				BluetoothDevice device = b.getParcelable(BleService.EXTRA_DEVICE);
				if (!devices.contains(device)) {
					devices.add(device);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mBleDevicesAdapter.notifyDataSetChanged();
						}
					});
				}
			}else if (action.equals(BleService.BLE_GATT_CONNECTED)) {
				BluetoothDevice device = mBleService.getBluetoothGatt().getDevice();
				String name;
				if(device!=null){
					name = device.getName();
				}else{
					name = "disconnected";
				}
				validateState(name);
			}else if (action.equals(BleService.BLE_GATT_DISCONNECTED)) {
				validateState("disconnected");
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_connect);
		initViews();
		setClick();
		devices = new ArrayList<>();
		mBleService = ((BleApplication)getApplication()).getBleService();
		mBleDevicesAdapter = new BleDevicesAdapter();
		lv_bleDevicesListView.setAdapter(mBleDevicesAdapter);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, BleService.getIntentFilter());
		if(mBleService.isConnected()){
			BluetoothDevice device = mBleService.getBluetoothGatt().getDevice();
			if(device!=null){
				validateState(device.getName());
			}
		}
		scanBleDevices(true);
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private void initViews() {
		lv_bleDevicesListView = (ListView) findViewById(R.id.listV);
		img_close = (ImageView) findViewById(R.id.img_close);
		img_refresh = (ImageView) findViewById(R.id.img_refresh);
		tv_state = (TextView) findViewById(R.id.tv_state);
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
	}
	
	private void setClick(){
		img_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scanBleDevices(false);
				finish();
			}
		});
		img_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scanBleDevices(true);				
			}
		});
		tv_state.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_state.setText("关闭连接...");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						disconnect();
						finish();
					}
				}, 1000);
				
			}
		});
		lv_bleDevicesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				tv_state.setText("正在连接...");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						connect(devices.get(position));
						finish();
						Toast.makeText(getApplicationContext(), "已连接至"+devices.get(position).getName(), 0).show();
					}
				}, 1000);
			}
		});
	}
	
	private void scanBleDevices(final boolean enable) {
//		if(!enable){
			mBleService.scanBleDevices(enable);
//		}else{
//			devices.clear();
//			mBleDevicesAdapter.notifyDataSetChanged();
//			mBleService.scanBleDevices(enable);
			
//		}
	}
	
	private void connect(BluetoothDevice device){
		mBleService.connectBleDevice(device);
	}
	private void disconnect(){
		scanBleDevices(false);
		mBleService.disconnectBleDevice();
	}
	
	private void validate(int state){
		switch (state) {
		case 1:
			img_refresh.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case 2:
			img_refresh.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}
	
	private void validateState(String device) {
		tv_state.setText(device);
	}
	

	private class BleDevicesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return devices.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (devices.size() > 0) {
				ViewHolder holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.item_ble_devices, parent, false);
				holder.deviceName = (TextView) convertView
						.findViewById(R.id.device_name);
				holder.deviceAddress = (TextView) convertView
						.findViewById(R.id.device_address);
				BluetoothDevice device = (BluetoothDevice) devices
						.get(position);
				holder.deviceName.setText(device.getName());
				;
				holder.deviceAddress.setText(device.getAddress());
				;
				return convertView;
			} else {
				return null;
			}
		}
		private class ViewHolder {
			private TextView deviceName;
			private TextView deviceAddress;
		}
	}

}
