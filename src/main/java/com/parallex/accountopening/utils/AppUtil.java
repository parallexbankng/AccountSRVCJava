package com.parallex.accountopening.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AppUtil {

	private static SimpleDateFormat MSG_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat MSG_DATE_TIME_FORMATTER1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public static String generateUUID() {
		Date now = new Date();
		SimpleDateFormat sbf = new SimpleDateFormat("ddMMyyyyhhmmss");

		return sbf.format(now);
	}

	public static String getTime() {
		Date now = new Date();
		SimpleDateFormat sbf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sbf2 = new SimpleDateFormat("hh:mm:ss.000");

		return sbf.format(now) + "T" + sbf2.format(now);
	}

	public static String getDate() {
		Date now = new Date();
		SimpleDateFormat sbf = new SimpleDateFormat("dd-MM-yyyy");

		return sbf.format(now);
	}

	public static String getMessageDateTime1() {
		return MSG_DATETIME_FORMATTER.format(new Date());
	}

	public static String generateReference() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return System.currentTimeMillis() / 1000 + sdf.format(now);
	}

	public static String getMessageDateTime() {
		SimpleDateFormat MSG_DATE_TIME_FORMATTER = new SimpleDateFormat("ddMMyyyyHHmmss");
		return MSG_DATE_TIME_FORMATTER.format(new Date());

	}

	public static String getMessageDateTime2() {
		SimpleDateFormat MSG_DATE_TIME_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return MSG_DATE_TIME_FORMATTER.format(new Date());

	}

	public static String getValueDateTime() {
		SimpleDateFormat MSG_DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		// SimpleDateFormat MSG_DATE_TIME_FORMATTER = new
		// SimpleDateFormat("yyyy-MM-dd");
		return MSG_DATE_TIME_FORMATTER.format(new Date());

	}

	public static String getExpirationDateTime(Timestamp tsp) {
		SimpleDateFormat MSG_DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Date dt1 = new Date(tsp.getTime());
		return MSG_DATE_TIME_FORMATTER.format(dt1);

	}

	public static String getValueDateTime1(String date1) {
		SimpleDateFormat MSG_DATE_TIME_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		Date d1 = null;
		try {
			// parsing date using SimpleDateFormat class
			d1 = sdf.parse(date1);
		}
		// catch block for handling ParseException
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		// pass UTC date to main method.
		return MSG_DATE_TIME_FORMATTER.format(d1);

	}

	public static String getCurrentUtcDateTime() {
		SimpleDateFormat MSG_DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");
		// set UTC time zone by using SimpleDateFormat class
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");
		Date d1 = null;
		try {
			// parsing date using SimpleDateFormat class
			d1 = ldf.parse(sdf.format(new Date()));
		}
		// catch block for handling ParseException
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		// pass UTC date to main method.
		return MSG_DATE_TIME_FORMATTER.format(d1) + "Z";
		// return d1.toString();
	}

	// create getCurrentUtcTime() method to get the current UTC time
	public static Date getCurrentUtcTime() throws ParseException { // handling ParseException
		// create an instance of the SimpleDateFormat class
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		// set UTC time zone by using SimpleDateFormat class
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// create another instance of the SimpleDateFormat class for local date format
		SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		// declare and initialize a date variable which we return to the main method
		Date d1 = null;
		// use try catch block to parse date in UTC time zone
		try {
			// parsing date using SimpleDateFormat class
			d1 = ldf.parse(sdf.format(new Date()));
		}
		// catch block for handling ParseException
		catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		// pass UTC date to main method.
		return d1;
	}

}
