package com.suping.i2_watch.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.TextView;
import com.suping.i2_watch.R;

public abstract class AbstractSetTimeActivity extends Activity {
	public NumberPicker np_min;
	public NumberPicker np_sec;
	public String[] values;
	public TextView tv_cancel;
	public TextView tv_confirm;
	public Bundle b;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_set_time);
		initViews();
		setClick();
	}

	private void initViews() {
		np_min = (NumberPicker) findViewById(R.id.np_min);
		np_min.setMaxValue(23);
		np_min.setMinValue(0);
		np_min.setValue(0);
//		((EditText) (np_min.getChildAt(1))).setFocusable(false);
//		((EditText) (np_min.getChildAt(1))).setFocusableInTouchMode(false);
		np_min.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		
		
		np_sec = (NumberPicker) findViewById(R.id.np_sec);
		values = new String[] { "00", "30" };
		np_sec.setDisplayedValues(values);
		np_sec.setMaxValue(values.length - 1);
		np_sec.setMinValue(0);
		np_sec.setValue(0);
//		((EditText) (np_sec.getChildAt(1))).setFocusable(false);
//		((EditText) (np_sec.getChildAt(1))).setFocusableInTouchMode(false);
		//防止点击弹出软键盘
		np_sec.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		
		tv_cancel = (TextView) findViewById(R.id.tv_negative);
		tv_confirm = (TextView) findViewById(R.id.tv_positive);
	}

	abstract public void confirm();
	

	private void setClick() {
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		np_min.setFormatter(new Formatter() {
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
				int min = np_min.getValue();
				int sec = np_sec.getValue();
				String minStr = min < 10 ? "0" + min : min + "";
				String secStr = values[sec];
				// Toast.makeText(getApplicationContext(), min+"|||||"+sec,
				// 0).show();
				b = new Bundle();
				b.putString("min", minStr);
				b.putString("sec", secStr);
				confirm();
			}
		});
		// np_sec.setFormatter(new Formatter() {
		// @Override
		// public String format(int value) {
		// switch (value) {
		// case 1:
		// return "30";
		// case 0:
		// return "00";
		// default:
		// break;
		// }
		// return "00";
		// }
		// });
	}
}
