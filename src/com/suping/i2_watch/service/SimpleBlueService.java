package com.suping.i2_watch.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.android.internal.telephony.ITelephony;
import com.suping.i2_watch.Main;
import com.suping.i2_watch.R;
import com.suping.i2_watch.entity.AbstractProtocolWrite;
import com.suping.i2_watch.entity.HistorySleep;
import com.suping.i2_watch.entity.HistorySport;
import com.suping.i2_watch.entity.I2WatchProtocolDataForNotify;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.entity.SportRemindProtocol;
import com.suping.i2_watch.util.DataUtil;
import com.suping.i2_watch.util.L;
import com.suping.i2_watch.view.AlertDialog;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.cardemulation.OffHostApduService;
import android.telephony.TelephonyManager;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class SimpleBlueService extends AbstractSimpleBlueService {

	public final static String ACTION_DATA_STEP_AND_CAL = "lite_data_step";
	public final static String ACTION_DATA_SYNC_TIME = "lite_data_sync_time";
	public final static String ACTION_CAMERA = "lite_data_camera";
	public final static String ACTION_DATA_MUSIC = "lite_data_music";
	public final static String ACTION_DATA_REMIND = "lite_data_remind";
	public final static String ACTION_DATA_HEART_RATE = "lite_data_heart_rate";
	public final static String ACTION_DATA_HISTORY_HEART_RATE = "lite_data_history_heart_rate";
	public final static String ACTION_DATA_HISTORY_STEP = "lite_data_history_step";
	public final static String ACTION_DATA_HISTORY_SLEEP = "lite_data_history_sleep";
	public final static String ACTION_DATA_HISTORY_DISTANCE = "lite_data_history_distance";
	public final static String ACTION_DATA_HISTORY_CAL = "lite_data_history_cal";
	public final static String ACTION_DATA_HISTORY_SPORT_TIME = "lite_data_history_sport_time";
	public final static String ACTION_DATA_HISTORY_SLEEP_FOR_TODAY = "lite_data_history_sleep_for_today";
	public final static String ACTION_SYNC_START = "ACTION_SYNC_START";
	public final static String ACTION_SYNC_END = "ACTION_SYNC_END";

	public final static String EXTRA_STEP_AND_CAL = "extra_step";
	public final static String EXTRA_SLEEP = "extra_sleep";
	public final static String EXTRA_HEART_RATE = "EXTRA_HEART_RATE";
	public final static String EXTRA_CAMERA = "EXTRA_CAMERA";
	private AlertDialog diloag;
	private boolean oncePlayMusic = true;

	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = AbstractSimpleBlueService.getIntentFilter();
		intentFilter.addAction(ACTION_DATA_STEP_AND_CAL);
		intentFilter.addAction(ACTION_DATA_SYNC_TIME);
		intentFilter.addAction(ACTION_CAMERA);
		intentFilter.addAction(ACTION_DATA_MUSIC);
		intentFilter.addAction(ACTION_DATA_REMIND);
		intentFilter.addAction(ACTION_DATA_HEART_RATE);
		intentFilter.addAction(ACTION_DATA_HISTORY_HEART_RATE);
		intentFilter.addAction(ACTION_DATA_HISTORY_STEP);
		intentFilter.addAction(ACTION_DATA_HISTORY_SLEEP);
		intentFilter.addAction(ACTION_DATA_HISTORY_DISTANCE);
		intentFilter.addAction(ACTION_DATA_HISTORY_CAL);
		intentFilter.addAction(ACTION_DATA_HISTORY_SPORT_TIME);
		intentFilter.addAction(ACTION_DATA_HISTORY_SLEEP_FOR_TODAY);
		intentFilter.addAction(ACTION_SYNC_START);
		intentFilter.addAction(ACTION_SYNC_END);
		return intentFilter;
	}

	@Override
	public void parseData(byte[] data) {
		I2WatchProtocolDataForNotify notify = I2WatchProtocolDataForNotify.instance();
		L.i(getClass(), "来自手环的收据 ：" + DataUtil.byteToHexString(data));
		switch (notify.getTypeFromData(data)) {
		case I2WatchProtocolDataForNotify.NOTIFY_PHOTO:
			sendBroadcastForPhoto();

			break;
		case I2WatchProtocolDataForNotify.NOTIFY_STEP_AND_CAL:
			long[] notifyStepAndCal = notify.notifyStepAndCal(data);
			L.i("计步：" + notifyStepAndCal[0]);
			L.i("卡洛里" + notifyStepAndCal[1]);
			L.i("时间" + notifyStepAndCal[2]);
			if (notifyStepAndCal != null) {
				sendBroadcastForStepAndCal(notifyStepAndCal);
			}
			break;

		case I2WatchProtocolDataForNotify.NOTIFY_HISTORY:
			final Object notifyHistory = notify.notifyHistory(data);
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (notifyHistory != null) {
						if (notifyHistory instanceof HistorySleep) {
							HistorySport sport = isExitSportHistory((HistorySport) notifyHistory);// 数据库有
							if (sport != null) {
								sport.setSportStep_1(((HistorySport) notifyHistory).getSportStep_1());
								sport.setSportStep_2(((HistorySport) notifyHistory).getSportStep_2());
								sport.setSportStep_3(((HistorySport) notifyHistory).getSportStep_3());
								sport.setSportTime_1(((HistorySport) notifyHistory).getSportTime_1());
								sport.setSportTime_2(((HistorySport) notifyHistory).getSportTime_2());
								sport.setSportTime_3(((HistorySport) notifyHistory).getSportTime_3());
								sport.save();
							} else {
								((HistorySport) notifyHistory).save();
							}
						} else if (notifyHistory instanceof HistorySleep) {
							HistorySleep sleep = isExitSleepHistory((HistorySleep) notifyHistory);
							if (sleep != null) {// 数据库有
								sleep.setSleepOneself_1(((HistorySleep) notifyHistory).getSleepOneself_1());
								sleep.setSleepOneself_2(((HistorySleep) notifyHistory).getSleepOneself_2());
								sleep.setSleepOneself_3(((HistorySleep) notifyHistory).getSleepOneself_3());
								sleep.setSleepAwak_1(((HistorySleep) notifyHistory).getSleepAwak_1());
								sleep.setSleepAwak_2(((HistorySleep) notifyHistory).getSleepAwak_2());
								sleep.setSleepAwak_3(((HistorySleep) notifyHistory).getSleepAwak_3());
								sleep.setSleepLight_1(((HistorySleep) notifyHistory).getSleepLight_1());
								sleep.setSleepLight_2(((HistorySleep) notifyHistory).getSleepLight_2());
								sleep.setSleepLight_3(((HistorySleep) notifyHistory).getSleepLight_3());
								sleep.setSleepDeep_1(((HistorySleep) notifyHistory).getSleepDeep_1());
								sleep.setSleepDeep_2(((HistorySleep) notifyHistory).getSleepDeep_2());
								sleep.setSleepDeep_3(((HistorySleep) notifyHistory).getSleepDeep_3());
								sleep.save();
							} else {
								((HistorySleep) notifyHistory).save();
							}
						}
					} else {
						L.v("数据null");
					}
				}
			});
			break;
		// case I2WatchProtocolDataForNotify.NOTIFY_INCOMING_REJECT:
		case I2WatchProtocolDataForNotify.NOTIFY_INCOMING:
			L.i("来电拒接");
			// 来电拒接
			endCall();
			break;
		case I2WatchProtocolDataForNotify.NOTIFY_INCOMING_SILENCE:
			L.i("来电静音");
			// 来电静音
			toggleRingerMute(getApplicationContext());
			break;

		case I2WatchProtocolDataForNotify.NOTIFY_INCOMING_CALLED:
			L.i("已接来电");
			// 已接来电
			break;
		case I2WatchProtocolDataForNotify.NOTIFY_SYNC_HISTORY:
			//同步开始 同步结束
			int notifySyncState = notify.notifySyncState(data);
			if (notifySyncState==1) {
				L.i("开始同步");
			}else if (notifySyncState==0) {
				L.i("同步结束");
			}
		
			break;

		default:
			break;
		}

	}

	private HistorySport isExitSportHistory(HistorySport sport) {
		List<HistorySport> sports = DataSupport.where("year=? and month =? and day = ? and hour = ? ", sport.getYear(), sport.getMonth(), sport.getDay(), sport.getHour()).find(HistorySport.class);
		if (sports != null && sports.size() > 0) {
			return sports.get(0);
		} else {
			return null;
		}
	}

	private HistorySleep isExitSleepHistory(HistorySleep sleep) {
		List<HistorySleep> sleeps = DataSupport.where("year=? and month =? and day = ? and hour = ? ", sleep.getYear(), sleep.getMonth(), sleep.getDay(), sleep.getHour()).find(HistorySleep.class);
		if (sleeps != null && sleeps.size() > 0) {
			return sleeps.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 挂电话
	 */
	private void endCall() {

		Class<TelephonyManager> c = TelephonyManager.class;
		try {
			Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = null;
			iTelephony = (ITelephony) getITelephonyMethod.invoke((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE), (Object[]) null);
			iTelephony.endCall();
		} catch (Exception e) {
		}

	}

	private static int previousMuteMode = -1;

	/**
	 * 来电静音
	 *
	 * @param context
	 */
	private void toggleRingerMute(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (previousMuteMode == -1) {
			previousMuteMode = am.getRingerMode();
			am.setRingerMode(0);
		}
		am.setRingerMode(previousMuteMode);
		previousMuteMode = -1;
	}

	private void sendBroadcastForStepAndCal(long[] notifyStepAndCal) {
		Intent intent = new Intent(ACTION_DATA_STEP_AND_CAL);
		intent.putExtra(EXTRA_STEP_AND_CAL, notifyStepAndCal);
		sendBroadcast(intent);
	}

	private void sendBroadcastForPhoto() {
		Intent intent = new Intent(ACTION_CAMERA);
		sendBroadcast(intent);
	}
	
	private void sendBroadcastForSyncStart() {
		Intent intent = new Intent(ACTION_SYNC_START);
		sendBroadcast(intent);
	}
	private void sendBroadcastForSyncOver() {
		Intent intent = new Intent(ACTION_SYNC_END);
		sendBroadcast(intent);
	}

	@Override
	public void doSomeThingWhereDisconnected() {
		// 断线提醒
		prepareRing();
		mHander.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (player != null && player.isLooping()) {
					player.stop();
					player.release();
				}
			}
		}, 5000);
		
		if (dialogSyncSetting!=null && dialogSyncSetting.isShowing()) {
			dialogSyncSetting.dismiss();
		}
	}

	private boolean isOnce = true;
	private MediaPlayer player;
	private ProgressDialog dialogSyncSetting;

	/**
	 * 获取断线铃音
	 * 
	 * @return
	 * @throws IllegalStateException
	 * @throws Exception
	 * @throws IOException
	 */
	public void prepareRing() {
		try {
			player = MediaPlayer.create(getApplicationContext(), R.raw.a1);
			player.setLooping(true);
			player.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					player.start();
				}
			});
			player.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doServicediscovered() {
		mHander.postDelayed((new Runnable() {
			
			@Override
			public void run() {
				doUpdateSetting();
			}
		}),2000);

	}

	private void doUpdateSetting() {
			dialogSyncSetting = new ProgressDialog(getApplicationContext());
			dialogSyncSetting.setCancelable(false);
			dialogSyncSetting.setMessage("同步手机数据 ...");
			dialogSyncSetting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialogSyncSetting.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialogSyncSetting.show();
		if (isBinded() && getConnectState() == BluetoothProfile.STATE_CONNECTED) {
			L.e("开始同步设置");
			// 1.运动提醒
			byte[] byteActivityRemindSync = I2WatchProtocolDataForWrite.protocolDataForActivityRemindSync(getApplicationContext()).toByte();
			// writeCharacteristic(byteActivityRemindSync);

			// 2.来电提醒
			byte[] protocolForCallingAlarmPeriodSync = I2WatchProtocolDataForWrite.protocolForCallingAlarmPeriodSync(getApplicationContext()).toByte();
			// writeCharacteristic(protocolForCallingAlarmPeriodSync);

			// 3.防丢提醒
			byte[] hexDataForLostOnoffI2Watch = I2WatchProtocolDataForWrite.hexDataForLostOnoffI2Watch(getApplicationContext());
			// writeCharacteristic(hexDataForLostOnoffI2Watch);

			// 4.闹钟
			byte[] protocolDataForClockSync = I2WatchProtocolDataForWrite.protocolDataForClockSync(getApplicationContext()).toByte();
			writeCharacteristic(protocolDataForClockSync);

			// 5.睡眠时间
			byte[] hexDataForSleepPeriodSync = I2WatchProtocolDataForWrite.hexDataForSleepPeriodSync(getApplicationContext());
			// writeCharacteristic(hexDataForSleepPeriodSync);

			// 6.亮度
			byte[] hexDataDecrease = I2WatchProtocolDataForWrite.hexDataForUpdateBrightness(getApplicationContext());
			// writeCharacteristic(hexDataDecrease);

			// 7.个性签名
			byte[] hexDataForSignatureSync = I2WatchProtocolDataForWrite.hexDataForSignatureSync(getApplicationContext());
			// writeCharacteristic(hexDataForSignatureSync);

			// 8.时间同步
			byte[] hexDataForTimeSync = I2WatchProtocolDataForWrite.hexDataForTimeSync(new Date(), getApplicationContext());
			// writeCharacteristic(hexDataForTimeSync);
			//9 .今天的数据
			byte[] hexDataForGetHistoryType = I2WatchProtocolDataForWrite.hexDataForGetHistoryType(1, 0);
			List<byte[]> values = new ArrayList<>();
			values.add(hexDataForTimeSync);
			 values.add(byteActivityRemindSync);
			 values.add(protocolForCallingAlarmPeriodSync);
			 values.add(hexDataForLostOnoffI2Watch);
			 values.add(protocolDataForClockSync);
			 values.add(hexDataForSleepPeriodSync);
			values.add(hexDataDecrease);
			values.add(hexDataForSignatureSync);
			values.add(hexDataForGetHistoryType);
			
			writeValueToDevice(values);
			L.e("同步设置结束");
		}
		
		mHander.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (dialogSyncSetting!=null && dialogSyncSetting.isShowing()) {
					dialogSyncSetting.dismiss();
				}
			}
		}, 30*1000);
	}

	@Override
	public void doWriteOver() {
		if (dialogSyncSetting!=null && dialogSyncSetting.isShowing()) {
			dialogSyncSetting.dismiss();
		}
	}
}
