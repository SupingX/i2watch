package com.suping.i2_watch.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.TextView;

import com.suping.i2_watch.R;

public class HeightActivity extends Activity {
	public NumberPicker numberPickerCm;
	public NumberPicker numberPickerFeet;
	public NumberPicker numberPickerInch;
	public NumberPicker numberPickerUnit;
	public LinearLayout linearLayoutFt;
	public String[] units;


	public TextView textViewCancel;
	public TextView textViewConfirm;
	public Bundle b;
	
	private static final int minHeightCm = 92;
	private static final int maxHeightCm = 242;
	
	private final static String CM = "cm";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_height);
		initViews();
		setClick();
		Bundle b = getIntent().getExtras();
		String unit = b.getString("unit");
		if(unit.equals(CM)){
			int cm = b.getInt(CM);
			numberPickerCm.setValue(cm);
			numberPickerUnit.setValue(0);
			numberPickerCm.setVisibility(View.VISIBLE);
			linearLayoutFt.setVisibility(View.GONE);
		}else if(unit.equals("ft")){
			int feet = b.getInt("feet");
			int inch = b.getInt("inch");
			numberPickerFeet.setValue(feet);
			numberPickerInch.setValue(inch);
			numberPickerUnit.setValue(1);
			numberPickerCm.setVisibility(View.GONE);
			linearLayoutFt.setVisibility(View.VISIBLE);
		}
		
	}

	private void initViews() {
		textViewCancel = (TextView) findViewById(R.id.tv_negative);
		textViewConfirm = (TextView) findViewById(R.id.tv_positive);
		linearLayoutFt = (LinearLayout) findViewById(R.id.ll_ft);
		
		//厘米
		numberPickerCm = (NumberPicker) findViewById(R.id.np_cm);
		numberPickerCm.setMaxValue(maxHeightCm);
		numberPickerCm.setMinValue(minHeightCm);
		numberPickerCm.setValue(175);
		numberPickerCm.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //设置不弹出软键盘
		
		//英尺
		numberPickerFeet = (NumberPicker) findViewById(R.id.np_feet);
		numberPickerFeet.setMaxValue(cmToFeet(maxHeightCm));
		numberPickerFeet.setMinValue(cmToFeet(minHeightCm));
		numberPickerFeet.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		numberPickerFeet.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				String tmp = String.valueOf(value);
				return tmp+"\'";
			}
		});
	
		//英寸
		numberPickerInch = (NumberPicker) findViewById(R.id.np_inch);
		numberPickerInch.setMaxValue(11);
		numberPickerInch.setMinValue(0);
		numberPickerInch.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); 
		numberPickerInch.setFormatter(new Formatter() {
			@Override
			public String format(int value) {
				String tmp = String.valueOf(value);
				return tmp+"\"";
			}
		});
		
		//单位
		numberPickerUnit = (NumberPicker) findViewById(R.id.np_unit);
		units = new String[] { CM, "ft" };
		numberPickerUnit.setDisplayedValues(units);
		numberPickerUnit.setMaxValue(units.length - 1);
		numberPickerUnit.setMinValue(0);
		numberPickerUnit.setValue(0);
		numberPickerUnit.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);	//防止点击弹出软键盘
		numberPickerUnit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				switch (newVal) {
				case 0:
					linearLayoutFt.setVisibility(View.GONE);
					numberPickerCm.setVisibility(View.VISIBLE);
					numberPickerCm.setValue(ftToCm(numberPickerFeet.getValue(), numberPickerInch.getValue()));
					break;
				case 1:
					linearLayoutFt.setVisibility(View.VISIBLE);
					numberPickerCm.setVisibility(View.GONE);
					numberPickerFeet.setValue(cmToFeet(numberPickerCm.getValue()));
					numberPickerInch.setValue(cmToInch(numberPickerCm.getValue()));
					break;

				default:
					break;
				}
			}
		});
		

	}

	

	private void setClick() {
		textViewCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		textViewConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int unit = numberPickerUnit.getValue();
				String unitStr = units[unit];
				switch (unit) {
				case 0:
					int cm = numberPickerCm.getValue();
					b = new Bundle();
					b.putInt(CM, cm);
					b.putString("unit", unitStr);
					Intent returnCm = new Intent(HeightActivity.this,InformationActivity.class);
					returnCm.putExtras(b);
					setResult(RESULT_OK, returnCm);
					break;
				case 1:
					int feet = numberPickerFeet.getValue();
					int inch = numberPickerInch.getValue();
					b = new Bundle();
					b.putInt("feet", feet);
					b.putInt("inch", inch);
					b.putString("unit", unitStr);
					Intent returnFt = new Intent(HeightActivity.this,InformationActivity.class);
					returnFt.putExtras(b);
					setResult(RESULT_OK, returnFt);
					break;
				default:
					break;
				}
				finish();
			}
		});
	}
	//1 英寸(inch)= 2.54 厘米(cm)   1 英尺(foot)= 12 英寸   1英尺=30.48厘米
	/**
	 * 厘米-->英尺
	 * @param cm
	 * @return
	 */
	private int cmToFeet(int cm){
		return (int) (cm / 30.48);
	}
	/**
	 * 厘米-->英寸
	 * @param cm
	 * @return
	 */
	private int cmToInch(int cm){
		int feet = cmToFeet(cm);
		return (int)(((cm-feet*30.48) / 2.54));
	}
	/**
	 * 英寸+英尺-->厘米
	 * @param foot
	 * @param inch
	 * @return
	 */
	private int ftToCm(int foot,int inch){
		return (int) Math.ceil(inch*2.54 + foot*30.48);
	}
}
