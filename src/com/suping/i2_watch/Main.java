package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.menu.MenuActivity;
import com.suping.i2_watch.setting.SettingActivity;
import com.suping.i2_watch.view.ColorsCircle;
import com.suping.i2_watch.view.RefreshForScrolView;
import com.suping.i2_watch.view.RefreshForScrolView.PullToRefreshListener;

public class Main extends Activity implements OnClickListener {

	private ImageView img_menu;
	private ImageView img_setting;
	private TextView tv_title;
	private ViewPager mViewPager;
	private List<View> mList;
//	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RefreshForScrolView mRefresh;
	private ColorsCircle cc_sport;
	private ColorsCircle cc_sleep;
	
	private boolean isOrnVer = false;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initViews();
		setClick();
		initViewPager();
	}

	private void initViews() {
		img_menu = (ImageView) findViewById(R.id.img_menu);
		img_setting = (ImageView) findViewById(R.id.img_setting);
		tv_title = (TextView) findViewById(R.id.tv_title);
//		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
//		mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
//				android.R.color.holo_green_light,
//				android.R.color.holo_orange_light,
//				android.R.color.holo_red_light);
		mRefresh = (RefreshForScrolView) findViewById(R.id.refresh);
		
	}

	private void setClick() {
		img_menu.setOnClickListener(this);
		img_setting.setOnClickListener(this);
		tv_title.setOnClickListener(this);
//		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				new Handler().postDelayed(new Runnable() {
//					public void run() {
//						mSwipeRefreshLayout.setRefreshing(false);
//						cc_sport.setmProgress(15000);
//						cc_sleep.setmProgress(15000);
//					}
//				}, 3000);
//			}
//		});
			mRefresh.setOnRefreshListener(new PullToRefreshListener() {
				
				@Override
				public void onRefresh() {
					try {
						Thread.sleep(3000);
//						cc_sport.setmProgress(15000);
//						cc_sleep.setmProgress(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mRefresh.finishRefreshing();
				}
			}, 1234);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_menu:
			Intent menu = new Intent(Main.this, MenuActivity.class);
			startActivity(menu);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);

			break;
		case R.id.img_setting:
			Intent setting = new Intent(Main.this, SettingActivity.class);
			startActivity(setting);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.tv_title:
			Intent intentBluetooth = new Intent(Main.this,ConnectActivity.class);
			startActivity(intentBluetooth);
			break;
		default:
			break;
		}
	}

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.guidePages);
		LayoutInflater inflater = getLayoutInflater();
		View sportV = inflater.inflate(R.layout.layout_info_sport, null);
		cc_sport = (ColorsCircle) sportV.findViewById(R.id.color_circle);
		View sleepV = inflater.inflate(R.layout.layout_info_sleep, null);
		cc_sleep = (ColorsCircle) sleepV.findViewById(R.id.color_circle);
		mList = new ArrayList<>();
		mList.add(sportV);
		mList.add(sleepV);
		mViewPager.setAdapter(new ViewPagerAdapter(mList));
		mViewPager.setCurrentItem(0);
		
		
		mViewPager.setOnTouchListener(new View.OnTouchListener() {  
	        @Override  
	        public boolean onTouch(View v, MotionEvent event ) {  
	        	
	        	switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					mViewPager.requestDisallowInterceptTouchEvent(true);
					break;
				case MotionEvent.ACTION_CANCEL:
					mViewPager.requestDisallowInterceptTouchEvent(false);
					break;

				default:
					break;
	        	}
	            return false;  
	        }
	    });  
	  
	    	mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
	    		
	    		@Override
	    		public void onPageSelected(int arg0) {
	    		}
	    		
	    		@Override
	    		public void onPageScrolled(int arg0, float arg1, int arg2) {
	    			mViewPager.getParent().requestDisallowInterceptTouchEvent(true); 
	    		}
	    		
	    		@Override
	    		public void onPageScrollStateChanged(int arg0) {
	    			
	    		}
	    	});
	}

	private class ViewPagerAdapter extends PagerAdapter {
		private List<View> listVs;

		public ViewPagerAdapter(List<View> list) {
			this.listVs = list;
		}

		@Override
		public int getCount() {
			return listVs.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (View) arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(listVs.get(position), 0);
			return listVs.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(listVs.get(position));
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
}
