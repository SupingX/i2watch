/*package com.suping.i2_watch.menu.async;

import android.os.AsyncTask;

import com.suping.i2_watch.entity.LitePalManager;

public class LoadSleepHistoryAsyncTask extends AsyncTask<String, Void, int[]> {
		
	@Override
	protected int[] doInBackground(String... params) {
		int[] dataForSleep = LitePalManager.instance().getDataForSleep(params[0]);
		return dataForSleep;
	}
	
	@Override
	protected void onPreExecute() {
		mOnPostExecuteListener.onPreExecute();
		super.onPreExecute();
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
		public void onPreExecute();
	}

	private OnPostExecuteListener mOnPostExecuteListener;

	public void setOnPostExecuteListener(OnPostExecuteListener l) {
		mOnPostExecuteListener = l;
	}
}
*/