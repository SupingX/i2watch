package com.suping.i2_watch.menu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.litepal.crud.DataSupport;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.suping.i2_watch.R;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.view.CountView;
import com.suping.i2_watch.view.CountView.OnDataChange;
import com.suping.i2_watch.view.CountView.OnSrollChange;

public class RecordFragment extends Fragment {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private int flag;
	private TextView tvCountSportInfo;

	/** 下一天/上一天 **/
	private ImageView reduce, inrease, reduceSleep, inreaseSleep;
	/** 日期 **/
	private TextView tvDate, tvDateSleep;
	/** 统计图 **/
	private CountView countSport, countSleep;
	/** 同步选择对话框 **/
	private AlertDialog dialogSync;
	
	
	/**
	 * 运动视图点击事件
	 */
	private OnClickListener sportOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.reduce:
				// TODO Auto-generated method stub
				Log.e("RecordFragment", "减少");
				textViewDateChange(tvDate, -1);
				countSport.setHours(new int[] { 22, 33, 44, 66, 111, 442, 515, 412, 666, 11, 22, 44, 55, 22, 13, 56,
						78, 89, 54, 65, 34, 55, 77, 22 });
				break;
			case R.id.increase:
				// TODO Auto-generated method stub
				Log.e("RecordFragment", "增加");
				textViewDateChange(tvDate, 1);
				break;
//			case R.id.tv_sync_sport:
//				showDialog();
//				logHistoryThread.start();
//				break;
			default:
				break;
			}
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
				// TODO Auto-generated method stub
				Log.e("RecordFragment", "增加sleep");
				textViewDateChange(tvDateSleep, -1);
				// DataSupport.deleteAll(History.class);
				add();
				break;
			case R.id.increase_sleep:
				// TODO Auto-generated method stub
				Log.e("RecordFragment", "增加sleep");
				// String dateStr = tvDateSleep.getText().toString();
				// tvDateSleep.setText(changeDate(dateStr, 1));
				// startAnimation(tvDateSleep);
				textViewDateChange(tvDateSleep, 1);
				getDataFromDb("150528", 0);
				break;
//			case R.id.tv_sync_sleep:
//				showDialog();
//				break;
			default:
				break;
			}
		}
	};
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
			countSleep = (CountView) getActivity().findViewById(R.id.count_sleep);
//			textViewSyncSleep = (TextView) getActivity().findViewById(R.id.tv_sync_sleep);
			// getActivity().registerForContextMenu(textViewSyncSleep);
			// //为按钮注册上下文菜单
			setLinstener(2);

			// 当是运动画面时
		} else if (flag == R.layout.fragment_record_sport) {
			reduce = (ImageView) getActivity().findViewById(R.id.reduce);
			inrease = (ImageView) getActivity().findViewById(R.id.increase);
			tvDate = (TextView) getActivity().findViewById(R.id.date);
			tvCountSportInfo = (TextView) getActivity().findViewById(R.id.count_sport_info);
			tvDate.setText(sdf.format(new Date()));
			countSport = (CountView) getActivity().findViewById(R.id.count_sport);
//			textViewSyncSport = (TextView) getActivity().findViewById(R.id.tv_sync_sport);
			setLinstener(1);
		}
		super.onActivityCreated(savedInstanceState);

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
			countSport.setOnDataChange(new OnDataChange() {
				@Override
				public void onChange() {
//					tvCountSportInfo.setText("数据改变了");
				}
			});
			countSport.setOnSrollChange(new OnSrollChange() {
				@Override
				public void onNext() {
					textViewDateChange(tvDate, 1);
					tvCountSportInfo.setText("下一天数据");
					countSport.setHours(getRandomData());
				}

				@Override
				public void onPrevious() {
					textViewDateChange(tvDate, -1);
					tvCountSportInfo.setText("前一天数据");
					countSport.setHours(getRandomData());
				}
			});

		} else if (id == 2) {
			reduceSleep.setOnClickListener(sleepOnClickListener);
			inreaseSleep.setOnClickListener(sleepOnClickListener);
			countSleep.setOnSrollChange(new OnSrollChange() {

				@Override
				public void onPrevious() {
					Log.e("RecordFragment", "上一天");
					textViewDateChange(tvDateSleep, -1);
					countSleep.setHours(getRandomData());
				}

				@Override
				public void onNext() {
					Log.e("RecordFragment", "下一天");
					textViewDateChange(tvDateSleep, 1);
					countSleep.setHours(getRandomData());
				}
			});
		}
	}

	/**
	 * 从数据库获取1-60分钟数据
	 * 
	 * @param date
	 *            日期
	 * @param type
	 *            运动0/睡眠1
	 * @return
	 */
	private int getDataFromDb(String date, int type) {
//		String conditions[] = new String[] { "timeIndex>=1 and timeIndex<60 and historyDate = ? and type= ?", date,
//				String.valueOf(type) };
//		int i0 = DataSupport.where(conditions).sum(History.class, "historyValue", int.class);
//		Log.e("", "i0 : " + i0);
		return 0;
	}

	/**
	 * 插入数据
	 */
	private void add() {
//		// 测试历史数据
//		// 模拟手环收据
//		int[] date = new int[] { 0x00017001, 0x00027002, 0x00037003, 0x00047004, 0x00057005, 0x00067006, 0x00077007,
//				0x00087008, 0x00097009, 0x00107010, 0x00117011, 0x00127012, 0xFF007012 };
//
//		List<History> list = new ArrayList<>();
//		for (int i = 0; i < date.length; i++) {
//			History h = new History(date[i], 3);
//			list.add(h);
//			// //存数据
//			// if (h.save()) {
//			// Toast.makeText(getActivity(), "存贮成功！！",
//			// Toast.LENGTH_LONG).show();
//			// } else {
//			// Toast.makeText(getActivity(), "存贮失败！！",
//			// Toast.LENGTH_LONG).show();
//			// }
//		}
//		DataSupport.saveAll(list);
	}

	/**
	 * 模拟数据 1-24小时数据
	 */
	private int[] getRandomData() {

		int hours[] = new int[24];
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
	
	private Thread logHistoryThread = new Thread(new Runnable() {
		@Override
		public void run() {
			logHistory();
		}
	});
	/**
	 * 打印所有的History
	 */
	private void logHistory() {
//		List<History> histories = DataSupport.findAll(History.class);
//		if (histories == null) {
//			return;
//		}
//		if (histories.size() == 0) {
//			return;
//		}
//		Log.v("RF", "List<History> -- size : " + histories.size());
//		for (History h : histories) {
//			Log.v("RF", "history -- id : " + h.getId());
//			Log.v("RF", "history -- type : " + h.getType());
//			Log.v("RF", "history -- index : " + h.getTimeIndex());
//			Log.v("RF", "history -- date : " + h.getHistoryDate());
//			Log.v("RF", "history -- value : " + h.getHistoryValue());
//			Log.v("RF", "----------------------------------------");
//		}
	}
}
