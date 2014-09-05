package com.wb.launcher3.realweather;

import java.io.File;

import android.net.Uri;

public class WeatherType {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.freeme.weather_type";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.freeme.weather_type";
    public static final Uri CONTENT_URI = Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator
            + "weather_type");
    public static final String ID = "_id";
    public static final String WEATHER_NAME = "weather_name";
    public static final String WEATHER_NAME_EN = "weather_name_en";
    public static final String WEATHER_NAME_ZHTW = "weather_name_zhtw";
    private int id;
    private String typeText;
    private String typeTextEN;
    private String typeTextTW;

    public WeatherType() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public String getTypeText() {
        return this.typeText;
    }

    public String getTypeTextEN() {
        return this.typeTextEN;
    }

    public String getTypeTextTW() {
        return this.typeTextTW;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public void setTypeTextEN(String typeTextEN) {
        this.typeTextEN = typeTextEN;
    }

    public void setTypeTextTW(String typeTextTW) {
        this.typeTextTW = typeTextTW;
    }
}
