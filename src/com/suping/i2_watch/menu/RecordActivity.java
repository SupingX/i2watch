package com.suping.i2_watch.menu;




import android.app.ProgressDialog;
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

import com.suping.i2_watch.R;
import com.suping.i2_watch.XtremApplication;
import com.suping.i2_watch.broadcastreceiver.SimpleBluetoothBroadcastReceiverBroadcastReceiver;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.view.ActionSheetDialog;
import com.suping.i2_watch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.suping.i2_watch.view.ActionSheetDialog.SheetItemColor;
import com.suping.i2_watch.view.AlertDialog;
import com.suping.i2_watch.view.NoScrollViewPager;

public class RecordActivity extends FragmentActivity implements OnClickListener{
	private NoScrollViewPager vp;
	private RadioButton rbSport;
	private RadioButton rbSleep;
	private RadioGroup rgTop;
	private ImageView imgBack;
	private int [] layouts = new int[]{
			R.layout.fragment_record_sport
			,R.layout.fragment_record_sleep
	};
	private ActionSheetDialog dialogSync;
	private ProgressDialog dialogSyncHistory;
	private TextView tvSync;
	private AbstractSimpleBlueService mSimpleBlueService;
	private Handler mHandler = new Handler(){
	};
	private SimpleBluetoothBroadcastReceiverBroadcastReceiver mReceiver = new SimpleBluetoothBroadcastReceiverBroadcastReceiver(){
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
		
		public void doSyncStart() {
		
		};
	};
	
	public boolean isConnected(){
		AbstractSimpleBlueService mSimpleBlueService = getSimpleBlueService();
		return 	 (null!=mSimpleBlueService&&mSimpleBlueService.isBinded()&&mSimpleBlueService.getConnectState()==BluetoothProfile.STATE_CONNECTED) ;
	}
	/** 默认退出 **/
	protected AbstractSimpleBlueService getSimpleBlueService() {
		return ((XtremApplication)getApplication()).getSimpleBlueService();
	}
	
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
			rgTop.check(0);
			break;
		case 1:
			rgTop.check(1);
			break;
		default:
			break;
		}
	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mSimpleBlueService = getSimpleBlueService();
		registerReceiver(mReceiver, SimpleBlueService.getIntentFilter());
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_record_sport:
			rgTop.check(0);
			break;
			
		case R.id.rb_record_sleep:
			rgTop.check(1);
			break;
			
		case R.id.img_back:
//			Intent retrunToMain = new Intent(this,MenuActivity.class);
//			startActivity(retrunToMain);
			this.finish();
//			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
//					R.anim.activity_from_left_to_right_exit);
			break;
		case R.id.tv_sync:
			showDialog();
			break;
		default:
			break;
		}
	}
	
	private void initViews(){
		rbSport = (RadioButton) findViewById(R.id.rb_record_sport);
		rbSleep = (RadioButton) findViewById(R.id.rb_record_sleep);
		rgTop = (RadioGroup) findViewById(R.id.rg_top);
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvSync = (TextView) findViewById(R.id.tv_sync);
		
		vp = (NoScrollViewPager) findViewById(R.id.vp);
		vp.setAdapter(new MyAdapter(getSupportFragmentManager()));
	
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//	    menu.add(0, 1, Menu.NONE, "发送");
//	    menu.add(0, 2, Menu.NONE, "标记为重要");
//	    menu.add(0, 3, Menu.NONE, "重命名");
//	    menu.add(0, 4, Menu.NONE, "删除");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	private void setListener(){
		rbSport.setOnClickListener(this);
		rbSleep.setOnClickListener(this);
		imgBack.setOnClickListener(this);
		tvSync.setOnClickListener(this);
		rgTop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.i("RecordActivity", "checkedId : " + checkedId);
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
		case 0:
			rbSport.setTextColor(getResources().getColor(R.color.color_sport_text));
			rbSleep.setTextColor(getResources().getColor(R.color.white));
			vp.setCurrentItem(0);
			break;
		case 1:
			rbSport.setTextColor(getResources().getColor(R.color.white));
			rbSleep.setTextColor(getResources().getColor(R.color.color_sleep_text));
			vp.setCurrentItem(1);
			break;

		default:
			break;
		}
	}
	
	private void showTipsDialog(){
		if (dialogSyncHistory!=null && dialogSyncHistory.isShowing()) {
			dialogSyncHistory.dismiss();dialogSyncHistory=null;
		}
		dialogSyncHistory = new ProgressDialog(getApplicationContext());
		dialogSyncHistory.setCancelable(false);
		dialogSyncHistory.setMessage("同步手环历史数据 ...");
		dialogSyncHistory.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialogSyncHistory.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialogSyncHistory.show();
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (dialogSyncHistory!=null &&dialogSyncHistory.isShowing()) {
					dialogSyncHistory.dismiss();
					L.e("同步历史数据超时");
				}
			}
		}, 60*1000);

	}
	/**
	 * 打开对话框
	 */
	private void showDialog() {
		dialogSync = new ActionSheetDialog(this).builder().setCancelable(false).setCanceledOnTouchOutside(false)
			.addSheetItem("同步所有历史记录", SheetItemColor.Blue, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
		
					Log.e("RecordFragment", "同步所有历史数据");
					if (isConnected()) {
						showTipsDialog();
						mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(2, 0));
					}else{
						new AlertDialog(getApplicationContext()).builder().setMsg("没有绑定手环").setCancelable(true).show();
					}
				
				}
			})
			.addSheetItem("同步今天记录", SheetItemColor.Blue, new OnSheetItemClickListener() {
				@Override
				public void onClick(int which) {
					Log.e("RecordFragment", "同步今天数据");
					if (isConnected()) {
						showTipsDialog();
						mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(1, 0));
					}else{
						new AlertDialog(getApplicationContext()).builder().setMsg("没有绑定手环").setCancelable(true).show();
					}
				}
			});
			dialogSync.show();
	}

	private class MyAdapter extends FragmentStatePagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			RecordFragment f = new RecordFragment();
			Bundle b = new Bundle();
			b.putInt("layout", layouts[arg0]);
			f.setArguments(b);
			return f;
		}

		@Override
		public int getCount() {
			return layouts.length;
		}
		
	}

}
