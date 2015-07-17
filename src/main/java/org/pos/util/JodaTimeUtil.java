package org.pos.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JodaTimeUtil {
	
	public static DateTime withTimeAtEndOfDay(DateTime dateTime) {
		if (null == dateTime) {
			return dateTime;
		}
		DateTime date = new DateTime(dateTime);
		date = date.withTime(23, 59, 59, 999);
		return date;
	}
	
	public static DateTime parse(String dateTimeStr, String pattern) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);
		DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
		return dateTime;
	}
	
}
