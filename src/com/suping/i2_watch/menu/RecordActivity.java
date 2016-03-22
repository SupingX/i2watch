package com.suping.i2_watch.menu;



import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.suping.i2_watch.BaseActivity;
import com.suping.i2_watch.R;
import com.suping.i2_watch.R.string;
import com.suping.i2_watch.XtremApplication;
import com.suping.i2_watch.broadcastreceiver.ImpXplBroadcastReceiver;
import com.suping.i2_watch.entity.History;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.entity.LitePalManager;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.xblue.XBlueBroadcastReceiver;
import com.suping.i2_watch.service.xblue.XBlueBroadcastUtils;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.view.ActionSheetDialog;
import com.suping.i2_watch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.suping.i2_watch.view.ActionSheetDialog.SheetItemColor;
import com.suping.i2_watch.view.AlertDialog;
import com.suping.i2_watch.view.NoScrollViewPager;

@SuppressLint("HandlerLeak")
public class RecordActivity extends BaseActivity implements OnClickListener{
	private final int SYNC_TIME_OUT = 3*60*1000;
	private NoScrollViewPager recordViewPager;
	private RadioButton rbSport;
	private RadioButton rbSleep;
	private RadioGroup rgTop;
	private ImageView imgBack;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private ActionSheetDialog dialogSync;
	private android.app.AlertDialog dialogSyncHistory;
	private TextView tvSync;
	private Handler mHandler = new Handler(){
	};
	private ImpXplBroadcastReceiver xplReceiver = new ImpXplBroadcastReceiver(){
		public void doSyncEnd() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (dialogSyncHistory!=null && dialogSyncHistory.isShowing()) {
						dialogSyncHistory.dismiss();
					}
				}
			});
		};
		public void doConnectStateChange(android.bluetooth.BluetoothDevice device, int state) {
			if (state == BluetoothGatt.STATE_DISCONNECTED) {
				if (dialogSyncHistory!=null && dialogSyncHistory.isShowing()) {
					dialogSyncHistory.dismiss();
				}
			}
			
		};
	};
	
	private XBlueBroadcastReceiver receiver = new XBlueBroadcastReceiver() {
		public void doDeviceFound(final java.util.ArrayList<BluetoothDevice> devices) {
		}

		@Override
		public void doServiceDiscovered(BluetoothDevice device) {
		}

		@Override
		public void doStepAndCalReceiver(final long[] data) {
			
		}

		@Override
		public void doSyncStart() {
			
		}

		@Override
		public void doSyncEnd() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (dialogSyncHistory!=null && dialogSyncHistory.isShowing()) {
						dialogSyncHistory.dismiss();
					}
				}
			});
		}

		@Override
		public void doCamera() {
			
		}

		@Override
		public void doConnectStateChange(BluetoothDevice device, final int state) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (state == BluetoothGatt.STATE_DISCONNECTED) {
						if (dialogSyncHistory!=null && dialogSyncHistory.isShowing()) {
							dialogSyncHistory.dismiss();
						}
					}
				}
			});
		
		};
	};
	
		
	private AlertDialog setCancelable;
