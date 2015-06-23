package com.suping.i2_watch.menu;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingActivity extends Activity implements OnClickListener{
	
	public final static String SHARE_INCOMING_START_HOUR = "incoming_start_hour";
	public final static String SHARE_INCOMING_START_MIN = "incoming_start_min";
	public final static String SHARE_INCOMING_END_HOUR = "incoming_end_hour";
	public final static String SHARE_INCOMING_END_MIN = "incoming_end_min";
	
	private ImageView imgBack;
	private TextView textViewTitle;
	private TextView textViewStart,textViewEnd;
	private RelativeLayout rlStart,rlEnd;
	private long exitTime = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incoming);
		initViews();
		setClick();
		textViewTitle.setText(getResources().getString(R.string.incoming_reminder));
	}
	
	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			
			switch (requestCode) {
			//from IncomingStarttimeActivity
			case 1:
				switch (resultCode) {
				case RESULT_OK:
					Bundle b = data.getExtras();
					String min = b.getString("min");
					String sec = b.getString("sec");
					String value = min+":"+sec;
					Log.e("incoming", "value : " + value);
					textViewStart.setText(value);
	//				SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_START, value);
					SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_START_HOUR, min);
					SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_START_MIN, sec);
					break;
				default:
					break;
				}
				break;
				//from IncomingEndtimeActivity
			case 2:
				switch (resultCode) {
				case RESULT_OK:
					Bundle b = data.getExtras();
					String min = b.getString("min");
					String sec = b.getString("sec");
					String value = min+":"+sec;
					textViewEnd.setText(value);
	//				SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_END, value);
					SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_END_HOUR, min);
					SharedPreferenceUtil.put(getApplicationContext(), SHARE_INCOMING_END_MIN, sec);
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
			Intent retrunToMain = new Intent(IncomingActivity.this,MenuActivity.class);
			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
			break;
			
		case R.id.rl_start:
			Intent startIntent = new Intent(IncomingActivity.this,IncomingStarttimeActivity.class);
			startActivityForResult(startIntent, 1);
			break;
		case R.id.rl_end:
			Intent endIntent = new Intent(IncomingActivity.this,IncomingEndtimeActivity.class);
			startActivityForResult(endIntent, 2);
			break;
		}
	}
	
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

	private void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		textViewTitle = (TextView) findViewById(R.id.tv_title);
		textViewStart = (TextView) findViewById(R.id.tv_start_value);
		textViewEnd = (TextView) findViewById(R.id.tv_end_value);
		rlStart = (RelativeLayout) findViewById(R.id.rl_start);
		rlEnd = (RelativeLayout) findViewById(R.id.rl_end);
		
		String incomingStartHour = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_INCOMING_START_HOUR, "07");
		String incomingStartMin = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_INCOMING_START_MIN, "00");
		textViewStart.setText(incomingStartHour + ":" +incomingStartMin);
		
		String incomingEndHour = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_INCOMING_END_HOUR, "07");
		String incomingEndMin = (String) SharedPreferenceUtil.get(getApplicationContext(), SHARE_INCOMING_END_MIN, "00");
		textViewEnd.setText(incomingEndHour + ":" + incomingEndMin);
		
	}

	private void setClick() {
		imgBack.setOnClickListener(this);
		textViewTitle.setOnClickListener(this);
		rlStart.setOnClickListener(this);
		rlEnd.setOnClickListener(this);
		
	}
}
