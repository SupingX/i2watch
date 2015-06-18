package com.suping.i2_watch.view;

import java.text.SimpleDateFormat;
import java.util.Date;



import com.suping.i2_watch.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 可下拉刷新的ScrollView
 * 
 * @author Administrator
 * 
 */
public class RefreshableScrollView extends LinearLayout implements
		OnTouchListener {

	/** 下拉状态 */
	public static final int STATUS_PULL_TO_REFRESH = 0;

	/** 释放立即刷新状态 */
	public static final int STATUS_RELEASE_TO_REFRESH = 1;

	/** 正在刷新状态 */
	public static final int STATUS_REFRESHING = 2;

	/** 刷新完成或未刷新状态 */
	public static final int STATUS_REFRESH_FINISHED = 3;

	/** 下拉头部回滚的速度 */
	public static final int SCROLL_SPEED = -20;

	/** 一分钟的毫秒值，用于判断上次的更新时间 */
	public static final long ONE_MINUTE = 60 * 1000;

	/** 一小时的毫秒值，用于判断上次的更新时间 */
	public static final long ONE_HOUR = 60 * ONE_MINUTE;

	/** 一天的毫秒值，用于判断上次的更新时间 */
	public static final long ONE_DAY = 24 * ONE_HOUR;

	/** 一月的毫秒值，用于判断上次的更新时间 */
	public static final long ONE_MONTH = 30 * ONE_DAY;

	/** 一年的毫秒值，用于判断上次的更新时间 */
	public static final long ONE_YEAR = 12 * ONE_MONTH;

	/** 上次更新时间的字符串常量，用于作为SharedPreferences的键值 */
	private static final String UPDATED_AT = "updated_time";

	/** 下拉刷新的回调接口 */
	private PullToRefreshListener mListener;

	/** 用于存储上次更新时间 */
	private SharedPreferences sharedPreferences;
	private SimpleDateFormat dataFormat;

	/** 下拉头的View */
	private View headerView;

	/** 下拉刷新的ScrollView */
	private ScrollView scrollView;

	/** 刷新时显示的进度条 */
	private ProgressBar progressBar;

	/** 指示下拉和释放的箭头 */
	private ImageView iv_arrow;

	/** 指示下拉和释放的文字描述 */
	private TextView tv_description;

	/** 上次更新时间的文字描述 */
	private TextView tv_updateAt;

	/** 下拉头的布局参数 */
	private MarginLayoutParams headerLayoutParams;

	// /** 上次更新时间的毫秒值 */
	// private long lastUpdateTime;

	/** 上次更新时间的时间串 */
	private String lastUpdateTime;

	/** 下拉头的高度 */
	private int hideHeaderHeight;

	/**
	 * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
	 * STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
	 */
	private int currentStatus = STATUS_REFRESH_FINISHED;;

	/** 记录上一次的状态是什么，避免进行重复操作 */
	private int lastStatus = currentStatus;

	/*** 手指按下时的屏幕纵坐标 */
	private float yDown;

	/** 在被判定为滚动之前用户手指可以移动的最大值。 */
	private int touchSlop;

	/** 是否已加载过一次layout，这里onLayout中的初始化只需加载一次 */
	private boolean loadOnce;

	/** 当前是否可以下拉，只有ScrollView滚动到头的时候才允许下拉 */
	private boolean isAbleToPull;

	public RefreshableScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public RefreshableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public RefreshableScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public void init(Context context) {
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		dataFormat = new SimpleDateFormat("MM/dd HH:mm");
		headerView = LayoutInflater.from(context).inflate(
				R.layout.layout_viewpager_pull_to_refresh_header, null, true);
		progressBar = (ProgressBar) headerView.findViewById(R.id.progress_bar);
		iv_arrow = (ImageView) headerView.findViewById(R.id.arrow);
		tv_description = (TextView) headerView.findViewById(R.id.description);
		tv_updateAt = (TextView) headerView.findViewById(R.id.updated_at);
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		refreshUpdatedTime();
		setOrientation(VERTICAL);
		addView(headerView, 0);
	}

	/**
	 * 进行一些关键性的初始化操作，比如：将下拉头向上偏移进行隐藏，给ScrollView注册touch事件。
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && !loadOnce) {
			hideHeaderHeight = -headerView.getHeight();
			headerLayoutParams = (MarginLayoutParams) headerView
					.getLayoutParams();
			headerLayoutParams.topMargin = hideHeaderHeight;
			scrollView = (ScrollView) getChildAt(1);
			scrollView.setOnTouchListener(this);
			// 先把下拉头隐藏
			headerView.setLayoutParams(headerLayoutParams);
			loadOnce = true;
		}
	}

	/**
	 * 当ScrollView被触摸时调用，其中处理了各种下拉刷新的具体逻辑。
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// setIsAbleToPull(event);
		// 获取的是LinearLayout
		View child1 = scrollView.getChildAt(0);
		// System.out.println("拉动了一下下top:" + scrollView.getScrollY());
		if (child1 != null) {
			if (scrollView.getScrollY() <= 0) {
				if (!isAbleToPull) {
					yDown = event.getRawY();
				}
				isAbleToPull = true;
			} else {
				if (headerLayoutParams.topMargin != hideHeaderHeight) {
					headerLayoutParams.topMargin = hideHeaderHeight;
					headerView.setLayoutParams(headerLayoutParams);
				}
				isAbleToPull = false;
			}
		} else {
			isAbleToPull = true;
		}
		if (isAbleToPull) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				yDown = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float yMove = event.getRawY();
				int distance = (int) (yMove - yDown);
				// 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
				if (distance <= 0
						&& headerLayoutParams.topMargin <= hideHeaderHeight) {
					return false;
				}
				if (distance < touchSlop) {
					return false;
				}
				if (currentStatus != STATUS_REFRESHING) {
					if (headerLayoutParams.topMargin > 0) {
						currentStatus = STATUS_RELEASE_TO_REFRESH;
					} else {
						currentStatus = STATUS_PULL_TO_REFRESH;
					}
					// 通过偏移下拉头的topMargin值，来实现下拉效果
					headerLayoutParams.topMargin = (distance / 2)
							+ hideHeaderHeight;
					headerView.setLayoutParams(headerLayoutParams);
				}
				break;
			case MotionEvent.ACTION_UP:
			default:
				if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
					// 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
					new RefreshingTask().execute();
				} else if (currentStatus == STATUS_PULL_TO_REFRESH) {
					// 松手时如果是下拉状态，就去调用隐藏下拉头的任务
					new HideHeaderTask().execute();
				}
				break;
			}
			// 时刻记得更新下拉头中的信息
			if (currentStatus == STATUS_PULL_TO_REFRESH
					|| currentStatus == STATUS_RELEASE_TO_REFRESH) {
				updateHeaderView();
				// 当前正处于下拉或释放状态，要让ScrollView失去焦点，否则被点击的那一项会一直处于选中状态
				scrollView.setPressed(false);
				scrollView.setFocusable(false);
				scrollView.setFocusableInTouchMode(false);
				lastStatus = currentStatus;
				// 当前正处于下拉或释放状态，通过返回true屏蔽掉ScrollView的滚动事件
				return true;
			}
		}
		return false;
	}

	/**
	 * 给下拉刷新控件注册一个监听器。
	 * 
	 * @param listener
	 *            监听器的实现。
	 * @param id
	 *            为了防止不同界面的下拉刷新在上次更新时间上互相有冲突， 请不同界面在注册下拉刷新监听器时一定要传入不同的id。
	 */
	public void setOnRefreshListener(PullToRefreshListener listener) {
		mListener = listener;
	}

	/**
	 * 当所有的刷新逻辑完成后，记录调用一下，否则你的ScrollView将一直处于正在刷新状态。
	 */
	public void finishRefreshing() {
		currentStatus = STATUS_REFRESH_FINISHED;
		// sharedPreferences.edit()
		// .putLong(UPDATED_AT, System.currentTimeMillis()).commit();
		sharedPreferences.edit()
				.putString(UPDATED_AT, dataFormat.format(new Date())).commit();
		new HideHeaderTask().execute();
	}

	/**
	 * 更新下拉头中的信息。
	 */
	private void updateHeaderView() {
		if (lastStatus != currentStatus) {
			if (currentStatus == STATUS_PULL_TO_REFRESH) {
				tv_description.setText(getResources().getString(
						R.string.pull_to_refresh));
				iv_arrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				rotateArrow();
			} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
				tv_description.setText("释放刷新");
				iv_arrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				rotateArrow();
			} else if (currentStatus == STATUS_REFRESHING) {
				tv_description.setText("正在刷新");
				progressBar.setVisibility(View.VISIBLE);
				iv_arrow.clearAnimation();
				iv_arrow.setVisibility(View.GONE);
			}
			// refreshUpdatedAtValue();
			refreshUpdatedTime();
		}
	}

	/**
	 * 根据当前的状态来旋转箭头。
	 */
	private void rotateArrow() {
		float pivotX = iv_arrow.getWidth() / 2f;
		float pivotY = iv_arrow.getHeight() / 2f;
		float fromDegrees = 0f;
		float toDegrees = 0f;
		if (currentStatus == STATUS_PULL_TO_REFRESH) {
			fromDegrees = 180f;
			toDegrees = 360f;
		} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
			fromDegrees = 0f;
			toDegrees = 180f;
		}
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
				pivotX, pivotY);
		animation.setDuration(100);
		animation.setFillAfter(true);
		iv_arrow.startAnimation(animation);
	}

	/**
	 * 刷新下拉头中上次更新时间的文字描述。
	 */
	private void refreshUpdatedTime() {
		lastUpdateTime = sharedPreferences.getString(UPDATED_AT, "");

		String updateAtValue;
		if (lastUpdateTime.equals("")) {
			updateAtValue = "无更新";
		} else {
			updateAtValue = getResources().getString(R.string.updated_at,
					lastUpdateTime);
		}
		tv_updateAt.setText(updateAtValue);
	}

	/**
	 * 刷新下拉头中上次更新时间的文字描述。
	 */
	// private void refreshUpdatedAtValue() {
	// lastUpdateTime = sharedPreferences.getLong(UPDATED_AT, -1);
	// long currentTime = System.currentTimeMillis();
	// long timePassed = currentTime - lastUpdateTime;
	// long timeIntoFormat;
	// String updateAtValue;
	// if (lastUpdateTime == -1) {
	// updateAtValue = getResources().getString(R.string.not_updated_yet);
	// } else if (timePassed < 0) {
	// updateAtValue = getResources().getString(R.string.time_error);
	// } else if (timePassed < ONE_MINUTE) {
	// updateAtValue = getResources().getString(R.string.updated_just_now);
	// } else if (timePassed < ONE_HOUR) {
	// timeIntoFormat = timePassed / ONE_MINUTE;
	// String value = timeIntoFormat + "分钟";
	// updateAtValue = String.format(
	// getResources().getString(R.string.updated_at), value);
	// } else if (timePassed < ONE_DAY) {
	// timeIntoFormat = timePassed / ONE_HOUR;
	// String value = timeIntoFormat + "小时";
	// updateAtValue = String.format(
	// getResources().getString(R.string.updated_at), value);
	// } else if (timePassed < ONE_MONTH) {
	// timeIntoFormat = timePassed / ONE_DAY;
	// String value = timeIntoFormat + "天";
	// updateAtValue = String.format(
	// getResources().getString(R.string.updated_at), value);
	// } else if (timePassed < ONE_YEAR) {
	// timeIntoFormat = timePassed / ONE_MONTH;
	// String value = timeIntoFormat + "个月";
	// updateAtValue = String.format(
	// getResources().getString(R.string.updated_at), value);
	// } else {
	// timeIntoFormat = timePassed / ONE_YEAR;
	// String value = timeIntoFormat + "年";
	// updateAtValue = String.format(
	// getResources().getString(R.string.updated_at), value);
	// }
	// tv_updateAt.setText(updateAtValue);
	// }

	/**
	 * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
	 * 
	 * @author Administrator
	 */
	class RefreshingTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			int topMargin = headerLayoutParams.topMargin;
			while (true) {
				topMargin = topMargin + SCROLL_SPEED;
				if (topMargin <= 0) {
					topMargin = 0;
					break;
				}
				publishProgress(topMargin);
				sleep(10);
			}
			currentStatus = STATUS_REFRESHING;
			publishProgress(0);
			if (mListener != null) {
				mListener.onRefresh();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... topMargin) {
			updateHeaderView();
			headerLayoutParams.topMargin = topMargin[0];
			headerView.setLayoutParams(headerLayoutParams);
		}

	}

	/**
	 * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
	 * 
	 * @author Administrator
	 */
	class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			int topMargin = headerLayoutParams.topMargin;
			while (true) {
				topMargin = topMargin + SCROLL_SPEED;
				if (topMargin <= hideHeaderHeight) {
					topMargin = hideHeaderHeight;
					break;
				}
				publishProgress(topMargin);
				sleep(10);
			}
			return topMargin;
		}

		@Override
		protected void onProgressUpdate(Integer... topMargin) {
			headerLayoutParams.topMargin = topMargin[0];
			headerView.setLayoutParams(headerLayoutParams);
		}

		@Override
		protected void onPostExecute(Integer topMargin) {
			headerLayoutParams.topMargin = topMargin;
			headerView.setLayoutParams(headerLayoutParams);
			currentStatus = STATUS_REFRESH_FINISHED;
		}
	}

	/**
	 * 使当前线程睡眠指定的毫秒数。
	 * 
	 * @param time
	 *            指定当前线程睡眠多久，以毫秒为单位
	 */
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
	 * 
	 * @author Administrator
	 * 
	 */
	public interface PullToRefreshListener {

		/**
		 * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑
		 */
		void onRefresh();

	}

}
