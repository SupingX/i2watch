package com.suping.i2_watch.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.suping.i2_watch.R;

public class SignatureSetActivity extends Activity implements OnClickListener{
	private EditText edSign;
	private TextView tvConfirm;
	private TextView tvCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_signature_set);
		initViews();
		setClick();
	}
	
	private void initViews() {
		tvConfirm = (TextView) findViewById(R.id.tv_confirm);
		tvCancel = (TextView) findViewById(R.id.tv_cancel);
		edSign = (EditText) findViewById(R.id.ed_sign);
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_close);
//		ImageSpan span = new ImageSpan(bitmap);
//		SpannableString spanString = new SpannableString("clear");
//		spanString.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		ed_sign.append(spanString);
		String str = getIntent().getBundleExtra("bundle").getString("value");
		if(!str.equals(getResources().getString(R.string.signature_is_not_set))){
			edSign.setText(getIntent().getBundleExtra("bundle").getString("value"));
		}
		//选择所有内容
		edSign.selectAll();
		//将光标移到最后一位
		edSign.setSelection(edSign.getText().length());
	}
	private void setClick() {
		tvConfirm.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		edSign.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
			String ed = edSign.getText().toString().trim();
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
