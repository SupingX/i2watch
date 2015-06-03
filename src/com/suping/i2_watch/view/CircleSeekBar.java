package com.suping.i2_watch.view;


import com.suping.i2_watch.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义圆环拖动条
 * 
 * @author Administrator
 * 
 */
public class CircleSeekBar extends View {
	/**
	 * 保存状态
	 */
	private static final String STATE_PARENT = "parent";
	private static final String STATE_ANGLE = "angle";

	/**
	 * 事件监听
	 */
	private OnCircleSeekBarChangedListener mOnCircleSeekBarChangeListener;

	/**
	 * 圆环paint对象
	 */
	private Paint mColorWheelPaint;

	/**
	 * 游标paint对象
	 */
	private Paint mPointerHaloPaint;
	private Paint mPointerHaloTopPaint;

	/**
	 * 游标为图画时的paint对象
	 */
	// private Paint mPointerColor;

	/**
	 * 圆环的宽度
	 */
	private final int mColorWheelStrokeWidth = 10;

	/**
	 * 游标所在圆环半径
	 */
	private float mPointerRadius;
	private float mPointerRadius2;

	/**
	 * The rectangle enclosing the color wheel.
	 */
	private RectF mColorWheelRectangle = new RectF();

	/**
	 * {@code true} 点击游标 {@code false} 停止
	 * 
	 * @see #onTouchEvent(MotionEvent)
	 */
	private boolean mUserIsMovingPointer = false;

	/**
	 * 
	 */
	private float mTranslationOffset;

	/**
	 * 圆环半径 Note: (Re)在onMeasure计算{@link #onMeasure(int, int)}
	 */
	private float mColorWheelRadius;

	private float mAngle;
	private String text;
	private int max = 20000;
	private int min = 1000;
	private String color_attr;
	// private SweepGradient shader;
	private Paint mArcColor;
	private String wheel_color_attr, wheel_unactive_color_attr,
			pointer_color_attr, pointer_halo_color_attr;
	private int init_position;
	private boolean block_end = false;
	private float lastX;
	private int last_radians = 0;
	private boolean block_start = false;

	private int arc_finish_radians = 270;
	// 左下角开始
	private int start_arc = 135;

	private float[] pointerPosition;
	private RectF mColorCenterHaloRectangle = new RectF();
	private int end_wheel;

	private static final int[] COLORS = new int[] { 0xFF22AC38, 0xFF009944,
		0xFF009B6B, 0xFF009E96, 0xFF00A0C1, 0xFF00A0E9, 0xFF0086D1,
		0xFF0068B7, 0xFF00479D, 0xFF1D2088, 0xFF601986, 0xFF920783,
		0xFFBE0081, 0xFFE4007F, 0xFFE5006A, 0xFFE5004F, 0xFFE60033,
		0xFFE60012, 0xFFEB6100, 0xFFF39800, 0xFFFCC800, 0xFFFFF100,
		0xFFCFDB00, 0xFF8FC31F, 0xFF22AC38 };

	public CircleSeekBar(Context context) {
		super(context);
		init(null, 0);
	}

