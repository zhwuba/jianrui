package com.wb.launcher3.realweather;

import android.content.ContentResolver;
import android.database.Cursor;
import android.text.TextUtils;

public class Weather {
    public static final String DATE_SEPERATOR = "/";

    private long mId = -1;
    private int mCode;
    private String mCity;
    private String mWeatherDate;// format month/day.; e.g. 2/3
    private long mDateMillion = -1;
    private int mWeatherDiff;// between 0~4
    private String mWeatherDescription;
    private int mTempHign;
    private int mTempLow;
    private String mWind;
    private int mIcon1;
    private int mIcon2;

    private int mCurTemp;
    private String mCurWind;
    private String mCurHumidity;
    private String mCurAir;
    private String mCurUPF;
    private String mComment;

    public Weather() {
        mCurTemp = 1000;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public String getWeatherDate() {
        return mWeatherDate;
    }

    public void setWeatherDate(String weatherDate) {
        this.mWeatherDate = weatherDate;
    }

    public long getDateMillion() {
        return mDateMillion;
    }

    public void setDateMillion(long dateMillion) {
        this.mDateMillion = dateMillion;
    }

    public int getWeatherDiff() {
        return mWeatherDiff;
    }

    public void setWeatherDiff(int weatherDiff) {
        this.mWeatherDiff = weatherDiff;
    }

    public String getWeatherDescription() {
        return mWeatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.mWeatherDescription = weatherDescription;
    }

    public int getTempHign() {
        return mTempHign;
    }

    public void setTempHign(int tempHign) {
        this.mTempHign = tempHign;
    }

    public int getTempLow() {
        return mTempLow;
    }

    public void setTempLow(int tempLow) {
        this.mTempLow = tempLow;
    }

    public String getWind() {
        return mWind;
    }

    public void setWind(String wind) {
        this.mWind = wind;
    }

    public int getIcon1() {
        return mIcon1;
    }

    public void setIcon1(int icon1) {
        this.mIcon1 = icon1;
    }

    public int getIcon2() {
        return mIcon2;
    }

    public void setIcon2(int icon2) {
        this.mIcon2 = icon2;
    }

    public int getCurTemp() {
        return mCurTemp;
    }

    public void setCurTemp(int curTemp) {
        this.mCurTemp = curTemp;
    }

    public String getCurWind() {
        return mCurWind;
    }

    public void setCurWind(String curWind) {
        this.mCurWind = curWind;
    }

    public String getCurHumidity() {
        return mCurHumidity;
    }

    public void setCurHumidity(String curHumidity) {
        this.mCurHumidity = curHumidity;
    }

    public String getCurAir() {
        return mCurAir;
    }

    public void setCurAir(String curAir) {
        this.mCurAir = curAir;
    }

    public String getCurUPF() {
        return mCurUPF;
    }

    public void setCurUPF(String curUPF) {
        this.mCurUPF = curUPF;
    }

    public String getComment() {
        return mComment;
    }

    public void setmComment(String comment) {
        this.mComment = comment;
    }

    public static String splitString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return str.split(":")[1];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Weather readWeatherFromDatabase(ContentResolver resolver, int code) {
        Cursor cursor = resolver.query(WeatherUtils.TODAY_URI, WeatherUtils.TODAY_QUERY, WeatherUtils.CODE + " = "
                + code, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            Weather weather = new Weather();
            weather.setId(cursor.getLong(cursor.getColumnIndex(WeatherUtils.ID)));
            weather.setCode(cursor.getInt(cursor.getColumnIndex(WeatherUtils.CODE)));
            weather.setCity(cursor.getString(cursor.getColumnIndex(WeatherUtils.CITY)));
            weather.setWeatherDate(cursor.getString(cursor.getColumnIndex(WeatherUtils.WEATHER_DATE)));
            weather.setWeatherDiff(cursor.getInt(cursor.getColumnIndex(WeatherUtils.WEATHER_DATE_DIFF)));
            weather.setWeatherDescription(cursor.getString(cursor.getColumnIndex(WeatherUtils.WEATHER_DESCRIPTION)));
            weather.setCurTemp(cursor.getInt(cursor.getColumnIndex(WeatherUtils.CURRENT_TEMPRETURE)));
            weather.setTempHign(cursor.getInt(cursor.getColumnIndex(WeatherUtils.TEMPRETURE_HIGH)));
            weather.setTempLow(cursor.getInt(cursor.getColumnIndex(WeatherUtils.TEMPRETURE_LOW)));
            weather.setWind(cursor.getString(cursor.getColumnIndex(WeatherUtils.WIND)));
            weather.setIcon1(cursor.getInt(cursor.getColumnIndex(WeatherUtils.ICON1)));
            weather.setIcon2(cursor.getInt(cursor.getColumnIndex(WeatherUtils.ICON2)));
            closeCursor(cursor);
            return weather;
        }
        closeCursor(cursor);
        return null;
    }

    public static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

}
