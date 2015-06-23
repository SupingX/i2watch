package com.suping.i2_watch.menu;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.DisplayUtil;
import com.suping.i2_watch.view.CameraInterface;
import com.suping.i2_watch.view.CameraInterface.CamOpenOverCallback;
import com.suping.i2_watch.view.CameraSurfaceView;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CameraActivity extends Activity implements CamOpenOverCallback {
	private CameraSurfaceView surfaceView = null;
	/** ImageView ： 返回、拍照 **/
	private ImageView imgBack, imgCamera;
	float previewRate = -1f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread openThread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
			}
		};
		openThread.start();
		setContentView(R.layout.activity_camera);
		initUI();
		initViewParams();

		imgCamera.setOnClickListener(new BtnListeners());
		imgBack.setOnClickListener(new BtnListeners());
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
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}

	private class BtnListeners implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
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
