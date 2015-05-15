package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.Main;
import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class MenuActivity extends Activity implements OnClickListener {
	private ImageView img_back;
	private TextView tv_title,tv_signset_value,tv_bright_value;
	private Button btn_increase,btn_decrease;
	private CheckBox cb_reminder,cb_reminder_nf;
	private RelativeLayout rl_reminder,rl_sleep,rl_clock,rl_camera,rl_signset;
	private long exitTime = 0;
	private static final int REQ_SIGNSET = 0x200200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initViews();
		
		setClick();
	}

	private void initViews() {
		img_back = (ImageView) findViewById(R.id.img_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_signset_value = (TextView) findViewById(R.id.tv_signset_value);
		tv_bright_value = (TextView) findViewById(R.id.tv_bright_value);
		btn_increase = (Button) findViewById(R.id.btn_increase);
		btn_decrease = (Button) findViewById(R.id.btn_decrease);
		rl_reminder = (RelativeLayout) findViewById(R.id.rl_reminder);
		rl_sleep = (RelativeLayout) findViewById(R.id.rl_sleep);
		rl_clock = (RelativeLayout) findViewById(R.id.rl_clock);
		rl_camera = (RelativeLayout) findViewById(R.id.rl_camera);
		rl_signset = (RelativeLayout) findViewById(R.id.rl_signset);
		cb_reminder = (CheckBox) findViewById(R.id.cb_reminder);
		cb_reminder_nf = (CheckBox) findViewById(R.id.cb_reminder_nf);
		
		tv_title.setText("Menu");
		String signature = (String) SharedPreferenceUtil
				.get(getApplicationContext(), "signature", "signature is not set");
		tv_signset_value.setText(signature);
		
		String bright = (String) SharedPreferenceUtil
				.get(getApplicationContext(), "bright", "5");
		tv_bright_value.setText(bright);
		
		boolean reminder =  (boolean) SharedPreferenceUtil
				.get(getApplicationContext(), "reminder", false);
		cb_reminder.setChecked(reminder);
		
		boolean reminder_nf =  (boolean) SharedPreferenceUtil
				.get(getApplicationContext(), "reminder_nf", false);
		cb_reminder_nf.setChecked(reminder_nf);
		
	}

	private void setClick() {
		img_back.setOnClickListener(this);
		tv_title.setOnClickListener(this);
		rl_reminder.setOnClickListener(this);
		rl_sleep.setOnClickListener(this);
		rl_clock.setOnClickListener(this);
		rl_camera.setOnClickListener(this);
		rl_signset.setOnClickListener(this);
		btn_increase.setOnClickListener(this);
		btn_decrease.setOnClickListener(this);
		cb_reminder.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					SharedPreferenceUtil.put(getApplicationContext(), "reminder", true);
				}else{
					SharedPreferenceUtil.put(getApplicationContext(), "reminder", false);
				}
			}
		});
		cb_reminder_nf.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					SharedPreferenceUtil.put(getApplicationContext(), "reminder_nf", true);
				}else{
					SharedPreferenceUtil.put(getApplicationContext(), "reminder_nf", false);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent retrunToMain = new Intent(MenuActivity.this, Main.class);
			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);

			break;

		case R.id.rl_reminder:
			Intent reminderIntent = new Intent(MenuActivity.this,
					ReminderActivity.class);
			startActivity(reminderIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.rl_sleep:
			Intent sleepIntent = new Intent(MenuActivity.this,
					SleepMonitorActivity.class);
			startActivity(sleepIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.rl_clock:
			Intent clockIntent = new Intent(MenuActivity.this,
					ClockActivity.class);
			startActivity(clockIntent);
			this.finish();
			overridePendingTransition(R.anim.activity_from_right_to_left_enter,
					R.anim.activity_from_right_to_left_exit);
			break;
		case R.id.rl_camera:
			Toast.makeText(MenuActivity.this, "camera", 0).show();
			Intent intent = new Intent(MenuActivity.this,CameraActivity.class);
			startActivity(intent);
			
			
			break;
		case R.id.rl_signset:
			Intent signsetIntent = new Intent(MenuActivity.this,SignatureSetActivity.class);
			String value = tv_signset_value.getText().toString().trim();
			Bundle b = new Bundle();
			b.putString("value", value);
			signsetIntent.putExtra("bundle", b);
			startActivityForResult(signsetIntent, REQ_SIGNSET);
			break;
			
		case R.id.btn_decrease:
			int bright1 = Integer.valueOf(tv_bright_value.getText().toString().trim());
			if(bright1==1){
				tv_bright_value.setText(String.valueOf(10));
			}else{
				tv_bright_value.setText(String.valueOf(bright1-1));
			}
			SharedPreferenceUtil.put(getApplicationContext(), "bright", tv_bright_value.getText().toString().trim());
			break;
			
		case R.id.btn_increase:
			int bright2 = Integer.valueOf(tv_bright_value.getText().toString().trim());
			if(bright2==10){
				tv_bright_value.setText(String.valueOf(1));
			}else{
				tv_bright_value.setText(String.valueOf(bright2+1));
			}
			SharedPreferenceUtil.put(getApplicationContext(), "bright", tv_bright_value.getText().toString().trim());
			break;
		default:
			break;
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		//from SignatureSetActivity
		case REQ_SIGNSET:
			if (resultCode == RESULT_OK) {
				String value = data.getExtras().getString("ed");
				tv_signset_value.setText(value);
				SharedPreferenceUtil.put(getApplicationContext(), "signature", value);
			} else if (resultCode == RESULT_CANCELED) {
			} else {
			}
			break;
		default:
			break;
		}
	}
}
