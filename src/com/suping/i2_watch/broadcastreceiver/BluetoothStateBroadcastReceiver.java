package com.suping.i2_watch.broadcastreceiver;


import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.view.AlertDialog;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class BluetoothStateBroadcastReceiver extends AbstractBluetoothStateBroadcastReceiver {

	private Context context;
	private AbstractSimpleBlueService mSimpleBlueService;

	public BluetoothStateBroadcastReceiver() {

	}

	public BluetoothStateBroadcastReceiver(Context context,AbstractSimpleBlueService mSimpleBlueService) {
		this.context = context;
		this.mSimpleBlueService = mSimpleBlueService;
	}

	@Override
	public void onBluetoothChange(int state, int previousState) {
		switch (state) {
		case BluetoothAdapter.STATE_ON:
			Log.i("", "蓝牙已打开");
			if (mSimpleBlueService.isScanning()) {
				mSimpleBlueService.scanDevice(false);
			}
			mSimpleBlueService.scanDevice(true);
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:
			Log.i("", "蓝牙关闭中。。。");
			break;
		case BluetoothAdapter.STATE_TURNING_ON:
			Log.i("", "蓝牙打开中。。。");
			break;
		case BluetoothAdapter.STATE_OFF:
			Log.i("", "蓝牙已关闭");
			Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
			if (mSimpleBlueService.isScanning()) {
				mSimpleBlueService.scanDevice(false);
			}
			mSimpleBlueService.close();
//			showdialog("蓝牙已关闭");
			break;
		default:
			break;
		}
	}
	
	private void showdialog(String msg) {
		AlertDialog dialog = new AlertDialog(context) .builder().setMsg(msg).setCancelable(true);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}
	

}
