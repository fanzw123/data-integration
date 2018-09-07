package com.chedaojunan.report.utils;

import com.chedaojunan.report.common.Constants;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

	private static final String TIME_PATTERN = "MM-dd-yy HH:mm:ss";

	// 取得本地时间：
	private Calendar cal = Calendar.getInstance();
	// 取得时间偏移量：
	private int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
	// 取得夏令时差：
	private int dstOffset = cal.get(Calendar.DST_OFFSET);

	// 获取年月日
	public String getYMD() {
		String ymd = new SimpleDateFormat(Constants.YMD_PATTERN).format(Calendar.getInstance().getTime());
		return ymd;
	}

	// 获取年月日(增加5分钟)
	public String getYMD_After5M(Long times) {
		String ymd = new SimpleDateFormat(Constants.YMD_PATTERN).format(times);
		return ymd;
	}

	// 获取时分
	public String getHM() {
		String hm = new SimpleDateFormat(Constants.HM_PATTERN).format(Calendar.getInstance().getTime());
		return hm;
	}

	// 获取时
	public String getHour() {
		String hour = new SimpleDateFormat("HH").format(Calendar.getInstance().getTime());
		return hour;
	}

	// 获取时(增加5分钟)
	public String getHour_After5M(Long times) {
		String hour = new SimpleDateFormat("HH").format(times);
		return hour;
	}

	// 获取分
	public String getMinute() {
		String minute = new SimpleDateFormat("mm").format(Calendar.getInstance().getTime());
		return minute;
	}

	// 获取分(增加5分钟)
	public String getMinute_After5M(Long times) {
		String minute = new SimpleDateFormat("mm").format(times);
		return minute;
	}

	// 从本地时间转化为UTC时间（10位）
	public long getUTCTimeFromLocal(long localTime) {

		cal.setTimeInMillis(localTime);
		// 从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		long utcMills = cal.getTimeInMillis()/1000L;

		return utcMills;
	}

	// 从UTC时间转化为本地时间
	public long getLocalTimeFromUTC(long utcTime) {

		cal.setTimeInMillis(utcTime);
		// 从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(Calendar.MILLISECOND, (zoneOffset + dstOffset));
		long localMills = cal.getTimeInMillis();

		return localMills;
	}

	// 根据时间戳获取年月日
	public String getYMDFromTime(long mill) {
		Date date = new Date(mill);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	// 根据时间戳获取时分
	public String getHMFromTime(long mill) {
		Date date = new Date(mill);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH_mm");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	// 根据时间戳获取时
	public String getHourFromTime(long mill) {
		Date date = new Date(mill);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	// 根据时间戳获取分
	public String getMinuteFromTime(long mill) {
		Date date = new Date(mill);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("mm");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	public static long convertTimeStringToEpochSecond(String timeString) {
		//System.out.println("haha:" + timeString);
		ZonedDateTime dateTime = ZonedDateTime.parse(timeString, DateTimeFormatter
				.ofPattern(TIME_PATTERN).withZone(ZoneId.of("UTC")));
		return dateTime.toEpochSecond();
	}

	public static String roundMilliSecondToNextMinute(String millisecondString) {
		long millisecond = Long.parseLong(millisecondString);
		long nextMinute = TimeUnit.MILLISECONDS.toMinutes(millisecond);
		return String.valueOf(TimeUnit.MINUTES.toMillis(nextMinute));
	}

}
