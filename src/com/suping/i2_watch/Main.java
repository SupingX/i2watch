package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.menu.MenuActivity;
import com.suping.i2_watch.setting.SettingActivity;
import com.suping.i2_watch.view.ColorsCircle;
import com.suping.i2_watch.view.RefreshForScrolView;
import com.suping.i2_watch.view.RefreshForScrolView.PullToRefreshListener;
import com.suping.i2_watch.view.RefreshableScrollView;

public class Main extends Activity implements OnClickListener {

	private ImageView imgMenu; 		//目录（左）
	private ImageView imgSetting;	//设置（右）
	private TextView textViewTitle; //连接蓝牙（中）
	private ViewPager mViewPager;   
	private List<View> mList;       //页面（运动+睡眠）
	private RefreshForScrolView mRefreshSleep;  //下拉刷新（睡眠）
	private RefreshForScrolView mRefreshSport;  //下拉刷新 （运动）
	private ColorsCircle circleSport;  //运动圆环
	private ColorsCircle circleSleep;  //睡眠圆环
	private long exitTime = 0; //退出间隔时间
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initViews();
		setClick();
		initViewPager();

	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_menu:
			//进入菜单页面
			Intent menu = new Intent(Main.this, MenuActivity.class);
			startActivity(menu);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);

			break;
		case R.id.img_setting:
			//进入设置页面
			Intent setting = new Intent(Main.this, SettingActivity.class);
			startActivity(setting);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.tv_title:
			//进入连接蓝牙页面
//			Intent intentBluetooth = new Intent(Main.this,ConnectActivity.class);
			Intent intentBluetooth = new Intent(Main.this,ConnectEvolveActivity.class);
			startActivity(intentBluetooth);
			break;
		default:
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
	
	/**
	 * 加载 Widgets
	 */
	private void initViews() {
		imgMenu = (ImageView) findViewById(R.id.img_menu);
		imgSetting = (ImageView) findViewById(R.id.img_setting);
		textViewTitle = (TextView) findViewById(R.id.tv_title);
	
	}

	/**
	 * 加载ViewPager
	 * 运动view
	 * 睡眠view
	 */
	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.guidePages);
		LayoutInflater inflater = getLayoutInflater();
		//运动
		View sportV = inflater.inflate(R.layout.layout_info_sport, null);
		circleSport = (ColorsCircle) sportV.findViewById(R.id.color_circle);
		mRefreshSport = (RefreshForScrolView) sportV.findViewById(R.id.refresh_sport);
		mRefreshSport.setOnRefreshListener(new PullToRefreshListener() {
			
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mRefreshSport.finishRefreshing();
						circleSport.setmProgress(3000);	
					}
				},3000);
			}
		}, 1);
		
		//睡眠
		View sleepV = inflater.inflate(R.layout.layout_info_sleep, null);
		circleSleep = (ColorsCircle) sleepV.findViewById(R.id.color_circle);
		mRefreshSleep = (RefreshForScrolView) sleepV.findViewById(R.id.refresh_sleep);
		mRefreshSleep.setOnRefreshListener(new PullToRefreshListener() {
			
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mRefreshSleep.finishRefreshing();
						circleSleep.setmProgress(13000);	
					}
				},3000);
			}
		}, 12);
		
		
		mList = new ArrayList<>();
		mList.add(sportV);
		mList.add(sleepV);
		mViewPager.setAdapter(new ViewPagerAdapter(mList));
		mViewPager.setCurrentItem(0);
		
		//运动|睡眠view切换
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//不要拦截该控件上的触摸事件
//				mViewPager.getParent().requestDisallowInterceptTouchEvent(true); 
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	private void setClick() {
		imgMenu.setOnClickListener(this);
		imgSetting.setOnClickListener(this);
		textViewTitle.setOnClickListener(this);
	}
	
	/**
	 * ViewPagerAdapter
	 * @author Administrator
	 *
	 */
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
}