//	private XplBluetoothService xplBluetoothService;
	private XBlueService xBlueService;
	
	
	@Override
	protected void onCreate(Bundle fragments) {
		super.onCreate(fragments);
		setContentView(R.layout.activity_record);
		initViews();
		setListener();
		Intent intent = getIntent();
		int flag = intent.getExtras().getInt("flag");
		switch (flag) {
		case 0:
			rbSport.setChecked(true);
			break;
		case 1:
			rbSleep.setChecked(true);
			break;
		default:
			break;
		}
	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
//		xplBluetoothService = getXplBluetoothService();
//		registerReceiver(xplReceiver, XplBluetoothService.getIntentFilter());
		
		xBlueService = getXBlueService();
		registerReceiver(receiver, XBlueBroadcastUtils.instance().getIntentFilter());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
		case R.id.img_back:
//			Intent retrunToMain = new Intent(this,MenuActivity.class);
//			startActivity(retrunToMain);
			this.finish();
//			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
//					R.anim.activity_from_left_to_right_exit);
			break;
		case R.id.tv_sync:
			
//			LoadTestData();
			
			
			if (xBlueService!=null && xBlueService.isAllConnected()) {
				
				showTipsDialog();
//				xplBluetoothService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(2, 0));
				xBlueService.write(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(2, 0));
			}else{
				showIosDialog(RecordActivity.this, getString(R.string.no_binded_device));
//				if (setCancelable==null) {
//					setCancelable = new AlertDialog(getApplicationContext()).builder().setMsg(getString(R.string.no_binded_device)).setCancelable(true);
//				}
			}
			break;
		default:
			break;
		}
	}
	
	private void LoadTestData() {
		History h = new History("2016", "01", "01", "01", 111, 222, 333, 23, 32, 33, 22, 33, 22, 44, 55, 66, 77, 88, 99, 111, 113, 114);
		History h1 = new History("2016", "01", "02", "02", 111, 222, 333, 23, 32, 33, 22, 33, 22, 44, 55, 66, 77, 88, 99, 111, 113, 114);
		History h2 = new History("2016", "01", "03", "01", 111, 222, 333, 23, 32, 33, 22, 33, 22, 44, 55, 66, 77, 88, 99, 111, 113, 114);
		History h3 = new History("2016", "01", "04", "01", 111, 222, 333, 23, 32, 33, 22, 33, 22, 44, 55, 66, 77, 88, 99, 111, 113, 114);
		History h4 = new History("2016", "01", "05", "01", 111, 222, 333, 23, 32, 33, 22, 33, 22, 44, 55, 66, 77, 88, 99, 111, 113, 114);
		History h5 = new History("2016", "01", "06", "01", 111, 222, 333, 23, 32, 33, 22, 33, 22, 44, 55, 66, 77, 88, 99, 111, 113, 114);
		History h6 = new History("2016", "01", "07", "01", 111, 222, 333, 23, 32, 33, 22, 33, 22, 44, 55, 66, 77, 88, 99, 111, 113, 114);
		LitePalManager.instance().saveHistory(h);
		LitePalManager.instance().saveHistory(h1);
		LitePalManager.instance().saveHistory(h2);
		LitePalManager.instance().saveHistory(h3);
		LitePalManager.instance().saveHistory(h4);
		LitePalManager.instance().saveHistory(h5);
		LitePalManager.instance().saveHistory(h6);
	}

	private void initViews(){
		rbSport = (RadioButton) findViewById(R.id.rb_record_sport);
		rbSleep = (RadioButton) findViewById(R.id.rb_record_sleep);
		rgTop = (RadioGroup) findViewById(R.id.rg_top);
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvSync = (TextView) findViewById(R.id.tv_sync);
		
		
		SleepRecordFragment sleepRecordFragment = new SleepRecordFragment();
		SportRecordFragment sportRecordFragment = new SportRecordFragment();
		fragments.add(sportRecordFragment);
		fragments.add(sleepRecordFragment);
		recordViewPager = (NoScrollViewPager) findViewById(R.id.vp);
		MyAdapter adapter = new MyAdapter(getSupportFragmentManager(),fragments);
		recordViewPager.setAdapter(adapter);
	
		
	}
	
	private void setListener(){
		imgBack.setOnClickListener(this);
		tvSync.setOnClickListener(this);
		rgTop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//Log.i("RecordActivity", "checkedId : " + checkedId);
				update(checkedId);
			}
		});
	}
	
	/**
	 * 更改视图
	 * @param id
	 */
	private void update(int id){
		switch (id) {
		case R.id.rb_record_sport:
			recordViewPager.setCurrentItem(0);
			break;
		case R.id.rb_record_sleep:
			recordViewPager.setCurrentItem(1);
			break;

		default:
			break;
		}
	}
	
	private void showTipsDialog(){
		if (dialogSyncHistory!=null && dialogSyncHistory.isShowing()) {
			dialogSyncHistory.dismiss();dialogSyncHistory=null;
		}
//		dialogSyncHistory = new ProgressDialog(getApplicationContext());
//		dialogSyncHistory.setCancelable(false);
//		dialogSyncHistory.setMessage(getString(R.string.sync_history));
//		dialogSyncHistory.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		dialogSyncHistory.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	
		
//		dialogSyncHistory= ProgressDialog.show(this, "", getString(R.string.sync_history),  
//		            false, false);  
//		dialogSyncHistory.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		dialogSyncHistory.show();
		dialogSyncHistory = showLoadingDialog(getString(R.string.sync_history), false);
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (dialogSyncHistory!=null &&dialogSyncHistory.isShowing()) {
					dialogSyncHistory.dismiss();
				}
			}
		}, SYNC_TIME_OUT);

	}
	/**
	 * 打开对话框
	 */
	private void showDialog() {
		dialogSync = new ActionSheetDialog(this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
			.addSheetItem(getString(R.string.sync_history_all), SheetItemColor.Blue, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
		
					//Log.e("RecordFragment", "同步所有历史数据");
					if (xBlueService!=null && xBlueService.isAllConnected()) {
							
						showTipsDialog();
						xBlueService.write(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(2, 0));
						}else{
						new AlertDialog(getApplicationContext()).builder().setMsg(getString(R.string.no_binded_device)).setCancelable(true).show();
					}
				
				}
			})
			.addSheetItem(getString(R.string.sync_history_today), SheetItemColor.Blue, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					//Log.e("RecordFragment", "同步今天数据");
//					if (isXplConnected()) {
					if (xBlueService!=null && xBlueService.isAllConnected()) {
						showTipsDialog();
						xBlueService.write(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(1, 0));
					}else{
						new AlertDialog(getApplicationContext()).builder().setMsg(getString(R.string.no_binded_device)).setCancelable(true).show();
					}
				}
			})
			;
			dialogSync.show();
	}
	
	

	private class MyAdapter extends FragmentStatePagerAdapter {
		private List<Fragment> fragments ;
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
	}

}
