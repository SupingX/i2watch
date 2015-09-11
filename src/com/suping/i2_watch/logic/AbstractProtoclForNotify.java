package com.suping.i2_watch.logic;

import java.util.Date;

public abstract class AbstractProtoclForNotify {
	/**
	 * 时间同步 0xAA 11
	 * @return
	 */
	public abstract byte[] notifyForSyncTime();
}
