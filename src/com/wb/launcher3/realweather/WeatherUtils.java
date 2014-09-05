package com.wb.launcher3.realweather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;

public class WeatherUtils {

    public static final String WIDGET_SWITCH_CITY_4x2 = "com.tyd.tadpolesweather.WIDGET_SWITCH_CITY_4X2";
    public static final String WIDGET_UPDATE_WEARHER_CITY = "com.tyd.tadpolesweather.WIDGET_UPDATE_WEATHER_CITY";
    public static final String WEATHER_DATA_CHANGE = "com.freeme.weather.WEATHER_DATA_CHANGE";

    private static final int DAYTIME_BEGIN_HOUR = 8;
    private static final int DAYTIME_END_HOUR = 18;

    // for the table of common weather
    public static final String ID = "_id";
    public static final String CODE = "code";
    public static final String CITY = "city";
    public static final String WEATHER_DATE = "weather_date";
    public static final String WEATHER_DATE_DIFF = "weather_date_diff";
    public static final String WEATHER_DESCRIPTION = "weather_description";
    public static final String TEMPRETURE_HIGH = "temp_hign";
    public static final String TEMPRETURE_LOW = "temp_low";
    public static final String WIND = "wind";
    public static final String ICON1 = "icon1";
    public static final String ICON2 = "icon2";
    // just for city.can sort the city
    public static final String NUM = "num";
    public static final String DISPLAY = "display";
    public static final String TIME = "time";
    // just for the weather of today
    public static final String CURRENT_TEMPRETURE = "current_tempreture";
    public static final String CURRENT_WIND = "current_wind";
    public static final String CURRENT_HUMIDITY = "current_humidity";
    public static final String CURRENT_AIR = "current_air";
    public static final String CURRENT_UPF = "current_upf";
    public static final String COMMENT = "comment";

    public static final Uri TODAY_URI = Uri.parse("content://com.freeme.provider.weather/todayinfo");
    public static final Uri CITY_URI = Uri.parse("content://com.freeme.provider.weather/cityinfo");
    public static final String[] TODAY_QUERY = { ID, CITY, CODE, WEATHER_DATE, WEATHER_DATE_DIFF, WEATHER_DESCRIPTION,
            TEMPRETURE_HIGH, TEMPRETURE_LOW, WIND, ICON1, ICON2, CURRENT_TEMPRETURE, CURRENT_WIND, CURRENT_HUMIDITY,
            CURRENT_AIR, CURRENT_UPF, COMMENT };

    public static final String[] CITY_INDEX_QUERY = { ID, CODE, CITY, NUM, TIME, DISPLAY };

    public static boolean isDaytime() {
        Time time = new Time();
        time.setToNow();
        return (time.hour >= DAYTIME_BEGIN_HOUR && time.hour <= DAYTIME_END_HOUR);
    }

    public static final int LOCALE_MODE_ENGLISH = 3;
    public static final int LOCALE_MODE_SIMPLIFIED = 0;
    public static final int LOCALE_MODE_TRADITIONAL = 1;
    public static final long MIN_GET_CURRENT_GAP = 900000;
    private Context mContext;

    public WeatherUtils(Context context) {
        super();
        this.mContext = context;
    }

    public static String getCityLocalTime(long beijingTime) {
        return new SimpleDateFormat("yyyy/MM/dd").format(Long.valueOf(beijingTime));
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("kk:mm").format(new Date());
    }

    public static String getCurrentTimezoneTime(int offset) {
        TimeZone timeZone = TimeZone.getDefault();
        if (timeZone.useDaylightTime() && timeZone.inDaylightTime(new Date())) {
            offset -= timeZone.getDSTSavings();
        }

        timeZone.setRawOffset(offset);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(timeZone);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        ((DateFormat) dateFormat).setTimeZone(timeZone);

        return ((DateFormat) dateFormat).format(cal.getTime());
    }

    public static String getDateString(Context context, long millions) {
        Calendar v0 = Calendar.getInstance();
        v0.setTimeInMillis(millions);
        return android.text.format.DateFormat.getDateFormat(context).format(v0.getTime());
    }

