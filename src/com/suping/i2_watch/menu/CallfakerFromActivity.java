package com.suping.i2_watch.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suping.i2_watch.R;

public class CallfakerFromActivity extends Activity {
	private TextView tvCancel;
	private TextView tvConfirm;
	private ListView mListView;
	private int selectedPosition;
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
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case RESULT_OK:

				Uri contactUri = data.getData(); //
				Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
				cursor.moveToFirst();
				String num = getContactNumberFromCursor(cursor);
				String name = getContactNameFromCursor(cursor);
				Log.e("custom", num + " " + name);
				mListView.setItemChecked(selectedPosition, true);
				returnResult(name + " " + num);
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
		tvCancel = (TextView) findViewById(R.id.tv_negative);
		tvConfirm = (TextView) findViewById(R.id.tv_positive);
		mListView = (ListView) findViewById(R.id.listV);
		edName = (EditText) findViewById(R.id.ed_name);
		edPhone = (EditText) findViewById(R.id.ed_phone);
		mListView.setAdapter(new ArrayAdapter<String>(CallfakerFromActivity.this,
				android.R.layout.select_dialog_singlechoice, new String[] { "通讯录随机抽取", "从通讯录中选择", "自定义联系人" }));
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// mListView.setItemChecked(0, true);
		rlOk = (LinearLayout) findViewById(R.id.ok);
	}

	private void setListener() {
		// 退出
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 确定
		tvConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 当选中自定义
				String name = edName.getText().toString().trim();
				String phone = edPhone.getText().toString().trim();
				returnResult(name + " " + phone);

			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					Log.e("listView", "随机--" + position);
					rlOk.setVisibility(View.GONE);
					selectedPosition = 0;
					String value = getRandom();
					returnResult(value);
					break;
				case 1:
					Log.e("listView", "导入--" + position);
					rlOk.setVisibility(View.GONE);
					selectedPosition = 1;
					Intent intentPick = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intentPick, 1);
					break;
				case 2:
					Log.e("listView", "自定义--" + position);
					rlOk.setVisibility(View.VISIBLE);
					selectedPosition = 2;
					break;
				default:
					break;
				}

			}
		});
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
							Log.e("通讯录", "联系人姓名： " + disPlayName + "，联系人电话: " + phoneNumber);
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
		if (!(list == null || list.size() == 0)) {
			int i = (int) (Math.random() * list.size()) + 1;
			Log.i("listView", "随机联系人--" + i);
			Log.i("listView", "随机联系人--" + list.get(i).toString());
			Iterator<Entry<String, String>> iter = list.get(i).entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> s = iter.next();
				name = s.getKey();
				phone = s.getValue();
				Log.w("listView", name + "==" + phone);
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
	private void returnResult(String value) {
		Intent intentRandom = new Intent(CallfakerFromActivity.this, CallfakerActivity.class);
		Bundle b = new Bundle();
		b.putString("cursor", value);
		intentRandom.putExtras(b);
		setResult(RESULT_OK, intentRandom);
		finish();
	}
}
