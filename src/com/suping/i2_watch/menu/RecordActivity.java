package com.suping.i2_watch.menu;




import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.suping.i2_watch.R;
import com.suping.i2_watch.view.NoScrollViewPager;

public class RecordActivity extends FragmentActivity implements OnClickListener{
	private NoScrollViewPager vp;
	private RadioButton rbSport;
	private RadioButton rbSleep;
	private RadioGroup rgTop;
	private ImageView imgBack;
	private int [] layouts = new int[]{
			R.layout.fragment_record_sport
			,R.layout.fragment_record_sleep
	};
	
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
//			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
//					R.anim.activity_from_left_to_right_exit);
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
		vp = (NoScrollViewPager) findViewById(R.id.vp);
		vp.setAdapter(new MyAdapter(getSupportFragmentManager()));
	
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    menu.add(0, 1, Menu.NONE, "发送");
	    menu.add(0, 2, Menu.NONE, "标记为重要");
	    menu.add(0, 3, Menu.NONE, "重命名");
	    menu.add(0, 4, Menu.NONE, "删除");
		super.onCreateContextMenu(menu, v, menuInfo);
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
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
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
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void update(int id){
		switch (id) {
		case 0:
			rbSport.setTextColor(getResources().getColor(R.color.color_top_text_pressed));
			rbSleep.setTextColor(getResources().getColor(R.color.white));
			vp.setCurrentItem(0);
			break;
		case 1:
			rbSport.setTextColor(getResources().getColor(R.color.white));
			rbSleep.setTextColor(getResources().getColor(R.color.color_top_text_pressed));
			vp.setCurrentItem(1);
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
			RecordFragment f = new RecordFragment();
			Bundle b = new Bundle();
			b.putInt("layout", layouts[arg0]);
			f.setArguments(b);
			return f;
		}

		@Override
		public int getCount() {
			return layouts.length;
		}
		
	}

}
