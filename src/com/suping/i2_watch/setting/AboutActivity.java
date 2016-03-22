package com.suping.i2_watch.setting;

import com.suping.i2_watch.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity {
	private ImageView imgBack;
	private TextView textViewTitle;
	private TextView tvVersionValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		imgBack = (ImageView) findViewById(R.id.img_back);
		textViewTitle = (TextView) findViewById(R.id.tv_title);
		tvVersionValue = (TextView) findViewById(R.id.tv_version_value);
		tvVersionValue.setText(getVersion());
		textViewTitle.setText(getResources().getString(R.string.about));
		imgBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent returnSetting = new Intent(AboutActivity.this,SettingActivity.class);
//				startActivity(returnSetting);
				finish();
				overridePendingTransition(
						//左右是指手机屏幕的左右
						R.anim.activity_from_left_to_right_enter,
						R.anim.activity_from_left_to_right_exit);
			}
		});
	}
	
	  public String getVersion() {
	      try {
	         PackageManager manager = this.getPackageManager();
	          PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	          String version = info.versionName;
	         return getString(R.string.app_version) + version;
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	      return getString(R.string.unknown);
	 }
}
