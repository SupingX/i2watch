package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.TextView;

import com.suping.i2_watch.R;

public class ReminderIntervalActivity extends Activity {
	
	private NumberPicker np;
	private TextView tv_cancel;
	private TextView tv_confirm;
	private String [] values;
	
	private final static int RESULT_INTERVAL = 11;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_reminder_interval);
		initViews();
		setClick();
		
	}


	private void initViews() {
		np = (NumberPicker) findViewById(R.id.np_interval);
		values = new String[]{"15","30","45","60","75","90","105","120"};
		np.setDisplayedValues(values);
		np.setMaxValue(values.length-1);
		np.setMinValue(0);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
//		((EditText)(np.getChildAt(0))).setFocusable(false);
//		((EditText)(np.getChildAt(0))).setFocusableInTouchMode(false);
		
		tv_cancel = (TextView) findViewById(R.id.tv_negative);
		tv_confirm = (TextView) findViewById(R.id.tv_positive);
	}


	private void setClick() {
		tv_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		tv_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String time = values[np.getValue()];
				Bundle b = new Bundle();
				b.putString("time", time);
				Intent intent = new Intent(ReminderIntervalActivity.this,ReminderActivity.class);
				intent.putExtras(b);
				setResult(RESULT_INTERVAL, intent);
				finish();
			}
		});
		
		np.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {
				
			}
		});
	}
}
