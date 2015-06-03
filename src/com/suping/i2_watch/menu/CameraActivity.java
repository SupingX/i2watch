package com.suping.i2_watch.menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.suping.i2_watch.R;
import com.suping.i2_watch.view.CameraPreview;

/**
 * 
 * 一般步骤： 检查并访问Camera —— 写代码检查是否存在cameras并请求使用 创建一个Preview类 ——
 * 创建一个preview类，继承自SurfaceView 并实现SurfaceHolder 接口。这个类是用来预览在拍照时的影像 创建一个Preview布局
 * —— 接下去创建一个跟Preview类对应的布局文件，里面可以放一些你想要的交互控件 为Capture(拍照)设置监听(Listener) ——
 * 为你的交互控件设置监听，比如按下一个Button 拍照并保存文件 —— 为拍照或录像写代码，并保存到输出流(output) 释放Camera ——
 * 在使用完后，必须要合理地释放，以供其他应用使用。
 * 
 * @author Administrator
 * 
 */

public class CameraActivity extends Activity implements OnClickListener {
	private ImageView img_back, img_position, img_camera;// 返回和切换前后置摄像头
	// private SurfaceView surface;
	// private ImageButton shutter;// 快门
	// private SurfaceHolder holder;
	// private Camera camera;// 声明相机
	// private String filepath = "";// 照片保存路径
	// private int cameraPosition = 1;// 0代表前置摄像头，1代表后置摄像头

	private final String TAG = this.getClass().getSimpleName();
	/** 照片 **/
	public static final int MEDIA_TYPE_IMAGE = 1;
	/** 视频 **/
	public static final int MEDIA_TYPE_VIDEO = 2;
	private Uri fileUri;
	private Camera mCamera;
	private CameraPreview mPreview;
	private FrameLayout preview ;
	/**当前摄像头**/
	private int currentNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 没有标题
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 拍照过程屏幕一直处于高亮
		// //设置手机屏幕朝向，一共有7种
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// SCREEN_ORIENTATION_BEHIND： 继承Activity堆栈中当前Activity下面的那个Activity的方向
		// SCREEN_ORIENTATION_LANDSCAPE： 横屏(风景照) ，显示时宽度大于高度
		// SCREEN_ORIENTATION_PORTRAIT： 竖屏 (肖像照) ， 显示时高度大于宽度
		// SCREEN_ORIENTATION_SENSOR
		// 由重力感应器来决定屏幕的朝向,它取决于用户如何持有设备,当设备被旋转时方向会随之在横屏与竖屏之间变化
		// SCREEN_ORIENTATION_NOSENSOR：
		// 忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)
		// SCREEN_ORIENTATION_UNSPECIFIED：
		// 未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择
		// SCREEN_ORIENTATION_USER： 用户当前的首选方向
		setContentView(R.layout.activity_camera);
		initViews();
		setClick();
		// Create an instance of Camera
		try {
			mCamera = getCameraInstance(0);
			mPreview = new CameraPreview(this, mCamera);
			// Create our Preview view and set it as the content of our activity.
			preview = (FrameLayout) findViewById(R.id.frame);
			preview.addView(mPreview);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private void initViews() {
		img_back = (ImageView) findViewById(R.id.img_back);
		img_position = (ImageView) findViewById(R.id.img_position);
		img_camera = (ImageView) findViewById(R.id.img_camera);
	}

	private void setClick() {
		img_back.setOnClickListener(this);
		img_position.setOnClickListener(this);
		img_camera.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.img_camera:
			// get an image from the camera
			try {
				mCamera.takePicture(null, null, mPicture);
				releaseMediaRecorder();
				releaseCamera();
				mCamera = Camera.open(0);
				mPreview = new CameraPreview(this, mCamera);
				preview.removeAllViews();
				preview.addView(mPreview);
				currentNum = 0;
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			break;
		case R.id.img_position:
			if(currentNum==0){
				// get an image from the camera
				try {
					releaseMediaRecorder();
					releaseCamera();
					mCamera = Camera.open(1);
					mPreview = new CameraPreview(this, mCamera);
					preview.removeAllViews();
					preview.addView(mPreview);
					currentNum = 1;
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}else{
				try {
					releaseMediaRecorder();
					releaseCamera();
					mCamera = Camera.open(0);
					mPreview = new CameraPreview(this, mCamera);
					preview.removeAllViews();
					preview.addView(mPreview);
					currentNum = 0;
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 为了得到图，要使用Camera.takePicture()方法。 这个方法带了三个参数，用来接收来自Camera的数据。
	 * 为了接收JPEG格式的数据，必须要实现Camera.PictureCallback接口，来接 收图像数据，并写入到一个文件。
	 */
	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			Log.d(TAG, "pictureFile:"+pictureFile);
			if (pictureFile == null) {
				return;
			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}
	};

	/**
	 * 1.检查camera Check if this device has a camera 检查机器是否有照相功能
	 * 
	 * */
	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/**
	 * 
	 * A safe way to get an instance of the Camera object. 获取相机
	 * 
	 * **/
	public  Camera getCameraInstance(int i) {
		Camera c = null;
		try {
//			int i = c.getNumberOfCameras();
//			Log.d(TAG, c.getNumberOfCameras()+"");
			c = Camera.open(i); // attempt to get a Camera instance
			currentNum = 0;
			// Camera.open(int). //访问特定的摄像头
			// int i = c.getNumberOfCameras();
			// c.getCameraInfo(1, );
//			c.setFaceDetectionListener(new MyFaceDetectionListener());
//			Camera.Parameters params = c.getParameters();
//			List<String> focusModes = params.getSupportedFocusModes();
//			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//			  // Autofocus mode is s'upported自动对焦？
			
//			}
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	/** Create a file Uri for saving an image or video */
	private  Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private  File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"SUPING");
		
		//下面的地址会随着用户删除应用而删除数据
		//CameraActivity.this.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}
		return mediaFile;
	}
	
	/**释放Camera**/
	@Override
	protected void onPause() {
		super.onPause();
		try {
			releaseMediaRecorder();
			releaseCamera();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	private MediaRecorder mMediaRecorder;

	/** 关闭摄像 **/
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	/** 关闭相机 **/
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}
	
}
