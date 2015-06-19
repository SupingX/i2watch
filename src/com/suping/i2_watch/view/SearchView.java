package com.suping.i2_watch.view;

import com.suping.i2_watch.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class SearchView extends View {

	private Bitmap backBp;
	private Bitmap romationBp;
	private ImageView imgBack;
	private ImageView imgRomation;
	private Animation operatingAnim;

	public SearchView(Context context) {
		super(context);
		backBp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_bg);
		romationBp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_search_rotation);
		operatingAnim = AnimationUtils.loadAnimation(context, R.anim.retate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		this.startAnimation(operatingAnim);
		
		
	}

	/**
	 * 测量尺寸时的回调方法
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 设置当前view的大小 width:view的宽，单位都是像素值 heigth:view的高，单位都是像素值
		setMeasuredDimension(backBp.getWidth(), backBp.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		Paint paint = new Paint();
		// 打开抗锯齿
		paint.setAntiAlias(true);
		// 画背景
		canvas.drawBitmap(backBp, 0, 0, paint);
		// 画滑块
		canvas.drawBitmap(romationBp, 0, 0, paint);
		super.onDraw(canvas);
	}

}