    public static final int getLocalLanguageMode(Context context) {
        int languageMode;
        Locale locale = context.getResources().getConfiguration().locale;

        if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
            languageMode = 0;
        } else if (Locale.TRADITIONAL_CHINESE.equals(locale)) {
            languageMode = 1;
        } else {
            languageMode = 3;
        }

        return languageMode;
    }

    public static float getScreenDensity(Resources res) {
        float density = 1.5f;

        if (res != null) {
            density = res.getDisplayMetrics().density;
        }

        return density;
    }

    public static int[] getScreenPixels(Resources res) {
        int[] pixels = new int[2];

        if (res != null) {
            DisplayMetrics metrics = res.getDisplayMetrics();
            pixels[0] = metrics.widthPixels;
            pixels[1] = metrics.heightPixels;
        }

        return pixels;
    }

    public static List<WeatherInfo> getWeatherInfoFromToday(Context context, List<WeatherInfo> weatherList) {
        ArrayList<WeatherInfo> newWeatherList = new ArrayList<WeatherInfo>();

        if (weatherList == null || weatherList.size() == 0) {
            return newWeatherList;
        }

        int size = weatherList.size();
        int start = -1;

        for (int i = 0; i < size; ++i) {
            if (WeatherUtils.isCurrentDay(context, weatherList.get(i).getDate().longValue())) {
                start = i;
                break;
            }
        }

        if (start > -1) {
            for (int i = start; i < size; ++i) {
                ((List) newWeatherList).add(weatherList.get(i));
            }
        }

        return (newWeatherList);
    }

    public static boolean isCentigrade(Context context) {
        try {
            context = context.createPackageContext("com.freeme.weather", 2);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return context.getSharedPreferences("data", 5).getBoolean("temperature_sign", true);
    }

    public static boolean isCurrentDay(Context context, long datemillion) {
        if (datemillion <= 0) {
            return false;
        }

        if (!WeatherUtils.getDateString(context, System.currentTimeMillis()).equals(
                WeatherUtils.getDateString(context, datemillion))) {
            return false;
        }

        return true;
    }

    public static boolean isCurrentDay(Context context, long datemillion, int offset) {
        boolean isCurrentDay = false;

        if (datemillion > 0) {
            String cityLocalTime = WeatherUtils.getCityLocalTime(datemillion);
            String tzTime = WeatherUtils.getCurrentTimezoneTime(offset);

            Log.d("WeatherDBHelper", "isCurrentDay, updateCityLocalDate = " + cityLocalTime + ",curCityDate = "
                    + tzTime);

            if (!TextUtils.isEmpty((cityLocalTime))) {
                isCurrentDay = cityLocalTime.equals(tzTime);
            }
        }

        return isCurrentDay;
    }

    public static boolean isEnableLocalWeather(Context context) {
        try {
            context = context.createPackageContext("com.freeme.weather", 2);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return context.getSharedPreferences("data", 5).getBoolean("current_city_weather", true);
    }

    public static boolean isInfoNone(String infor) {
        if (infor == null || infor.isEmpty() || "None" == infor || "None".equals(infor)) {
            return true;
        }

        return false;
    }

    public static boolean isNeedToAutoGetLocalCity(Context context) {
        if (!WeatherUtils.isEnableLocalWeather(context)) {
            return false;
        }

        try {
            context = context.createPackageContext("com.freeme.weather", 2);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Math.abs(System.currentTimeMillis()
                - context.getSharedPreferences("data", 5).getLong("get_current_time", 0)) >= 900000) {
            return true;
        }

        return false;
    }

    public static boolean isNight() {
        boolean isNight = false;
        String time = WeatherUtils.getCurrentTime();

        if (time.compareTo("18:00") >= 0 || time.compareTo("00:00") >= 0 && time.compareTo("05:59") <= 0) {
            isNight = true;
        }

        return isNight;
    }

    public static void startGetLocalCity(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.freeme.weather.auto_getlocalcity");

        try {
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchMainActivity() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addFlags(268566528);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(new ComponentName("com.freeme.weather", "com.freeme.weather.FreemeMainActivity"));

        try {
            this.mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int transDataFromCelsiusToF(int celsius) {
        return ((int) Math.round((((double) celsius)) * 9 / 5 + 32));
    }
}
