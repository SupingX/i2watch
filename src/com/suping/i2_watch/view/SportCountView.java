package com.suping.i2_watch.view;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SportCountView extends View {
	/** 竖条数 **/
	private int sum = 24;
	/** 数据条画笔 **/
	private Paint mRectPaint;
	/** x轴画笔  **/
	private Paint mPaintX;
	/** 时间下标画笔 **/
	private Paint mTextPaint;
	/** 每条数据的宽度  **/
	private float widthRect;
	/**左右2边的空白宽度**/
	private float whiteWith = 25;
	/** 按下时的坐标x值  **/
	private float xDown = 0;
	/** 24小时数据  **/
	private int[] hours = new int[] {
			// 1440条数据
			// 24条数据
			41, 32, 31, 41, 51, 121, 121, 122, 142, 64, 64, 51, 24, 64, 231, 23, 45, 23, 51, 63, 22, 56, 33, 66

	};
	/** 长条的最大高度   **/
	private float rectMaxHeight;
	
	/** 数据改变  **/
	private OnDataChange mOnDataChange;
	private OnSrollChange mOnSrollChange;

	/**
	 * 数据变化时回调
	 * 
	 * @author Administrator
	 */
	public interface OnDataChange {
		/**
		 *数据变化时
		 */
		public void onChange(); // 数据变化
	}
	/**
	 * 滑动时
	 * @author Administrator
	 *
	 */
	public interface OnSrollChange{
		/**
		 * 滑动到下一条时
		 */
		public void onNext(); 
		/**
		 * 滑动到上一条时
		 */
		public void onPrevious(); 
	}

	public SportCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaintX = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// mRectPaint.setColor(Color.RED);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.BLUE);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawLine(0, getHeight() - 50, getWidth(), getHeight() - 50, mPaintX);
		drawRects(canvas);

		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setHours(int[] hours) {
		this.hours = hours;
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
			Log.e("SportCountView", "xMove : " + xMove);
			Log.e("SportCountView", "xDown : " + xDown);
		
			float distance = xMove - xDown;
			Log.e("SportCountView", "distance : " + distance);
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

	private void drawRects(Canvas canvas) {
		rectMaxHeight = getHeight() - 25;
		// 保存原始的数据。
		int[] old = Arrays.copyOf(hours, hours.length);
		// 排序 后 -->升序
		Arrays.sort(hours);
	
		Log.d("sportCountView", "sportCountView -->" + hours[hours.length - 1]);
	
		String[] times = new String[] { "01", "04", "08", "12", "16", "20", "24", };
		sum = hours.length;
		// 每个数据的宽度（包括空白） 总的宽度/数据的个数
		float widthPer = (getWidth() - whiteWith * 2) / sum;
		Log.d("SportCountView", "widthPer : " + widthPer);
		// 数据的宽度 2/3
		widthRect = widthPer * 2 / 3;
		// Log.d("SportCountView", "widthRect : " + widthRect);
		// 空白的宽度 1/3
		float widthSpace = widthPer - widthRect;
		// Log.d("SportCountView", "widthSpace : " + widthSpace);
		// 绘制时间点
		// Log.d("SportCountView", "getHeight : " + getHeight());
		for (int i = 0; i < old.length; i++) {
			// Log.d("---------------------", "       i       " + i);
			// 每个条形高度 （即top），为rectMaxHeight * (1-old[i]/hours[hours.length-1])
			// RectF rect = new RectF(widthSpace+i*widthPer,
			// getHeight()-hours[i]-50, (i+1)*widthPer, getHeight()-50);
			// Log.d("SportCountView", "i= " + i + " , hours[hours.length-1] = "
			// + hours[hours.length-1] + ", old[i]  = " + old[i]);
			// Log.d("SportCountView",
			// "rectMaxHeight * (1-(old[i]*1.0/hours[hours.length-1]) ---->" +
			// rectMaxHeight * (1-(old[i]*1.0/hours[hours.length-1])));
	
			float left = whiteWith + i * widthPer;
			float top = (float) (rectMaxHeight * (1 - (old[i] * 1.0 / hours[hours.length - 1])));
			float right = whiteWith + widthRect + i * widthPer;
			float bottom = getHeight() - 50;
	
			RectF rect = new RectF(left, top, right, bottom);
			// Log.d("SportCountView", "rect : " + rect.toString());
			canvas.drawRect(rect, mRectPaint);
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
}
