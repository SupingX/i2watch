package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.menu.MenuActivity;
import com.suping.i2_watch.menu.RecordActivity;
import com.suping.i2_watch.setting.PedometerActivity;
import com.suping.i2_watch.setting.SettingActivity;
import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.suping.i2_watch.view.ColorsCircle;
import com.suping.i2_watch.view.RefreshForScrolView;
import com.suping.i2_watch.view.RefreshForScrolView.PullToRefreshListener;
import com.xtremeprog.sdk.ble.BleManager;
import com.xtremeprog.sdk.ble.BleService;
/**
 * 主页
 * 
 * @author Administrator
 *
 */
public class Main extends Activity implements OnClickListener {
	/** 功能菜单  **/
	private ImageView imgMenu;
	/** 设置  **/
	private ImageView imgSetting;
	/** 标题 ： 点击连接蓝牙 **/
	private TextView tvTitle;
	/** 运动/睡眠 目标 **/
	private TextView tvSportGoal,tvSleepGoal;
	/** 运动/睡眠 完成度  **/
	private TextView tvSportComplete,tvSleepComplete;
	/** 运动  kal time distancee **/
	private TextView tvSportKal,tvSportTime,tvSportDistance;
	/** 运动/睡眠信息  **/
	private TextView tvSportTips,tvSleepTips;
	/** 睡眠 深度/轻度/清醒 时间  **/
	private TextView tvSleepDeep,tvSleepLight,tvAwak;
	/** viewpager **/
	private ViewPager mViewPager;
	/** 页面 ： 包括睡眠和运动页面  **/
	private List<View> mList;
	/** 下拉刷新  **/
	private RefreshForScrolView mSportRefresh;
	private RefreshForScrolView mSleepRefresh;
	
	
	/** 圆环 sport **/
	private ColorsCircle ccSport;
	/** 圆环 sleep  **/
	private ColorsCircle ccSleep;
	/** 导航圆点radioGroup **/
	private RadioGroup rgDot;
	/** 导航圆点 radioButton : sleep DOT sport DOT **/
	private RadioButton rbDotSleep,rbDotSport;
	
	private RelativeLayout rlSportCircle;
	private RelativeLayout rlSleepCircle;
	
	private long exitTime = 0;
	
	private BleManager mBleManager;
	
	private Handler mHandler = new Handler(){
		
	};
	private Runnable reScan = new Runnable() {
		@Override
		public void run() {
			if(mBleManager.isEnabled()){
				if(!mBleManager.isConnectted()){
					mBleManager.startScan();
				}
			} else {
				Toast.makeText(Main.this, "蓝牙已关闭，请重新连接", Toast.LENGTH_LONG).show();
				mBleManager.clear();
				updateBlueState();
				startActivity(new Intent(Main.this,ConnectEvolveActivity.class));
			}
		}
	};
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BleService.BLE_GATT_CONNECTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBleManager.setConnectted(true);
						//连接成功，则停止扫描
						mBleManager.stopScan();
						updateBlueState();
					}
				});
				
			} else if(action.equals(BleService.BLE_GATT_DISCONNECTED)){
				Log.e("Main.java", "蓝牙断开");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mBleManager.setConnectted(false);
						updateBlueState();
					}
				});
				mHandler.post(reScan);
			} else if(action.equals(BleService.BLE_CHARACTERISTIC_WRITE)){
			} else if(action.equals(BleService.BLE_DEVICE_FOUND)){
				Log.e("Main", "BLE_DEVICE_FOUND...");
				// 找到设备
				Bundle b = intent.getExtras();
				final BluetoothDevice device = b.getParcelable(BleService.EXTRA_DEVICE);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mBleManager.reconnect(device.getAddress());
					}
				});
			} else if(action.equals(BleService.BLE_CHARACTERISTIC_CHANGED)){
			} else if(action.equals(BleService.BLE_SERVICE_DISCOVERED)){
			}
		}

		
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initViewPager();
		setListener();
		intiSportValue();
		intiSleepValue();
	}
	
	@Override
	protected void onResume() {
		IntentFilter mIntentFilter = BleService.getIntentFilter();
		registerReceiver(mReceiver, mIntentFilter);
		mBleManager = ((XtremApplication)getApplication()).getBleManager();
		Log.e("Main.java", "mBleManager : " + mBleManager);
		updateBlueState();
		super.onResume();
	}
	@Override
	protected void onPause() {
		unregisterReceiver(mReceiver);
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		mHandler.removeCallbacks(reScan);
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_menu:
			Intent menu = new Intent(Main.this, MenuActivity.class);
			startActivity(menu);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter, R.anim.activity_from_left_to_right_exit);
	
			break;
		case R.id.img_setting:
			Intent setting = new Intent(Main.this, SettingActivity.class);
			startActivity(setting);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.tv_title:
			//第一次进入，不回扫描，则不用停止
			//连接成功，也不用停止
			//断开连接，会启动扫描。点击了连接就停止扫描
			if(mBleManager.isScanning()){
				mBleManager.stopScan();
			}
			
			//第一次进入，未连接地址可为空
			//已经连接，地址不为空
			//断开了连接，可自动连接。但是点击了连接，就设置地址为空
			if(!mBleManager.isConnectted()){
				mBleManager.setCurrentAddress(null);
			}
			mHandler.removeCallbacks(reScan);
			Intent intentBluetooth = new Intent(Main.this, ConnectEvolveActivity.class);
			startActivity(intentBluetooth);
			break;
		
