package com.suping.i2_watch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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

import com.suping.i2_watch.R;

public class ColorsCircle extends View {
	/** 彩环渐变颜色 **/
	private final int[] COLORS = new int[] { 0xFF22AC38, 0xFF009944,
			0xFF009B6B, 0xFF009E96, 0xFF00A0C1, 0xFF00A0E9, 0xFF0086D1,
			0xFF0068B7, 0xFF00479D, 0xFF1D2088, 0xFF601986, 0xFF920783,
			0xFFBE0081, 0xFFE4007F, 0xFFE5006A, 0xFFE5004F, 0xFFE60033,
			0xFFE60012, 0xFFEB6100, 0xFFF39800, 0xFFFCC800, 0xFFFFF100,
			0xFFCFDB00, 0xFF8FC31F, 0xFF22AC38 };
	private Context mContext;
	private Paint mBackgroundPaint = null;
	private Paint mProgressPaint = null;
	private Paint mSmallPaint = null;

	private RectF mArcRectF = null;
	private Shader mShader;
	private int mProgressMax = 100;
	private int mProgress = 0;

	private int mViewHeight = 250;
	private int mViewWidth = 250;
	private int mSize;
	private int mRadius = 0;
	private int mSmallRadius = 0;
	private int mCenterX ;
	private int mCenterY ;
	private  int mProgressColor;
	/** bar **/
	private Drawable bar;

	private OnProgressChangeListener mOnProgressChangeListener = null;

	public void setOnProgressChangeListener(
			OnProgressChangeListener mOnProgressChangeListener) {
		this.mOnProgressChangeListener = mOnProgressChangeListener;
	}

	public interface OnProgressChangeListener {
		void onProgressChanged(int progress);
	}

