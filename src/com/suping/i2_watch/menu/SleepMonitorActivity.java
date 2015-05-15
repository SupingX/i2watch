package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class SleepMonitorActivity extends Activity implements OnClickListener{
	private ImageView img_back;
	private TextView tv_title;
	
	private TextView tv_target_value,tv_start_value,tv_end_value;
	private RelativeLayout rl_start,rl_end,rl_target;
	
	private final static int REQ_TARGET = 4;
	private final static int REQ_STARTTIME = 5;
	private final static int REQ_ENDTIME = 6;
	private final static int RESULT_TARGET = 44;
	private final static int RESULT_STARTTIME = 55;
	private final static int RESULT_ENDTIME = 66;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_monitor);
		initViews();
		setClick();
		tv_title.setText("Sleep monitor");
	}
	
	private void initViews() {
		img_back = (ImageView) findViewById(R.id.img_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_target_value = (TextView) findViewById(R.id.tv_target_value);
		tv_start_value = (TextView) findViewById(R.id.tv_start_value);
		tv_end_value = (TextView) findViewById(R.id.tv_end_value);
		rl_start = (RelativeLayout) findViewById(R.id.rl_start);
		rl_end = (RelativeLayout) findViewById(R.id.rl_end);
		rl_target = (RelativeLayout) findViewById(R.id.rl_target);
		
		String sleep_start = (String) SharedPreferenceUtil.get(getApplicationContext(), "sleep_start", "7:00");
		tv_start_value.setText(sleep_start);
		String sleep_end = (String) SharedPreferenceUtil.get(getApplicationContext(), "sleep_end", "7:00");
		tv_end_value.setText(sleep_end);
		String sleep_target = (String) SharedPreferenceUtil.get(getApplicationContext(), "sleep_target", "7:00");
		tv_target_value.setText(sleep_target);
		
	}
	
	private void setClick() {
		img_back.setOnClickListener(this);
		tv_title.setOnClickListener(this);
		rl_start.setOnClickListener(this);
		rl_target.setOnClickListener(this);
		rl_end.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent retrunToMain = new Intent(SleepMonitorActivity.this,MenuActivity.class);
			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
			break;
			
		case R.id.rl_target:
			Intent targelIntent = new Intent(SleepMonitorActivity.this,SleepMonitorTargetActivity.class);
			startActivityForResult(targelIntent, REQ_TARGET);
			break;
		case R.id.rl_start:
			Intent startIntent = new Intent(SleepMonitorActivity.this,SleepMonitorStartActivity.class);
			startActivityForResult(startIntent, REQ_STARTTIME);
			break;
		case R.id.rl_end:
			Intent endIntent = new Intent(SleepMonitorActivity.this,SleepMonitorEndActivity.class);
			startActivityForResult(endIntent, REQ_ENDTIME);
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQ_TARGET:
			switch (resultCode) {
			case RESULT_TARGET:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min+":"+sec;
				tv_target_value.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), "sleep_target", value);
				break;
			default:
				break;
			}
			break;
		case REQ_STARTTIME:
			switch (resultCode) {
			case RESULT_STARTTIME:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min+":"+sec;
				tv_start_value.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), "sleep_start", value);
				break;
			default:
				break;
			}
			break;
		case REQ_ENDTIME:
			switch (resultCode) {
			case RESULT_ENDTIME:
				Bundle b = data.getExtras();
				String min = b.getString("min");
				String sec = b.getString("sec");
				String value = min+":"+sec;
				tv_end_value.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), "sleep_end", value);
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}
	}
	
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