	public CircleSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public CircleSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.HoloCircleSeekBar, defStyle, 0);

		initAttributes(a);

		a.recycle();

		mColorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mColorWheelPaint.setColor(Color.parseColor("#FFC9C9C9"));
		mColorWheelPaint.setStyle(Paint.Style.STROKE);
		mColorWheelPaint.setStrokeWidth(mColorWheelStrokeWidth);

		mPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerHaloPaint.setColor(Color.BLACK);
		mPointerHaloPaint.setAlpha(0x50);
		mPointerHaloTopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerHaloTopPaint.setColor(Color.parseColor("#FF048ABF"));

		// 设置游标滑过的背景属性
		// 做彩环
		Shader shader = new SweepGradient(0, 0, COLORS, null);
		mArcColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcColor.setShader(shader);
		// mArcColor.setColor(Color.GREEN);
		mArcColor.setStyle(Paint.Style.STROKE);
		mArcColor.setStrokeWidth(mColorWheelStrokeWidth);

		arc_finish_radians = (int) calculateAngleFromText(init_position) - 90;

		if (arc_finish_radians > end_wheel)
			arc_finish_radians = end_wheel;
		mAngle = calculateAngleFromRadians(arc_finish_radians > end_wheel ? end_wheel
				: arc_finish_radians);
		text = String.valueOf(calculateTextFromAngle(arc_finish_radians));

		invalidate();
	}

	private void initAttributes(TypedArray a) {

		max = a.getInteger(R.styleable.HoloCircleSeekBar_max, 20000);

		color_attr = a.getString(R.styleable.HoloCircleSeekBar_color);
		wheel_color_attr = a
				.getString(R.styleable.HoloCircleSeekBar_wheel_active_color);
		wheel_unactive_color_attr = a
				.getString(R.styleable.HoloCircleSeekBar_wheel_unactive_color);
		pointer_color_attr = a
				.getString(R.styleable.HoloCircleSeekBar_pointer_color);
		pointer_halo_color_attr = a
				.getString(R.styleable.HoloCircleSeekBar_pointer_halo_color);

		init_position = a.getInteger(
				R.styleable.HoloCircleSeekBar_init_position, 0);

		start_arc = a.getInteger(R.styleable.HoloCircleSeekBar_start_angle, 0);
		end_wheel = a.getInteger(R.styleable.HoloCircleSeekBar_end_angle, 360);

		mPointerRadius = a.getDimension(
				R.styleable.HoloCircleSeekBar_pointer_size, 24);
		mPointerRadius2 = mPointerRadius / 2;

		last_radians = end_wheel;

		if (init_position < start_arc)
			init_position = calculateTextFromStartAngle(start_arc);

		if (color_attr != null) {
			try {
				Color.parseColor(color_attr);
			} catch (IllegalArgumentException e) {
			}
			Color.parseColor(color_attr);
		} else {
		}

		if (wheel_color_attr != null) {
			try {
				Color.parseColor(wheel_color_attr);
			} catch (IllegalArgumentException e) {
			}

		} else {
		}
		if (wheel_unactive_color_attr != null) {
			try {
				Color.parseColor(wheel_unactive_color_attr);
			} catch (IllegalArgumentException e) {
			}

		} else {
		}

		if (pointer_color_attr != null) {
			try {
				Color.parseColor(pointer_color_attr);
			} catch (IllegalArgumentException e) {
			}

		} else {
		}

		if (pointer_halo_color_attr != null) {
			try {
				Color.parseColor(pointer_halo_color_attr);
			} catch (IllegalArgumentException e) {
			}

		} else {
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.translate(mTranslationOffset, mTranslationOffset);

		// 滑过的弧
		canvas.drawArc(mColorWheelRectangle, start_arc + 270, end_wheel
				- (start_arc), false, mColorWheelPaint);

		// 背景弧
		canvas.drawArc(mColorWheelRectangle, start_arc + 270,
				(arc_finish_radians) > (end_wheel) ? end_wheel - (start_arc)
						: arc_finish_radians - start_arc, false, mArcColor);
		// 游标为圆形
		canvas.drawCircle(pointerPosition[0], pointerPosition[1],
				mPointerRadius, mPointerHaloPaint);
		canvas.drawCircle(pointerPosition[0], pointerPosition[1],
				mPointerRadius2, mPointerHaloTopPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int min = Math.min(width, height);
		setMeasuredDimension(min, min);

		mTranslationOffset = min * 0.5f;
		mColorWheelRadius = mTranslationOffset - mPointerRadius;

		mColorWheelRectangle.set(-mColorWheelRadius, -mColorWheelRadius,
				mColorWheelRadius, mColorWheelRadius);

		mColorCenterHaloRectangle.set(-mColorWheelRadius / 2,
				-mColorWheelRadius / 2, mColorWheelRadius / 2,
				mColorWheelRadius / 2);

		pointerPosition = calculatePointerPosition(mAngle);

	}

	private int calculateTextFromAngle(float angle) {
		int text = (int) (angle * max / 360);
		text = (int) Math.ceil(text / 100.00) * 100;
		if (text <= min) {
			text = min;
		}
		if (text >= max) {
			text = max;
		}
		// System.out.println("angle：" + angle + "   text:" + text);
		return text;
	}

	private int calculateTextFromStartAngle(float angle) {
		int text = (int) (angle * 100 * 200 / 360);
		text = (int) Math.ceil(text / 100.00) * 100;
		if (text <= min) {
			text = min;
		}
		if (text >= max) {
			text = max;
		}
		return text;
	}

	private double calculateAngleFromText(int position) {
		if (position == 0 || position >= max)
			return (float) 90;

		double f = (double) max / (double) position;

		double f_r = 360 / f;

		double ang = f_r + 90;

		return ang;

	}

	private int calculateRadiansFromAngle(float angle) {
		float unit = (float) (angle / (2 * Math.PI));
		if (unit < 0) {
			unit += 1;
		}
		int radians = (int) ((unit * 360) - ((360 / 4) * 3));
		if (radians < 0)
			radians += 360;
		return radians;
	}

	private float calculateAngleFromRadians(int radians) {
		return (float) (((radians + 270) * (2 * Math.PI)) / 360);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Convert coordinates to our internal coordinate system
		float x = event.getX() - mTranslationOffset;
		float y = event.getY() - mTranslationOffset;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mAngle = (float) java.lang.Math.atan2(y, x);
			block_end = false;
			block_start = false;
			mUserIsMovingPointer = true;

			arc_finish_radians = calculateRadiansFromAngle(mAngle);

			if (arc_finish_radians > end_wheel) {
				arc_finish_radians = end_wheel;
				block_end = true;
			}

			if (!block_end && !block_start) {
				text = String
						.valueOf(calculateTextFromAngle(arc_finish_radians));
				pointerPosition = calculatePointerPosition(mAngle);
				if (mOnCircleSeekBarChangeListener != null)
					// down的时候也注册一下OnCircleSeekBarChangeListener
					mOnCircleSeekBarChangeListener.onProgressChanged(this,
							Integer.parseInt(text), true);
				invalidate();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mUserIsMovingPointer) {
				mAngle = (float) java.lang.Math.atan2(y, x);
				int radians = calculateRadiansFromAngle(mAngle);

				if (last_radians > radians && radians < (360 / 6) && x > lastX
						&& last_radians > (360 / 6)) {

					if (!block_end && !block_start)
						block_end = true;

				} else if (last_radians >= start_arc
						&& last_radians <= (360 / 4) && radians <= (360 - 1)
						&& radians >= ((360 / 4) * 3) && x < lastX) {
					if (!block_start && !block_end)
						block_start = true;

				} else if (radians >= end_wheel && !block_start
						&& last_radians < radians) {
					block_end = true;
				} else if (radians < end_wheel && block_end
						&& last_radians > end_wheel) {
					block_end = false;
				} else if (radians < start_arc && last_radians > radians
						&& !block_end) {
					block_start = true;
				} else if (block_start && last_radians < radians
						&& radians > start_arc && radians < end_wheel) {
					block_start = false;
				}

				if (block_end) {
					arc_finish_radians = end_wheel;
					text = String.valueOf(max);
					mAngle = calculateAngleFromRadians(arc_finish_radians);
					pointerPosition = calculatePointerPosition(mAngle);
				} else if (block_start) {
					arc_finish_radians = start_arc;
					mAngle = calculateAngleFromRadians(arc_finish_radians);
					text = String.valueOf(min);
					pointerPosition = calculatePointerPosition(mAngle);
					if (mOnCircleSeekBarChangeListener != null)
						mOnCircleSeekBarChangeListener.onProgressChanged(this,
								Integer.parseInt(text), true);
				} else {
					// text = String.valueOf(calculateTextFromAngle(mAngle));
					arc_finish_radians = calculateRadiansFromAngle(mAngle);
					text = String
							.valueOf(calculateTextFromAngle(arc_finish_radians));
					pointerPosition = calculatePointerPosition(mAngle);
					if (mOnCircleSeekBarChangeListener != null)
						mOnCircleSeekBarChangeListener.onProgressChanged(this,
								Integer.parseInt(text), true);
				}
				invalidate();

				last_radians = radians;
			}
			break;
		case MotionEvent.ACTION_UP:
			mUserIsMovingPointer = false;
			break;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		lastX = x;

		return true;
	}

	/**
	 * Calculate the pointer's coordinates on the color wheel using the supplied
	 * angle.
	 * 
	 * @param angle
	 *            The position of the pointer expressed as angle (in rad).
	 * 
	 * @return The coordinates of the pointer's center in our internal
	 *         coordinate system.
	 */
	private float[] calculatePointerPosition(float angle) {
		float x = (float) (mColorWheelRadius * Math.cos(angle));
		float y = (float) (mColorWheelRadius * Math.sin(angle));

		return new float[] { x, y };
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable(STATE_PARENT, superState);
		state.putFloat(STATE_ANGLE, mAngle);

		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle savedState = (Bundle) state;

		Parcelable superState = savedState.getParcelable(STATE_PARENT);
		super.onRestoreInstanceState(superState);

		mAngle = savedState.getFloat(STATE_ANGLE);
		arc_finish_radians = calculateRadiansFromAngle(mAngle);
		text = String.valueOf(calculateTextFromAngle(arc_finish_radians));
		pointerPosition = calculatePointerPosition(mAngle);

	}

	public void setOnSeekBarChangedListener(OnCircleSeekBarChangedListener l) {
		mOnCircleSeekBarChangeListener = l;
	}

	public int getProgress() {
		int progress = Integer.parseInt(text);
		return progress;
	}

	public void setProgress(int progress) {
		text = String.valueOf(progress);
		int radians = progress * 360 / max;
		mAngle = calculateAngleFromRadians(radians);
		arc_finish_radians = calculateRadiansFromAngle(mAngle);
		pointerPosition = calculatePointerPosition(mAngle);
		// System.out.println("angle:" + mAngle + "  arc_finish_radians:"
		// + arc_finish_radians);
		if (mOnCircleSeekBarChangeListener != null) {
			mOnCircleSeekBarChangeListener.onProgressChanged(this, progress,
					true);
		}
		invalidate();
	}

	public void setMaxValue(int max) {
		this.max = max;
		invalidate();
	}

	public int getMaxValue(int max) {
		return max;
	}

	public void setMinValue(int min) {
		this.min = min;
		invalidate();
	}

	public int getMinValue() {
		return min;
	}

	public interface OnCircleSeekBarChangedListener {

		public abstract void onProgressChanged(CircleSeekBar seekBar,
				int progress, boolean fromUser);

	}

}
