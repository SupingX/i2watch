package com.suping.i2_watch.menu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.suping.i2_watch.R;
import com.suping.i2_watch.broadcastreceiver.SimpleBluetoothBroadcastReceiverBroadcastReceiver;
import com.suping.i2_watch.entity.LitePalManager;
import com.suping.i2_watch.menu.async.LoadHistoryAsyncTask;
import com.suping.i2_watch.menu.async.LoadHistoryAsyncTask.OnPostExecuteListener;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.DateUtil;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.view.SleepCountView;
import com.suping.i2_watch.view.SportCountView;
import com.suping.i2_watch.view.SportCountView.OnStepsChangeListener;

public class RecordFragment extends Fragment {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private int flag;

	/** 下一天/上一天 **/
	private ImageView reduce, inrease, reduceSleep, inreaseSleep;
	/** 日期 **/
	private TextView tvDate, tvDateSleep;
	/** 统计图 **/
	// private CountView
	// countSleep;
	private SportCountView sportCountView;
	private SleepCountView sleepCountView;
	private Handler mHandler = new Handler() {

	};

	private SimpleBluetoothBroadcastReceiverBroadcastReceiver mReceiver = new SimpleBluetoothBroadcastReceiverBroadcastReceiver() {
		public void doSyncEnd() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

				}
			});
		};

		public void doSyncStart() {
			L.i("同步历史数据开始");
		};
	};

	/**
	 * 运动视图点击事件
	 */
	private OnClickListener sportOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.reduce:
				Log.e("RecordFragment", "减少");
				textViewDateChange(tvDate, -1);
				String dateStr1 = tvDate.getText().toString();
				// sportCountView.setSteps(getRandomData());//模拟数据
				int[] dataForSport1 = LitePalManager.instance().getDataForSport(dateStr1);
				if (dataForSport1 != null) {
					sportCountView.setSteps(dataForSport1);
				}
				int[] historyForCount1 = LitePalManager.instance().getHistoryForCount(DateUtil.stringToDate(dateStr1, "yyyy-MM-dd"));
				updateSportInfo(historyForCount1[0], historyForCount1[1]);
				break;
			case R.id.increase:
				Log.e("RecordFragment", "增加");
				textViewDateChange(tvDate, 1);
				// sportCountView.setSteps(getRandomData());//模拟数据

				String dateStr2 = tvDate.getText().toString();
				int[] dataForSport2 = LitePalManager.instance().getDataForSport(dateStr2);
				if (dataForSport2 != null) {
					sportCountView.setSteps(dataForSport2);
				}

				int[] historyForCount2 = LitePalManager.instance().getHistoryForCount(DateUtil.stringToDate(dateStr2, "yyyy-MM-dd"));
				updateSportInfo(historyForCount2[0], historyForCount2[1]);
				break;
			default:
				break;
			}
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					String dateStr1 = tvDate.getText().toString();
					updateSportUI(dateStr1);
				}
			});

		}
	};

	/**
	 * 睡眠视图点击事件
	 */
	private OnClickListener sleepOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.reduce_sleep:
				Log.e("RecordFragment", "增加sleep");
				textViewDateChange(tvDateSleep, -1);
//				int[] historyForCount1 = LitePalManager.instance().getHistoryForCount(DateUtil.stringToDate(dateStr1, "yyyy-MM-dd"));
//				updateSleepInfo(historyForCount1[2], historyForCount1[3], historyForCount1[4]);
//				int[] dataForSleep1 = LitePalManager.instance().getDataForSleep(dateStr1);
//				if (dataForSleep1 != null) {
//					sleepCountView.setSleeps(dataForSleep1);
//				}
			
				break;
			case R.id.increase_sleep:
				Log.e("RecordFragment", "增加sleep");
				textViewDateChange(tvDateSleep, 1);
