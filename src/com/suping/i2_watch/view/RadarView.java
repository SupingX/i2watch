package com.suping.i2_watch.view;

import com.suping.i2_watch.R;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 雷达
 * @author Administrator
 *
 */
public class RadarView extends View {
	private Bitmap bitBg;
	private Bitmap bitRadar;
	private float angel = 0.0f;
	private Matrix mMatrix;
	private float mHeight;
	private float mWidth;
	private ValueAnimator mValueAnimator;

	public RadarView(Context context) {
		super(context);
		bitBg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_bg);
		bitRadar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_rotation);
		mMatrix = new Matrix();
		mHeight = bitBg.getHeight();
		mWidth = bitBg.getWidth();
	}

	public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		bitBg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_bg);
		bitRadar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_rotation);
		mMatrix = new Matrix();
		mHeight = bitBg.getHeight();
		mWidth = bitBg.getWidth();
	}

	public RadarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bitBg = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_bg);
		bitRadar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_rotation);
		mMatrix = new Matrix();
		mHeight = bitBg.getHeight();
		mWidth = bitBg.getWidth();
	}

	/**
	 * 开始旋转
	 */
	public void start() {
		mValueAnimator = ValueAnimator.ofFloat(0f, 360f);
		mValueAnimator.setDuration(2000);
		mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Log.e("雷达", "angel : " + angel);
				angel = (Float) animation.getAnimatedValue();
				mMatrix.reset();
				mMatrix.setRotate(angel, mWidth / 2, mHeight / 2);
				invalidate();
			}
		});
		LinearInterpolator lin = new LinearInterpolator();
		mValueAnimator.setInterpolator(lin);
		mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
//		mValueAnimator.start();
		mValueAnimator.reverse();
	}
	/**
	 * 停止旋转
	 */
	public void stop() {
		if(	mValueAnimator!=null ){
			mValueAnimator.cancel();
		}
	
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) mWidth, (int) mHeight);
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.e("雷达", "onDraw()");
		Paint p = new Paint();
		p.setAntiAlias(true);
		canvas.drawColor(getResources().getColor(R.color.white));
		canvas.drawBitmap(bitBg, 0, 0, p);
		canvas.drawBitmap(bitRadar, mMatrix, p);
		super.onDraw(canvas);
	}

}
