package com.suping.i2_watch.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import com.suping.i2_watch.R;
import com.suping.i2_watch.entity.AbstractProtocolWrite;
import com.suping.i2_watch.entity.I2WatchProtocolDataForWrite;
import com.suping.i2_watch.entity.SportRemindProtocol;
import com.suping.i2_watch.view.AlertDialog;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;


public class SimpleBlueService extends AbstractSimpleBlueService {

	public final static String ACTION_DATA_STEP = "lite_data_step";
	public final static String ACTION_DATA_SYNC_TIME = "lite_data_sync_time";
	public final static String ACTION_DATA_CAMERA = "lite_data_camera";
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

	public final static String EXTRA_STEP = "extra_step";
	public final static String EXTRA_SLEEP = "extra_sleep";
	public final static String EXTRA_HEART_RATE = "EXTRA_HEART_RATE";
	public final static String EXTRA_CAMERA = "EXTRA_CAMERA";
	private AlertDialog diloag;
	private boolean oncePlayMusic = true;

	public static IntentFilter getIntentFilter() {
		IntentFilter intentFilter = AbstractSimpleBlueService.getIntentFilter();
		intentFilter.addAction(ACTION_DATA_STEP);
		intentFilter.addAction(ACTION_DATA_SYNC_TIME);
		intentFilter.addAction(ACTION_DATA_CAMERA);
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
		return intentFilter;
	}

	@Override
	public void parseData(byte[] data) {
	}

	@Override
	public void doSomeThingWhereDisconnected() {
		//断线提醒
		ring();
		mHander.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (player.isLooping()) {
					player.stop();
					player.release();
				}
			}
		}, 5000);
	}


	private boolean isOnce = true;
	private MediaPlayer player;
	/**
	 * 获取断线铃音
	 * 
	 * @return
	 * @throws IllegalStateException
	 * @throws Exception
	 * @throws IOException
	 */
	public void ring() {
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
	
	


}
