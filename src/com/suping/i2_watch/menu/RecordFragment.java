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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.suping.i2_watch.R;
import com.suping.i2_watch.entity.HistorySleep;
import com.suping.i2_watch.entity.HistorySport;
import com.suping.i2_watch.util.DataUtil;
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

	/**
	 * 运动视图点击事件
	 */
	private OnClickListener sportOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stepTotal = 0;
			timeTotal = 0;
			switch (v.getId()) {
			case R.id.reduce:
				Log.e("RecordFragment", "减少");
				List<HistorySport> sports = DataSupport.where("year=? and month =? and day = ? and hour = ? ", "2015", "05", "19", "0").find(HistorySport.class);
				L.e(sports + "");
				textViewDateChange(tvDate, -1);
				String dateStr1 = tvDate.getText().toString();
				// sportCountView.setSteps(getRandomData());//模拟数据
				int[] dataForSport1 = getDataForSport(dateStr1);
				if (dataForSport1 != null) {
					sportCountView.setSteps(dataForSport1);
				}

				break;
			case R.id.increase:
				Log.e("RecordFragment", "增加");
				textViewDateChange(tvDate, 1);
				// sportCountView.setSteps(getRandomData());//模拟数据
				String dateStr2 = tvDate.getText().toString();
				int[] dataForSport2 = getDataForSport(dateStr2);
				if (dataForSport2 != null) {
					sportCountView.setSteps(dataForSport2);
				}
				break;
			default:
				break;
			}

			updateSportInfo(stepTotal, timeTotal);
		}
	};

	private int stepTotal;
	private int timeTotal;

	/**
	 * 睡眠视图点击事件
	 */
	private OnClickListener sleepOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			deepTotal = 0;
			lightTotal = 0;
			awakTotal = 0;
			switch (v.getId()) {
			case R.id.reduce_sleep:
				Log.e("RecordFragment", "增加sleep");
				textViewDateChange(tvDateSleep, -1);
				String dateStr1 = tvDateSleep.getText().toString();
				int[] dataForSleep1 = getDataForSleep(dateStr1);
				if (dataForSleep1 != null) {
					sleepCountView.setSleeps(dataForSleep1);
				}
				break;
			case R.id.increase_sleep:
				Log.e("RecordFragment", "增加sleep");
				textViewDateChange(tvDateSleep, 1);
				String dateStr2 = tvDateSleep.getText().toString();
				getDataForSleep(dateStr2);
				int[] dataForSleep2 = getDataForSleep(dateStr2);
				if (dataForSleep2 != null) {
					sleepCountView.setSleeps(dataForSleep2);
				}
				break;
			default:
				break;
			}

			updateSleepInfo(deepTotal, lightTotal, awakTotal);
		}
	};
	private TextView tvSportCal;
	private TextView tvSportTime;
	private TextView tvSportDistance;
	private int deepTotal;
	private int lightTotal;
	private int awakTotal;
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

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 当是睡眠画面时
		if (flag == R.layout.fragment_record_sleep) {
			reduceSleep = (ImageView) getActivity().findViewById(R.id.reduce_sleep);
			inreaseSleep = (ImageView) getActivity().findViewById(R.id.increase_sleep);
			tvDateSleep = (TextView) getActivity().findViewById(R.id.date_sleep);
			tvDateSleep.setText(sdf.format(new Date()));
			tvSleepDeep = (TextView) getActivity().findViewById(R.id.tv_count_sleep_deep_value);
			tvSleepLight = (TextView) getActivity().findViewById(R.id.tv_count_sleep_light_value);
			tvSleepAwak = (TextView) getActivity().findViewById(R.id.tv_count_sleep_awak_value);
		
			sleepCountView = (SleepCountView) getActivity().findViewById(R.id.count_sleep);
			// getActivity().registerForContextMenu(textViewSyncSleep);
			// //为按钮注册上下文菜单
			setLinstener(2);
			int[] dataForSleep = getDataForSleep(tvDateSleep.getText().toString().trim());
			if (dataForSleep != null) {
				sleepCountView.setSleeps(dataForSleep);
			}
			updateSleepInfo(deepTotal, lightTotal, awakTotal);
			// 当是运动画面时
		} else if (flag == R.layout.fragment_record_sport) {
			reduce = (ImageView) getActivity().findViewById(R.id.reduce);
			inrease = (ImageView) getActivity().findViewById(R.id.increase);
			tvDate = (TextView) getActivity().findViewById(R.id.date);
			tvDate.setText(sdf.format(new Date()));
			sportCountView = (SportCountView) getActivity().findViewById(R.id.count_sport);
			tvSportCal = (TextView) getActivity().findViewById(R.id.tv_count_sport_cal_value);
			tvSportTime = (TextView) getActivity().findViewById(R.id.tv_count_sport_time_value);
			tvSportDistance = (TextView) getActivity().findViewById(R.id.tv_count_sport_distance_value);
			int[] dataForSport = getDataForSport(tvDate.getText().toString().trim());
			if (dataForSport != null) {
				sportCountView.setSteps(dataForSport);
			}
			updateSportInfo(stepTotal, timeTotal);
			setLinstener(1);
		}
		super.onActivityCreated(savedInstanceState);
	}

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

	private int[] getDataForSport(String date) {
		String[] split = date.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		int steps[] = new int[72];
		for (int i = 0; i < 24; i++) {
			String hour = String.valueOf(i).trim();
			List<HistorySport> sports = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(HistorySport.class);
			if (sports != null && sports.size() > 0) {
				HistorySport sport = sports.get(0);
				steps[3 * i] = sport.getSportStep_1();
				steps[3 * i + 1] = sport.getSportStep_2();
				steps[3 * i + 2] = sport.getSportStep_3();

				stepTotal += (steps[3 * i] + steps[3 * i + 1] + steps[3 * i + 2]);
				timeTotal += (sport.getSportTime_1() + sport.getSportTime_2() + sport.getSportTime_3());
			} else {
				L.e("数据库无运动数据");
			}
		}
		return steps;
	}

	private int[] getDataForSleep(String date) {
		String[] split = date.split("-");
		String year = split[0];
		String month = split[1];
		String day = split[2];
		L.i(year);
		L.i(month);
		L.i(day);
		int sleeps[] = new int[72];
		for (int i = 0; i < 24; i++) {
			String hour = String.valueOf(i).trim();
			List<HistorySleep> sleepList = DataSupport.where("year=? and month =? and day = ? and hour = ? ", year, month, day, hour).find(HistorySleep.class);
			if (sleepList != null && sleepList.size() > 0) {
				// 获得每个小时的数据
				HistorySleep sleep = sleepList.get(0);
				// 0-20分钟
				int sleepDeep_1 = sleep.getSleepDeep_1();
				int sleepLight_1 = sleep.getSleepLight_1();
				int sleepAwak_1 = sleep.getSleepAwak_1();
				sleeps[3 * i] = getValue(sleepDeep_1, sleepLight_1, sleepAwak_1);
				// 20-40分钟
				int sleepDeep_2 = sleep.getSleepDeep_2();
				int sleepLight_2 = sleep.getSleepLight_2();
				int sleepAwak_2 = sleep.getSleepAwak_2();
				sleeps[3 * i + 1] = getValue(sleepDeep_2, sleepLight_2, sleepAwak_2);
				// 40-60分钟
				int sleepDeep_3 = sleep.getSleepDeep_3();
				int sleepLight_3 = sleep.getSleepLight_3();
				int sleepAwak_3 = sleep.getSleepAwak_3();
				sleeps[3 * i + 2] = getValue(sleepDeep_3, sleepLight_3, sleepAwak_3);
				// 统计数据
				deepTotal += (sleepDeep_1 + sleepDeep_2 + sleepDeep_3);
				lightTotal += (sleepLight_1 + sleepLight_2 + sleepLight_3);
				awakTotal += (sleepAwak_1 + sleepAwak_2 + sleepAwak_3);
			} else {
				L.e("数据库无睡眠数据");
			}
		}
		return sleeps;
	}

	/**
	 * 0--深睡 1--浅睡 2--清醒 默认0
	 */
	private int getValue(int deep, int light, int awak) {
		// 比较3个点确定显示的值
		int value = 0;
		if (deep >= light && deep >= awak) {// 深睡
			value = 0;
		} else if (light > deep && light >= awak) {// 浅睡
			value = 1;
		} else if (awak > light && awak > deep) {// 清醒
			value = 2;
		}
		return value;
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
