package com.suping.i2_watch.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class RefreshForScrolView extends LinearLayout implements OnTouchListener {
	private static final String SHARE_UPDATE_TIME = "share_update_time";
	/*** 下拉状态 ***/
	public static final int STATUS_PULL_TO_REFRESH = 0;
	/*** 释放立即刷新状态 ***/
	public static final int STATUS_RELEASE_TO_REFRESH = 1;
	/*** 正在刷新状态 ***/
	public static final int STATUS_REFRESHING = 2;
	/*** 刷新完成或未刷新状态 ***/
	public static final int STATUS_REFRESH_FINISHED = 3;
	/*** 当前默认状态为已刷新 ***/
	private int currentStatus = STATUS_REFRESH_FINISHED;
	/*** 上次状态 ***/
	private int lastStatus = currentStatus;

	/*** 下拉头的布局参数 */
	private MarginLayoutParams headerLayoutParams;
	private View header;
	private TextView tv_state;
	private TextView tv_time;
	private ProgressBar mBar;
	private Context mContext;
	private ImageView img_up;
//	private ViewPager mScrollView;
	private View mScrollView;

	private int headerHeight;
	/*** 下拉的y的值 ***/
	private float yDown;
	/*** 触发移动事件的最短距离 ****/
	private int touchSlop;

	private PullToRefreshListener mListener;
	private int mId;

	/*** 是否已加载过一次layout，这里onLayout中的初始化只需加载一次 ***/
	private boolean loadOnce;
	private boolean ableToPull;
	
	     
	public RefreshForScrolView(Context context, AttributeSet attrs) {
		   super(context, attrs);
		header = LayoutInflater.from(context).inflate(R.layout.header, null,
				true);
		mBar = (ProgressBar) header.findViewById(R.id.bar);
		mBar.setVisibility(View.GONE);
		tv_state = (TextView) header.findViewById(R.id.tv_state);
		tv_time = (TextView) header.findViewById(R.id.tv_time);
		img_up = (ImageView) header.findViewById(R.id.img_up);
		mContext = context;
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		refreshUpdatedAtValue();
		setOrientation(VERTICAL);
		addView(header, 0);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);

		if (changed && !loadOnce) {
			headerHeight = -header.getHeight();
			headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
			headerLayoutParams.topMargin = headerHeight;
//			mScrollView = (ViewPager) getChildAt(1);
			mScrollView = getChildAt(1);
			mScrollView.setOnTouchListener(this);
			
			
			loadOnce = true;
		}
	}

	/**
	 * 根据当前ListView的滚动状态来设定 {@link #ableToPull}
	 * 的值，每次都需要在onTouch中第一个执行，这样可以判断出当前应该是滚动ListView，还是应该进行下拉。
	 * 
	 * @param event
	 */
	private void setIsAbleToPull(MotionEvent event) {
		if(mScrollView!=null){
			if(mScrollView.getTop()==0){
				if (!ableToPull) {
					yDown = event.getRawY();//相对屏幕的Y坐标
				}
				ableToPull = true;
			}
//			else {
//				if (headerLayoutParams.topMargin != headerHeight) {
//					headerLayoutParams.topMargin = headerHeight;
//					header.setLayoutParams(headerLayoutParams);
//				}
//				ableToPull = false;
//			}
		} else {
			ableToPull = false;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		setIsAbleToPull(event);
		if (ableToPull) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				yDown = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float yMove = event.getRawY();
				int distance = (int) (yMove - yDown);
				// 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
				if (distance <= 0
						&& headerLayoutParams.topMargin <= headerHeight) {
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
							+ headerHeight;
					header.setLayoutParams(headerLayoutParams);
				}
				break;
			case MotionEvent.ACTION_UP:
			default:
				Log.e("下拉刷新", "松开 currentStatus : " + currentStatus);
				if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
					Log.e("下拉刷新", "status_release_to_refresh");
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
				// 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
				mScrollView.setPressed(false);
				mScrollView.setFocusable(false);
				mScrollView.setFocusableInTouchMode(false);
				lastStatus = currentStatus;
				// 当前正处于下拉或释放状态，通过返回true屏蔽掉ListView的滚动事件
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
	public void setOnRefreshListener(PullToRefreshListener listener, int id) {
		mListener = listener;
		mId = id;
	}

	/**
	 * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
	 */
	public void finishRefreshing() {
		currentStatus = STATUS_REFRESH_FINISHED;
		// preferences.edit().putLong(UPDATED_AT + mId,
		// System.currentTimeMillis()).commit();
		
		//yyyy-MM-dd hh:mm:ss -->为12小时制 
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Log.e("刷新时间", "time : " + time);
		SharedPreferenceUtil.put(mContext, SHARE_UPDATE_TIME + mId,time);
		
		new HideHeaderTask().execute();
	}

	/**
	 * 更新下拉头中的信息。
	 */
	private void updateHeaderView() {
		if (lastStatus != currentStatus) {
			if (currentStatus == STATUS_PULL_TO_REFRESH) {
				tv_state.setText("下拉刷新数据");
				img_up.setVisibility(View.VISIBLE);
				mBar.setVisibility(View.GONE);
				rotateArrow();
			} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
				tv_state.setText(("松开刷新数据"));
				rotateArrow();
			} else if (currentStatus == STATUS_REFRESHING) {
				mBar.setVisibility(View.VISIBLE);
				img_up.clearAnimation();
				img_up.setVisibility(View.GONE);
				tv_state.setText("正在刷新");
			
			}
			 refreshUpdatedAtValue();
		}
	}

	/**
	 * 根据当前的状态来旋转箭头。
	 */
	private void rotateArrow() {
		float pivotX = img_up.getWidth() / 2f;
		float pivotY = img_up.getHeight() / 2f;
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
		img_up.startAnimation(animation);
	}

	/**
	 * 刷新下拉头中上次更新时间的文字描述。
	 */
	
	private void refreshUpdatedAtValue(){
		String time = (String) SharedPreferenceUtil.get(mContext, SHARE_UPDATE_TIME +mId, "");
		if(time.equals("")){
			tv_time.setText("未曾更新");
		} else {
			tv_time.setText("上次更新于 : " + time);
		}
	}
//	private void refreshUpdatedAtValue() {
		// lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
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
		// updateAtValue =
		// String.format(getResources().getString(R.string.updated_at), value);
		// } else if (timePassed < ONE_DAY) {
		// timeIntoFormat = timePassed / ONE_HOUR;
		// String value = timeIntoFormat + "小时";
		// updateAtValue =
		// String.format(getResources().getString(R.string.updated_at), value);
		// } else if (timePassed < ONE_MONTH) {
		// timeIntoFormat = timePassed / ONE_DAY;
		// String value = timeIntoFormat + "天";
		// updateAtValue =
		// String.format(getResources().getString(R.string.updated_at), value);
		// } else if (timePassed < ONE_YEAR) {
		// timeIntoFormat = timePassed / ONE_MONTH;
		// String value = timeIntoFormat + "个月";
		// updateAtValue =
		// String.format(getResources().getString(R.string.updated_at), value);
		// } else {
		// timeIntoFormat = timePassed / ONE_YEAR;
		// String value = timeIntoFormat + "年";
		// updateAtValue =
		// String.format(getResources().getString(R.string.updated_at), value);
		// }
		// updateAt.setText(updateAtValue);
//	}

	/**
	 * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
	 * 
	 * @author guolin
	 */
	class RefreshingTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			int topMargin = headerLayoutParams.topMargin;
			while (true) {
				topMargin = topMargin + (-20);
				if (topMargin <= 0) {
					topMargin = 0;
					break;
				}
				publishProgress(topMargin);
				sleep(10);
			}	
			Log.e("刷新task", currentStatus+" 前");
			currentStatus = STATUS_REFRESHING;
			Log.e("刷新task", currentStatus+" 后");
			publishProgress(0);
			if (mListener != null) {
				mListener.onRefresh();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... topMargin) {
			headerLayoutParams.topMargin = topMargin[0];
			header.setLayoutParams(headerLayoutParams);
			updateHeaderView();
		}

	}

	/**
	 * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
	 * 
	 * @author guolin
	 */
	class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			int topMargin = headerLayoutParams.topMargin;
			while (true) {
				topMargin = topMargin + (-2);
				if (topMargin <= headerHeight) {
					topMargin = headerHeight;
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
			header.setLayoutParams(headerLayoutParams);
		}

		@Override
		protected void onPostExecute(Integer topMargin) {
			headerLayoutParams.topMargin = topMargin;
			header.setLayoutParams(headerLayoutParams);
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
	 * @author guolin
	 */
	public interface PullToRefreshListener {
		/**
		 * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的， 你可以不必另开线程来进行耗时操作。
		 */
		void onRefresh();
	}


}
