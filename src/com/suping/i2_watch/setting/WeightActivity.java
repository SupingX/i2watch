package com.suping.i2_watch.setting;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.NumberPicker;
import android.widget.TextView;

public class WeightActivity extends Activity {
	private static final int minWeight = 32;
	private static final int maxWeight = 255;
	
	private NumberPicker numberPickerValue;
	private NumberPicker numberPickerUnit;
	private String[] units;

	private TextView textViewCancel;
	private TextView textViewConfirm;
	private Bundle b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_weight);
		initViews();
		setClick();
	}

	private void initViews() {
		textViewCancel = (TextView) findViewById(R.id.tv_negative);
		textViewConfirm = (TextView) findViewById(R.id.tv_positive);
		
		//值
		numberPickerValue = (NumberPicker) findViewById(R.id.np_value);
		numberPickerValue.setMaxValue(maxWeight);
		numberPickerValue.setMinValue(minWeight);
		numberPickerValue.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //设置不弹出软键盘
		
		//单位
		numberPickerUnit = (NumberPicker) findViewById(R.id.np_unit);
		units = new String[] { "kg", "lb" };
		numberPickerUnit.setDisplayedValues(units);
		numberPickerUnit.setMaxValue(units.length - 1);
		numberPickerUnit.setMinValue(0);
//		numberPickerUnit.setValue(0);
		numberPickerUnit.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);	//防止点击弹出软键盘
		numberPickerUnit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				switch (newVal) {
				case 0:
					numberPickerValue.setValue(lbTokg((numberPickerValue.getValue())));
					break;
				case 1:
					numberPickerValue.setValue(kgTolb((numberPickerValue.getValue())));
					break;

				default:
					break;
				}
			}
		});
		initValue();

	}

	private void initValue(){
		String unit = (String) SharedPreferenceUtil.get(WeightActivity.this, InformationActivity.SHARE_WEIGHT_UNIT, "kg");
		int value = 0;
		if(unit.equals(InformationActivity.KG)){
			numberPickerUnit.setValue(0);
			value = (int) SharedPreferenceUtil.get(WeightActivity.this, InformationActivity.SHARE_WEIGHT_VALUE_KG, 170);
		} else if (unit.equals(InformationActivity.LB)){
			value = (int) SharedPreferenceUtil.get(WeightActivity.this, InformationActivity.SHARE_WEIGHT_VALUE_LB, 25);
			numberPickerUnit.setValue(1);
		}
		numberPickerValue.setValue(value);
		Log.e("[体重", "unit : " + unit + ",value : " + value + "]");
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
					int kg = numberPickerValue.getValue();
					b = new Bundle();
					b.putInt("weightValue", kg);
					b.putString("weightUnit", unitStr);
					Intent returnKg = new Intent(WeightActivity.this,InformationActivity.class);
					returnKg.putExtras(b);
					setResult(RESULT_OK, returnKg);
					break;
				case 1:
					int lb = numberPickerValue.getValue();
					b = new Bundle();
					b.putInt("weightValue", lb);
					b.putString("weightUnit", unitStr);
					Intent returnLb = new Intent(WeightActivity.this,InformationActivity.class);
					returnLb.putExtras(b);
					setResult(RESULT_OK, returnLb);
					break;
				default:
					break;
				}
				finish();
			}
		});
	}
	
	//1磅 = 0.4535924 千克
	/**
	 * 千克-->磅
	 * @param cm
	 * @return
	 */
	private int kgTolb(int kg){
		return (int) Math.ceil(kg / 0.4536);
	}
	/**
	 * 磅-->千克
	 * @param cm
	 * @return
	 */
	private int lbTokg(int lb){
		return  (int) (lb*0.4536);
	}
}
