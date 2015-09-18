package com.suping.i2_watch.menu.async;

import com.suping.i2_watch.entity.LitePalManager;

import android.os.AsyncTask;

public class LoadHistoryAsyncTask extends AsyncTask<String, Void, int[]> {

	@Override
	protected int[] doInBackground(String... params) {
		int[] dataForSleep = LitePalManager.instance().getDataForSleep(params[0]);
		return dataForSleep;
	}

	@Override
	protected void onPostExecute(int[] result) {
		super.onPostExecute(result);
		if (mOnPostExecuteListener != null) {
			mOnPostExecuteListener.onPostExecute(result);
		}
	}

	public interface OnPostExecuteListener {
		public void onPostExecute(int[] result);
	}

	private OnPostExecuteListener mOnPostExecuteListener;

	public void setOnPostExecuteListener(OnPostExecuteListener l) {
		mOnPostExecuteListener = l;
	}
}