//		case R.id.tv_sport_goal:
		case R.id.color_circle_sport:
//		case R.id.rl_sport_circle:
			
			Log.d("Main", "我来处理");
			Intent recordIntent1 = new Intent(Main.this, RecordActivity.class);
			Bundle b1 = new Bundle();
			b1.putInt("flag", 0);
			recordIntent1.putExtras(b1);
			startActivity(recordIntent1);
			break;
			
//		case R.id.tv_sleep_pedo:
		case R.id.color_circle_sleep:
			Log.d("Main", "我来处理");
//		case R.id.rl_sleep_circle:
			Intent recordIntent2 = new Intent(Main.this, RecordActivity.class);
			Bundle b2 = new Bundle();
			b2.putInt("flag", 1);
			recordIntent2.putExtras(b2);
			startActivity(recordIntent2);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initViews() {
		
		imgMenu = (ImageView) findViewById(R.id.img_menu);
		imgSetting = (ImageView) findViewById(R.id.img_setting);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		rgDot = (RadioGroup) findViewById(R.id.rg_dot);
		rbDotSport = (RadioButton) findViewById(R.id.rb_dot_sport);
		rbDotSleep = (RadioButton) findViewById(R.id.rb_dot_sleep);
		
	}

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.guidePages);
		LayoutInflater inflater = getLayoutInflater();
		//运动界面  初始化
		View sportV = inflater.inflate(R.layout.layout_info_sport, null);
		ccSport = (ColorsCircle) sportV.findViewById(R.id.color_circle_sport);
		mSportRefresh = (RefreshForScrolView) sportV.findViewById(R.id.refresh_sport);
		rlSportCircle = (RelativeLayout) sportV.findViewById(R.id.rl_sport_circle);
		tvSportGoal = (TextView) sportV.findViewById(R.id.tv_sport_goal);
		tvSportComplete = (TextView) sportV.findViewById(R.id.tv_sport_complete);
		tvSportDistance = (TextView) sportV.findViewById(R.id.tv_distance_value);
		tvSportKal = (TextView) sportV.findViewById(R.id.tv_burned_value);
		tvSportTime = (TextView) sportV.findViewById(R.id.tv_time_value);
		tvSportTips = (TextView) sportV.findViewById(R.id.tv_sport_tips);
		
		//睡眠界面  初始化
		View sleepV = inflater.inflate(R.layout.layout_info_sleep, null);
		ccSleep = (ColorsCircle) sleepV.findViewById(R.id.color_circle_sleep);
		mSleepRefresh = (RefreshForScrolView) sleepV.findViewById(R.id.refresh_sleep);
		rlSleepCircle = (RelativeLayout) sleepV.findViewById(R.id.rl_sleep_circle);
		tvSleepGoal = (TextView) sleepV.findViewById(R.id.tv_sleep_pedo);
		tvSleepComplete = (TextView) sportV.findViewById(R.id.tv_sleep_complete);
		tvSleepDeep = (TextView) sportV.findViewById(R.id.tv_deep_value);
		tvSleepLight = (TextView) sportV.findViewById(R.id.tv_light_value);
		tvAwak = (TextView) sportV.findViewById(R.id.tv_awake_value);
		tvSleepTips = (TextView) sportV.findViewById(R.id.tv_sleep_tips);
		
		
		//FOR TEST
		tvSleepGoal.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 v.getParent().requestDisallowInterceptTouchEvent(true);
				 tvSleepGoal.setBackgroundColor(Color.RED);
				return true;
			}
		});
	
		
		
		
		mList = new ArrayList<>();
		mList.add(sportV);
		mList.add(sleepV);
		mViewPager.setAdapter(new ViewPagerAdapter(mList));
		mViewPager.setCurrentItem(0);
	
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					rbDotSport.setButtonDrawable(getResources().getDrawable(R.drawable.ic_dot_selected));
					rbDotSleep.setButtonDrawable(getResources().getDrawable(R.drawable.ic_dot_unselected));
					break;
				case 1:
					rbDotSport.setButtonDrawable(getResources().getDrawable(R.drawable.ic_dot_unselected));
					rbDotSleep.setButtonDrawable(getResources().getDrawable(R.drawable.ic_dot_selected));
					break;

				default:
					break;
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	/**
	 * 初始化运动数据
	 */
	private void intiSportValue(){
		//获取目标步数
		int step = (int) SharedPreferenceUtil.get(getApplicationContext(), PedometerActivity.SHARE_GOAL, 10000);
		//设置目标步数
		tvSportGoal.setText(step + "");
		//设置最大值
		ccSport.setmProgressMax(step);
		//设置卡洛里
		tvSportKal.setText(DataUtil.getKal(step));
		//设置距离
		tvSportDistance.setText(DataUtil.getDistance(step));
		//设置时间
		float time = 0.1f;
		tvSportTime.setText(time + "");
		//设置完成度
		int compltete = 5000;
		tvSportComplete.setText("" + compltete);
		//设置圆环进度
		ccSport.setmProgress(compltete);
		//设置运动信息
		double b = compltete/step;
		Log.e("MAIN", b + "<--");
		
		tvSportTips.setText("You had completed " + DataUtil.getPercent(b) + " ,come on.");
	}
	
	/**
	 * 初始化睡眠数据
	 */
	private void intiSleepValue(){
		
	}
	
	
	/**
	 * 设置各view的listener事件
	 */
	private void setListener() {
		imgMenu.setOnClickListener(this);
		imgSetting.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		tvSportGoal.setOnClickListener(this);
		tvSleepGoal.setOnClickListener(this);
		ccSleep.setOnClickListener(this);
		ccSport.setOnClickListener(this);
//		rlSleepCircle.setOnClickListener(this);
//		rlSportCircle.setOnClickListener(this);
		
		//sportV下拉刷新事件
		mSportRefresh.setOnRefreshListener(new PullToRefreshListener() {

			@Override
			public void onRefresh() {
				mHandler.postDelayed((new Runnable() {
					
					@Override
					public void run() {
						ccSport.setmProgress(4200);
						mSportRefresh.finishRefreshing();
					}
				}),2000);
			
			}
		}, 2);
		
		//sleepV下拉刷新事件
		mSleepRefresh.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						ccSleep.setmProgress(12200);
						mSleepRefresh.finishRefreshing();
				
					}
				},2000);
			}
		}, 1);
		
