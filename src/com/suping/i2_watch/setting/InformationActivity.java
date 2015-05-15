package com.suping.i2_watch.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.suping.i2_watch.view.CleanEditText;

public class InformationActivity extends Activity implements OnClickListener {
	private ImageView imgBack, imgMale, imgFemale;
	private TextView textViewTitle, textViewHeightValue, textViewHeightUnit,
			textViewWeightValue, textViewWeightUnit,textViewBirthdayValue;
	private CleanEditText editTextName;
	private RelativeLayout relaviteHeight, relaviteWeight, relaviteBirthday;
	private boolean isMale;
	private int cm;
	private int feet;
	private int inch;
	private int weightValue;
	private String heightUnit;
	private String weightUnit;
	private int year;
	private int month;
	private int day;

	private final static int REQ_HEIGHT = 12221121;
	private final static int REQ_WEIGHT = 1222111121;
	private final static int REQ_BIRTHDAY = 3313131;
	private final static String CM = "cm";
	private final static String FT = "ft";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		initViews();
		setClick();
	}

	private void initViews() {

		// 初始化一些wedgets
		imgBack = (ImageView) findViewById(R.id.img_back);
		imgMale = (ImageView) findViewById(R.id.img_male);
		imgFemale = (ImageView) findViewById(R.id.img_female);
		relaviteHeight = (RelativeLayout) findViewById(R.id.rl_height);
		relaviteWeight = (RelativeLayout) findViewById(R.id.rl_weight);
		relaviteBirthday = (RelativeLayout) findViewById(R.id.rl_birthday);

		// 初始化标题
		textViewTitle = (TextView) findViewById(R.id.tv_title);
		textViewTitle.setText("Personal info");

		// 初始化姓名
		editTextName = (CleanEditText) findViewById(R.id.ed_name);
		String name = (String) SharedPreferenceUtil.get(
				getApplicationContext(), "personal_name", "");
		editTextName.setText(name);
		editTextName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				String name = editTextName.getText().toString().trim();
				SharedPreferenceUtil.put(getApplicationContext(),
						"personal_name", name);
				editTextName.setClearIconVisible(false);
				editTextName.clearFocus();// 清除焦点
				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
							0);
				}
				return false;
			}
		});

		// 初始化性别
		isMale = (boolean) SharedPreferenceUtil.get(getApplicationContext(),
				"isMale", true);
		Log.d("OB", "isMale:" + isMale);
		validateGender();
		enableGender();

		// 初始化身高
		textViewHeightValue = (TextView) findViewById(R.id.tv_height_value);
		textViewHeightUnit = (TextView) findViewById(R.id.tv_height_unit);
		heightUnit = (String) SharedPreferenceUtil.get(getApplicationContext(),
				"height_unit", CM);
		textViewHeightUnit.setText(heightUnit);
		if (heightUnit.equals(CM)) {
			cm = (int) SharedPreferenceUtil.get(getApplicationContext(),
					"height_cm", 170);
			textViewHeightValue.setText(String.valueOf(cm));
		} else if (heightUnit.equals(FT)) {
			feet = (int) SharedPreferenceUtil.get(getApplicationContext(),
					"height_feet", 0);
			inch = (int) SharedPreferenceUtil.get(getApplicationContext(),
					"height_inch", 0);
			textViewHeightValue.setText(String.valueOf(feet) + "\""
					+ String.valueOf(inch) + "\'");
		}

		// 初始化体重
		textViewWeightValue = (TextView) findViewById(R.id.tv_weight_value);
		textViewWeightUnit = (TextView) findViewById(R.id.tv_weight_unit);
		weightUnit = (String) SharedPreferenceUtil.get(getApplicationContext(),
				"weight_unit", "kg");
		textViewWeightUnit.setText(weightUnit);
		weightValue = (int) SharedPreferenceUtil.get(getApplicationContext(),
				"weight_value", 170);
		textViewWeightValue.setText(String.valueOf(weightValue));
		
		//初始化生日
		textViewBirthdayValue = (TextView) findViewById(R.id.tv_birhtday_value);
		year = (int) SharedPreferenceUtil.get(getApplicationContext(), "year", 2000);
		month = (int) SharedPreferenceUtil.get(getApplicationContext(), "month", 1);
		day = (int) SharedPreferenceUtil.get(getApplicationContext(), "day", 1);
		textViewBirthdayValue.setText(year + "-" 
						+ (month < 10 ? ("0" + month) : month) + "-" 
						+ (day < 10 ? ("0" + day) : day));
	}
	
	private void setClick() {
		imgBack.setOnClickListener(this);
		imgMale.setOnClickListener(this);
		imgFemale.setOnClickListener(this);
		relaviteHeight.setOnClickListener(this);
		relaviteWeight.setOnClickListener(this);
		relaviteBirthday.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent returnSetting = new Intent(InformationActivity.this,
					SettingActivity.class);
			startActivity(returnSetting);
			this.finish();
			overridePendingTransition(
					// 左右是指手机屏幕的左右
					R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
			break;
		case R.id.img_male:
			isMale = true;
			validateGender();
			enableGender();
			SharedPreferenceUtil.put(getApplicationContext(), "isMale", isMale);
			break;
		case R.id.img_female:
			isMale = false;
			validateGender();
			enableGender();
			SharedPreferenceUtil.put(getApplicationContext(), "isMale", isMale);
			break;
		case R.id.rl_height:
			Intent heightIntent = new Intent(InformationActivity.this,
					HeightActivity.class);
			Bundle heightData = new Bundle();
			heightData.putString("unit", heightUnit);
			if (isCm(heightUnit)) {
				heightData.putInt(CM, cm);
			} else if (isFt(heightUnit)) {
				heightData.putInt("feet", feet);
				heightData.putInt("inch", inch);
			}
			heightIntent.putExtras(heightData);
			startActivityForResult(heightIntent, REQ_HEIGHT);
			break;

		case R.id.rl_weight:
			Intent weightIntent = new Intent(InformationActivity.this,
					WeightActivity.class);
			Bundle weightData = new Bundle();
			weightData.putString("unit", weightUnit);
			weightData.putInt("value", weightValue);
			weightIntent.putExtras(weightData);
			startActivityForResult(weightIntent, REQ_WEIGHT);
			break;
			
		case R.id.rl_birthday:
			Intent birthdayIntent = new Intent(InformationActivity.this,
					BirthdayActivity.class);
			Bundle birthdayData = new Bundle();
			birthdayData.putInt("year", year);
			birthdayData.putInt("month", month);
			birthdayData.putInt("day", day);
			birthdayIntent.putExtras(birthdayData);
			startActivityForResult(birthdayIntent, REQ_BIRTHDAY);
			
			break;
		default:
			break;
		}
	}

	private boolean isCm(String unit) {
		return unit.equals(CM);
	}

	private boolean isFt(String unit) {
		return unit.equals(FT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_HEIGHT:
			switch (resultCode) {
			case RESULT_OK:
				Bundle b = data.getExtras();
				heightUnit = b.getString("unit");
				textViewHeightUnit.setText(heightUnit);
				if (null != heightUnit) {
					if (heightUnit.equals(CM)) {
						cm = b.getInt(CM);
						textViewHeightValue.setText(String.valueOf(cm));
						SharedPreferenceUtil.put(getApplicationContext(),
								"height_unit", heightUnit);
						SharedPreferenceUtil.put(getApplicationContext(),
								"height_cm", cm);
					} else if (heightUnit.equals(FT)) {
						feet = b.getInt("feet");
						inch = b.getInt("inch");
						textViewHeightValue.setText(String.valueOf(feet) + "\""
								+ String.valueOf(inch) + "\'");
						SharedPreferenceUtil.put(getApplicationContext(),
								"height_unit", heightUnit);
						SharedPreferenceUtil.put(getApplicationContext(),
								"height_feet", feet);
						SharedPreferenceUtil.put(getApplicationContext(),
								"height_inch", inch);
					}
				}
				break;

			default:
				break;
			}
			break;
		case REQ_WEIGHT:
			switch (resultCode) {
			case RESULT_OK:
				Bundle weightData = data.getExtras();
				weightUnit = weightData.getString("weightUnit");
				textViewWeightUnit.setText(weightUnit);
				weightValue = weightData.getInt("weightValue");
				textViewWeightValue.setText(String.valueOf(weightValue));
				SharedPreferenceUtil.put(getApplicationContext(),
						"weight_value", weightValue);
				SharedPreferenceUtil.put(getApplicationContext(),
						"weight_unit", weightUnit);
				break;
			default:
				break;
			}
			break;
		case REQ_BIRTHDAY:
			switch (resultCode) {
			case RESULT_OK:
				Bundle brithdayData = data.getExtras();
				 year = brithdayData.getInt("year");
				 month = brithdayData.getInt("month");
				 day = brithdayData.getInt("day");
				textViewBirthdayValue.setText(year + "-" 
						+ (month < 10 ? ("0" + month) : month) + "-" 
						+ (day < 10 ? ("0" + day) : day));
				SharedPreferenceUtil.put(getApplicationContext(),
						"year", year);
				SharedPreferenceUtil.put(getApplicationContext(),
						"month", month);
				SharedPreferenceUtil.put(getApplicationContext(),
						"day", day);
				break;

			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 当是 男 时：男的图片亮色 /女的图片灰色 当非 男 时：男的图片灰色 /女的图片亮色
	 */
	public void validateGender() {
		if (isMale) {
			imgMale.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_male));
			imgFemale.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_female_unless));
		} else {
			imgMale.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_male_unless));
			imgFemale.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_female));
		}
	}

	/**
	 * 当是 男 时：男不能点击，女能点击； 当非 男 时：男能点，不不能点；
	 */
	public void enableGender() {

		if (!isMale) {
			imgMale.setFocusable(true);
			imgMale.setClickable(true);
			imgFemale.setFocusable(false);
			imgFemale.setClickable(false);
		} else {
			imgMale.setFocusable(false);
			imgMale.setClickable(false);
			imgFemale.setFocusable(true);
			imgFemale.setClickable(true);
		}
	}
}
