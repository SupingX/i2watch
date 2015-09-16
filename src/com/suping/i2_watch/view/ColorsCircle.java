package com.suping.i2_watch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.suping.i2_watch.R;

public class ColorsCircle extends View {
	/** 彩环渐变颜色 **/
	private final int[] COLORS = new int[] { 0xFF22AC38, 0xFF009944, 0xFF009B6B, 0xFF009E96, 0xFF00A0C1, 0xFF00A0E9,
			0xFF0086D1, 0xFF0068B7, 0xFF00479D, 0xFF1D2088, 0xFF601986, 0xFF920783, 0xFFBE0081, 0xFFE4007F, 0xFFE5006A,
			0xFFE5004F, 0xFFE60033, 0xFFE60012, 0xFFEB6100, 0xFFF39800, 0xFFFCC800, 0xFFFFF100, 0xFFCFDB00, 0xFF8FC31F,
			0xFF22AC38 };
	/**
	 * 大环背景画笔
	 */
	private Paint mBackgroundPaint = null;
	/**
	 * 大环画笔
	 */
	private Paint mProgressPaint = null;
	/**
	 * 小环的画笔
	 */
	private Paint mSmallPaint = null;
	/**
	 * 大环的bounds
	 */
	private RectF mArcRectF = null;
	/**
	 * 大环的渐变色
	 */
	private Shader mShader;
	/**
	 * 最大进度
	 */
	private int mProgressMax = 100;
	/**
	 * 当前进度
	 */
	private int mProgress = 0;

	private int mViewHeight;
	private int mViewWidth;
	/**
	 * 大环的半径
	 */
	private int mRadius = 0;
	/**
	 * 小环的半径
	 */
	private int mSmallRadius = 0;
	/**
	 * 圆心坐标X
	 */
	private int mCenterX;
	/**
	 * 圆心坐标Y
	 */
	private int mCenterY;
	/**
	 * 大环的颜色 当没有颜色时 为彩环 
	 */
	private int mProgressColor;
	
