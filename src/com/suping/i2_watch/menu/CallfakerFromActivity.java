package com.suping.i2_watch.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.suping.i2_watch.R;

public class CallfakerFromActivity extends Activity implements OnClickListener {
	private TextView tvCancel;
	private TextView tvConfirm;
	private TextView tvRandom;
	private TextView tvChoose;
	private TextView tvCustom;
	private LinearLayout rlOk;
	private EditText edName, edPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFinishOnTouchOutside(false);// 设置为true点击区域外消失
		setContentView(R.layout.activity_callfaker_from);
		initViews();
		setListener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("onActivityResult","onActivityResult");
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case RESULT_OK:
				Uri contactUri = data.getData(); //
				Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
				cursor.moveToFirst();
				String num = getContactNumberFromCursor(cursor);
				String name = getContactNameFromCursor(cursor);
				Log.e("choose", num + " " + name);
				returnResult(name + " " + num,RESULT_OK);
				finish();
				break;
			case RESULT_CANCELED :
				returnResult(null,RESULT_CANCELED);
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
	
	
	@Override
	protected void onDestroy() {
		returnResult(null, RESULT_CANCELED);
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	
		case R.id.tv_positive:
			// 当选中自定义
			String name = edName.getText().toString().trim();
			if(name==null||name.equals("")){Toast.makeText(CallfakerFromActivity.this, "姓名为空", Toast.LENGTH_SHORT).show();return;}
			String phone = edPhone.getText().toString().trim();
			if(phone==null||phone.equals("")){Toast.makeText(CallfakerFromActivity.this, "号码为空", Toast.LENGTH_SHORT).show();return;}
			returnResult(name + " " + phone,RESULT_OK);
			break;
		case R.id.tv_negative:
			returnResult(null,RESULT_CANCELED);
			break;
		case R.id.tv_random:
			Log.e("listView", "随机--" );
			update(1);
			String value = getRandom();
			rlOk.setVisibility(View.GONE);
			returnResult(value,RESULT_OK);
			break;
		case R.id.tv_choose:
			Log.e("listView", "导入--");
			update(2);
			rlOk.setVisibility(View.GONE);
			Intent intentPick = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intentPick, 1);
//			finish();
			break;
		case R.id.tv_custom:
			Log.e("listView", "自定义--" );
			update(3);
			rlOk.setVisibility(View.VISIBLE);
			break;
		
		default:
			break;
		}
	}
	
	private void clear(Resources r){
		tvRandom.setTextColor(r.getColor(R.color.confirm));
		tvCustom.setTextColor(r.getColor(R.color.confirm));
		tvChoose.setTextColor(r.getColor(R.color.confirm));
	}
	
	private void update(int index){
		Resources r = getResources();
		clear(r);
		switch (index) {
		case 1:
			tvRandom.setTextColor(r.getColor(R.color.color_top_text_pressed));
			break;
		case 2:
			tvChoose.setTextColor(r.getColor(R.color.color_top_text_pressed));
			break;
		case 3:
			tvCustom.setTextColor(r.getColor(R.color.color_top_text_pressed));
			break;
		default:
			break;
		}
	}
	private void initViews() {
		tvCancel = (TextView) findViewById(R.id.tv_negative);
		tvConfirm = (TextView) findViewById(R.id.tv_positive);
		tvCustom = (TextView) findViewById(R.id.tv_custom);
		tvRandom = (TextView) findViewById(R.id.tv_random);
		tvChoose = (TextView) findViewById(R.id.tv_choose);
		edName = (EditText) findViewById(R.id.ed_name);
		edPhone = (EditText) findViewById(R.id.ed_phone);
		rlOk = (LinearLayout) findViewById(R.id.ok);
	}

	private void setListener() {
		tvCancel.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		tvChoose.setOnClickListener(this);
		tvCustom.setOnClickListener(this);
		tvRandom.setOnClickListener(this);
	}
		
		
	/**
	 * 获取联系人姓名+电话Map<Integer,Map<String,String>>
	 */
	private List<Map<String, String>> getCursorList() {
		List<Map<String, String>> list = new ArrayList<>();
		ContentResolver contentResolver = getContentResolver();
		// 获得所有的联系人
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cursor.moveToFirst()) {
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			do {
				// 获得联系人的ID号
				String contactId = cursor.getString(idColumn);
				// 获得联系人姓名
				String disPlayName = cursor.getString(displayNameColumn);
				//
				// 查看该联系人有多少个电话号码。如果没有这返回值为0
				int phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

				if (phoneCount > 0) {
					// // 获得联系人的电话号码列表
					Cursor phonesCursor = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
					if (phonesCursor.moveToFirst()) {
						do {
							// // 遍历所有的电话号码
							String phoneNumber = phonesCursor.getString(phonesCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							Log.v("通讯录", "联系人姓名： " + disPlayName + "，联系人电话: " + phoneNumber);
							Map<String, String> map = new HashMap<String, String>();
							map.put(disPlayName, phoneNumber);
							list.add(map);
						} while (phonesCursor.moveToNext());
					}
					phonesCursor.close();
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	/**
	 * 获取随机联系人
	 * 
	 * @return
	 */
	private String getRandom() {
		List<Map<String, String>> list = getCursorList();
		String name = "";
		String phone = "";
		Log.i("listView", "list.size()--" + list.size());
		if (!(list == null || list.size() == 0)) {
//			int i = (int) (Math.random() * list.size());
			int i = new Random().nextInt(list.size());
			Log.i("listView", "随机数--" + i);
			Log.i("listView", "随机联系人--" + list.get(i).toString());
			Iterator<Entry<String, String>> iter = list.get(i).entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> s = iter.next();
				name = s.getKey();
				phone = s.getValue();
				Log.i("listView", "随机：" +name + "--〉" + phone);
			}
		} else {
			Log.e("listView", "联系人列表为空...");
		}
		return name + " " + phone;
	}

	/**
	 * 获取联系人名称
	 * 
	 * @param cursor
	 * @return 名称
	 */
	private String getContactNameFromCursor(Cursor cursor) {
		String name = "";
		name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		return name;
	}

	/**
	 * 获取联系人号码
	 * 
	 * @param cursor
	 * @return 号码
	 */
	private String getContactNumberFromCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String result = "";
		if (phoneNum > 0) {
			// 获得联系人的ID号
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			// 获得联系人电话的cursor
			Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
			if (phone.moveToFirst()) {
				for (; !phone.isAfterLast(); phone.moveToNext()) {
					int indexNumber = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					String phoneNumber = phone.getString(indexNumber);
					result = phoneNumber;
				}
				if (!phone.isClosed()) {
					phone.close();
				}
			}
		}
		return result;
	}

	/**
	 * 返回联系人
	 * 
	 * @author Administrator
	 * 
	 */
	private void returnResult(String value,int result) {
		Intent intentRandom  = new Intent(CallfakerFromActivity.this, CallfakerActivity.class);
		Bundle b;
		if (value!=null) {
			b = new Bundle();
			b.putString("cursor", value);
			intentRandom.putExtras(b);
		}
		setResult(result, intentRandom);
		finish();
	}
}
