//package com.suping.i2_watch.menu;
//
//
//import java.util.Calendar;
//
//
//
//
//
//import com.suping.i2_watch.Main;
//import com.suping.i2_watch.R;
//
//import android.app.AlarmManager;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.media.RingtoneManager;
//import android.os.SystemClock;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.content.WakefulBroadcastReceiver;
//import android.util.Log;
//
///**
// *   一个WakefulBroadcastReceiver是广播接收器的一个特殊类型，它可以创建和管理你
// *   应用的PARITAL_WAKE_LOCK。一个WakeBroadcastReceiver接收到广播后
// *   将工作传递给Service（一个典型的IntentService），直到确保设备没有休眠。
// *   如果你在交接工作给服务的时候没有保持唤醒锁，在工作还没完成之前就允许设备休眠的话，将会出现一些你不愿意看到的情况
// *   
// * @author Administrator
// *
// */
//public class AlarmReceiver extends WakefulBroadcastReceiver{
//	private AlarmManager mAlarmManager;
//    private PendingIntent mPendingIntent;
//	@Override
//	public void onReceive(Context context, Intent intent) {
////		   int mReceivedID = Integer.parseInt(intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID));
////	        // Get notification title from Reminder Database
////	        ReminderDatabase rb = new ReminderDatabase(context);
////	        Reminder reminder = rb.getReminder(mReceivedID);
////	        String mTitle = reminder.getTitle();
//		
//		 // Create intent to open ReminderEditActivity on notification click
//		 //notification 打开时打开主页
//		Log.e("alarm", "自启动闹铃");
//        Intent editIntent = new Intent(context, Main.class);
//        editIntent.putExtra("data", "111");
//        PendingIntent mClick = PendingIntent.getActivity(context, 1, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Create Notification
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle(context.getResources().getString(R.string.app_name))
//                .setTicker("闹钟响了")  //可扩展
//                .setContentText("闹铃：12：00") //可扩展
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //声音
//                .setContentIntent(mClick) //点击进入对应activity
//                .setAutoCancel(true) 
//                .setOnlyAlertOnce(true);
//
//        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        nManager.notify(1, mBuilder.build());
//	}
//	
//	 public void setAlarm(Context context, Calendar calendar, int ID) {
//	        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//	        // Put Reminder ID in Intent Extra
//	        Intent intent = new Intent(context, this.getClass());
//	        intent.putExtra("EXTRA_REMINDER_ID", Integer.toString(ID));
//	        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//	        // Calculate notification time
//	        Calendar c = Calendar.getInstance();
//	        long currentTime = c.getTimeInMillis();
//	        long diffTime = calendar.getTimeInMillis() - currentTime;
//
//	        // Start alarm using notification time
//	        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
//	                SystemClock.elapsedRealtime() + diffTime,
//	                mPendingIntent);
//
//	        // Restart alarm if device is rebooted
//	        // 重启自启动
//	        ComponentName receiver = new ComponentName(context, this.getClass());
//	        PackageManager pm = context.getPackageManager();
//	        pm.setComponentEnabledSetting(receiver,
//	                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//	                PackageManager.DONT_KILL_APP);
//	    }
//	 	
//	 public void setRepeatAlarm(Context context, Calendar calendar, int ID, long RepeatTime) {
//	        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//	        // Put Reminder ID in Intent Extra
//	        Intent intent = new Intent(context, this.getClass());
//	        intent.putExtra("EXTRA_REMINDER_ID", Integer.toString(ID));
//	        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//	        // Calculate notification timein
//	        Calendar c = Calendar.getInstance();
//	        long currentTime = c.getTimeInMillis();
//	        long diffTime = calendar.getTimeInMillis() - currentTime;
//
//	        // Start alarm using initial notification time and repeat interval time
//	        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
//	                SystemClock.elapsedRealtime() + diffTime,
//	                RepeatTime , mPendingIntent);
//
//	        // Restart alarm if device is rebooted
//	        ComponentName receiver = new ComponentName(context, this.getClass());
//	        PackageManager pm = context.getPackageManager();
//	        pm.setComponentEnabledSetting(receiver,
//	                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//	                PackageManager.DONT_KILL_APP);
//	    }
//
//	    public void cancelAlarm(Context context, int ID) {
//	        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//	        // Cancel Alarm using Reminder ID
//	        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, this.getClass()), 0);
//	        mAlarmManager.cancel(mPendingIntent);
//
//	        // Disable alarm
//	        ComponentName receiver = new ComponentName(context, this.getClass());
//	        PackageManager pm = context.getPackageManager();
//	        pm.setComponentEnabledSetting(receiver,
//	                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//	                PackageManager.DONT_KILL_APP);
//	    }
//
//	 
//	 	
//}