	/**
	 * 进度改变时的监听
	 */
	private OnProgressChangeListener mOnProgressChangeListener = null;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
//		Log.e("onLayout", getWidth() + "," + getHeight());
	}

	@Override
	protected void onFinishInflate() {
//		Log.e("onFinishInflate", getWidth() + "," + getHeight());
		super.onFinishInflate();
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.e("onMeasure", getWidth() + "," + getHeight());
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		Log.e("onDraw", getWidth() + "," + getHeight());
		mCenterX = getWidth() / 2;
		mCenterY = getHeight() / 2;
		int left = mCenterX - mRadius;
		int right = mCenterX + mRadius;
		int top = mCenterY - mRadius;
		int bottom = mCenterY + mRadius;
		mArcRectF.set(left, top, right, bottom);
		// 彩环背景圈
		canvas.drawCircle(mCenterX, mCenterY, mRadius, mBackgroundPaint);

		// 小圈
		canvas.drawCircle(mCenterX, mCenterY, mSmallRadius, mSmallPaint);

		// 大环
		if (mProgressColor != 0) {
			mProgressPaint.setColor(mProgressColor);
		} else {
			mShader = new SweepGradient(mCenterX, mCenterY, COLORS, null);
			mProgressPaint.setShader(mShader);
		}
		canvas.drawArc(this.mArcRectF, -90f, toAngle(mProgress), false, mProgressPaint);
		super.onDraw(canvas);
	}
	
	
	/**
	 * 初始化一些参数
	 * @param attrs
	 * @param mContext
	 */
	private void initViewAttrs(AttributeSet attrs,Context mContext) {
	
		 // 读取XML属性
		
		TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ColorsCircle);
		int backgroundColor = mTypedArray.getColor(R.styleable.ColorsCircle_background_color, 0xdddddddd);
		int progress_color = mTypedArray.getColor(R.styleable.ColorsCircle_progress_color, 0);
		int small_backgroundColor = mTypedArray.getColor(R.styleable.ColorsCircle_small_background_color, 0xdddddddd);
		float radius = mTypedArray.getDimension(R.styleable.ColorsCircle_radius, (250f / 2));
		float small_radius = mTypedArray.getDimension(R.styleable.ColorsCircle_small_radius, 0);
		float stroke = mTypedArray.getDimension(R.styleable.ColorsCircle_stroke, 10f);
		float small_stroke = mTypedArray.getDimension(R.styleable.ColorsCircle_small_stroke, 4f);
		mViewWidth = mTypedArray.getDimensionPixelOffset(R.styleable.ColorsCircle_width, 0);
		mViewHeight = mTypedArray.getDimensionPixelOffset(R.styleable.ColorsCircle_height, 0);
		int max = mTypedArray.getInt(R.styleable.ColorsCircle_max, 100);
		int progress = mTypedArray.getInt(R.styleable.ColorsCircle_progress, 0);
		mTypedArray.recycle();
		
		
		 // 初始化画笔
		
		mProgressPaint = new Paint();
		mBackgroundPaint = new Paint();
		mSmallPaint = new Paint();
	
		mProgressPaint.setAntiAlias(true);
		mProgressPaint.setStyle(Paint.Style.STROKE);
		mProgressPaint.setStrokeWidth(stroke);
	
		mBackgroundPaint.setAntiAlias(true);
		mBackgroundPaint.setColor(backgroundColor);
		mBackgroundPaint.setStyle(Paint.Style.STROKE);
		mBackgroundPaint.setStrokeWidth(stroke);
	
		mSmallPaint.setAntiAlias(true);
		mSmallPaint.setColor(small_backgroundColor);
		mSmallPaint.setStyle(Paint.Style.STROKE);
		mSmallPaint.setStrokeWidth(small_stroke);
		
		mRadius = (int) radius;
		mSmallRadius = (int) small_radius;
		mProgress = progress;
		mProgressMax = max;
		mProgressColor = progress_color;
		
		
		 //传递当前进度
		
		if (null != mOnProgressChangeListener) {
			mOnProgressChangeListener.onProgressChanged(mProgress);
		}
	}

	/**
	 * 根据进度得到角度
	 * @param progress
	 * @return
	 */
	private float toAngle(int progress) {
		float angle = 0;
		angle = 360 * progress / mProgressMax;
		return angle;
	}

	public void setOnProgressChangeListener(OnProgressChangeListener mOnProgressChangeListener) {
		this.mOnProgressChangeListener = mOnProgressChangeListener;
	}

	/**
	 * 构造方法 一
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ColorsCircle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViewAttrs(attrs,context);
		mArcRectF = new RectF();
	}

	/**
	 * 构造方法 二
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ColorsCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViewAttrs(attrs,context);
		mArcRectF = new RectF();
	}

	public int getProgressMax() {
		return mProgressMax;
	}

	public void setmProgressMax(int mProgressMax) {
		this.mProgressMax = mProgressMax;
		invalidate();
	}

	public synchronized int getmProgress() {
		return mProgress;
	}

	public synchronized void setmProgress(int mProgress) {
		if (mProgress < 0) {
			this.mProgress = 0;
		}
		if (mProgress > mProgressMax) {
			mProgress = mProgressMax;
		}
		if (mProgress <= mProgressMax) {
			this.mProgress = mProgress;
		}
		this.mProgress = mProgress;
		if (null != mOnProgressChangeListener) {
			mOnProgressChangeListener.onProgressChanged(mProgress);
		}
		invalidate();
	}

	/**
	 * 进度改变的接口
	 * @author Administrator
	 *
	 */
	public interface OnProgressChangeListener {
		void onProgressChanged(int progress);
	}
	
//	float  yDown ;
//	boolean isMove;
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		isMove = false;
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			Log.i("ColorCircle", "down");
//			yDown = ev.getRawY();  
////			setBackgroundColor(Color.GREEN);
//			break;
//		case MotionEvent.ACTION_MOVE:
//			Log.i("ColorCircle", "move");
////			return false;
//			isMove = true;
//			return false;
//		case MotionEvent.ACTION_UP:
//			Log.i("ColorCircle", "up");
//			float yMove = ev.getRawY();  
//			if(yMove-yDown>10){
//				Log.i("ColorCircle", "up1");
//				//是下拉
//				isMove = true;
////				return false;
//			} else {
//				Log.i("ColorCircle", "up2");
////				return true;
//				isMove = false;
//				callOnClick();
//			}
//			break;
//		default:
//			break;
//		}
//		Log.i("ColorCircle", "isMove : " + isMove);
////		Log.d("MyLinerLayout", "MyLinerLayout ----" + String.valueOf(ev.getAction()) + "--onTouchEvent : 自己能处理？");
////		return super.onTouchEvent(ev);
////		return super.onTouchEvent(ev);
//		return !isMove;
//	}
//	
//	
//	public void setClick(){
//		Log.i("MyLinerLayout", "click");
//		
//	}
}
