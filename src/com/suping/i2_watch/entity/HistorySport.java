package com.suping.i2_watch.entity;


import org.litepal.crud.DataSupport;


/**
 * * 协议［30位］ ： 0x  25       xx           xx         xxxxxxxx  xxxxxxxx   xxxxxxxx 　
 * * 表示:         0x  [协议头]　[历史天数0~4] [小时0~23] [dat0-19] [dat20-39] [dat40-59] 　
 * * 
 * * 其中每个时段dat包含32位 　
 * * [bit0-bit15]   [翻身次数|运动步数] 　
 * * [bit16-bit20] 　[清醒时间|运动时间] 　
 * * [bit21-bit25]　[浅睡时间] 　
 * * [bit26-bit30] 　[深睡时间] 　
 * * [bit31]　　　　 [模式 1:sleep | 0:step] 　
 * *
 * 
 * @author Administrator
 *
 */
public class HistorySport extends DataSupport {

	private int id;
	/** 记录日期 **/
	private String year;
	private String month;
	private String day;
	/**0~23**/
	private String hour;
	/** 1个小时内3个值  运动步数**/
	private int sportStep_1;
	private int sportStep_2;
	private int sportStep_3;
	/** 1个小时内3个值  运动时间　**/
	private int sportTime_1;
	private int sportTime_2;
	private int sportTime_3;
	
	
	
	public HistorySport(String year, String month, String day, String hour, int sportStep_1, int sportStep_2, int sportStep_3, int sportTime_1, int sportTime_2, int sportTime_3) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.sportStep_1 = sportStep_1;
		this.sportStep_2 = sportStep_2;
		this.sportStep_3 = sportStep_3;
		this.sportTime_1 = sportTime_1;
		this.sportTime_2 = sportTime_2;
		this.sportTime_3 = sportTime_3;
	}

	public int getSportTime_1() {
		return sportTime_1;
	}

	public void setSportTime_1(int sportTime_1) {
		this.sportTime_1 = sportTime_1;
	}

	public int getSportTime_2() {
		return sportTime_2;
	}

	public void setSportTime_2(int sportTime_2) {
		this.sportTime_2 = sportTime_2;
	}

	public int getSportTime_3() {
		return sportTime_3;
	}

	public void setSportTime_3(int sportTime_3) {
		this.sportTime_3 = sportTime_3;
	}

	public String getYear() {
		return year;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public int getSportStep_1() {
		return sportStep_1;
	}

	public void setSportStep_1(int sportStep_1) {
		this.sportStep_1 = sportStep_1;
	}

	public int getSportStep_2() {
		return sportStep_2;
	}

	public void setSportStep_2(int sportStep_2) {
		this.sportStep_2 = sportStep_2;
	}

	public int getSportStep_3() {
		return sportStep_3;
	}

	public void setSportStep_3(int sportStep_3) {
		this.sportStep_3 = sportStep_3;
	}
	
	
	
	public void setYear(String year) {
		this.year = year;
	}


	public String getMonth() {
		return month;
	}


	public void setMonth(String month) {
		this.month = month;
	}


	public String getDay() {
		return day;
	}


	public void setDay(String day) {
		this.day = day;
	}


	public HistorySport() {
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "["+this.year+
				"/"+this.month
				+"/"+this.day
				+"/"+this.hour
				+"/"+this.sportStep_1
				+"/"+this.sportStep_2
				+"/"+this.sportStep_3
				+"/"+this.sportTime_1
				+"/"+this.sportTime_2
				+"/"+this.sportTime_3 +"]"
				;
	}
	
//	public HistorySport(int value, int tag) {
//		super();
//		 //数据 ：0x0001 8 001,
//		 //时间点前16位 00 00 00 00 00 00 00 00 （31-16）
//	
//		
//			int hour = (value >> 16) & 0xFFFF;
//		 //num 1 001 1是type 001是num 一起为 8
//		 int num = (value >> 12) & 0b111;
////		 this.type = (value >> 15) & 0x01;
//		 //值 ：00 00 00 00 00 00
//		 this.sportStep = value & 0xFFF;
//		 //时间差 0 1 2 3 4 当前日期位置tag 存贮日期位置num
//		 int diff = -1 * (tag + 5 - num) % 5;
//		
//		 Date date = new Date();
//		 Calendar calendar = Calendar.getInstance();// 得到日历
//		 calendar.setTime(date);// 把当前时间赋给日历
//		 calendar.add(Calendar.DAY_OF_MONTH, diff);// 设置为前diff天 diff为负数
//		 this.historyDate = new
//		 SimpleDateFormat("yyMMdd").format(calendar.getTime());
//		
//		 Log.v("History", "timeIndex : " + timeIndex);
//		 Log.v("History", "num : " + num);
//		 Log.v("History", "historyDate : " + historyDate);
//		 Log.v("History", "type : " + type);
//		 Log.v("History", "historyValue : " + historyValue);
//	}

}
