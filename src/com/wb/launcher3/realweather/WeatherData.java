package com.wb.launcher3.realweather;

import java.io.File;

import android.net.Uri;

public class WeatherData {
    public static final String AUTHORITY = "com.freeme.weather.provider.data";
    public static final String CONTENT = "content://";

    public WeatherData() {
        super();
    }

    public static final Uri ATTENT_CITY_CONTENT_URI(String cityId) {
        return Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator + "attent_city"
                + File.separator + cityId);
    }

    public static final Uri CHANGE_CITY_UPDATE(long cityId, int is_updated) {
        return Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator + "attent_city"
                + File.separator + "is_updated" + File.separator + cityId + File.separator + is_updated);
    }

    public static final Uri CHANGE_CURRENT_CITY(long cityId) {
        return Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator + "attent_city"
                + File.separator + "current_city" + File.separator + cityId);
    }

    public static final Uri CHANGE_SORT_URI(long cityId, int from, int to) {
        return Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator + "attent_city"
                + File.separator + cityId + File.separator + from + File.separator + to);
    }

    public static final Uri CITY_CONTENT_URI(String cityId) {
        return Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator + "area" + File.separator
                + cityId);
    }

    public static final Uri WEATHER_INFO_CONTENT_URI(String cityId) {
        return Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator + "weather_info"
                + File.separator + cityId);
    }

    public static final Uri WEATHER_TYPE_CONTENT_URI(String id) {
        return Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator + "weather_type"
                + File.separator + id);
    }
}
