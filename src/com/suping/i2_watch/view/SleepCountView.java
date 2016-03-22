package com.suping.i2_watch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SleepCountView extends View {

	private Paint mPaintRect;
	private Paint mPaintLine;
	private Paint mPaintText;
	private float perX;
	private float spaceY = 30f;
	private float mHeight;
	private float mWidth;

	/** 24*3小时数据 **/
	private int[] sleeps = new int[72];
//			{
//				1,1,1,2,2,0,0,0,1,0,1,2,
//				1,1,1,2,2,0,0,0,1,0,1,2,
//				1,1,1,2,2,0,0,0,1,0,1,2,
//				1,1,1,2,2,0,0,0,1,0,1,2,
//				1,1,1,2,2,0,0,0,1,0,1,2,
//				1,1,1,2,2,0,0,0,1,0,1,2
//			};
	private int max;

	public SleepCountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SleepCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SleepCountView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaintRect = new Paint();
		mPaintRect.setAntiAlias(true); // 消除锯齿
		mPaintRect.setStyle(Paint.Style.FILL_AND_STROKE); // 绘制空心圆
		mPaintRect.setStrokeWidth(1); // 设置进度条宽度
		mPaintRect.setColor(Color.rgb(234,23,70)); // 设置进度条颜色
		mPaintRect.setStrokeJoin(Paint.Join.ROUND);
		mPaintRect.setStrokeCap(Paint.Cap.ROUND); // 设置圆角

		mPaintLine = new Paint();
		mPaintLine.setAntiAlias(true); // 消除锯齿
		mPaintLine.setStyle(Paint.Style.FILL); // 绘制空心圆
		mPaintLine.setStrokeWidth(4); // 设置进度条宽度
		mPaintLine.setColor(Color.BLACK); // 设置进度条颜色
		mPaintLine.setStrokeJoin(Paint.Join.ROUND);
		mPaintLine.setStrokeCap(Paint.Cap.ROUND); // 设置圆角

		mPaintText = new Paint();
		mPaintText.setAntiAlias(true); // 消除锯齿
		mPaintText.setStyle(Paint.Style.FILL); // 绘制空心圆
		mPaintText.setStrokeWidth(2); // 设置进度条宽度
		mPaintText.setColor(Color.BLACK); // 设置进度条颜色
		mPaintText.setStrokeJoin(Paint.Join.ROUND);
		mPaintText.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
		mPaintText.setTextSize(15);
		

	}

	@Override
	protected void onDraw(Canvas canvas) {
		getMax();
		mWidth = getWidth();
		mHeight = getHeight();
		perX = (mWidth) / (float) sleeps.length;

		// 画线x轴
		drawLine(canvas);
		// 画数据块
		drawRect(canvas);

		super.onDraw(canvas);

	}

	private int maxIndex;

	/**
	 * 获取最大数据的值
	 */
	private void getMax() {
		max= 0;
		maxIndex=0;
		for (int i = 0; i < sleeps.length; i++) {
			if (max < sleeps[i]) {
				max = sleeps[i];
				maxIndex = i;
			}
		}
	}

	private void drawRect(Canvas canvas) {
		for (int i = 0; i < sleeps.length; i++) {
			float rectHeight = getRectHeight(sleeps[i]);
			float left = perX * i;
			float top = mHeight - spaceY - rectHeight;
			float right = perX * (i + 1);
			float bottom = mHeight - spaceY;
			RectF rectF = new RectF(left, top, right, bottom);
			canvas.drawRect(rectF, mPaintRect);

			// 画数字下标
			if (i % 3 == 0) {

				if (i % 2 != 0) {
					Rect rectText = new Rect();
					mPaintText.getTextBounds("00", 0, "00".length(), rectText);
					String text = "";
					if (String.valueOf(i / 3).length() == 1) {
						text = "0" + String.valueOf(i / 3);
					} else {
						text = String.valueOf(i / 3);
					}
					canvas.drawText(text + "", perX * (i+1) - ((rectText.width() / 2-perX / 2)), mHeight - spaceY+5 + rectText.height(), mPaintText);
				}
			}
			
		

		}

	}

	/**
	 * 根据数据 确定数据块的高度
	 * 
	 * 0--深睡
	 * 1--浅睡
	 * 2--清醒
	 * 默认0
	 * @param i
	 */
	private float getRectHeight(int value) {
//		mPaintRect.setColor(Color.rgb(15, 39, 135));
		mPaintRect.setColor(Color.rgb(200, 98, 34));
		float height = (float) ((mHeight - 2 * spaceY) * 3) / 3;
//		float height = (float) ((mHeight - 2 * spaceY) * 1) / 3;;
		switch (value) {
		case 0:
			mPaintRect.setColor(Color.rgb(15, 39, 135));
			height = (float) ((mHeight - 2 * spaceY) * 1) / 3;
			break;
		case 1:
			mPaintRect.setColor(Color.rgb(54, 131, 221));
			height = (float) ((mHeight - 2 * spaceY) * 2) / 3;
			break;
		case 2:
			mPaintRect.setColor(Color.rgb(200, 98, 34));
			height = (float) ((mHeight - 2 * spaceY) * 3) / 3;
			break;
		default:
			break;
		}
		return height;
	}

	private void drawLine(Canvas canvas) {
		canvas.drawLine(0, mHeight - spaceY, mWidth, mHeight - spaceY, mPaintLine);
	}
	
	public void setSleeps(int[] sleeps){
		this.sleeps = sleeps;
		if (mOnSleepsChangeListener!=null) {
			mOnSleepsChangeListener.onChange(sleeps);
		}
		invalidate();
	}
	public interface  OnSleepsChangeListener{
		public void onChange(int[] steps);
	}
	private OnSleepsChangeListener mOnSleepsChangeListener;
	public void setOnStepsChangeListener(OnSleepsChangeListener l){
		this.mOnSleepsChangeListener = l;
	}

}
