package com.suping.i2_watch.setting;

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

import com.suping.i2_watch.Main;
import com.suping.i2_watch.R;

public class SettingActivity extends Activity implements OnClickListener{
	private ImageView imgBack;
	private TextView textViewTitle;
	private RelativeLayout relativeInformation;
	private RelativeLayout relativePedo;
	private RelativeLayout relativeAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initViews();
		setClick();
		textViewTitle.setText(getResources().getString(R.string.setting));
	}
	
	
	private void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		textViewTitle = (TextView) findViewById(R.id.tv_title);
		relativeInformation = (RelativeLayout) findViewById(R.id.rl_information);
		relativePedo = (RelativeLayout) findViewById(R.id.rl_pedo);
		relativeAbout = (RelativeLayout) findViewById(R.id.rl_about);
	}

	private void setClick() {
		imgBack.setOnClickListener(this);
		relativeInformation.setOnClickListener(this);
		relativePedo.setOnClickListener(this);
		relativeAbout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent menu = new Intent(SettingActivity.this,Main.class);
			startActivity(menu);
			this.finish();
			overridePendingTransition(
					//左右是指手机屏幕的左右
					R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
		
			break;
		case R.id.rl_information:
			Intent infomationIntent = new Intent(SettingActivity.this,InformationActivity.class);
			startActivity(infomationIntent);
			this.finish();
			overridePendingTransition(
					//左右是指手机屏幕的左右
					R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.rl_pedo:
			Intent pedoIntent = new Intent(SettingActivity.this,PedometerActivity.class);
			startActivity(pedoIntent);
			this.finish();
			overridePendingTransition(
					//左右是指手机屏幕的左右
					R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.rl_about:
			Intent aboutIntent = new Intent(SettingActivity.this,AboutActivity.class);
			startActivity(aboutIntent);
			this.finish();
			overridePendingTransition(
					//左右是指手机屏幕的左右
					R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
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
