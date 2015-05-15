package com.suping.i2_watch.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.suping.i2_watch.R;

public class CleanEditText extends EditText implements OnFocusChangeListener,TextWatcher {

	private Drawable mClearDrawable;

	public CleanEditText(Context context) {
		super(context);
	}
	public CleanEditText(Context context,boolean isShhow) {
		super(context);
	}
	public CleanEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CleanEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources() 
                    .getDrawable(R.drawable.ic_close); 
        } 
        
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth()/2, mClearDrawable.getIntrinsicHeight()/2); 
        setClearIconVisible(false); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		 if (getCompoundDrawables()[2] != null) { 
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				boolean touchable = event.getX() > (getWidth()
						- getPaddingRight() - mClearDrawable
							.getIntrinsicWidth())
						&& (event.getX() < ((getWidth() - getPaddingRight())));
				if (touchable) {
					this.setText("");
				}
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		  if (hasFocus) { 
	            setClearIconVisible(getText().length() > 0); 
	        } else { 
	            setClearIconVisible(false); 
	        }
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		setClearIconVisible(text.length() > 0);
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
	
	public void setClearIconVisible(boolean visible) { 
			if (this.isEnabled()) {
				if(this.isFocused()){//防止有内容就显示X号 只有有焦点时才显示X号
					Drawable right = visible ? mClearDrawable : null;
					setCompoundDrawables(getCompoundDrawables()[0],
							getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
				}
			}
	    }
	
	
}
