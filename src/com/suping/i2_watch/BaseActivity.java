package com.suping.i2_watch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.service.AddressSaved;
import com.suping.i2_watch.service.XplBluetoothService;
import com.suping.i2_watch.service.xblue.XBlueBondedUtils;
import com.suping.i2_watch.service.xblue.XBlueBroadcastReceiver;
import com.suping.i2_watch.service.xblue.XBlueBroadcastUtils;
import com.suping.i2_watch.service.xblue.XBlueService;
import com.suping.i2_watch.service.xblue.XBlueUtils;
import com.suping.i2_watch.util.ToastUtils;
import com.suping.i2_watch.view.LoadingTextView;

/**
 * 未使用
 * 
 * @author Administrator
 *
 */
public abstract class BaseActivity extends FragmentActivity {
	protected XtremApplication mApplication;

	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;

	protected static AlertDialog syncSettingDialog;
	private Handler mhandler = new Handler() {

	};
	private SyncSettingBroadcastReceiver receiver = new SyncSettingBroadcastReceiver() {
		@Override
		public void doSyncSetting(final boolean isSyncSetting) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (isSyncSetting) {
						if (isRunningForeground(BaseActivity.this)) {
							if (syncSettingDialog == null) {
								syncSettingDialog = showLoadingDialog(getString(R.string.sycn_setting_ing), true);
							}
							mhandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									if (syncSettingDialog != null) {
										syncSettingDialog.dismiss();
										syncSettingDialog = null;
//										Log.e("", "同步设置超时");
									}
								}
							}, 15 * 1000);
						} else {
							settingNotify();
						}
					} else {
						if (syncSettingDialog != null) {
							syncSettingDialog.dismiss();
							syncSettingDialog = null;
						}
					}
				}
			});

		}
		
		

		@Override
		public void doOpenVirturlActivity() {
//			Intent intent = new Intent(BaseActivity.this, VirtualActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			startActivity(intent);
		}
	};
	

	
	
	
	
	private NotificationManager nm;

	private Notification syncSettingNotify;

	protected int mScreenHeight;
	protected float mDensity;
	protected List<AsyncTask<Void, Void, Boolean>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();

	/**
	 * 判断某个界面是否在前台
	 * 
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	public boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			// 应用程序位于堆栈的顶层
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 通过RunningAppProcessInfo类判断（不需要额外权限）：
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBackground(Context context) {

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//					Log.i("后台", appProcess.processName);
					return true;
				} else {
//					Log.i("前台", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}
	
	
	
	/**
	 * 是否在前台 < uses-permission android:name =“android.permission.GET_TASKS” />
	 * 
	 * @param context
	 * @return
	 */
	public boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName())) {
			return true;
		}

		return false;
	}

	public void settingNotify() {
		if (nm == null) {
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		Intent intent = new Intent(getApplicationContext(), Main.class);
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
		syncSettingNotify = new Notification.Builder(this).setSmallIcon(R.drawable.ic_small).setTicker(getString(R.string.sycn_setting_ing)).setContentTitle(getString(R.string.sync))
				.setContentText(getString(R.string.sycn_setting_ing)).setContentIntent(pi).setDefaults(Notification.DEFAULT_SOUND).setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setOnlyAlertOnce(true).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)).build();
		nm.notify(2, syncSettingNotify);
	}

	public void showIosDialog(Context context, String msg) {
		new com.suping.i2_watch.view.AlertDialog(context).builder().setMsg(msg).setCancelable(true).show();
	}

	public void showIosDialog(Context context, String msg, String title) {
		new com.suping.i2_watch.view.AlertDialog(context).builder().setMsg(msg).setTitle(title).setCancelable(true).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (XtremApplication) getApplication();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
		IntentFilter filter = new IntentFilter();
		filter.addAction(XplBluetoothService.ACTION_SYNC_SETTING);
		filter.addAction(XplBluetoothService.ACTION_VIR);
		registerReceiver(receiver, filter);
		
		

	}

	@Override
	protected void onResume() {
		if (syncSettingNotify != null) {
			nm.cancel(2);
		}
//		if (AddressSaved.isBinded(this)) {
//			if (!isXplConnected()) {
//				XplBluetoothService xplBluetoothService = getXplBluetoothService();
//				if (xplBluetoothService!=null) {
//					
//					xplBluetoothService.scanDevice(true);
//				}
//			}
//		}
		
		XBlueService xBlueService = getXBlueService();
		if (xBlueService!=null) {
			if (xBlueService.isAllConnected()) {
//				xBlueService.stopScan();
				xBlueService.stopScan();
				
			}else{
				xBlueService.startScan();
//				xBlueService.startScan();
//				HashSet<String> blues = XBlueUtils.getBlues(getApplicationContext());
//				if (blues!=null) {
//					Iterator<String> iterator = blues.iterator();
//					while (iterator.hasNext()) {
//						String next = iterator.next();
//						Log.e("", "next" + next);
//						BluetoothDevice remoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(next);
//						if (remoteDevice != null) {
//							int bondState = remoteDevice.getBondState();
//							if (bondState == BluetoothDevice.BOND_BONDED) {
//								System.out.println("已绑定");
//								xBlueService.connect(remoteDevice);
//								break;
//							} else if (bondState == BluetoothDevice.BOND_BONDING) {
////								System.out.println("绑定中?????????");
////								XBlueBondedUtils.cancelBondProcess(BluetoothDevice.class, remoteDevice);
//////								System.out.println("绑定中!!!!!!!!!!");
////								XBlueBondedUtils.removeBond(BluetoothDevice.class, remoteDevice);
//							} else if (bondState == BluetoothDevice.BOND_NONE) {
////								System.out.println("没有绑定");
////								cancelBondProcess(BluetoothDevice.class, remoteDevice);
////								System.out.println("绑定中!!!!!!!!!!");
////								removeBond(BluetoothDevice.class, remoteDevice);
//							}
//						}
//						
//					}
//				}
			}
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		clearAsyncTask();
		unregisterReceiver(receiver);

		if (syncSettingDialog != null) {
			syncSettingDialog.dismiss();
			syncSettingDialog = null;
		}
		super.onDestroy();
	}

	protected AlertDialog showLoadingDialog(String msg, boolean is) {
		View view = LayoutInflater.from(this).inflate(R.layout.view_loading, null);
		LoadingTextView tv = (LoadingTextView) view.findViewById(R.id.tv_loading);
		tv.setText(msg);
		AlertDialog.Builder b = new AlertDialog.Builder(this).setView(view).setCancelable(is);
		AlertDialog create = b.create();
		create.show();
		tv.start();
		return create;
	}

	/**
	 * 等待框
	 * 
	 * @param msg
	 * @return
	 */
	protected ProgressDialog showProgressDialog(String msg) {
		ProgressDialog pDialog;
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		pDialog.setMessage(msg);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
		return pDialog;
	}

	/**
	 * 等待框
	 * 
	 * @param msg
	 * @return
	 */
	protected ProgressDialog showProgressDialog(String msg, boolean icancel) {
		ProgressDialog pDialog;
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(icancel);
		pDialog.setMessage(msg);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
		return pDialog;
	}

	protected void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Boolean>> iterator = mAsyncTasks.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** 显示自定义Toast提示(来自res) **/
	protected void showCustomToast(int resId) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(getString(resId));
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	/** Debug输出Log日志 **/
	protected void showLogDebug(String tag, String msg) {
		Log.d(tag, msg);
	}

	/** Error输出Log日志 **/
	protected void showLogError(String tag, String msg) {
		Log.e(tag, msg);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 含有标题和内容的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message).show();
		return alertDialog;
	}

	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message, String positiveText, DialogInterface.OnClickListener onPositiveClickListener, String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener).show();
		return alertDialog;
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message, int icon, String positiveText, DialogInterface.OnClickListener onPositiveClickListener, String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message).setIcon(icon).setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener).show();
		return alertDialog;
	}

	/** 默认退出 **/
	protected void defaultFinish() {
		super.finish();
	}

//	protected XplBluetoothService getXplBluetoothService() {
//		XtremApplication app = (XtremApplication) getApplication();
//		return app.getXplBluetoothService();
//	}

//	protected boolean isXplConnected() {
////		XplBluetoothService xplBluetoothService = getXplBluetoothService();
////		if (xplBluetoothService != null) {
////			return xplBluetoothService.isBluetoothConnected();
////		}
////		return false;
//		return false;
//	}
	
	public XBlueService getXBlueService(){
		XtremApplication app = (XtremApplication) getApplication();
		return app.getXBlueService();
	}
	

//	protected void checkBlues() {
//		
////		runOnUiThread(new Runnable() {
////			@Override
////			public void run() {
////				XplBluetoothService xplBluetoothService = getXplBluetoothService();
////				if (xplBluetoothService != null) {
////					if (!xplBluetoothService.isBluetoothEnable()) {
////						// 蓝牙不可用
////						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////						enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////						startActivity(enableBtIntent);
////					} else {
////						if (!isXplConnected()) {
////							if (AddressSaved.isBinded(getApplicationContext())) {
////								xplBluetoothService.scanDevice(true);
////							}
////						}
////					}
////				}
////			}
////		});
//	}
}
