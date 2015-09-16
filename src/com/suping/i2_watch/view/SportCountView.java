package com.suping.i2_watch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SportCountView extends View {

	private Paint mPaintRect;
	private Paint mPaintLine;
	private Paint mPaintText;
	private Paint mPaintTextTop;
	private float perX;
	private float spaceY = 30f;
	private float spaceX = 50f;
	private float mHeight;
	private float mWidth;
	private int rectColor;
	/** 24小时数据 **/
	private int[] steps = new int[72] ;
	private int max;

	public SportCountView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SportCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SportCountView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaintRect = new Paint();
		mPaintRect.setAntiAlias(true); // 消除锯齿
		mPaintRect.setStyle(Paint.Style.FILL_AND_STROKE); // 绘制空心圆
		mPaintRect.setColor(Color.RED); // 设置进度条颜色
		mPaintRect.setStrokeWidth(1); // 设置进度条宽度
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
		mPaintTextTop = new Paint();
		mPaintTextTop.setAntiAlias(true); // 消除锯齿
		mPaintTextTop.setStyle(Paint.Style.FILL); // 绘制空心圆
		mPaintTextTop.setStrokeWidth(2); // 设置进度条宽度
		mPaintTextTop.setColor(Color.BLACK); // 设置进度条颜色
		mPaintTextTop.setStrokeJoin(Paint.Join.ROUND);
		mPaintTextTop.setStrokeCap(Paint.Cap.ROUND); // 设置圆角
		mPaintTextTop.setTextSize(15);
		

	}

	@Override
	protected void onDraw(Canvas canvas) {
		getMax();
		mWidth = getWidth();
		mHeight = getHeight();
		perX = (mWidth-2*spaceX) / (float) steps.length;

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
		for (int i = 0; i < steps.length; i++) {
			if (max < steps[i]) {
				max = steps[i];
				maxIndex = i;
			}
		}
	}

	private void drawRect(Canvas canvas) {
		for (int i = 0; i < steps.length; i++) {
			float rectHeight = getRectHeight(steps[i]);
			float left = perX * i+spaceX;
			float top = mHeight - spaceY - rectHeight;
			float right = perX * (i + 1)+spaceX;
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
					canvas.drawText(text + "", spaceX+perX * (i+1) - ((rectText.width() / 2-perX / 2)), mHeight - spaceY+5 + rectText.height(), mPaintText);
				}
			}
			if (maxIndex ==i) {
				Rect rectText = new Rect();
				mPaintTextTop.getTextBounds(String.valueOf(max), 0, String.valueOf(max).length(), rectText);
				canvas.drawText(String.valueOf(max) + "", spaceX+perX * (i) - ((rectText.width() / 2-perX / 2)), top-rectText.height()/2, mPaintTextTop);
			}
		}

	}



	/**
	 * 根据数据 确定数据块的高度
	 * 
	 * @param i
	 */
	private float getRectHeight(int value) {
		return (float) ((mHeight - 2 * spaceY) * value) / max;
	}
	
	private void drawLine(Canvas canvas) {
		canvas.drawLine(0, mHeight - spaceY, mWidth, mHeight - spaceY, mPaintLine);
	}
	
	public void setSteps(int[] steps){
		this.steps = steps;
		if (mOnStepsChangeListener!=null) {
			mOnStepsChangeListener.onChange(steps);
		}
		invalidate();
	}
	public interface  OnStepsChangeListener{
		public void onChange(int[] steps);
	}
	private OnStepsChangeListener mOnStepsChangeListener;
	public void setOnStepsChangeListener(OnStepsChangeListener l){
		this.mOnStepsChangeListener = l;
	}

}
