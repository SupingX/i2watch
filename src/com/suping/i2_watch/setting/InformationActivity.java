package com.suping.i2_watch.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;
import com.suping.i2_watch.view.CleanEditText;

public class InformationActivity extends Activity implements OnClickListener {
	public final static int REQ_HEIGHT = 12221121;
	public final static int REQ_WEIGHT = 1222111121;
	public final static int REQ_BIRTHDAY = 3313131;

	/** 存贮地址 **/
	public final static String SHARE_PERSON_NAME = "share_personal_name";
	public final static String SHARE_ISMALE = "share_is_male";
	public final static String SHARE_HEIGHT_UNIT = "share_height_unit";
	public final static String SHARE_HEIGHT_VALUE_CM = "share_height_value_cm";
	public final static String SHARE_HEIGHT_VALUE_FEET = "share_height_value_feet";
	public final static String SHARE_HEIGHT_VALUE_INCH = "share_height_value_inch";
	public final static String SHARE_WEIGHT_UNIT = "share_weight_unit";
	public final static String SHARE_WEIGHT_VALUE_KG = "share_weight_value_kg";
	public final static String SHARE_WEIGHT_VALUE_LB = "share_weight_value_lb";
	public final static String SHARE_BIRTHDAY_YEAR = "share_birthday_year";
	public final static String SHARE_BIRTHDAY_MONTH = "share_birthday_month";
	public final static String SHARE_BIRTHDAY_DAY = "share_birthday_day";

	public final static String CM = "cm";
	public final static String FT = "ft";
	public final static String KG = "kg";
	public final static String LB = "lb";

