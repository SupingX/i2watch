package com.suping.i2_watch.view;

import java.util.Arrays;

import com.suping.i2_watch.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CountView extends View {
	/** 竖条数 **/
	private int sum = 24;
	/** 数据条画笔 **/
	private Paint mRectPaint;
	private int mRectPaintColor;
	/** x轴画笔 **/
	private Paint mPaintX;
	/** 时间下标画笔 **/
	private Paint mTextPaint;
	/** 最底下的横线 **/
	private Paint mBottomPaint;
	/** 每条数据的宽度 **/
	private float widthRect;
	/** 左右2边的空白宽度 **/
	private float whiteWith = 25;
	/** 按下时的坐标x值 **/
	private float xDown = 0;
	/** 24小时数据 **/
	private int[] hours = new int[] {
			// 1440条数据
			// 24条数据
			41, 32, 31, 41, 51, 121, 121, 122, 142, 64, 64, 51, 24, 64, 231, 23, 45, 23, 51, 63, 22, 56, 33, 66

	};
	/** 长条的最大高度 **/
	private float rectMaxHeight;

	/** 数据改变 **/
	private OnDataChange mOnDataChange;
	private OnSrollChange mOnSrollChange;
	private int[] olds;

	/**
	 * 数据变化时回调
	 * 
	 * @author Administrator
	 */
	public interface OnDataChange {
		/**
		 * 数据变化时
		 */
		public void onChange(); // 数据变化
	}

	/**
	 * 滑动时
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnSrollChange {
		/**
		 * 滑动到下一条时
		 */
		public void onNext();

		/**
		 * 滑动到上一条时
		 */
		public void onPrevious();
	}

	public CountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
		mPaintX = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRectPaint.setColor(mRectPaintColor);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBottomPaint.setColor(mRectPaintColor);
		olds = Arrays.copyOf(hours, hours.length);
		
		
	}
	
	private void init(Context context, AttributeSet attrs){
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CountView);
		mRectPaintColor = ta.getColor(R.styleable.CountView_rect_color, Color.BLACK);
		ta.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawRects(canvas);
