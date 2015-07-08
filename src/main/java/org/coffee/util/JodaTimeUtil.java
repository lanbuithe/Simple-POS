package org.coffee.util;

import org.joda.time.DateTime;

public class JodaTimeUtil {
	
	public static DateTime withTimeAtEndOfDay(DateTime dateTime) {
		if (null == dateTime) {
			return dateTime;
		}
		DateTime date = new DateTime(dateTime);
		date = date.withTime(23, 59, 59, 999);
		return date;
	}
	
}
