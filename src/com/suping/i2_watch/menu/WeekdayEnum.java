package com.suping.i2_watch.menu;

public enum WeekdayEnum {
	
	  MON(0b00000010), TUE(0b00000100), WED(0b00001000), THU(0b00010000), FRI(0b00100000), SAT(0b01000000), SUN(0b00000001);
	  private final int day;
	  private WeekdayEnum(int day) {

		  this.day = day;

		  }
	public int getDay() {
		return day;
	}
}
