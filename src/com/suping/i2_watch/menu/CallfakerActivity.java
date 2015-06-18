package com.suping.i2_watch.menu;

import com.suping.i2_watch.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CallfakerActivity extends Activity implements OnClickListener {
	
	private ImageView imgBack;
	private TextView tvTitle,tvPhone;
	private RelativeLayout rl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callfaker);
		initViews();
		setClick();
		tvTitle.setText("虚拟来电");
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		//from CallfakerFromActivity
		case 1:
			switch (resultCode) {
			case RESULT_OK:
				Bundle b = data.getExtras();
				String cursor = b.getString("cursor");
				tvPhone.setText(cursor);
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void initViews() {
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvPhone = (TextView) findViewById(R.id.tv_phone);
		rl = (RelativeLayout) findViewById(R.id.rl_call);
	}
	
	private void setClick() {
		imgBack.setOnClickListener(this);
		tvPhone.setOnClickListener(this);
		rl.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			Intent retrunToMain = new Intent(CallfakerActivity.this,
					MenuActivity.class);
			startActivity(retrunToMain);
			this.finish();
			overridePendingTransition(R.anim.activity_from_left_to_right_enter,
					R.anim.activity_from_left_to_right_exit);
			break;
		case R.id.rl_call:
			Log.e("callfaker", "!!!!!");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(CallfakerActivity.this,
							CallfakerFromActivity.class);
					startActivityForResult(intent, 1);
				}
			});
			break;

		default:
			break;
		}
	}
	// 连续按退出间隔时间
	private long exitTime = 0;
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
}
