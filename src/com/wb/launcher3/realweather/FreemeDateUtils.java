package com.wb.launcher3.realweather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

public class FreemeDateUtils {
    public static final int CHINA_TIME_ZONE = 8;
    public static final String TAG = "FreemeDateUtils";

    public FreemeDateUtils() {
        super();
    }

    public static long getCurrentMills(long beijing) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        cal.setTimeInMillis(beijing);
        int year = cal.get(1);
        int month = cal.get(2);
        int date = cal.get(5);
        int hourOfDay = cal.get(10);
        int minute = cal.get(12);
        int second = cal.get(13);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute, second);
        return calendar.getTimeInMillis();
    }

    public static long getCurrentTimeMillisOfChina(Context context) {
        return System.currentTimeMillis() + (((long) (8f - FreemeDateUtils.getCurrentTimeZone()))) * 3600000;
    }

    public static float getCurrentTimeZone() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        int off = tz.getRawOffset();
        off = (off / 1000) / 60;
        int hour = Math.abs(off) / 60;
        float minutes = Math.abs(off) % 60.0f;

        if (off < 0) {
            return -(hour + (minutes / 60.0f));
        }

        return (hour + (minutes / 60.0f));
    }

    public static String getDateString(Context context, long millions) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millions);

        return DateFormat.getDateFormat(context).format(cal.getTime());
    }

    public static String getDateStringWithFormat(long million, String pattern) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(million);

        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    public static String getHourString(Context context, long datemillion) {
        return FreemeDateUtils.getDateStringWithFormat(datemillion, "HH");
    }

    private static int getHours(String time) {
        if (TextUtils.isEmpty((time))) {
            return -1;
        }

        int hour = -1;
        int index = time.indexOf(58);
        try {
            hour = Integer.parseInt(time.substring(0, index));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hour;
    }

    private static int getMinutes(String time) {
        if (TextUtils.isEmpty((time))) {
            return -1;
        }

        int minutes = -1;
        int index = time.indexOf(58) + 1;
        try {
            minutes = Integer.parseInt(time.substring(index));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return minutes;
    }

    public static long getTimeMillion(float timeZone) {
        return System.currentTimeMillis() + (((long) (timeZone - FreemeDateUtils.getCurrentTimeZone()))) * 3600000;
    }

    public static boolean isDayTimeNow(String sunrise, String sunset, int offset) {
        if ((TextUtils.isEmpty(sunset)) || (TextUtils.isEmpty(sunrise))) {
            return true;
        }

        TimeZone timeZone = TimeZone.getDefault();

        if (timeZone.useDaylightTime()) {
            if (timeZone.inDaylightTime(new Date(System.currentTimeMillis()))) {
                offset -= timeZone.getDSTSavings();
            }
        }

        timeZone.setRawOffset(offset);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(timeZone);

        int sysHours = calendar.get(11);
        int sysMinute = calendar.get(12);
        Log.i("date", sysHours + " sysMinute:" + sysMinute);

        if ((getHours(sunrise) >= sysHours) || (sysHours >= getHours(sunset))) {
            if ((getHours(sunrise) != sysHours) || (sysMinute < getMinutes(sunrise))) {
                if ((getHours(sunset) != sysHours) || (sysMinute > getMinutes(sunset))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isNowDayTime(Context context) {
        return FreemeDateUtils.isTimeMillisDayTime(context, System.currentTimeMillis());
    }

    public static boolean isNowDayTimeInZoneTime(Context context, float timeZone) {
        return FreemeDateUtils.isTimeMillisDayTime(context, FreemeDateUtils.getCurrentTimeMillisOfChina(context)
                + (((long) (timeZone - 8f))) * 3600000);
    }

    private static boolean isTimeMillisDayTime(Context context, long timeMillion) {
        boolean isDay = true;
        int hourtime = Integer.valueOf(FreemeDateUtils.getHourString(context, timeMillion)).intValue();

        if (hourtime >= 18 || hourtime < 6) {
            isDay = false;
        }

        return isDay;
    }

    public static boolean isTimeTodayInChina(Context context, long timeMllion) {
        if (!FreemeDateUtils.getDateString(context,
                System.currentTimeMillis() - (((long) (FreemeDateUtils.getCurrentTimeZone() - 8f))) * 3600000).equals(
                FreemeDateUtils.getDateString(context, timeMllion))) {
            return false;
        }

        return true;
    }

    public static long millsFromDataString(String dateStr) {
        long mills = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            mills = format.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mills;
    }
}
