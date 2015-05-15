package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.suping.i2_watch.R;
import com.suping.i2_watch.util.SharedPreferenceUtil;

public class SignatureSetActivity extends Activity implements OnClickListener{
	private EditText ed_sign;
	private TextView tv_confirm;
	private TextView tv_cancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_signature_set);
		initViews();
		setClick();
	}
	
	private void initViews() {
		tv_confirm = (TextView) findViewById(R.id.tv_confirm);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		ed_sign = (EditText) findViewById(R.id.ed_sign);
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_close);
//		ImageSpan span = new ImageSpan(bitmap);
//		SpannableString spanString = new SpannableString("clear");
//		spanString.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		ed_sign.append(spanString);
		ed_sign.setText(getIntent().getBundleExtra("bundle").getString("value"));
		//选择所有内容
		ed_sign.selectAll();
		//将光标移到最后一位
		ed_sign.setSelection(ed_sign.getText().length());
	}
	private void setClick() {
		tv_confirm.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		ed_sign.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		   @Override
		   public void onFocusChange(View v, boolean hasFocus) {
		       //如果获得焦点，则弹出键盘
			   if (hasFocus) {
		            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		       }
		   }
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_confirm:
			String ed = ed_sign.getText().toString().trim();
			Bundle b = new Bundle();
			b.putString("ed", ed);
			Intent intent = new Intent(SignatureSetActivity.this,MenuActivity.class);
			intent.putExtras(b);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.tv_cancel:
			finish();
			break;

		default:
			break;
		}
	}
}
