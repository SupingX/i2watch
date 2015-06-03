package com.suping.i2_watch.menu;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class ClockActivity extends Activity implements OnClickListener{
	//请求/结果状态 
	private static final int REQ_1 = 0x01;
	private static final int REQ_2 = 0x02;
	private static final int REQ_3 = 0x03;
	private static final int RESULT_1 = 0x11;
	private static final int RESULT_2 = 0x21;
	private static final int RESULT_3 = 0x31;
	//存贮地址 星期
	private final static String SETTIME_1 = "clockSet_1";
	private final static String SETTIME_2 = "clockSet_2";
	private final static String SETTIME_3 = "clockSet_3";
	//check
	private final static String CHECKBOX_1 = "clock_cb_item_1";
	private final static String CHECKBOX_2 = "clock_cb_item_2";
	private final static String CHECKBOX_3 = "clock_cb_item_3";
	
	private ImageView textViewImgBack;
	private TextView textViewTitle;
	//闹钟设置
	private TextView textViewSet_1;
	private TextView textViewSet_2;
	private TextView textViewSet_3;
	//周日-周一
	private TextView textViewMon_1;
	private TextView textViewTue_1;
	private TextView textViewWen_1;
	private TextView textViewThu_1;
	private TextView textViewSat_1;
	private TextView textViewFri_1;
	private TextView textViewSun_1;
	private TextView textViewMon_3;
	private TextView textViewTue_3;
	private TextView textViewWen_3;
	private TextView textViewThu_3;
	private TextView textViewSat_3;
	private TextView textViewFri_3;
	private TextView textViewSun_3;
	private TextView textViewMon_2;
	private TextView textViewTue_2;
	private TextView textViewWen_2;
	private TextView textViewThu_2;
	private TextView textViewSat_2;
	private TextView textViewFri_2;
	private TextView textViewSun_2;
	//开关
	private CheckBox checkBoxItem_1;
	private CheckBox checkBoxItem_2;
	private CheckBox checkBoxItem_3;
	private int clockValue_1;
	private int clockValue_2;
	private int clockValue_3;

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock);
		initViews();
		setClick();
		textViewTitle.setText("Clock");
	}
	private void initViews() {
		textViewImgBack = (ImageView) findViewById(R.id.img_back);
		textViewTitle = (TextView) findViewById(R.id.tv_title);
		
		checkBoxItem_1 = (CheckBox) findViewById(R.id.cb_item_1);
		checkBoxItem_2 = (CheckBox) findViewById(R.id.cb_item_2);
		checkBoxItem_3 = (CheckBox) findViewById(R.id.cb_item_3);
		
		textViewMon_1 =  (TextView) findViewById(R.id.tv_mon_1);
		textViewTue_1 =  (TextView) findViewById(R.id.tv_tue_1);
		textViewWen_1 =  (TextView) findViewById(R.id.tv_wen_1);
		textViewThu_1 =  (TextView) findViewById(R.id.tv_thu_1);
		textViewSat_1 =  (TextView) findViewById(R.id.tv_sat_1);
		textViewFri_1 =  (TextView) findViewById(R.id.tv_fri_1);
		textViewSun_1 =  (TextView) findViewById(R.id.tv_sun_1);
		textViewMon_2 =  (TextView) findViewById(R.id.tv_mon_2);
		textViewTue_2 =  (TextView) findViewById(R.id.tv_tue_2);
		textViewWen_2 =  (TextView) findViewById(R.id.tv_wen_2);
		textViewThu_2 =  (TextView) findViewById(R.id.tv_thu_2);
		textViewSat_2 =  (TextView) findViewById(R.id.tv_sat_2);
		textViewFri_2 =  (TextView) findViewById(R.id.tv_fri_2);
		textViewSun_2 =  (TextView) findViewById(R.id.tv_sun_2);
		textViewMon_3 =  (TextView) findViewById(R.id.tv_mon_3);
		textViewTue_3 =  (TextView) findViewById(R.id.tv_tue_3);
		textViewWen_3 =  (TextView) findViewById(R.id.tv_wen_3);
		textViewThu_3 =  (TextView) findViewById(R.id.tv_thu_3);
		textViewSat_3 =  (TextView) findViewById(R.id.tv_sat_3);
		textViewFri_3 =  (TextView) findViewById(R.id.tv_fri_3);
		textViewSun_3 =  (TextView) findViewById(R.id.tv_sun_3);
		
		textViewSet_1 =  (TextView) findViewById(R.id.tv_clock_1);
		textViewSet_2 =  (TextView) findViewById(R.id.tv_clock_2);
		textViewSet_3 =  (TextView) findViewById(R.id.tv_clock_3);
		checkBoxItem_1 =  (CheckBox) findViewById(R.id.cb_item_1);
		checkBoxItem_2 =  (CheckBox) findViewById(R.id.cb_item_2);
		checkBoxItem_3 =  (CheckBox) findViewById(R.id.cb_item_3);
		
		//初始化开关
		boolean clock_cb_item_1 =  (boolean) SharedPreferenceUtil
				.get(getApplicationContext(), "clock_cb_item_1", false);
		checkBoxItem_1.setChecked(clock_cb_item_1);
		boolean clock_cb_item_2 =  (boolean) SharedPreferenceUtil
				.get(getApplicationContext(), "clock_cb_item_2", false);
		checkBoxItem_2.setChecked(clock_cb_item_2);
		boolean clock_cb_item_3 =  (boolean) SharedPreferenceUtil
				.get(getApplicationContext(), "clock_cb_item_3", false);
		checkBoxItem_3.setChecked(clock_cb_item_3);
		
		//初始化闹钟时刻
		String clock_1 = (String) SharedPreferenceUtil
				.get(getApplicationContext(), "tv_clock_1", "07:30");
		textViewSet_1.setText(clock_1);
		String clock_2 = (String) SharedPreferenceUtil
				.get(getApplicationContext(), "tv_clock_2", "07:30");
		textViewSet_2.setText(clock_2);
		String clock_3 = (String) SharedPreferenceUtil
				.get(getApplicationContext(), "tv_clock_3", "07:30");
		textViewSet_3.setText(clock_3);
		
		//初始化星期
		initWeekday();
		
	}
	
	/**
	 * 初始化星期
	 */
	private void initWeekday() {
		updateWeekDay(4);
		// 1111 1111 & 0000 0001 = 0000 0001; 开
		// 1111 1110 & 0000 0001 = 0000 0000; 关
		updateTextview(textViewSun_1,WeekdayEnum.SUN.getDay(),clockValue_1);
		updateTextview(textViewMon_1,WeekdayEnum.MON.getDay(),clockValue_1);
		updateTextview(textViewTue_1,WeekdayEnum.TUE.getDay(),clockValue_1);
		updateTextview(textViewWen_1,WeekdayEnum.WED.getDay(),clockValue_1);
		updateTextview(textViewThu_1,WeekdayEnum.THU.getDay(),clockValue_1);
		updateTextview(textViewFri_1,WeekdayEnum.FRI.getDay(),clockValue_1);
		updateTextview(textViewSat_1,WeekdayEnum.SAT.getDay(),clockValue_1);
		updateTextview(textViewSun_2,WeekdayEnum.SUN.getDay(),clockValue_2);
		updateTextview(textViewMon_2,WeekdayEnum.MON.getDay(),clockValue_2);
		updateTextview(textViewTue_2,WeekdayEnum.TUE.getDay(),clockValue_2);
		updateTextview(textViewWen_2,WeekdayEnum.WED.getDay(),clockValue_2);
		updateTextview(textViewThu_2,WeekdayEnum.THU.getDay(),clockValue_2);
		updateTextview(textViewFri_2,WeekdayEnum.FRI.getDay(),clockValue_2);
		updateTextview(textViewSat_2,WeekdayEnum.SAT.getDay(),clockValue_2);
		updateTextview(textViewSun_3,WeekdayEnum.SUN.getDay(),clockValue_3);
		updateTextview(textViewMon_3,WeekdayEnum.MON.getDay(),clockValue_3);
		updateTextview(textViewTue_3,WeekdayEnum.TUE.getDay(),clockValue_3);
		updateTextview(textViewWen_3,WeekdayEnum.WED.getDay(),clockValue_3);
		updateTextview(textViewThu_3,WeekdayEnum.THU.getDay(),clockValue_3);
		updateTextview(textViewFri_3,WeekdayEnum.FRI.getDay(),clockValue_3);
		updateTextview(textViewSat_3,WeekdayEnum.SAT.getDay(),clockValue_3);
	}
	
	private void setClick() {
		textViewImgBack.setOnClickListener(this);
		textViewTitle.setOnClickListener(this);
		textViewMon_1.setOnClickListener(this);
		textViewTue_1.setOnClickListener(this);
		textViewWen_1.setOnClickListener(this);
		textViewThu_1.setOnClickListener(this);
		textViewFri_1.setOnClickListener(this);
		textViewSat_1.setOnClickListener(this);
		textViewSun_1.setOnClickListener(this);
		textViewMon_2.setOnClickListener(this);
		textViewTue_2.setOnClickListener(this);
		textViewWen_2.setOnClickListener(this);
		textViewThu_2.setOnClickListener(this);
		textViewFri_2.setOnClickListener(this);
		textViewSat_2.setOnClickListener(this);
		textViewSun_2.setOnClickListener(this);
		textViewMon_3.setOnClickListener(this);
		textViewTue_3.setOnClickListener(this);
		textViewWen_3.setOnClickListener(this);
		textViewThu_3.setOnClickListener(this);
		textViewFri_3.setOnClickListener(this);
		textViewSat_3.setOnClickListener(this);
		textViewSun_3.setOnClickListener(this);
		textViewSet_1.setOnClickListener(this);
		textViewSet_2.setOnClickListener(this);
		textViewSet_3.setOnClickListener(this);
		
		checkBoxItem_1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					SharedPreferenceUtil.put(getApplicationContext(), CHECKBOX_1, true);
				}else{
					SharedPreferenceUtil.put(getApplicationContext(), CHECKBOX_1, false);
				}
			}
		});
		checkBoxItem_2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					SharedPreferenceUtil.put(getApplicationContext(), CHECKBOX_2, true);
				}else{
					SharedPreferenceUtil.put(getApplicationContext(), CHECKBOX_2, false);
				}
			}
		});
		checkBoxItem_3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					SharedPreferenceUtil.put(getApplicationContext(), CHECKBOX_3, true);
				}else{
					SharedPreferenceUtil.put(getApplicationContext(), CHECKBOX_3, false);
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
	
		
		switch (v.getId()) {
		case R.id.img_back:
			Intent retrunToMenu = new Intent(ClockActivity.this,MenuActivity.class);
			startActivity(retrunToMenu);
			this.finish();
			overridePendingTransition(
					R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
			break;
		case R.id.tv_sun_1:
			updateWeekDay(1);
			repeatWeekday(textViewSun_1,WeekdayEnum.SUN.getDay(),clockValue_1,SETTIME_1);
			break;
		case R.id.tv_mon_1:
			updateWeekDay(1);
			repeatWeekday(textViewMon_1,WeekdayEnum.MON.getDay(),clockValue_1,SETTIME_1);
			break;
		case R.id.tv_tue_1:
			updateWeekDay(1);
			repeatWeekday(textViewTue_1,WeekdayEnum.TUE.getDay(),clockValue_1,SETTIME_1);
			break;
		case R.id.tv_wen_1:
			updateWeekDay(1);
			repeatWeekday(textViewWen_1,WeekdayEnum.WED.getDay(),clockValue_1,SETTIME_1);
			break;
		case R.id.tv_thu_1:
			updateWeekDay(1);
			repeatWeekday(textViewThu_1,WeekdayEnum.THU.getDay(),clockValue_1,SETTIME_1);
			break;
		case R.id.tv_fri_1:
			updateWeekDay(1);
			repeatWeekday(textViewFri_1,WeekdayEnum.FRI.getDay(),clockValue_1,SETTIME_1);
			break;
		case R.id.tv_sat_1:
			updateWeekDay(1);
			repeatWeekday(textViewSat_1,WeekdayEnum.SAT.getDay(),clockValue_1,SETTIME_1);
			break;
		case R.id.tv_sun_2:
			updateWeekDay(2);
			repeatWeekday(textViewSun_2,WeekdayEnum.SUN.getDay(),clockValue_2,SETTIME_2);
			break;
		case R.id.tv_mon_2:
			updateWeekDay(2);
			repeatWeekday(textViewMon_2,WeekdayEnum.MON.getDay(),clockValue_2,SETTIME_2);
			break;
		case R.id.tv_tue_2:
			updateWeekDay(2);
			repeatWeekday(textViewTue_2,WeekdayEnum.TUE.getDay(),clockValue_2,SETTIME_2);
			break;
		case R.id.tv_wen_2:
			updateWeekDay(2);
			repeatWeekday(textViewWen_2,WeekdayEnum.WED.getDay(),clockValue_2,SETTIME_2);
			break;
		case R.id.tv_thu_2:
			updateWeekDay(2);
			repeatWeekday(textViewThu_2,WeekdayEnum.THU.getDay(),clockValue_2,SETTIME_2);
			break;
		case R.id.tv_fri_2:
			updateWeekDay(2);
			repeatWeekday(textViewFri_2,WeekdayEnum.FRI.getDay(),clockValue_2,SETTIME_2);
			break;
		case R.id.tv_sat_2:
			updateWeekDay(2);
			repeatWeekday(textViewSat_2,WeekdayEnum.SAT.getDay(),clockValue_2,SETTIME_2);
			break;
		case R.id.tv_sun_3:
			updateWeekDay(3);
			repeatWeekday(textViewSun_3,WeekdayEnum.SUN.getDay(),clockValue_3,SETTIME_3);
			break;
		case R.id.tv_mon_3:
			updateWeekDay(3);
			repeatWeekday(textViewMon_3,WeekdayEnum.MON.getDay(),clockValue_3,SETTIME_3);
			break;
		case R.id.tv_tue_3:
			updateWeekDay(3);
			repeatWeekday(textViewTue_3,WeekdayEnum.TUE.getDay(),clockValue_3,SETTIME_3);
			break;
		case R.id.tv_wen_3:
			updateWeekDay(3);
			repeatWeekday(textViewWen_3,WeekdayEnum.WED.getDay(),clockValue_3,SETTIME_3);
			break;
		case R.id.tv_thu_3:
			updateWeekDay(3);
			repeatWeekday(textViewThu_3,WeekdayEnum.THU.getDay(),clockValue_3,SETTIME_3);
			break;
		case R.id.tv_fri_3:
			updateWeekDay(3);
			repeatWeekday(textViewFri_3,WeekdayEnum.FRI.getDay(),clockValue_3,SETTIME_3);
			break;
		case R.id.tv_sat_3:
			updateWeekDay(3);
			repeatWeekday(textViewSat_3,WeekdayEnum.SAT.getDay(),clockValue_3,SETTIME_3);
			break;
		case R.id.tv_clock_1:
			Intent intent_1 = new Intent(ClockActivity.this,ClockSetActivity_1.class);
			startActivityForResult(intent_1, REQ_1);
			break;
		case R.id.tv_clock_2:
			Intent intent_2 = new Intent(ClockActivity.this,ClockSetActivity_2.class);
			startActivityForResult(intent_2, REQ_2);
			break;
		case R.id.tv_clock_3:
			Intent intent_3 = new Intent(ClockActivity.this,ClockSetActivity_3.class);
			startActivityForResult(intent_3, REQ_3);
			break;
		default:
			break;
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
		switch (requestCode) {
		case REQ_1:
			switch (resultCode) {
			case RESULT_1:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min+":"+sec;
				textViewSet_1.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), SETTIME_1, value);
				break;
			default:
				break;
			}
			break;
		case REQ_2:
			switch (resultCode) {
			case RESULT_2:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min+":"+sec;
				textViewSet_2.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), SETTIME_2, value);
				break;
			default:
				break;
			}
			break;
		case REQ_3:
			switch (resultCode) {
			case RESULT_3:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min+":"+sec;
				textViewSet_3.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), SETTIME_3, value);
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
	 * 
	 * 星期的选择与取消
	 * 
	 * @param tv		元素：	
	 * @param weekday   二进制星期：0b00000010 代表星期一 ；0b00000001 代表星期日
	 * @param value     存储的值 ：0b11000010 代表星期一五六是选中状态
	 * @param addr		存贮的地址
	 */
	private void repeatWeekday(TextView tv,int weekday,int value,String addr) {
		//抑或
		value ^= weekday;
		
		Log.d("OB", "设置-->" + Integer.toBinaryString(value));
		updateTextview(tv, weekday,value);
		SharedPreferenceUtil.put(getApplicationContext(),
				addr, value);
	}
	
	/**
	 * 
	 * 选中与取消时 更新View
	 * 
	 * @param tv
	 * @param weekday
	 * @param value
	 */
	private void updateTextview(TextView tv,int weekday,int value){
		//例如星期天：
		// 1111 1111 & 0000 0001 = 0000 0001; 开
		// 1111 1110 & 0000 0001 = 0000 0000; 关
		int color = (value & weekday) == weekday ? getResources().getColor(
				R.color.color_top_text_pressed) : getResources().getColor(
				R.color.grey);
				tv.setTextColor(color);
	}
	
	private void updateWeekDay(int flag){
		switch (flag) {
		case 1:
			clockValue_1 = (int) SharedPreferenceUtil.get(getApplicationContext(),
					SETTIME_1, 0b00000000);
//			Log.e("app", clockValue_1+"");
			Log.d("OB", "读取clockSet_1-->" + Integer.toBinaryString(clockValue_1));
			break;
		case 2:
			clockValue_2 = (int) SharedPreferenceUtil.get(getApplicationContext(),
					SETTIME_2, 0b00000000);
			Log.d("OB", "读取clockSet_2-->" + Integer.toBinaryString(clockValue_2));
			break;
		case 3:
			clockValue_3 = (int) SharedPreferenceUtil.get(getApplicationContext(),
					SETTIME_3, 0b00000000);
			Log.d("OB", "读取clockSet_3-->" + Integer.toBinaryString(clockValue_3));
			break;
		case 4:
			clockValue_1 = (int) SharedPreferenceUtil.get(getApplicationContext(),
					SETTIME_1, 0b00000000);
				Log.d("OB", "读取clockSet_1-->" + Integer.toBinaryString(clockValue_1));
			clockValue_2 = (int) SharedPreferenceUtil.get(getApplicationContext(),
					SETTIME_2, 0b00000000);
			Log.d("OB", "读取clockSet_2-->" + Integer.toBinaryString(clockValue_2));
			clockValue_3 = (int) SharedPreferenceUtil.get(getApplicationContext(),
					SETTIME_3, 0b00000000);
			Log.d("OB", "读取clockSet_3-->" + Integer.toBinaryString(clockValue_3));
			break;

		default:
			break;
		}
	
		
		
	
	}
	 
}
