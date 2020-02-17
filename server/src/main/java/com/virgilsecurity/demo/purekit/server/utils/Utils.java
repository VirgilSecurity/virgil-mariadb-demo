package com.virgilsecurity.demo.purekit.server.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;

public class Utils {

	public static String generateId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String generateSsn() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
	}

	public static Date today() {
		return truncate(new Date());
	}

	public static Date yesterday() {
		return DateUtils.addDays(today(), -1);
	}

	private static Date truncate(Date date) {
		return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
	}

}