	private ImageView imgBack, imgMale, imgFemale;
	private TextView textViewTitle, textViewHeightValue, textViewHeightUnit, textViewWeightValue, textViewWeightUnit,
			textViewBirthdayValue;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		initViews();
		setClick();
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
						SharedPreferenceUtil.put(getApplicationContext(), SHARE_HEIGHT_UNIT, heightUnit);
						SharedPreferenceUtil.put(getApplicationContext(), SHARE_HEIGHT_VALUE_CM, cm);
					} else if (heightUnit.equals(FT)) {
						feet = b.getInt("feet");
						inch = b.getInt("inch");
						textViewHeightValue.setText(String.valueOf(feet) + "\"" + String.valueOf(inch) + "\'");
						SharedPreferenceUtil.put(getApplicationContext(), SHARE_HEIGHT_UNIT, heightUnit);
						SharedPreferenceUtil.put(getApplicationContext(), SHARE_HEIGHT_VALUE_FEET, feet);
						SharedPreferenceUtil.put(getApplicationContext(), SHARE_HEIGHT_VALUE_INCH, inch);
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
				if (weightUnit.equals(KG)) {
					SharedPreferenceUtil.put(getApplicationContext(), SHARE_WEIGHT_VALUE_KG, weightValue);
				} else if (weightUnit.equals(LB)) {
					SharedPreferenceUtil.put(getApplicationContext(), SHARE_WEIGHT_VALUE_LB, weightValue);
				}
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_WEIGHT_UNIT, weightUnit);
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
				textViewBirthdayValue.setText(year + "-" + (month < 10 ? ("0" + month) : month) + "-"
						+ (day < 10 ? ("0" + day) : day));
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_BIRTHDAY_YEAR, year);
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_BIRTHDAY_MONTH, month);
				SharedPreferenceUtil.put(getApplicationContext(), SHARE_BIRTHDAY_DAY, day);
				break;

			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent returnSetting = new Intent(InformationActivity.this, SettingActivity.class);
			startActivity(returnSetting);
			this.finish();
			overridePendingTransition(
			// 左右是指手机屏幕的左右
					R.anim.activity_from_left_to_right_enter, R.anim.activity_from_left_to_right_exit);
			break;
		case R.id.img_male:
			isMale = true;
			validateGender();
			enableGender();
			SharedPreferenceUtil.put(getApplicationContext(), SHARE_ISMALE, isMale);
			break;
		case R.id.img_female:
			isMale = false;
			validateGender();
			enableGender();
			SharedPreferenceUtil.put(getApplicationContext(), SHARE_ISMALE, isMale);
			break;
		case R.id.rl_height:
			Intent heightIntent = new Intent(InformationActivity.this, HeightActivity.class);
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
			Intent weightIntent = new Intent(InformationActivity.this, WeightActivity.class);
			startActivityForResult(weightIntent, REQ_WEIGHT);
			break;

		case R.id.rl_birthday:
			Intent birthdayIntent = new Intent(InformationActivity.this, BirthdayActivity.class);
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

	private void initViews() {

		// 初始化一些wedgetS
		imgBack = (ImageView) findViewById(R.id.img_back);
		imgMale = (ImageView) findViewById(R.id.img_male);
		imgFemale = (ImageView) findViewById(R.id.img_female);
		relaviteHeight = (RelativeLayout) findViewById(R.id.rl_height);
		relaviteWeight = (RelativeLayout) findViewById(R.id.rl_weight);
		relaviteBirthday = (RelativeLayout) findViewById(R.id.rl_birthday);

		textViewTitle = (TextView) findViewById(R.id.tv_title);
		editTextName = (CleanEditText) findViewById(R.id.ed_name);
		textViewHeightValue = (TextView) findViewById(R.id.tv_height_value);
		textViewHeightUnit = (TextView) findViewById(R.id.tv_height_unit);
		textViewWeightValue = (TextView) findViewById(R.id.tv_weight_value);
		textViewWeightUnit = (TextView) findViewById(R.id.tv_weight_unit);
		textViewBirthdayValue = (TextView) findViewById(R.id.tv_birhtday_value);
		// 初始化标题
		textViewTitle.setText("Personal info");
		// 初始化姓名
		initName();
		// 初始化性别
		initSex();
		// 初始化身高
		initHeight();
		// 初始化体重
		initWeight();
		// 初始化生日
		initBirthday();
	}

	private void initName() {
		String name = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_PERSON_NAME, "");
		editTextName.setText(name);
		editTextName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				saveNameAndUpdate();
				return false;
			}
		});
		
	}

	private void saveNameAndUpdate() {
		String name = editTextName.getText().toString().trim();
		SharedPreferenceUtil.put(getApplicationContext(), SHARE_PERSON_NAME, name);
		editTextName.setClearIconVisible(false);
		editTextName.clearFocus();// 清除焦点
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void initSex() {
		isMale = (boolean) SharedPreferenceUtil.get(getApplicationContext(), SHARE_ISMALE, true);
		Log.d("OB", "isMale:" + isMale);
		validateGender();
		enableGender();
	}

	private void initHeight() {
		heightUnit = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_HEIGHT_UNIT, CM);
		textViewHeightUnit.setText(heightUnit);
		if (heightUnit.equals(CM)) {
			cm = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_HEIGHT_VALUE_CM, 170);
			textViewHeightValue.setText(String.valueOf(cm));
		} else if (heightUnit.equals(FT)) {
			feet = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_HEIGHT_VALUE_FEET, 0);
			inch = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_HEIGHT_VALUE_INCH, 0);
			textViewHeightValue.setText(String.valueOf(feet) + "\"" + String.valueOf(inch) + "\'");
		}

	}

	private void initWeight() {
		weightUnit = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_WEIGHT_UNIT, KG);
		textViewWeightUnit.setText(weightUnit);

		if (weightUnit.equals(KG)) {
			weightValue = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_WEIGHT_VALUE_KG, 170);
			textViewWeightValue.setText(String.valueOf(weightValue));
		} else if (weightUnit.equals(LB)) {
			weightValue = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_WEIGHT_VALUE_LB, 25);
			textViewWeightValue.setText(String.valueOf(weightValue));
		}
	}

	private void initBirthday() {
		year = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_BIRTHDAY_YEAR, 2000);
		month = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_BIRTHDAY_MONTH, 1);
		day = (int) SharedPreferenceUtil.get(getApplicationContext(), SHARE_BIRTHDAY_DAY, 1);
		textViewBirthdayValue.setText(year + "-" + (month < 10 ? ("0" + month) : month) + "-"
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

	private boolean isCm(String unit) {
		return unit.equals(CM);
	}

	private boolean isFt(String unit) {
		return unit.equals(FT);
	}

	/**
	 * 当是 男 时：男的图片亮色 /女的图片灰色 当非 男 时：男的图片灰色 /女的图片亮色
	 */
	private void validateGender() {
		if (isMale) {
			imgMale.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));
			imgFemale.setImageDrawable(getResources().getDrawable(R.drawable.ic_female_unless));
		} else {
			imgMale.setImageDrawable(getResources().getDrawable(R.drawable.ic_male_unless));
			imgFemale.setImageDrawable(getResources().getDrawable(R.drawable.ic_female));
		}
	}

	/**
	 * 当是 男 时：男不能点击，女能点击； 当非 男 时：男能点，不不能点；
	 */
	private void enableGender() {

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