//				String dateStr2 = tvDateSleep.getText().toString();
//				int[] dataForSleep2 = LitePalManager.instance().getDataForSleep(dateStr2);
//				if (dataForSleep2 != null) {
//					sleepCountView.setSleeps(dataForSleep2);
//				}
//				int[] historyForCount2 = LitePalManager.instance().getHistoryForCount(DateUtil.stringToDate(dateStr2, "yyyy-MM-dd"));
//				updateSleepInfo(historyForCount2[2], historyForCount2[3], historyForCount2[4]);
				break;
			default:
				break;
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					String dateStr1 = tvDateSleep.getText().toString();
					updateSleepUI(dateStr1);
				}
			});
		
		}
	};
	private TextView tvSportCal;
	private TextView tvSportTime;
	private TextView tvSportDistance;
	private TextView tvSleepDeep;
	private TextView tvSleepLight;
	private TextView tvSleepAwak;

	/**
	 * Dialog视图点击事件
	 * 
	 * 0 取消读取历史数据 1 读取当天历史数据 2 读取所有历史数据 0x5a 删除所有历史数据
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		flag = getArguments().getInt("layout");
		View v = inflater.inflate(flag, null);
		getActivity().registerReceiver(mReceiver, SimpleBlueService.getIntentFilter());
		return v;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 当是睡眠画面时
		if (flag == R.layout.fragment_record_sleep) {
			if (reduceSleep==null) {
				reduceSleep = (ImageView) getActivity().findViewById(R.id.reduce_sleep);
			}
			if (inreaseSleep ==null) {
				inreaseSleep = (ImageView) getActivity().findViewById(R.id.increase_sleep);
			}
			if (tvDateSleep==null) {
				tvDateSleep = (TextView) getActivity().findViewById(R.id.date_sleep);
				tvDateSleep.setText(sdf.format(new Date()));
			}

			if (tvSleepDeep==null) {
				tvSleepDeep = (TextView) getActivity().findViewById(R.id.tv_count_sleep_deep_value);
			}
			
			if (tvSleepLight==null) {
				
				tvSleepLight = (TextView) getActivity().findViewById(R.id.tv_count_sleep_light_value);
			}
			if (tvSleepAwak==null) {
				
				tvSleepAwak = (TextView) getActivity().findViewById(R.id.tv_count_sleep_awak_value);
			}
			if (sleepCountView==null) {
				sleepCountView = (SleepCountView) getActivity().findViewById(R.id.count_sleep);
			}
			// getActivity().registerForContextMenu(textViewSyncSleep);
			// //为按钮注册上下文菜单
			setLinstener(2);
			updateSleepUI(tvDateSleep.getText().toString().trim());
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					updateSleepUI(tvDateSleep.getText().toString().trim());
					
				}
			});

			// 当是运动画面时
		} else if (flag == R.layout.fragment_record_sport) {
			if (reduce == null) {
				reduce = (ImageView) getActivity().findViewById(R.id.reduce);
			}
			if (inrease == null) {
				inrease = (ImageView) getActivity().findViewById(R.id.increase);
			}
			if (tvDate == null) {
				tvDate = (TextView) getActivity().findViewById(R.id.date);
				tvDate.setText(sdf.format(new Date()));
			}
			if (sportCountView == null) {
				sportCountView = (SportCountView) getActivity().findViewById(R.id.count_sport);
			}
			if (tvSportCal == null) {
				tvSportCal = (TextView) getActivity().findViewById(R.id.tv_count_sport_cal_value);
			}
			if (tvSportTime == null) {
				tvSportTime = (TextView) getActivity().findViewById(R.id.tv_count_sport_time_value);
			}
			if (tvSportDistance == null) {
				tvSportDistance = (TextView) getActivity().findViewById(R.id.tv_count_sport_distance_value);
			}
			setLinstener(1);
			
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					updateSportUI(tvDate.getText().toString().trim());
					
				}
			});
		}
		super.onActivityCreated(savedInstanceState);
	}

	private void updateSportUI(final String dateString) {
	
		// 更新运动数据
		int[] dataForSport = LitePalManager.instance().getDataForSport(dateString);
		if (dataForSport != null) {
			int[] historyForCount2 = LitePalManager.instance().getHistoryForCount(DateUtil.stringToDate(tvDate.getText().toString().trim(), "yyyy-MM-dd"));
			updateSportInfo(historyForCount2[0], historyForCount2[1]);
		}
		int[] dataForSport2 = LitePalManager.instance().getDataForSport(dateString);
		if (dataForSport2 != null) {
			sportCountView.setSteps(dataForSport2);
		}
	}

	private void updateSleepUI(final String dateString) {
		LoadHistoryAsyncTask loadHistoryAsyncTask = new LoadHistoryAsyncTask();
		loadHistoryAsyncTask.setOnPostExecuteListener(new OnPostExecuteListener() {
			
			@Override
			public void onPostExecute(int[] result) {
				updateSleepInfo(result[2], result[3], result[4]);
			}
		});
		loadHistoryAsyncTask.execute(dateString);
		
		
//		int[] dataForSleep = LitePalManager.instance().getDataForSleep(tvDateSleep.getText().toString().trim());
//		Date stringToDate = DateUtil.stringToDate(dateString, "yyyy-MM-dd");
//		if (dataForSleep != null) {
//			sleepCountView.setSleeps(dataForSleep);
//			int[] historyForCount2 = LitePalManager.instance().getHistoryForCount(stringToDate);
//		
//		}
//		int[] dataForSleep1 = LitePalManager.instance().getDataForSleep(dateString);
//		if (dataForSleep1 != null) {
//			sleepCountView.setSleeps(dataForSleep1);
//		}
	}

	// private void loadTodaySleepInfo() {
	// //加载今天的睡眠数据
	// int[] dataForSleep =
	// LitePalManager.instance().getDataForSleep(tvDateSleep.getText().toString().trim());
	// Date stringToDate =
	// DateUtil.stringToDate(tvDateSleep.getText().toString().trim(),"yyyy-MM-dd");
	// if (dataForSleep != null) {
	// sleepCountView.setSleeps(dataForSleep);
	// int[] historyForCount2 =
	// LitePalManager.instance().getHistoryForCount(stringToDate);
	// updateSleepInfo(historyForCount2[2], historyForCount2[3],
	// historyForCount2[4]);
	// }
	// int[] dataForSleep1 =
	// LitePalManager.instance().getDataForSleep(tvDateSleep.getText().toString().trim());
	// if (dataForSleep1 != null) {
	// sleepCountView.setSleeps(dataForSleep1);
	// }
	// }
	//
	// private void loadTodaySportInfo() {
	// //加载今天的运动数据
	// int[] dataForSport =
	// LitePalManager.instance().getDataForSport(tvDate.getText().toString().trim());
	// if (dataForSport != null) {
	// int[] historyForCount2 =
	// LitePalManager.instance().getHistoryForCount(DateUtil.stringToDate(tvDate.getText().toString().trim(),
	// "yyyy-MM-dd"));
	// updateSportInfo(historyForCount2[0], historyForCount2[1]);
	// }
	// int[] dataForSport2 =
	// LitePalManager.instance().getDataForSport(tvDate.getText().toString().trim());
	// if (dataForSport2 != null) {
	// sportCountView.setSteps(dataForSport2);
	// }
	// }

	/**
	 * 更新运动信息
	 * 
	 * @param step
	 * @param time
	 */
	private void updateSportInfo(int step, int time) {
		tvSportCal.setText(DataUtil.getKal(step));
		tvSportTime.setText(DataUtil.format(time / 60f));
		tvSportDistance.setText(DataUtil.getDistance(step));
	}

	/**
	 * 更新运动信息
	 * 
	 * @param stepTotal
	 * @param time
	 */
	private void updateSleepInfo(int deep, int light, int awak) {
		tvSleepDeep.setText(DataUtil.format(deep / 60f));
		tvSleepLight.setText(DataUtil.format(light / 60f));
		tvSleepAwak.setText(DataUtil.format(awak / 60f));
	}

	/**
	 * 设置监听
	 * 
	 * @param id
	 *            id=1：运动 id=2：睡眠
	 */
	private void setLinstener(int id) {
		if (id == 1) {
			reduce.setOnClickListener(sportOnClickListener);
			inrease.setOnClickListener(sportOnClickListener);
			sportCountView.setOnStepsChangeListener(new OnStepsChangeListener() {

				@Override
				public void onChange(int[] steps) {

				}
			});

		} else if (id == 2) {
			reduceSleep.setOnClickListener(sleepOnClickListener);
			inreaseSleep.setOnClickListener(sleepOnClickListener);
			// countSleep.setOnSrollChange(new OnSrollChange() {
			//
			// @Override
			// public void onPrevious() {
			// Log.e("RecordFragment", "上一天");
			// textViewDateChange(tvDateSleep, -1);
			// countSleep.setHours(getRandomData());
			// }
			//
			// @Override
			// public void onNext() {
			// Log.e("RecordFragment", "下一天");
			// textViewDateChange(tvDateSleep, 1);
			// countSleep.setHours(getRandomData());
			// }
			// });
		}
	}

	/**
	 * 模拟数据 1-24小时数据
	 */
	private int[] getRandomData() {

		int hours[] = new int[72];
		for (int i = 0; i < hours.length; i++) {
			Random random = new Random();
			hours[i] = random.nextInt(200) + 1; // 随机获取[0-200]
		}
		Log.d("RecordFragment", "hours : " + hours);
		return hours;
	}

	/**
	 * 动画 闪烁
	 * 
	 * @param v
	 */
	private void startAnimation(View v) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0f, 1.0f);
		anim.setDuration(1000);
		anim.start();
	}

	/**
	 * 
	 * @param flag
	 */
	private void textViewDateChange(TextView tv, int flag) {
		String dateStr = tv.getText().toString();
		tv.setText(changeDate(dateStr, flag));
		startAnimation(tv);
	}

	/**
	 * 改变日期
	 * 
	 * @param d
	 * @param flag
	 *            1 增加一天 -1 减少一天
	 * @return
	 */
	private String changeDate(String d, int flag) {
		Date date;
		try {
			date = sdf.parse(d);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DAY_OF_MONTH, flag);
			String dateStr = sdf.format(c.getTime());
			return dateStr;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
}
