package com.suping.i2_watch.menu;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.suping.i2_watch.R;

public class RecordActivity extends FragmentActivity implements OnClickListener{
	private ViewPager vp;
	private RadioButton rbSport;
	private RadioButton rbSleep;
	private RadioGroup rgTop;
	private ImageView imgBack;
	private List<Fragment> fragments;
	
	@Override
	protected void onCreate(Bundle fragments) {
		super.onCreate(fragments);
		setContentView(R.layout.activity_record);
		initViews();
		setListener();
		Intent intent = getIntent();
		int flag = intent.getExtras().getInt("flag");
		switch (flag) {
		case 0:
			rgTop.check(0);
			break;
		case 1:
			rgTop.check(1);
			break;
		default:
			break;
		}
	
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_record_sport:
			rgTop.check(0);
			break;
			
		case R.id.rb_record_sleep:
			rgTop.check(1);
			break;
			
		case R.id.img_back:
//			Intent retrunToMain = new Intent(this,MenuActivity.class);
//			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
			break;

		default:
			break;
		}
	}
	
	private void initViews(){
		rbSport = (RadioButton) findViewById(R.id.rb_record_sport);
		rbSleep = (RadioButton) findViewById(R.id.rb_record_sleep);
		rgTop = (RadioGroup) findViewById(R.id.rg_top);
		imgBack = (ImageView) findViewById(R.id.img_back);
		vp = (ViewPager) findViewById(R.id.vp);
//		fragments = new ArrayList<>();
//		Fragment f1 = new Fragment();
//		Fragment f2 = new Fragment();
//		fragments.add(f1);
//		fragments.add(f2);
//		vp.setAdapter(new MyAdapter(getSupportFragmentManager()));
		
	}
	
	private void setListener(){
		rbSport.setOnClickListener(this);
		rbSleep.setOnClickListener(this);
		imgBack.setOnClickListener(this);
		rgTop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.e("Record", "onCheckedChanged");
				update(checkedId);
			}
		});
	}
	
	private void update(int id){
		switch (id) {
		case 0:
		
			rbSport.setTextColor(getResources().getColor(R.color.color_top_text_pressed));
			rbSleep.setTextColor(getResources().getColor(R.color.white));
			break;
		case 1:
			rbSport.setTextColor(getResources().getColor(R.color.white));
			rbSleep.setTextColor(getResources().getColor(R.color.color_top_text_pressed));
			break;

		default:
			break;
		}
	}
	
	private class MyAdapter extends FragmentStatePagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return 2;
		}
		
	}

}
