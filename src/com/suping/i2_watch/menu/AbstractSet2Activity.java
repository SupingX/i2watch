package com.suping.i2_watch.menu;

import com.suping.i2_watch.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker.Formatter;

public abstract class AbstractSet2Activity extends Activity {

	public NumberPicker npHour;
	public NumberPicker npMin;
	
	public TextView tv_cancel;
	public TextView tv_confirm;
	public Bundle b;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_abstract_set2);
		initViews();
		setClick();
	}

	private void initViews() {
		npHour = (NumberPicker) findViewById(R.id.np_min);
		npHour.setMaxValue(23);
		npHour.setMinValue(0);
//		np_min.setValue(0);
		//防止点击弹出软键盘
		npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		
		
		npMin = (NumberPicker) findViewById(R.id.np_sec);
		npMin.setMaxValue(59);
		npMin.setMinValue(0);
//		np_sec.setValue(0);
		//防止点击弹出软键盘
		npMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		
		tv_cancel = (TextView) findViewById(R.id.tv_negative);
		tv_confirm = (TextView) findViewById(R.id.tv_positive);
		
		initValue();
	}

	abstract public void confirm();
	/**
	 * 加载初始值
	 */
	abstract public void initValue();
	

	private void setClick() {
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		npHour.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				String tmp = String.valueOf(value);
				if (value < 10) {
					tmp = "0" + tmp;
				}
				return tmp;
			}
		});
		npMin.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				String tmp = String.valueOf(value);
				if (value < 10) {
					tmp = "0" + tmp;
				}
				return tmp;
			}
		});
		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int min = npHour.getValue();
				int sec = npMin.getValue();
				String minStr = min < 10 ? "0" + min : min + "";
				String secStr = sec < 10 ? "0" + sec : sec + "" ;
//				if(!checkTime()){
//					Toast.makeText(AbstractSet2Activity.this, "时间设置有误！", Toast.LENGTH_LONG).show();
//					return;
//				}
				// Toast.makeText(getApplicationContext(), min+"|||||"+sec,
				// 0).show();
				b = new Bundle();
				b.putString("min", minStr);
				b.putString("sec", secStr);
				confirm();
			}
		});
	}
	/**
	 * 检查时间设置是否正确
	 */
	abstract public boolean checkTime();
}
