package com.suping.i2_watch.setting;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.Formatter;
import android.widget.TextView;

import com.suping.i2_watch.R;

public class BirthdayActivity extends Activity {
	private TextView textViewCancel;
	private TextView textViewConfirm;
	private NumberPicker numberPickerYear;
	private NumberPicker numberPickerMonth;
	private NumberPicker numberPickerDay;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_birthday);
		
		initViews();
		setListener();
		
		Bundle b = getIntent().getExtras();
		int year = b.getInt("year");
		int month = b.getInt("month");
		int day = b.getInt("day");
		numberPickerYear.setValue(year);
		numberPickerMonth.setValue(month);
		numberPickerDay.setValue(day);
	}

	private void initViews() {
		textViewCancel = (TextView) findViewById(R.id.tv_negative);
		textViewConfirm = (TextView) findViewById(R.id.tv_positive);
		
		
		numberPickerYear = (NumberPicker) findViewById(R.id.np_year);
		numberPickerMonth = (NumberPicker) findViewById(R.id.np_month);
		numberPickerDay = (NumberPicker) findViewById(R.id.np_day);
		
		//年
		Calendar calendar = Calendar.getInstance();
		numberPickerYear.setMaxValue(calendar.get(Calendar.YEAR));
		numberPickerYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //设置不弹出软键盘
		numberPickerYear.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				return String.valueOf(value)+"年";
			}
		});
		numberPickerYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				int month = numberPickerMonth.getValue();
				int day = numberPickerDay.getValue();
				if(month==2){
					if(newVal % 4 == 0 && newVal % 100 != 0 || newVal % 400 == 0){//闰年
						numberPickerDay.setMaxValue(29);
						if(day>=29){
							numberPickerDay.setValue(29);
						}
					}else{
						numberPickerDay.setMaxValue(28);
						if(day>=28){
							numberPickerDay.setValue(28);
						}
					}
				}
			}	
		});
		
		//月
		numberPickerMonth.setMaxValue(12);
		numberPickerMonth.setMinValue(1);
		numberPickerMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //设置不弹出软键盘
		numberPickerMonth.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				return String.valueOf(value)+"月";
			}
		});
		numberPickerMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				int day = numberPickerDay.getValue();
				switch (newVal) {
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
					numberPickerDay.setMaxValue(31);
					if(day>=31){
						numberPickerDay.setValue(31);
					}
					break;
				case 4:
				case 6:
				case 9:
				case 11:
					numberPickerDay.setMaxValue(30);
					if(day>=30){
						numberPickerDay.setValue(30);
					}
					break;
				case 2:
					int year = numberPickerYear.getValue();
					if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0){//闰年
						numberPickerDay.setMaxValue(29);
						if(day>=29){
							numberPickerDay.setValue(29);
						}
					}else{
						numberPickerDay.setMaxValue(28);
						if(day>=28){
							numberPickerDay.setValue(28);
						}
					}
				default:
					break;
				}
			}
		});
		
		//日
		numberPickerDay.setMaxValue(30);
		numberPickerDay.setMinValue(1);
		numberPickerDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //设置不弹出软键盘
		numberPickerDay.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				return String.valueOf(value)+"日";
			}
		});
		
	}

	private void setListener() {
		
	
		textViewCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		textViewConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int year = numberPickerYear.getValue();
				int month = numberPickerMonth.getValue();
				int day = numberPickerDay.getValue();
				Bundle data = new Bundle();
				data.putInt("year", year);
				data.putInt("month", month);
				data.putInt("day", day);
				Intent returnBirthday  =new Intent(BirthdayActivity.this,InformationActivity.class);
				returnBirthday.putExtras(data);
				setResult(RESULT_OK, returnBirthday);
				finish();
			}
		});
		
		
		
	
	}
	
}
