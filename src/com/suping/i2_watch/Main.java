package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.pullrefresh.ui.PullToRefreshBase;
import com.lee.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.lee.pullrefresh.ui.PullToRefreshScrollView;
import com.suping.i2_watch.broadcastreceiver.ClsUtils;
import com.suping.i2_watch.broadcastreceiver.ImpXplBroadcastReceiver;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.entity.LitePalManager;
import com.suping.i2_watch.menu.MenuActivity;
import com.suping.i2_watch.menu.RecordActivity;
import com.suping.i2_watch.service.AddressSaved;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.xblue.XBlueBroadcastReceiver;
import com.suping.i2_watch.service.xblue.XBlueBroadcastUtils;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.setting.PedometerActivity;
import com.suping.i2_watch.setting.SettingActivity;
import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.suping.i2_watch.util.ToastUtils;
import com.suping.i2_watch.view.ActionSheetDialog;
import com.suping.i2_watch.view.ActionSheetDialog.OnSheetItemClickListener;
import com.suping.i2_watch.view.ActionSheetDialog.SheetItemColor;
import com.suping.i2_watch.view.ColorsCircle;

/**
 * 主页
 * 
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
	// private AbstractSimpleBlueService mSimpleBlueService;
	private ProgressDialog loadSleepDialog;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
		};
	};
		
	private XBlueService xBlueService;
	private BroadcastReceiver otherReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (XBlueBroadcastUtils.ACTION_BLUETOOTH_ADAPTER_DISABLE.equals(action)) {
//				Toast.makeText(getApplicationContext(), "蓝牙不可用，请重启蓝牙", Toast.LENGTH_SHORT).show();
//				toast.setText("蓝牙不可用，请重启蓝牙");
//				toast.setDuration(Toast.LENGTH_SHORT);
//				toast.show();
				ToastUtils.showToast(getApplicationContext(), "蓝牙不可用，请重启蓝牙");
			}else if (XBlueBroadcastUtils.ACTION_DO_NOT_SUPPORT_BLE.equals(action)) {
//				Toast.makeText(getApplicationContext(), "设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
//				toast.setText("设备不支持低功耗蓝牙");
//				toast.setDuration(Toast.LENGTH_SHORT);
//				toast.show();
				ToastUtils.showToast(getApplicationContext(), "设备不支持低功耗蓝牙");
			}
		}
			
	};
	private XBlueBroadcastReceiver receiver = new XBlueBroadcastReceiver() {
		public void doDeviceFound(final java.util.ArrayList<BluetoothDevice> devices) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
				}
			});
		}

		@Override
		public void doServiceDiscovered(BluetoothDevice device) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
				}
			});
		}

		@Override
		public void doStepAndCalReceiver(final long[] data) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if (data != null) {
						int step = (int) data[0];
						int cal = (int) data[1];
						int time = (int) data[2];
						intiSportValue(step, cal, time);
					}
				}
			});
		}

		@Override
		public void doSyncStart() {
			isSyncing = true;
		}

		@Override
		public void doSyncEnd() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					isSyncing = false;
					ccSleep.setmProgress(0);
					sleepPull.onPullDownRefreshComplete();
					// 刷新今天的睡眠信息
					Date date = new Date();
					String startHour = (String) SharedPreferenceUtil.get(Main.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_START_HOUR, "22");
					String endHour = (String) SharedPreferenceUtil.get(Main.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_END_HOUR, "7");
					float[] sleepInfo = LitePalManager.instance().getSleepInfoForSetting(date, startHour, endHour);
					intiSleepValue(sleepInfo[0], sleepInfo[1], sleepInfo[2]);
				}
			});
		}

		@Override
		public void doCamera() {
			
		}

		@Override
		public void doConnectStateChange(BluetoothDevice device, int state) {
			
		};
	};
	
	
	
	private boolean isSyncing = false;
	private ImpXplBroadcastReceiver xplReceiver = new ImpXplBroadcastReceiver() {
		public void doStepAndCalReceiver(long[] data) {
			if (data != null) {
				int step = (int) data[0];
				int cal = (int) data[1];
				int time = (int) data[2];
				// Toast.makeText(Main.this, "运动信息已刷新...",
				// Toast.LENGTH_SHORT).show();
				intiSportValue(step, cal, time);
			}
		};

		public void doSyncStart() {
			isSyncing = true;
		};

		public void doSyncEnd() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					isSyncing = false;
					ccSleep.setmProgress(0);
					sleepPull.onPullDownRefreshComplete();
					// 刷新今天的睡眠信息
					Date date = new Date();
					String startHour = (String) SharedPreferenceUtil.get(Main.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_START_HOUR, "22");
					String endHour = (String) SharedPreferenceUtil.get(Main.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_END_HOUR, "7");
					float[] sleepInfo = LitePalManager.instance().getSleepInfoForSetting(date, startHour, endHour);
					intiSleepValue(sleepInfo[0], sleepInfo[1], sleepInfo[2]);
				}
			});
		};
	};

	private ObjectAnimator animationLoadingSleep;
	private BluetoothDevice remoteDevice;
//	private XplBluetoothService xplBluetoothService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initViewPager();
		setListener();
		intiSportValue(0, 0, 0);
		intiSleepValue(0, 0, 0);
		
		IntentFilter filterOther = new IntentFilter();
		filterOther.addAction(XBlueBroadcastUtils.ACTION_BLUETOOTH_ADAPTER_DISABLE);
		filterOther.addAction(XBlueBroadcastUtils.ACTION_DO_NOT_SUPPORT_BLE);
		registerReceiver(otherReceiver, filterOther);

	}

	@Override
	protected void onStart() {
		super.onStart();
//		xplBluetoothService = getXplBluetoothService();
//		registerReceiver(xplReceiver, XplBluetoothService.getIntentFilter());
		xBlueService = getXBlueService();
		registerReceiver(receiver, XBlueBroadcastUtils.instance().getIntentFilter());
	}

	private void setGoalText() {
		// 获取目标步数
		int goalStep = (int) SharedPreferenceUtil.get(getApplicationContext(), PedometerActivity.SHARE_GOAL, 5000);
		// 设置目标步数
		tvSportGoal.setText(goalStep + "");
		// 设置最大值
		ccSport.setmProgressMax(goalStep);
		// 目标睡眠
		int goalSleep = getSleepGoal();
		String target = DataUtil.format(goalSleep / 60f);
		tvSleepGoal.setText(target);
		// 设置最大值 单位分钟
		ccSleep.setmProgressMax(goalSleep);
	}

	/**
	 * 睡眠目标 单位：分钟
	 * 
	 * @return
	 */
	private int getSleepGoal() {
		// 目标睡眠
		String hour = (String) SharedPreferenceUtil.get(this, I2WatchProtocolDataForWrite.SHARE_MONITOR_TARGET_HOUR, "12");
		String min = (String) SharedPreferenceUtil.get(this, I2WatchProtocolDataForWrite.SHARE_MONITOR_TARGET_MIN, "0");
		int goalSleep = (int) (Integer.valueOf(hour) * 60 + Integer.valueOf(min));
		return goalSleep;

	}

	@Override
	protected void onResume() {
		
		setGoalText();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
//				if (isXplConnected()) {
//				}
				if (xBlueService!=null  ) {
					if (xBlueService.isAllConnected()) {
						xBlueService.write(I2WatchProtocolDataForWrite.hexDataForGetTodayTotalStepAndCal());
					}else{
//						xBlueService.startScan();
					}
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
//		unregisterReceiver(xplReceiver);
		unregisterReceiver(otherReceiver);
		unregisterReceiver(receiver);
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
		// ccSport.stopAnimation();
		// ccSport.setmProgressAnimation(step);

		// double b = step*1.0f/ccSport.getProgressMax();
		String start = getString(R.string.sport_tips_start);
		String end = getString(R.string.sport_tips_end);
		tvSportTips.setText(start + DataUtil.getPercent(step, ccSport.getProgressMax()) + end);
	}

	/**
	 * 初始化睡眠数据（单位：分钟）
	 * 
	 * @param deep
	 * @param light
	 * @param awak
	 */
	private void intiSleepValue(float deep, float light, float awak) {
		// 完成睡眠
		float total = deep + light;

		tvSleepComplete.setText(DataUtil.format(total / 60f));
		ccSleep.setmProgress((int) (total));
		// ccSleep.stopAnimation();
		// ccSleep.setmProgressAnimation((int)(total));
		// 深睡、浅睡、清醒
		tvSleepDeep.setText(DataUtil.format(deep / 60f));
		tvSleepLight.setText(DataUtil.format(light / 60f));
		tvAwak.setText(DataUtil.format(((getSleepGoal() - total) <= 0 ? 0 : (getSleepGoal() - total)) / 60f));
		// 睡眠质量
		tvSleepTips.setText(getSleepAdvise(deep, light, awak));
	}

	/**
	 * 睡眠评价 单位：分钟
	 * 
	 * @param deep
	 * @param light
	 * @param awak
	 * @return
	 */
	private String getSleepAdvise(float deep, float light, float awak) {
		String advise = "";
		int sleepGoal = getSleepGoal();
		if (deep + light > sleepGoal * 2 / 3) {
			advise = getString(R.string.sleep_info);
		} else if (deep + light < sleepGoal * 2 / 3 && deep + light > sleepGoal / 3) {
			advise = getString(R.string.sleep_info_normal);
		} else if (deep + light < sleepGoal / 3 && deep + light > 0) {
			advise = getString(R.string.sleep_info_bad);
		} else {
			advise = getString(R.string.sleep_info_default);
		}
		return advise;
	}

	/**
	 * 设置各view的listener事件
	 */
	private void setListener() {
		imgMenu.setOnClickListener(this);
		imgSetting.setOnClickListener(this);
		tvSleepGoal.setOnClickListener(this);
		ccSleep.setOnClickListener(this);
		ccSport.setOnClickListener(this);

		// sportV下拉刷新事件
		sportPull.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {

//				if (isXplConnected()) {
//					xplBluetoothService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetTodayTotalStepAndCal());
//				}
				
				if (xBlueService!=null && xBlueService.isAllConnected() ) {
					xBlueService.write(I2WatchProtocolDataForWrite.hexDataForGetTodayTotalStepAndCal());
				}
				
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (xBlueService!=null) {
							if (!xBlueService.isAllConnected()) {
								showIosDialog(Main.this, getString(R.string.no_binded_device));
							}
						}
						sportPull.onPullDownRefreshComplete();
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
				// if (isSyncing) {
				// showIosDialog(getApplicationContext(),getString(R.string.sync_ing));
				// sportPull.onPullDownRefreshComplete();
				// if (isXplConnected()) {
				// xplBluetoothService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(2,
				// 0));
				// }
				// return ;
				// }
				//

				/**
				 * 问题：同步今天就没有昨天的数据。需同步所有属于才有 。
				 */
				/*
				 * if (isConnected()) { mHandler.postDelayed(new Runnable() {
				 * 
				 * @Override public void run() { // startAnimation(); //
				 * loadSleepDialog =
				 * showProgressDialog(getString(R.string.loading_sleep)); //
				 * Toast.makeText(getApplicationContext(),
				 * getString(R.string.loading_sleep),
				 * Toast.LENGTH_SHORT).show();
				 * mSimpleBlueService.writeCharacteristic
				 * (DataUtil.getBytesByString("25010000")); } }, 2 * 1000);
				 * }else{
				 */
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (xBlueService!=null && xBlueService.isAllConnected() ) {
							xBlueService.write(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(1, 0));
						}
					/*	if (isXplConnected()) {
							xplBluetoothService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForGetHistoryType(1, 0));
						}*/ else {
							// 未完成
							Date date = new Date();
							String startHour = (String) SharedPreferenceUtil.get(Main.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_START_HOUR, "22");
							String endHour = (String) SharedPreferenceUtil.get(Main.this, I2WatchProtocolDataForWrite.SHARE_MONITOR_END_HOUR, "7");
							float[] sleepInfo = LitePalManager.instance().getSleepInfoForSetting(date, startHour, endHour);
							intiSleepValue(sleepInfo[0], sleepInfo[1], sleepInfo[2]);
						}
						
						sportPull.onPullDownRefreshComplete();
					}
				}, 2 * 1000);

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

			}
		});

	}

	private void exitBindFromPhone() {
		try {
			String bindedAddress = AddressSaved.getBindedAddress(getApplicationContext());
			if (BluetoothAdapter.checkBluetoothAddress(bindedAddress)) {
				BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bindedAddress);
				if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
					Log.e("", bindedAddress + " --本地绑定了");
					ClsUtils.cancelBondProcess(BluetoothDevice.class, btDevice);
					ClsUtils.cancelPairingUserInput(BluetoothDevice.class, btDevice);
				} else if (btDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
					Log.e("", bindedAddress + "绑定中");
					ClsUtils.removeBond(BluetoothDevice.class, btDevice);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {

		// MusicManager.instance(getApplicationContext()).start(R.raw.crystal);

		ActionSheetDialog exitDialog = new ActionSheetDialog(this).builder();
		exitDialog.setTitle(getString(R.string.confirm_exit));
		exitDialog.addSheetItem(getString(R.string.confirm), SheetItemColor.Red, new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
//						if (xplBluetoothService != null) {
//							xplBluetoothService.setIsExit(true);
//							xplBluetoothService.close();
//						}
						if (xBlueService!=null) {
							xBlueService.closeAll();
						}
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								finish();
								System.exit(0);
								// android.os.Process.killProcess(android.os.Process.myPid());

							}
						}, 2000);
					}
				});

			}
		}).show();
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
}
