package com.suping.i2_watch.view;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;

import com.suping.i2_watch.util.CamParaUtil;
import com.suping.i2_watch.util.FileUtil;
import com.suping.i2_watch.util.ImageUtil;

public class CameraInterface {
	private static final String TAG = "yanzi";
	private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean isPreviewing = false;
	private float mPreviwRate = -1f;
	private static CameraInterface mCameraInterface;

	public interface CamOpenOverCallback {
		public void cameraHasOpened();
	}

	private CameraInterface() {

	}

	public static synchronized CameraInterface getInstance() {
		if (mCameraInterface == null) {
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}

	/**
	 * 打开Camera
	 * 
	 * @param callback
	 */
	public void doOpenCamera(CamOpenOverCallback callback) {
		Log.i(TAG, "Camera open....");
		try {
			mCamera = Camera.open();
		} catch (Exception e) {
			Log.e("CameraInterface", "openCamera异常。。。");
		}
		Log.i(TAG, "Camera open over....");
		
		//开启预览？
		callback.cameraHasOpened();
	}

	/**
	 * 开启预览
	 * 
	 * @param holder
	 * @param previewRate
	 */
	public void doStartPreview(SurfaceHolder holder, float previewRate) {
		Log.i(TAG, "doStartPreview...");
		if (isPreviewing) {
			mCamera.stopPreview();
			return;
		}
		if (mCamera != null) {
			mParams = mCamera.getParameters();
			// setPictureFormat(int pixel_format):设置图片的格式，其取值为PixelFormat
			// YCbCr_420_SP、PixelFormatRGB_565或者PixelFormatJPEG。
			mParams.setPictureFormat(PixelFormat.JPEG);// 设置拍照后存储的图片格式
		
			
			CamParaUtil.getInstance().printSupportPictureSize(mParams);
			CamParaUtil.getInstance().printSupportPreviewSize(mParams);
			// 设置PreviewSize和PictureSize
			Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(mParams.getSupportedPictureSizes(),
					previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(mParams.getSupportedPreviewSizes(),
					previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);
			
			
			mCamera.setDisplayOrientation(90);//旋转90°
			CamParaUtil.getInstance().printSupportFocusMode(mParams);
			List<String> focusModes = mParams.getSupportedFocusModes();
			if (focusModes.contains("continuous-video")) {
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);//焦点
			}
			mCamera.setParameters(mParams);

			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();// 开启预览
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mCamera.release();
				mCamera = null;
				isPreviewing=false;
			}

			isPreviewing = true;
			mPreviwRate = previewRate;

			mParams = mCamera.getParameters(); // 重新get一次
			Log.i(TAG,
					"最终设置:PreviewSize--With = " + mParams.getPreviewSize().width + "Height = "
							+ mParams.getPreviewSize().height);
			Log.i(TAG,
					"最终设置:PictureSize--With = " + mParams.getPictureSize().width + "Height = "
							+ mParams.getPictureSize().height);
		}
	}

	/**
	 * 停止预览，释放Camera
	 */
	public void doStopCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			isPreviewing = false;
			mPreviwRate = -1f;
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 拍照
	 */
	public synchronized void doTakePicture() {
		Log.e("--- CameraInterface ----", "isPreviewing : " + isPreviewing);
		if (isPreviewing && (mCamera != null)) {
			mCamera.takePicture(new MyShutterCallback(), null, new MyJpegPictureCallback());
		}
	}

	/**  ↓↓↓↓↓↓↓↓↓↓↓↓↓ 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量  ↓↓↓↓↓↓↓↓↓↓↓↓↓  **/
	
	// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
	private class MyShutterCallback implements ShutterCallback {
	
		public void onShutter() {
			// TODO Auto-generated method stub
			Log.i(TAG, "myShutterCallback:onShutter...");
		}
	};
	
	// 拍摄的未压缩原数据的回调,可以为null
	private PictureCallback mRawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myRawCallback:onPictureTaken...");

		}
	};
	
	// 对jpeg图像数据的回调,最重要的一个回调
	private class MyJpegPictureCallback implements PictureCallback {
	
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if (null != data) {
				b = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
				mCamera.stopPreview();
				isPreviewing = false;
			}
			// 保存图片到sdcard
			if (null != b) {
				// 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation",
				// 90)失效。
				// 图片竟然不能旋转了，故这里要旋转下
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
//				FileUtil.saveBitmap(rotaBitmap);
				new SavePictureTask().execute(rotaBitmap);
			}
			// 再次进入预览
			mCamera.startPreview();
			isPreviewing = true;
		}
	};
	/**  ↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量   ↑↑↑↑↑↑↑↑↑↑↑↑↑↑   **/
	
	//异步存贮照片
	private class SavePictureTask extends AsyncTask<Bitmap, String, Boolean> {
		@Override
		protected Boolean doInBackground(Bitmap... params) {
			Log.i("CameraInterface", "SavePictureTask ...存贮预览" );
			try {
				FileUtil.saveBitmap(params[0]);
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mCamera.release();
				mCamera = null;
				isPreviewing=false;
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				Log.i("CameraInterface", "SavePictureTask ...存贮成功" );
			} else {
				Log.i("CameraInterface", "SavePictureTask ...存贮失败" );
			}
		}
	}
}
