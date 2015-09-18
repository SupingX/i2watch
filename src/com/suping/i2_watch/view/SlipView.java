package com.suping.i2_watch.view;


import com.suping.i2_watch.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * 模拟Android来电,滑动接听、挂断电话
 * 
 * @author Administrator
 * 
 */
public class SlipView extends View implements OnTouchListener {

	/** 左侧的可滑动图片 */
	private Bitmap left_bitmap;
	/** 右侧可滑动图片 */
	private Bitmap right_bitmap;
	/** 自定义SlipView的宽度 */
	private int view_width;
	/** 自定义SlipView的高度 */
	private int view_height;
	/** 左侧图片的宽度 */
	private int left_bitmap_width;
	/** 左侧图片的高度 */
	private int left_bitmap_height;
	/** 右侧 图片的宽度 */
	private int right_bitmap_width;
	/** 右侧图片的高度 */
	private int right_bitmap_height;
	private boolean left_slip, right_slip;
	private float nowLeftX, nowRightX;

	private Paint paint;
	private OnPhoneSlipListener mOnPhoneSlipListener;
	public static final String SLIP_LEFT = "LEFT";
	public static final String SLIP_RIGHT = "RIGHT";
	private Paint paintRect;

	public SlipView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}

	public SlipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public SlipView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		
		//120,144,70
		//172,68,84
		
		left_bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.jog_tab_left_confirm_green);
		right_bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.jog_tab_right_confirm_red);

		left_bitmap_width = left_bitmap.getWidth();
		left_bitmap_height = left_bitmap.getHeight();
		right_bitmap_width = right_bitmap.getWidth();
		right_bitmap_height = right_bitmap.getHeight();

		paint = new Paint();
		paint.setAntiAlias(true);
//		paint.setColor(Color.parseColor("#96c0c0c0"));
		paint.setColor(Color.BLACK);
		
		paintRect = new Paint();
		paintRect.setAntiAlias(true);
//		paint.setColor(Color.parseColor("#96c0c0c0"));
	
		setOnTouchListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		view_width = getMeasuredWidth();
		view_height = getMeasuredHeight();
		LayoutParams params = getLayoutParams();
		if (params.width == -1) {
			// MATCH_PARENT
			view_width = getMeasuredWidth();
		} else if (params.width == -2) {
			// WRAP_CONTENT
			view_width = getMeasuredWidth();
		} else {
			view_width = params.width;
		}
		if (params.height == -1) {
			// MATCH_PARENT
			view_height = getMeasuredHeight();
		} else if (params.height == -2) {
			// WRAP_CONTENT
			view_height = left_bitmap_height;
		} else {
			view_height = params.height;
		}

		setMeasuredDimension(view_width, view_height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (right_slip) {
			canvas.drawBitmap(right_bitmap, nowRightX < view_width
					- right_bitmap_width ? nowRightX : view_width
					- right_bitmap_width, 0, paint);
			LinearGradient  linearGradient = new LinearGradient
					(nowRightX + right_bitmap_width, 0, right_bitmap_width, right_bitmap_height,Color.argb(155,172,68,84), Color.argb( 0,172, 68, 84), Shader.TileMode.CLAMP);
			paintRect.setShader(linearGradient);
			canvas.drawRect(nowRightX + right_bitmap_width, 0, view_width,
					right_bitmap_height-2, paintRect);
			
		} else {
			canvas.drawBitmap(right_bitmap, view_width - right_bitmap_width, 0,
					paint);
		}
		if (left_slip) {
			canvas.drawBitmap(left_bitmap,
					(nowLeftX > left_bitmap_width ? nowLeftX
							- left_bitmap_width : 0), 0, paint);
			LinearGradient  linearGradient = new LinearGradient
					(nowRightX + right_bitmap_width, 0, right_bitmap_width, right_bitmap_height, Color.argb( 0,120,144,70), Color.argb(155,120,144,70),Shader.TileMode.CLAMP);
			paintRect.setShader(linearGradient);
			canvas.drawRect(0, 0, nowLeftX - left_bitmap_width,
					left_bitmap_height-2, paintRect);
		} else {
			canvas.drawBitmap(left_bitmap, 0, 0, paint);
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (event.getX() > left_bitmap_width
					&& event.getX() < view_width - right_bitmap_width
					|| event.getY() > view_height
					|| event.getY() > left_bitmap_height) {
				return false;
			} else {
				if (event.getX() < left_bitmap_width) {
					left_slip = true;
					nowLeftX = event.getX();
				} else if (event.getX() > view_width - right_bitmap_width) {
					right_slip = true;
					nowRightX = event.getX();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (left_slip) {
				nowLeftX = event.getX();
			}
			if (right_slip) {
				nowRightX = event.getX();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (left_slip) {
				nowLeftX = event.getX();
				if (mOnPhoneSlipListener != null) {
					mOnPhoneSlipListener.onPhoneSliped(SLIP_LEFT, nowLeftX);
				}
				left_slip = false;
				nowLeftX = 0;
			}
			if (right_slip) {
				nowRightX = event.getX();
				if (mOnPhoneSlipListener != null) {
					mOnPhoneSlipListener.onPhoneSliped(SLIP_RIGHT, nowRightX);
				}
				right_slip = false;
				nowRightX = view_width;
			}
			break;

		default:
			break;
		}
		invalidate();
		return true;
	}

	public void setOnPhoneSlipListener(OnPhoneSlipListener l) {
		this.mOnPhoneSlipListener = l;
	}

	public interface OnPhoneSlipListener {
		/**
		 * 接听、挂断按钮滑动事件监听
		 * 
		 * @param type
		 *            当前滑动的是左边接听还是右边挂断
		 * @param x
		 *            当前位置的X坐标
		 */
		public abstract void onPhoneSliped(String type, float x);
	}

}
