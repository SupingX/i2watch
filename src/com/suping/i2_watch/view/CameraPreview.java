package com.suping.i2_watch.view;


import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private final String TAG = this.getClass().getSimpleName();
	private SurfaceHolder mHolder;
	private Camera mCamera;

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CameraPreview(Context context, Camera camera) {
		super(context);
		if (camera != null) {
			mCamera = camera;
			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			// 把这个监听添加进去，这样可以监听到创建和销毁的事件了
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明  
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	/** implements SurfaceHolder.Callback --START **/

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "camera is created");
		// The Surface has been created, now tell the camera where to draw the
		// preview.
//		try {
//			// 设置camera预览的角度，因为默认图片是倾斜90度的
//			mCamera.setDisplayOrientation(90);
//			// 设置holder主要是用于surfaceView的图片的实时预览，以及获取图片等功能，可以理解为控制camera的操作..
//			mCamera.setPreviewDisplay(holder);
//			mCamera.startPreview();
//
//			// startFaceDetection();
//
//		} catch (IOException e) {
//			mCamera.release();
//			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
//		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

//		if (mHolder.getSurface() == null) {
//			// preview surface does not exist
//			return;
//		}
//
//		// stop preview before making changes
//		try {
//			mCamera.stopPreview();
//		} catch (Exception e) {
//			mCamera.release();
//			// ignore: tried to stop a non-existent preview
//		}
//		// set preview size and make any resize, rotate or
//		// reformatting changes here
//		// start preview with new settings
//		try {
//			mCamera.setPreviewDisplay(mHolder);
//			mCamera.startPreview();
//			// startFaceDetection();
//		} catch (Exception e) {
//			mCamera.release();
//			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
//		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		Log.d(TAG, "camera is destroyed");
//		mCamera.release();
//		mCamera = null;
//		 CameraManager.getInstance().doStopCamera();  
	}

	/** implements SurfaceHolder.Callback --END **/

	/**
	 * 启动人脸识别特性
	 */
	public void startFaceDetection() {
		// Try starting Face Detection
		Camera.Parameters params = mCamera.getParameters();
		// start face detection only *after* preview has started
		if (params.getMaxNumDetectedFaces() > 0) {
			// camera supports face detection, so can start it:
			mCamera.startFaceDetection();
		}
	}
}
