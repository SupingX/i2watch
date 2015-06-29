package com.suping.i2_watch.view;

import android.os.AsyncTask;

public class SportRecordAsynTask extends AsyncTask<Void, Void, byte[]>{
	
	/**
	 * 从下位机下载信息
	 */
	public SportRecordAsynTask() {
		
		
		
	}


	/** 
	 * 该方法由后台进程进行调用，进行主要的耗时的那些计算。 
	 * 该方法在onPreExecute方法之后进行调用。当然在执行过程中 
	 * 我们可以每隔多少秒就调用一次publishProgress方法，更新 
	 * 进度信息 
	 * 
	 * 这里的Integer参数对应AsyncTask中的第一个参数   Params
     * 这里的String返回值对应AsyncTask的第三个参数  Result
	 */
	@Override
	protected byte[] doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/** 
	* doInBackground中调用了publishProgress之后，ui线程就会 
	* 调用该方法。你可以在这里动态的改变进度条的进度，让用户知道 
	* 当前的进度。 
	* 
	* 这里的Intege参数对应AsyncTask中的第二个参数  
	*/
	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}
	
	
	/** 
	* 当doInBackground执行完毕之后，由ui线程调用。可以在这里 
	* 返回我们计算的最终结果给用户。 
	* 这里的String参数对应AsyncTask中的第三个参数
	*/
	@Override
	protected void onPostExecute(byte[] result) {
//       tv.setText("异步操作执行结束" + result);  
		super.onPostExecute(result);
	}

}
