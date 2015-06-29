package com.suping.i2_watch.menu.db;

import android.util.Log;


public class DbUtil {
	public static int[] getData(byte[]data){
		//String value = DataUtil.getStringByBytes(data);
		//0x 25 00 000290ff 0000900f 00039fff 00049f0f 0000
		String value ="2500000290ff0000900f00039fff00049f0f0000";
		String protocol  = value.substring(0,2); // 历史数据协议头   [0,2)
		String dateNum =  value.substring(2,4); //最新历史数据TAG [2,4)
		String minData1 = value.substring(4,12); //1分钟
//			//0002 90ff
//			//00 00 00 00 00 00 00 10 |  10 01  | 00 00 11 11 11 11
//			String minBinary1 = DataUtil.HexToBinary(minData1);
//			//31~16 
//			//15
//			//14~12
//			//11~0
//			String minValue1 = minBinary1.substring(minBinary1.length()-12, minBinary1.length());
//			String minNum1 = minBinary1.substring(minBinary1.length()-16, minBinary1.length()-12);
//			String min1 = minBinary1.substring(0,minBinary1.length()-16 );
//			
//			History h = new History();
//			h.setHistoryDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
			
		String minData2 = value.substring(12,20);//2分钟
		String minData3 = value.substring(20,28);//3分钟
		String minData4 = value.substring(28,36);//4分钟
		String other = value.substring(36,39); //其他
		
		Log.v("DbUtil", "------------------data---------------------");
		Log.v("DbUtil", "protocol : " + protocol);
		Log.v("DbUtil", "dateNum : " + dateNum);
		Log.v("DbUtil", "minData1 : " + minData1);
//		Log.i("DbUtil", "--minBinary1 : " + minBinary1);
//		Log.e("DbUtil", "----minValue1 : " + minValue1);
//		Log.e("DbUtil", "----minNum1 : " + minNum1);
//		Log.e("DbUtil", "----min1 : " + min1);
		Log.v("DbUtil", "minData2 : " + minData2);
		Log.v("DbUtil", "minData3 : " + minData3);
		Log.v("DbUtil", "minData4 : " + minData4);
		Log.v("DbUtil", "other : " + other);
		Log.v("DbUtil", "------------------data---------------------");
		
		
//		Log.e("DbUtil","历史数据1 ---->" + Integer.parseInt(minData1, 16));//十六进制转十进制
		
		int [] values = new int[]{Integer.parseInt(minData1, 16)
							,Integer.parseInt(minData2, 16)
							,Integer.parseInt(minData3, 16)
							,Integer.parseInt(minData4, 16)};
//		return new String[]{minData1,minData2,minData3,minData4};
		return values;
	}
	
	/**
	 * 获取当前的TAG
	 * @param data
	 * @return
	 */
	public static int getTag(byte[] data) {
		// String value = DataUtil.getStringByBytes(data);
		// 0x 25 00 000290ff 0000900f 00039fff 00049f0f 0000
		String value = "2500000290ff0000900f00039fff00049f0f0000";
		String dateTag = value.substring(2, 4); // 最新历史数据NUM [2,4)
		return Integer.valueOf(dateTag);
	}
}