//	ccSport.setOnTouchListener(new OnTouchListener() {
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//			float xD = 0 ;
//			float yD = 0;
//			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//					xD= event.getX();
//					yD = event.getY();
//			break;
//			case MotionEvent.ACTION_MOVE:
//			return false;
//			case MotionEvent.ACTION_UP:
//				float xUp = event.getX();
//				float yUp = event.getX();
////				if(Math.abs(xD-xUp)<4&& Math.abs(yD-yUp)<4){
//					if((xD==xUp)&& (yD==yUp)){
//					Log.d("Main", "我来处理");
//					Intent recordIntent1 = new Intent(Main.this, RecordActivity.class);
//					Bundle b1 = new Bundle();
//					b1.putInt("flag", 0);
//					recordIntent1.putExtras(b1);
//					startActivity(recordIntent1);
//					return true;
//					}else {
//						return false;
//					}
//			default:
//				break;
//			}
//		
//			return false;
//		}
//	});	

	}
	
	/**
	 * 蓝牙状态  红色：未连接
	 */
	private void updateBlueState() {
		if(mBleManager.isConnectted()){
			tvTitle.setTextColor(Color.WHITE);
		}else {
			tvTitle.setTextColor(Color.RED);
		}
	}
	
	/**
	 * viewpager adapter
	 * @author Administrator
	 *
	 */
	private class ViewPagerAdapter extends PagerAdapter {
		private List<View> listVs;

		public ViewPagerAdapter(List<View> list) {
			this.listVs = list;
		}

		@Override
		public int getCount() {
			return listVs.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (View) arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(listVs.get(position), 0);
			return listVs.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(listVs.get(position));
		}

	}
}
