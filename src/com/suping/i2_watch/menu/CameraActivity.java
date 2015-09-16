package com.suping.i2_watch.menu;

import com.suping.i2_watch.BaseActivity;
import com.suping.i2_watch.MyBroadcastReceiver;
import com.suping.i2_watch.R;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.service.AbstractSimpleBlueService;
import com.suping.i2_watch.service.SimpleBlueService;
import com.suping.i2_watch.util.DisplayUtil;
import com.suping.i2_watch.view.CameraInterface;
import com.suping.i2_watch.view.CameraInterface.CamOpenOverCallback;
import com.suping.i2_watch.view.CameraSurfaceView;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CameraActivity extends BaseActivity implements CamOpenOverCallback {
	private CameraSurfaceView surfaceView = null;
	/** ImageView ： 返回、拍照 **/
	private ImageView imgBack, imgCamera;
	private AbstractSimpleBlueService mSimpleBlueService;
		private MyBroadcastReceiver mReceiver = new MyBroadcastReceiver(){
		public void doCamera() {
			CameraInterface.getInstance().doTakePicture();
		};
	};
	
	float previewRate = -1f;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	
	private Runnable openThread = new Runnable() {

		@Override
		public void run() {
			CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_camera);
		initUI();
		initViewParams();
		imgCamera.setOnClickListener(new BtnListeners());
		imgBack.setOnClickListener(new BtnListeners());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mSimpleBlueService = getSimpleBlueService();
		registerReceiver(mReceiver, SimpleBlueService.getIntentFilter());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (isConnected()) {
			mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForUpdatePhotographState(1));
		}
		Thread openThread = new Thread() {
			@Override
			public void run() {
				CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
			}
		};
		openThread.start();
		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		CameraInterface.getInstance().doStopCamera();
	}
	
	
	@Override
	protected void onStop() {
		if (isConnected()) {
			mSimpleBlueService.writeCharacteristic(I2WatchProtocolDataForWrite.hexDataForUpdatePhotographState(0));
		}
		
		unregisterReceiver(mReceiver);
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	
	}

	private void initUI() {

		imgBack = (ImageView) findViewById(R.id.img_back);
		imgCamera = (ImageView) findViewById(R.id.img_camera);
		surfaceView = (CameraSurfaceView) findViewById(R.id.surface);
	}

	private void initViewParams() {
		android.view.ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
		params.width = p.x;
		params.height = p.y;
		previewRate = DisplayUtil.getScreenRate(this); // 默认全屏的比例预览
		surfaceView.setLayoutParams(params);

		// 手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
		// LayoutParams p2 = shutterBtn.getLayoutParams();
		// p2.width = DisplayUtil.dip2px(this, 80);
		// p2.height = DisplayUtil.dip2px(this, 80);;
		// shutterBtn.setLayoutParams(p2);

	}

	@Override
	public void cameraHasOpened() {
		// TODO Auto-generated method stub
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		Log.d("CaneraActivity", " holder.isCreating() -->" + holder.isCreating());
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}

	private class BtnListeners implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.img_camera:
				CameraInterface.getInstance().doTakePicture();
				break;
			case R.id.img_back:
				finish();
				break;
			default:
				break;
			}
		}

	}
}
