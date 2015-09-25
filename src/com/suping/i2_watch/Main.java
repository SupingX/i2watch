package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.lee.pullrefresh.ui.PullToRefreshScrollView;
import com.suping.i2_watch.broadcastreceiver.SimpleBluetoothBroadcastReceiverBroadcastReceiver;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.entity.LitePalManager;
import com.suping.i2_watch.menu.MenuActivity;
import com.suping.i2_watch.menu.RecordActivity;
import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.setting.PedometerActivity;
import com.suping.i2_watch.setting.SettingActivity;
import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.FileUtil;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.util.ScreenShot;
import com.suping.i2_watch.util.ShareUtil;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.suping.i2_watch.view.ActionSheetDialog;
import com.suping.i2_watch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.suping.i2_watch.view.ActionSheetDialog.SheetItemColor;
import com.suping.i2_watch.view.ColorsCircle;

/**
 * 主页
 * @author Administrator
 *
 */
public class Main extends BaseActivity implements OnClickListener {
	/** 功能菜单 **/
	private ImageView imgMenu;
	/** 设置 **/
	private ImageView imgSetting;
	/** 标题 ： 点击连接蓝牙 **/
	private TextView tvTitle;
	/** 运动/睡眠 目标 **/
	private TextView tvSportGoal, tvSleepGoal;
	/** 运动/睡眠 完成度 **/
	private TextView tvSportComplete, tvSleepComplete;
	/** 运动参数 卡洛里（kal） 时间（time） （距离distancee **/
	private TextView tvSportKal, tvSportTime, tvSportDistance;
	/** 运动/睡眠信息 **/
	private TextView tvSportTips, tvSleepTips;
	/** 睡眠 深度/轻度/清醒 时间 **/
	private TextView tvSleepDeep, tvSleepLight, tvAwak;
	/** viewpager **/
	private ViewPager mViewPager;
	/** 页面 list： 包括睡眠和运动页面 **/
	private List<View> mList;
	/** 下拉刷新 运动 **/
	private PullToRefreshScrollView sportPull;
	/** 下拉刷新 睡眠 **/
	private PullToRefreshScrollView sleepPull;
	/** 下拉刷新 运动界面 的ScrollView **/
	private ScrollView sportScrloo;
	/** 下拉刷新 睡眠界面 的ScrollView **/
	private ScrollView sleepScrloo;
	/** 圆环 运动（sport） **/
	private ColorsCircle ccSport;
	/** 圆环 睡眠（sleep） **/
	private ColorsCircle ccSleep;
	// /** 导航圆点 radioGroup **/
	// private RadioGroup rgDot;
	/** 导航圆点 radioButton : sleep DOT & sport DOT **/
	private RadioButton rbDotSleep, rbDotSport;
	/** 蓝牙工具 **/
	private AbstractSimpleBlueService mSimpleBlueService;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String path  = (String) msg.obj;
			ShareUtil.shareImage(path, Main.this);
		};
	};
	private SimpleBluetoothBroadcastReceiverBroadcastReceiver mReceiver = new SimpleBluetoothBroadcastReceiverBroadcastReceiver() {
		public void doDiscoveredWriteService() {
			// doUpdateSetting();
		};
		public void doStepAndCalReceiver(long[] data) {
			if (data!=null) {
			int step = (int) data[0];
			int cal = (int) data[1];
			int time = (int) data[2];
			intiSportValue(step, cal, time);
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 拍照过程屏幕一直处于高亮
		// //设置手机屏幕朝向，一共有7种
		setContentView(R.layout.activity_main);
		initViews();
		initViewPager();
		setListener();
		intiSportValue(0, 0, 0); 
		intiSleepValue(0, 0, 0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mSimpleBlueService = getSimpleBlueService();
		registerReceiver(mReceiver, SimpleBlueService.getIntentFilter());
	}

	private void setGoalText() {
		// 获取目标步数
		int goalStep = (int) SharedPreferenceUtil.get(getApplicationContext(), PedometerActivity.SHARE_GOAL, 10000);
		L.v("goalStep :" + goalStep);
		// 设置目标步数
		tvSportGoal.setText(goalStep + "");
		// 设置最大值
		ccSport.setmProgressMax(goalStep);

		// 目标睡眠
		String hour = (String) SharedPreferenceUtil.get(this, I2WatchProtocolDataForWrite.SHARE_MONITOR_TARGET_HOUR, "12");
		String min = (String) SharedPreferenceUtil.get(this, I2WatchProtocolDataForWrite.SHARE_MONITOR_TARGET_MIN, "0");
		int goalSleep = (int) (Integer.valueOf(hour) + Integer.valueOf(min) / 60f);
		String target = DataUtil.format(goalSleep);
		tvSleepGoal.setText(target);
		// 设置最大值
		ccSleep.setmProgressMax(goalSleep);
	}

	@Override
	protected void onResume() {
		setGoalText();
		checkBlue();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isConnected()) {
					mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetTodayTotalStepAndCal());
				}
			}
		}, 2000);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_menu:
			Intent menu = new Intent(Main.this, MenuActivity.class);
			startActivity(menu);
			overridePendingTransition(R.anim.activity_from_left_to_right_enter, R.anim.activity_from_left_to_right_exit);
			break;
		case R.id.img_setting:
			Intent setting = new Intent(Main.this, SettingActivity.class);
			startActivity(setting);
			overridePendingTransition(R.anim.activity_from_right_to_left_enter, R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.tv_title:
//			Intent intentBluetooth = new Intent(Main.this, SinaActivity.class);
//			startActivity(intentBluetooth);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
				Bitmap bitmap = ScreenShot.takeScreenShot(Main.this);
				String path = FileUtil.getandSaveCurrentImage(Main.this,bitmap);
				if (path!=null) {
					Message msg = new Message();
					msg.obj = path;
					mHandler.sendMessage(msg);
				}
				}
			});
			break;
		case R.id.color_circle_sport:
			Intent recordIntent1 = new Intent(Main.this, RecordActivity.class);
			Bundle b1 = new Bundle();
			b1.putInt("flag", 0);
			recordIntent1.putExtras(b1);
			startActivity(recordIntent1);
			break;

		case R.id.color_circle_sleep:
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

	private void initViews() {
		imgMenu = (ImageView) findViewById(R.id.img_menu);
		imgSetting = (ImageView) findViewById(R.id.img_setting);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		// rgDot = (RadioGroup) findViewById(R.id.rg_dot);
		rbDotSport = (RadioButton) findViewById(R.id.rb_dot_sport);
		rbDotSleep = (RadioButton) findViewById(R.id.rb_dot_sleep);
	}

	/**
	 * 初始化viewpager
	 */
	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.guidePages);
		LayoutInflater inflater = getLayoutInflater();
		// 运动界面 初始化
		View sportV = inflater.inflate(R.layout.layout_info_sport, null);
		ccSport = (ColorsCircle) sportV.findViewById(R.id.color_circle_sport);
		tvSportGoal = (TextView) sportV.findViewById(R.id.tv_sport_goal);
		tvSportComplete = (TextView) sportV.findViewById(R.id.tv_sport_complete);
		tvSportDistance = (TextView) sportV.findViewById(R.id.tv_distance_value);
		tvSportKal = (TextView) sportV.findViewById(R.id.tv_burned_value);
		tvSportTime = (TextView) sportV.findViewById(R.id.tv_time_value);
		tvSportTips = (TextView) sportV.findViewById(R.id.tv_sport_tips);
		// >运动下拉刷新
		sportPull = new PullToRefreshScrollView(this);
		sportScrloo = sportPull.getRefreshableView();
		sportScrloo.addView(sportV);

		// 睡眠界面 初始化
		View sleepV = inflater.inflate(R.layout.layout_info_sleep, null);
		ccSleep = (ColorsCircle) sleepV.findViewById(R.id.color_circle_sleep);
		tvSleepGoal = (TextView) sleepV.findViewById(R.id.tv_sleep_goal);
		tvSleepComplete = (TextView) sleepV.findViewById(R.id.tv_sleep_complete);
		tvSleepDeep = (TextView) sleepV.findViewById(R.id.tv_deep_value);
		tvSleepLight = (TextView) sleepV.findViewById(R.id.tv_light_value);
		tvAwak = (TextView) sleepV.findViewById(R.id.tv_awake_value);
		tvSleepTips = (TextView) sleepV.findViewById(R.id.tv_sleep_tips);
		// >睡眠下拉刷新
		sleepPull = new PullToRefreshScrollView(this);
		sleepScrloo = sleepPull.getRefreshableView();
		sleepScrloo.addView(sleepV);

		mList = new ArrayList<>();
		mList.add(sportPull);
		mList.add(sleepPull);

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
				// mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	/**
	 * 初始化运动数据
	 */
	private void intiSportValue(int step, int cal, int time) {

		// 设置卡洛里
		tvSportKal.setText(DataUtil.format(cal / 1000f));
		// 设置距离
		tvSportDistance.setText(DataUtil.getDistance(step));
		// 设置时间
		tvSportTime.setText(DataUtil.format(time / 60f));
		// 设置完成度
		tvSportComplete.setText("" + step);
		// 设置圆环进度
		ccSport.setmProgress(step);
		// double b = step*1.0f/ccSport.getProgressMax();
		String start = getResources().getString(R.string.sport_tips_start);
		String end = getResources().getString(R.string.sport_tips_end);
		tvSportTips.setText(start + DataUtil.getPercent(step, ccSport.getProgressMax()) + end);
	}

	/**
	 * 初始化睡眠数据
	 */
	private void intiSleepValue(int deep, int light, int awak) {
		// 完成睡眠
		tvSleepComplete.setText(deep + light + "");
		ccSleep.setmProgress(light + deep);
		// 深睡、浅睡、清醒
		tvSleepDeep.setText(deep + "");
		tvSleepLight.setText(light + "");
		tvAwak.setText(awak + "");
		// 睡眠质量
		// tvSleepTips.setText("睡眠质量 : 优");
		tvSleepTips.setText(getSleepAdvise(deep, light, awak));
	}

	private String getSleepAdvise(int deep, int light, int awak) {
		String advise = "--";
		// 计算
		advise = "睡眠质量 : 优";
		return advise;
	}

	/**
	 * 设置各view的listener事件
	 */
	private void setListener() {
		imgMenu.setOnClickListener(this);
		imgSetting.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		tvSleepGoal.setOnClickListener(this);
		ccSleep.setOnClickListener(this);
		ccSport.setOnClickListener(this);

		// sportV下拉刷新事件
		sportPull.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {

				if (isConnected()) {
					mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetTodayTotalStepAndCal());
				}
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(Main.this, "运动信息已刷新...", Toast.LENGTH_SHORT).show();
						// ccSport.setmProgress(mProgress);
						sportPull.onPullDownRefreshComplete();
						// DataUtil.setLastUpdateTime(sportPull);
					}
				}, 2 * 1000);
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
			}
		});
		// sleepV下拉刷新事件
		sleepPull.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (isConnected()) {
					// 刷新今天的睡眠信息
					int[] dataSleeps = LitePalManager.instance().getHistoryForCount(new Date());
					intiSleepValue(dataSleeps[2], dataSleeps[3], dataSleeps[4]);

				}
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {

						Toast.makeText(Main.this, "睡眠信息已刷新...", Toast.LENGTH_SHORT).show();
						sleepPull.onPullDownRefreshComplete();
						// DataUtil.setLastUpdateTime(sleepPull);
					}
				}, 2 * 1000);

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

			}
		});

	}


	/**
	 * 检查蓝牙
	 */
	private void checkBlue() {
		Log.v("", "-----检查蓝牙-----");
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mSimpleBlueService != null) {
					// 确认蓝牙

					if (!mSimpleBlueService.isEnable()) {
						// if (onceEnter) {
						// Intent enableBtIntent = new
						// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						// startActivityForResult(enableBtIntent, 1);
						// // showIosDialog();
						// onceEnter = false;
						// }
					} else {
						if (null != mSimpleBlueService && mSimpleBlueService.isBinded() && mSimpleBlueService.getConnectState() != BluetoothProfile.STATE_CONNECTED) {
							if (mSimpleBlueService.isScanning()) {
								mSimpleBlueService.scanDevice(false);
							}
							L.v("初次进入主界面，发觉绑定设备，尝试搜索重连");
							mSimpleBlueService.scanDevice(true);
						}
					}
				} else {
					Log.e("", "-----检查蓝牙-----service为空");
				}

			}
		});
	}

	@Override
	public void onBackPressed() {
		/*** ----------------------for test block--------------------------- **/
		// mSimpleBlueService.writeCharacteristic(DataUtil.getBytesByString("25010000"));
		// mSimpleBlueService.writeCharacteristic(DataUtil.getBytesByString("25020000"));
		// mSimpleBlueService.writeCharacteristic(DataUtil.getBytesByString("910e61626364656667"));
//		 mSimpleBlueService.writeCharacteristic(DataUtil.getBytesByString("910e3d3e3f40414243"));
		// mSimpleBlueService.writeCharacteristic(ProtocolForWrite.instance().getByteForWeather(0x01,
		// 0x40 , 0x54));
		// showdialog("hahhahah");

//		DataSupport.deleteAll(HistorySleep.class);
//		DataSupport.deleteAll(HistorySport.class);
	
		
		
//		List<HistorySport> findAll = DataSupport.findAll(HistorySport.class);
//		L.i(findAll.toString());
//		for (HistorySport hs : findAll) {
//			L.v(hs.toString());
//		}
		
		/*** ------------ for test block ---------------- -------------------**/

		ActionSheetDialog exitDialog = new ActionSheetDialog(this).builder();
		exitDialog.setTitle("退出程序？");
		exitDialog.addSheetItem("确定", SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				finish();
			}
		}).show();
		// super.onBackPressed();
		int tempInt = Integer.valueOf("-20");
		Log.e("", "________________________tempInt : " + tempInt);

	}

	/**
	 * viewpager adapter
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
			((ViewPager) container).removeView(listVs.get(position));
		}
	}
	//
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// 0 10001 00001 10011 0010 0010 0001 0001

	// 0 00100 01001 00010 0011 0011 0100 0100

	// Object obj =
	// I2WatchProtocolDataForNotify.instance().notifyHistory(DataUtil.getBytesByString("250322443322114433221144332211"));
	// if (obj instanceof HistorySport) {
	// HistorySport s = (HistorySport) obj;
	// L.v(s.toString());
	// }

	// L.v((0b10011L&0b11111) + "");
	// long value = 0b01000100001100110010001000010001L>>16&0b11111;
	// L.v(value + "");
	//
	// int data_1 = 0b1100_0000_0000_0000_1111_1111_0101_1111;
	// long s = data_1 & 0x0FFFFFFFFl;
	// long type = s>>31;
	//
	// L.v(data_1+"");
	// L.v(s+"");
	// L.v(type+"");
	// long ss = s>>4&0b11111;
	// L.v(ss+"");
	// mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForTimeSync(new
	// Date(), this));

	// mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForSignatureSync(this));

	// startActivity(new Intent(Main.this, VirtualActivity.class));
	// if (null != mSimpleBlueService && mSimpleBlueService.isBinded() &&
	// mSimpleBlueService.getConnectState() == BluetoothProfile.STATE_CONNECTED)
	// {
	// mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForHasCallingCount(MessageUtil.readMissCall(getApplicationContext())));
	// }

	// Test.instance().CreateHistoryData();
	// // 5.睡眠
	// byte[] hexDataForSleepPeriodSync =
	// I2WatchProtocolDataForWrite.hexDataForSleepPeriodSync(getApplicationContext());
	// mSimpleBlueService.writeCharacteristic(hexDataForSleepPeriodSync);

	// // 1.运动提醒
	// byte[] byteActivityRemindSync =
	// I2WatchProtocolDataForWrite.protocolDataForActivityRemindSync(getApplicationContext()).toByte();
	// // mSimpleBlueService.writeCharacteristic(byteActivityRemindSync);
	//
	// // 2.来电提醒
	// byte[] protocolForCallingAlarmPeriodSync =
	// I2WatchProtocolDataForWrite.protocolForCallingAlarmPeriodSync(getApplicationContext()).toByte();
	// mSimpleBlueService.writeCharacteristic(protocolForCallingAlarmPeriodSync);
	//
	// // 3.防丢提醒
	// byte[] hexDataForLostOnoffI2Watch =
	// I2WatchProtocolDataForWrite.hexDataForLostOnoffI2Watch(getApplicationContext());
	// mSimpleBlueService.writeCharacteristic(hexDataForLostOnoffI2Watch);

	// 4.闹钟

	// //闹钟设置数据 ：15007F0101007F0102007F0103
	// byte[] protocolDataForClockSync =
	// I2WatchProtocolDataForWrite.protocolDataForClockSync(getApplicationContext()).toByte();
	// mSimpleBlueService.writeCharacteristic((protocolDataForClockSync));

	//
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() ==
	// KeyEvent.ACTION_DOWN) {
	// if ((System.currentTimeMillis() - exitTime) > 2000) {
	// Toast.makeText(getApplicationContext(), "再按一次退出程序",
	// Toast.LENGTH_SHORT).show();
	// exitTime = System.currentTimeMillis();
	// } else {
	// finish();
	// System.exit(0);
	// }
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	
	
	
}