	public ColorsCircle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initViewAttrs(attrs);
		mArcRectF = new RectF();
	}

	public ColorsCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initViewAttrs(attrs);
		mArcRectF = new RectF();
	}

	private void initViewAttrs(AttributeSet attrs) {
		TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs,
				R.styleable.ColorsCircle);
		int backgroundColor = mTypedArray.getColor(
				R.styleable.ColorsCircle_background_color, 0xdddddddd);
		int progress_color = mTypedArray.getColor(
				R.styleable.ColorsCircle_progress_color, 0);
		int small_backgroundColor = mTypedArray.getColor(
				R.styleable.ColorsCircle_small_background_color, 0xdddddddd);
		float radius = mTypedArray.getDimension(
				R.styleable.ColorsCircle_radius, (250f / 2));
		float small_radius = mTypedArray.getDimension(
				R.styleable.ColorsCircle_small_radius, 0);
		float stroke = mTypedArray.getDimension(
				R.styleable.ColorsCircle_stroke, 10f);
		float small_stroke = mTypedArray.getDimension(
				R.styleable.ColorsCircle_small_stroke, 4f);
		mViewWidth = mTypedArray.getDimensionPixelOffset(R.styleable.ColorsCircle_width, 250);
		mViewHeight = mTypedArray.getDimensionPixelOffset(R.styleable.ColorsCircle_height, 250);
		int max = mTypedArray.getInt(R.styleable.ColorsCircle_max, 100);
		int progress = mTypedArray.getInt(R.styleable.ColorsCircle_progress, 0);

		// mThumbDrawable =
		// mTypedArray.getDrawable(R.styleable.CircleSeekBar_android_thumb);
		// Log.d("OB", "mThumbDrawable-->" + mThumbDrawable);
		// mThumbWidth = mThumbDrawable.getIntrinsicWidth();
		// mThumbHeight = mThumbDrawable.getIntrinsicHeight();
		//
		// mThumbNormal = new int[]{-android.R.attr.state_focused,
		// -android.R.attr.state_pressed,
		// -android.R.attr.state_selected, -android.R.attr.state_checked};
		// mThumbPressed = new int[]{android.R.attr.state_focused,
		// android.R.attr.state_pressed,
		// android.R.attr.state_selected, android.R.attr.state_checked};

		mTypedArray.recycle();

		bar = getContext().getResources().getDrawable(
				R.drawable.ic_scrubber_control_normal_holo);

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
		// mSmallPaint.setShader(mShader);
		mSmallPaint.setStyle(Paint.Style.STROKE);
		mSmallPaint.setStrokeWidth(small_stroke);

		mRadius = (int) radius;
		mSmallRadius = (int) small_radius;
		mProgress = progress;
		 if(null != mOnProgressChangeListener){  
	    	   mOnProgressChangeListener.onProgressChanged(mProgress);  
	       }
		mProgressMax = max;
		mProgressColor = progress_color;
		
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewWidth = getWidth();
		mViewHeight = getHeight();

		mSize = mViewWidth > mViewHeight ? mViewHeight : mViewWidth;

		mCenterX = mViewWidth / 2;
		mCenterY = mViewHeight / 2;

		int left = mCenterX - mRadius;
		int right = mCenterX + mRadius;
		int top = mCenterY - mRadius;
		int bottom = mCenterY + mRadius;
		mArcRectF.set(left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 彩环背景圈
		canvas.drawCircle(mCenterX, mCenterY, mRadius, mBackgroundPaint);

		// 小圈
		canvas.drawCircle(mCenterX, mCenterY, mSmallRadius, mSmallPaint);

		// 大环
		if(mProgressColor!=0){
			mProgressPaint.setColor(mProgressColor);
		} else {
			mShader = new SweepGradient(mCenterX, mCenterY, COLORS, null);
			mProgressPaint.setShader(mShader);
		}
//		 canvas.drawArc(this.mArcRectF, 0.0f-90f, 90f, false, mProgressPaint);
		canvas.drawArc(this.mArcRectF, -90f, toAngle(mProgress), false,
				mProgressPaint);
		Log.d("OB", "toAngle"+toAngle(0));

		// bar
		double x = mCenterX + mRadius
				* Math.sin(Math.toRadians(mProgress * 360 / mProgressMax));
		double y = mCenterY - mRadius
				* Math.cos(Math.toRadians(mProgress * 360 / mProgressMax));
		int barLeft = (int) (float) (x - bar.getIntrinsicWidth() / 2);
		int barTop = (int) (float) (y - bar.getIntrinsicHeight() / 2);
		int barRight = (int) (barLeft + bar.getIntrinsicWidth());
		int barBottom = (int) (barTop + bar.getIntrinsicHeight());
		bar.setBounds((int) barLeft, (int) barTop, barRight, barBottom);
		bar.draw(canvas);
		
		super.onDraw(canvas);
	}

	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
		 	float eventX = event.getX();  
	        float eventY = event.getY();  
	 switch (event.getAction()) {
	 case MotionEvent.ACTION_DOWN:
		 seekTo(eventX,eventY);
	 break;
	 case MotionEvent.ACTION_UP:
		 seekTo(eventX,eventY);
	 break;
	 case MotionEvent.ACTION_MOVE:
		 seekTo(eventX,eventY);
	 break;
	
	 default:
	 break;
	 }
	 return true;
	 }
   private void seekTo(float eventX, float eventY) {  
	   //角度
	   double radian = Math.atan2(eventY - mCenterY, eventX - mCenterX);  
       /* 
        * 由于atan2返回的值为[-pi,pi] 
        * 因此需要将弧度值转换一下，使得区间为[0,2*pi] 
        */  
       if (radian < 0){  
           radian = radian + 2*Math.PI;  
       } 
       //数值 度-->数值  用Math.toDegrees()
       float angle = Math.round(Math.toDegrees(radian));
       mProgress = (int) (mProgressMax * (angle+90) / 360);
       if(null != mOnProgressChangeListener){  
    	   mOnProgressChangeListener.onProgressChanged(mProgress);  
       }
       invalidate(); 
   }
	 /**
	     * 判断点是否在环上？
	     * @param eventX
	     * @param eventY
	     * @return
	     */
	 private boolean isPointOnThumb(float eventX, float eventY) {  
	        boolean result = false;  
	        double distance = Math.sqrt(Math.pow(eventX - mCenterX, 2)  
	                + Math.pow(eventY - mCenterY, 2));  
	        if (distance < mSize && distance > (mSize / 2 - bar.getIntrinsicWidth())){  
	            result = true;  
	        }  
	        return result;  
	    }
	/**
	 * 设置seekbar位置？
	 * 
	 * 将数字转为 弧度 
	 * 当前的度数为
	 * mProgress * 360 / mProgressMax
	 * 转化为弧度：
	 * Math.Math.toRadians(mProgress * 360 / mProgressMax);
	 * 
	 */
	private float toAngle(int progress) {
		float angle = 0;
		angle = 360 * progress / mProgressMax ;
		return angle;
	}

	public Paint getmBackgroundPaint() {
		return mBackgroundPaint;
	}

	public void setmBackgroundPaint(Paint mBackgroundPaint) {
		this.mBackgroundPaint = mBackgroundPaint;
	}

	public Paint getmProgressPaint() {
		return mProgressPaint;
	}

	public void setmProgressPaint(Paint mProgressPaint) {
		this.mProgressPaint = mProgressPaint;
	}

	public Paint getmSmallPaint() {
		return mSmallPaint;
	}

	public void setmSmallPaint(Paint mSmallPaint) {
		this.mSmallPaint = mSmallPaint;
	}

	public synchronized int getmProgressMax() {
		return mProgressMax;
	}

	public synchronized void setmProgressMax(int mProgressMax) {
		this.mProgressMax = mProgressMax;
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
		 if(null != mOnProgressChangeListener){  
	    	   mOnProgressChangeListener.onProgressChanged(mProgress);  
	       }
		invalidate();
	}

	public int getmViewHeight() {
		return mViewHeight;
	}

	public void setmViewHeight(int mViewHeight) {
		this.mViewHeight = mViewHeight;
	}

	public int getmViewWidth() {
		return mViewWidth;
	}

	public void setmViewWidth(int mViewWidth) {
		this.mViewWidth = mViewWidth;
	}

	public int getmRadius() {
		return mRadius;
	}

	public void setmRadius(int mRadius) {
		this.mRadius = mRadius;
	}

	public int getmSmallRadius() {
		return mSmallRadius;
	}

	public void setmSmallRadius(int mSmallRadius) {
		this.mSmallRadius = mSmallRadius;
	}

	public int getmCenterX() {
		return mCenterX;
	}

	public void setmCenterX(int mCenterX) {
		this.mCenterX = mCenterX;
	}

	public int getmCenterY() {
		return mCenterY;
	}

	public void setmCenterY(int mCenterY) {
		this.mCenterY = mCenterY;
	}
}
