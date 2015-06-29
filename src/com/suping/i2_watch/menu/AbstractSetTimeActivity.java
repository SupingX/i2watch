package com.suping.i2_watch.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.Formatter;
import android.widget.TextView;

import com.suping.i2_watch.R;

/**
 * 抽象类
 * 有2个NumberPicker和确定/取消的视图
 * @author Administrator
 *
 */
public abstract class AbstractSetTimeActivity extends Activity {
	//时刻
	public NumberPicker npHour;
	//分钟
	public NumberPicker npMin;
	//分钟的取值范围列表
	public String[] values;
	
	public TextView tvCancel;
	public TextView tvConfirm;
	//待发送的数据
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
		npHour = (NumberPicker) findViewById(R.id.np_min);
		npHour.setMaxValue(23);
		npHour.setMinValue(0);
//		npHour.setValue(0);
//		((EditText) (np_min.getChildAt(1))).setFocusable(false);
//		((EditText) (np_min.getChildAt(1))).setFocusableInTouchMode(false);
		//防止点击弹出软键盘
		npHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		
		
		npMin = (NumberPicker) findViewById(R.id.np_sec);
		values = new String[] { "00", "30" };
		npMin.setDisplayedValues(values);
		npMin.setMaxValue(values.length - 1);
		npMin.setMinValue(0);
//		npMin.setValue(0);
//		((EditText) (np_sec.getChildAt(1))).setFocusable(false);
//		((EditText) (np_sec.getChildAt(1))).setFocusableInTouchMode(false);
		//防止点击弹出软键盘
		npMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		
		initValue();
		
		tvCancel = (TextView) findViewById(R.id.tv_negative);
		tvConfirm = (TextView) findViewById(R.id.tv_positive);
	}

	private void setClick() {
		//退出
		tvCancel.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//确定
		tvConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int hour = npHour.getValue();
				int min = npMin.getValue();
				
				if(!checkTime()){
					Toast.makeText(AbstractSetTimeActivity.this, "时间设置有误！", Toast.LENGTH_LONG).show();
					return;
				}
				
				String minStr = hour < 10 ? "0" + hour : hour + "";
				String secStr = values[min];
				// Toast.makeText(getApplicationContext(), min+"|||||"+sec,
				// 0).show();
				b = new Bundle();
				b.putString("min", minStr);
				b.putString("sec", secStr);
				confirm();
			}
		});
		//格式化
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
		
	}
	
	/**
	 * 抽象方法
	 * 设置返回intent
	 */
	abstract public void confirm();
	/**
	 * 加载初始值
	 */
	abstract public void initValue();
	/**
	 * 检查时间设置是否正确
	 */
	abstract public boolean checkTime();
}