//		drawRectsTest(canvas);
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	private int measureHeight(int heightMeasureSpec) {
		int resultSize = 0;
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		int size = MeasureSpec.getSize(heightMeasureSpec);
		if(MeasureSpec.EXACTLY ==mode){
			resultSize = size;
		}else if(mode == MeasureSpec.AT_MOST){
			resultSize = getWidth()-getPaddingTop()-getPaddingBottom();
			resultSize = Math.min(resultSize, size);
		}
		return resultSize;
	}

	private int measureWidth(int widthMeasureSpec) {
		int resultSize = 0;
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		int size = MeasureSpec.getSize(widthMeasureSpec);
		if(MeasureSpec.EXACTLY ==mode){
			resultSize = size;
		}else if(mode == MeasureSpec.AT_MOST){
			resultSize = getWidth()-getPaddingRight()-getPaddingLeft();
			resultSize = Math.min(resultSize, size);
		}
		return resultSize;
	}

	public void setHours(int[] hours) {
		this.hours = hours;
		olds = Arrays.copyOf(hours, hours.length);
		if (mOnDataChange != null) {
			mOnDataChange.onChange();
		}
		invalidate();
	}

	public void setOnDataChange(OnDataChange l) {
		this.mOnDataChange = l;
	}

	public void setOnSrollChange(OnSrollChange mOnSrollChange) {
		this.mOnSrollChange = mOnSrollChange;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.setClickable(true);
		this.setLongClickable(true);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getX();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			float xMove = event.getX();
			float distance = xMove - xDown;
			if (distance > 0 && Math.abs(distance) > 10) {
				// 右移
				if (mOnSrollChange != null) {
					mOnSrollChange.onNext();
					return true;
				}
				// 左移
			} else if (distance < 0 && Math.abs(distance) > 10) {
				if (mOnSrollChange != null) {
					mOnSrollChange.onPrevious();
					return true;
				}
			}
			break;

		default:
			break;
		}

		return super.onTouchEvent(event);
	}

	private void drawRectsTest(Canvas canvas) {
		// 数据条最大宽度
		rectMaxHeight = getHeight() - 25;
		// x轴的Y坐标
		float lineHeight = getHeight() - 50;
		// 排序 -->升序
		Arrays.sort(hours);
		// 时间点
		String[] times = new String[] { "01", "04", "08", "12", "16", "20", "24", };
		sum = hours.length;
		// 每个数据的宽度（包括空白） 总的宽度/数据的个数
		float widthPer = (getWidth() - whiteWith * 2) / sum;
		// 数据的宽度 2/3
		widthRect = widthPer * 2 / 3;
		// 空白的宽度 1/3
		float widthSpace = widthPer - widthRect;
		// x轴
		canvas.drawLine(whiteWith, lineHeight, getWidth() - whiteWith - widthSpace, lineHeight, mPaintX);
		for (int i = 0; i < olds.length; i++) {
			float left = whiteWith + i * widthPer;
			float top = (float) (rectMaxHeight * (1 - (olds[i] * 1.0 / hours[hours.length - 1])));
			float right = whiteWith + widthRect + i * widthPer;
			float bottom = getHeight() - 50;
			RectF rect = new RectF(left, top, right, bottom);
			// 绘制数据条
			canvas.drawRect(rect, mRectPaint);
			// 绘制数据条最低的直线(和X轴重叠)
			canvas.drawLine(whiteWith + widthPer * i, lineHeight, whiteWith + widthPer * i + widthRect, lineHeight,
					mBottomPaint);
			// 绘制时间点
			if (i % 4 == 0) {
				if (i == 0) {
					canvas.drawText(times[i / 4], whiteWith, getHeight() - 25, mTextPaint);
				} else {
					canvas.drawText(times[i / 4], whiteWith + (i - 1) * widthPer, getHeight() - 25, mTextPaint);
				}
			}
			if (i == 23) {
				canvas.drawText("24", whiteWith + i * widthPer, getHeight() - 25, mTextPaint);
			}
		}
	}
	private void drawRects(Canvas canvas) {
		// 数据条最大宽度
		rectMaxHeight = getHeight() - 25;
		// x轴的Y坐标
		float lineHeight = getHeight() - 50;
		// 排序 -->升序
		Arrays.sort(hours);
		// 时间点
		String[] times = new String[] { "01:00", "04:00", "08:00", "12:00", "16:00", "20:00", "24:00", };
		//字的方块 可以得到字的宽度和高度
		Rect rectText = new Rect();
		mTextPaint.getTextBounds(times[0], 0, times[0].length(), rectText);
		// 每个数据的宽度（包括空白） 总的宽度/数据的个数
		sum = hours.length;
		float widthPer = (getWidth() - whiteWith * 2) / sum;
		// 数据的宽度 2/3
		widthRect = widthPer * 2 / 3;
		// 空白的宽度 1/3
		float widthSpace = widthPer - widthRect;
		// x轴
		canvas.drawLine(whiteWith, lineHeight, getWidth() - whiteWith - widthSpace, lineHeight, mPaintX);
		for (int i = 0; i < olds.length; i++) {
			float left = whiteWith + i * widthPer;
			float top = (float) (rectMaxHeight * (1 - (olds[i] * 1.0 / hours[hours.length - 1])));
			float right = whiteWith + widthRect + i * widthPer;
			float bottom = getHeight() - 50;
			RectF rect = new RectF(left, top, right, bottom);
			// 绘制数据条
			canvas.drawRect(rect, mRectPaint);
			// 绘制数据条最低的直线(比X轴高一个单位)
			canvas.drawLine(whiteWith + widthPer * i, lineHeight-1, whiteWith + widthPer * i + widthRect, lineHeight-1,
					mBottomPaint);
			// 绘制时间点
			if (i % 4 == 0) {
				if (i == 0) {
					canvas.drawText(times[i / 4], whiteWith + widthRect/2 - rectText.width()/2, getHeight() - 25, mTextPaint);
				} else {
					canvas.drawText(times[i / 4], whiteWith + (i - 1) * widthPer + widthRect/2 - rectText.width()/2, getHeight() - 25, mTextPaint);
				}
			}
			if (i == 23) {
				canvas.drawText("24:00", whiteWith + i * widthPer + widthRect/2 - rectText.width()/2, getHeight() - 25, mTextPaint);
			}
		}
	}
}
