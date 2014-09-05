package com.wb.launcher3.realweather;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class WeatherDataHelper {
    public static final String CITY_NAME = "name";
    public static final String CITY_NAME_EN = "name_en";
    public static final String CITY_NAME_IN = "name_in";
    public static final String CITY_NAME_TH = "name_th";
    public static final String CITY_NAME_VI = "name_vi";
    private static final int EXCEPT_LOCAL_WEATHER = 0;
    private static final int FLAGE_CURRENT_CITY = 1;
    private static final int INCLUDE_LOCAL_WEATHER = 1;
    private static final String[] PROJECTION_NAME_IN_ENGLISH = new String[] { "_id", "city_pinyin" };
    private static final String[] PROJECTION_NAME_IN_SIMPLIFIED = new String[] { "_id", "city_name" };
    private static final String[] PROJECTION_NAME_IN_TRADITIONAL = new String[] { "_id", "city_name_zhtw" };
    public static final String SORT_ORDER_SORT_NUMBER = "sort ASC";
    public static final String SORT_ORDER_WEATHER_DATE = "date ASC";
    public static final String TAG = "WeatherDBHelper";
    private Context mContext;
    private ContentResolver mResolver;

    public WeatherDataHelper(Context context) {
        super();
        this.mContext = context;
        this.mResolver = context.getContentResolver();
    }

    private void changeAttentCityCurrentFlage(long cityId) {
        try {
            Uri city = WeatherData.CHANGE_CURRENT_CITY(cityId);
            ContentResolver resolver = this.mContext.getContentResolver();
            ContentValues content = new ContentValues();
            content.put("current", Integer.valueOf(1));
            resolver.update(city, content, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }

        cursor.close();
    }

    public void dropTable(SQLiteDatabase database, String table) {
        database.execSQL("DROP TABLE IF EXISTS " + table);
    }

    public int getAttentCityCount(boolean includeLocalWeather) {
        int cout = 0;
        StringBuilder sb = new StringBuilder();

        if (!includeLocalWeather) {
            sb.append("location").append("=").append(0);
        }

        Cursor cursor = this.mResolver.query(AttentCity.CONTENT_URI, null, sb.toString(), null, "sort ASC");
        if (cursor != null) {
            cout = cursor.getCount();
            cursor.close();
        }

        return cout;
    }

    public long getAttentCityIdByCityId(long cityId) {
        Cursor cursor = null;

        String[] projection = new String[] { "_id", "city_id" };
        String selection = "city_id" + "=" + cityId;

        try {
            cursor = this.mResolver.query(AttentCity.CONTENT_URI, projection, selection, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long id = -1;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndex("_id"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return id;
    }

    public AttentCity getCityBySortAttentCity(int sort) {
        long cityId = this.getCityIdBySort(sort);
        AttentCity city = null;

        if (cityId != 0) {
            city = new AttentCity();
            city.setCityId(((int) cityId));
            city.setWeatherInfoList(this.getOneAttentCityWeatherInfoList(city.getCityId()));
        }

        return city;
    }

    public long getCityIdByAttentCityId(long attentCityId) {
        Cursor cursor = null;
        String[] projection = new String[] { "_id", "city_id" };
        String selection = "_id" + "=" + attentCityId;

        try {
            cursor = this.mResolver.query(AttentCity.CONTENT_URI, projection, selection, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long id = -1;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndex("city_id"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return id;
    }

    public long getCityIdBySort(int sort) {
        long cityId;

        Cursor cursor = this.mResolver.query(AttentCity.CONTENT_URI, new String[] { "_id", "city_name_en" }, "sort"
                + "=" + sort, null, null);

        if (cursor == null) {
            cityId = 0;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            cityId = 0;
        } else {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            cityId = (_id);
        }

        return cityId;
    }

    public String getCityNameById(Context context, long cityId) {
        String[] projection;
        String cityName = null;
        int languageMode = WeatherUtils.getLocalLanguageMode(context);
        String selection = String.format("_id=%d", Long.valueOf(cityId));

        switch (languageMode) {
        case 0: {
            projection = WeatherDataHelper.PROJECTION_NAME_IN_SIMPLIFIED;
            break;
        }
        case 1: {
            projection = WeatherDataHelper.PROJECTION_NAME_IN_TRADITIONAL;
            break;
        }
        case 3: {
            projection = WeatherDataHelper.PROJECTION_NAME_IN_ENGLISH;
            break;
        }
        default: {
            projection = WeatherDataHelper.PROJECTION_NAME_IN_SIMPLIFIED;
            break;
        }
        }

        Cursor cursor = context.getContentResolver().query(WeatherData.CITY_CONTENT_URI(String.valueOf(cityId)),
                projection, selection, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            cityName = cursor.getString(1);
        }

        if (cursor != null) {
            cursor.close();
        }

        return cityName;
    }

    public int getCitySort(long cityId) {
        int city;
        int sort = -2;

        Cursor cursor = this.mResolver.query(WeatherData.ATTENT_CITY_CONTENT_URI(String.valueOf(cityId)),
                new String[] { "sort" }, null, null, null);

        if (cursor == null) {
            city = sort;
        } else {
            if (cursor.moveToFirst()) {
                sort = cursor.getInt(cursor.getColumnIndex("sort"));
            }

            cursor.close();
            city = sort;
        }

        return city;
    }

    public AttentCity getCurrentAttentCity(Context context) {
        Cursor cursor = null;
        String[] projection;
        int stringLen = 3;
        int id = -1;
        String cityName = null;
        AttentCity attentCity = null;

        switch (WeatherUtils.getLocalLanguageMode(context)) {
        case 0: {
            projection = new String[stringLen];
            projection[0] = "_id";
            projection[1] = "current";
            projection[2] = "city_name";
            break;
        }
        case 1: {
            projection = new String[stringLen];
            projection[0] = "_id";
            projection[1] = "current";
            projection[2] = "city_name_tw";
            break;
        }
        case 3: {
            projection = new String[stringLen];
            projection[0] = "_id";
            projection[1] = "current";
            projection[2] = "city_name_en";
            break;
        }
        default: {
            projection = new String[stringLen];
            projection[0] = "_id";
            projection[1] = "current";
            projection[2] = "city_name";
            break;
        }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("current").append("=").append(1);

        try {
            cursor = context.getContentResolver().query(AttentCity.CONTENT_URI, projection, sb.toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex("_id"));
            cityName = cursor.getString(2);
        }

        if (cursor != null) {
            cursor.close();
        }

        attentCity.setId(id);
        attentCity.setCityName(cityName);
        return attentCity;
    }

    public String getCurrentAttentCityName(long cityId, Context context) {
        Cursor cursor = null;
        String currentAttentCityname = null;
        Locale locale = context.getResources().getConfiguration().locale;
        String[] projection = new String[] { "locale", "city_name_en", "city_name", "location" };
        StringBuilder sb = new StringBuilder();
        sb.append("_id").append("=").append(cityId);

        try {
            cursor = context.getContentResolver().query(AttentCity.CONTENT_URI, projection, sb.toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String v10 = cursor.getString(cursor.getColumnIndex(projection[0]));
            if (cursor.getInt(cursor.getColumnIndex(projection[3])) == 0) {
                if (!locale.toString().equals(v10) && !"".equals(v10) && v10 != null) {
                    currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[1]));
                    if (currentAttentCityname == null || "".equals(currentAttentCityname)
                            || currentAttentCityname.isEmpty()) {
                        currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[2]));
                    }
                } else {
                    currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[2]));
                    if (currentAttentCityname == null || "".equals(currentAttentCityname)
                            || currentAttentCityname.isEmpty()) {
                        currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[1]));
                    }
                }
            } else {
                if (!locale.toString().equals("zh_CN")) {
                    currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[1]));
                    if (currentAttentCityname == null || "".equals(currentAttentCityname)
                            || currentAttentCityname.isEmpty()) {
                        currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[2]));
                    }
                } else
                    currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[2]));
                if (currentAttentCityname == null || "".equals(currentAttentCityname)
                        || currentAttentCityname.isEmpty()) {
                    currentAttentCityname = cursor.getString(cursor.getColumnIndex(projection[1]));
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return currentAttentCityname;
    }

    public String getCurrentAttentGoCityName(Context context, long cityId) {
        return this.getGoCityName(cityId, new String[] { "name", "name_en" }, context);
    }

    public WeatherInfo getCurrentCityGoWeather(long cityId) {
        WeatherInfo weatherInfo;
        Cursor cursor = null;
        String goCityCode = this.getGoCityCode(cityId);
        WeatherInfo result = null;

        try {
            String[] projection = new String[7];
            projection[0] = "weather_id";
            projection[1] = "date";
            projection[2] = "current_temp";
            projection[3] = "day_temp";
            projection[4] = "night_temp";
            projection[5] = "current_weather";
            projection[6] = "day_weather";
            cursor = this.mResolver.query(WeatherInfo.CONTENT_URI, projection, "go_city_code=?",
                    new String[] { goCityCode }, null);
            int goCityOffset = this.getGoCityOffset(cityId);
            Log.d("WeatherDBHelper", "getCurrentCityGoWeather, offset = " + goCityOffset + ",cityId = " + cityId);

            if (cursor != null && cursor.getCount() > 1) {
                int indexStart = 0;
                int indexEnd = 0;

                while (true) {
                    if (indexStart < cursor.getCount()) {
                        cursor.moveToPosition(indexStart);
                        if (WeatherUtils.isCurrentDay(this.mContext, cursor.getLong(cursor.getColumnIndex("date")),
                                goCityOffset)) {
                            indexEnd = indexStart;
                        } else {
                            ++indexStart;
                            continue;
                        }
                    }

                    cursor.moveToPosition(indexEnd);
                    weatherInfo = new WeatherInfo();

                    int weatherId = cursor.getInt(cursor.getColumnIndex("weather_id"));
                    weatherInfo.setWeatherId(weatherId);
                    String weatherType = this.getWeatherTypeById(weatherId, this.mContext);

                    if (WeatherUtils.isInfoNone(weatherType)) {
                        weatherType = cursor.getString(cursor.getColumnIndex("day_weather"));
                    }

                    weatherInfo.setCurrentWeather(weatherType);
                    int dayTemp = cursor.getInt(cursor.getColumnIndex("day_temp"));
                    int nightTemp = cursor.getInt(cursor.getColumnIndex("night_temp"));

                    if (dayTemp < nightTemp && dayTemp != -273 && nightTemp != -273) {
                        int temp = dayTemp;
                        dayTemp = nightTemp;
                        nightTemp = temp;
                    }

                    weatherInfo.setDayTemp(dayTemp);
                    weatherInfo.setNightTemp(nightTemp);
                    String currentTemp = cursor.getString(cursor.getColumnIndex("current_temp"));

                    if (WeatherUtils.isInfoNone(currentTemp)) {
                        result = weatherInfo;
                    } else {
                        weatherInfo.setCurrentTemp(currentTemp);
                        result = weatherInfo;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            cursor.close();
        }

        return result;
    }

    public long getCurrentCityId(Context context) {
        Cursor cursor = null;
        long id = -1;
        String[] projection = new String[] { "_id", "current" };
        StringBuilder sb = new StringBuilder();
        sb.append("current").append("=").append(1);

        try {
            cursor = context.getContentResolver().query(AttentCity.CONTENT_URI, projection, sb.toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndex("_id"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return id;
    }

    public boolean getCurrentCityIsDay(long cityId) {
        Cursor cursor = null;
        String sunset = null;
        String sunrise = null;

        try {
            String[] projection = new String[3];
            projection[0] = "_id";
            projection[1] = "sunrise";
            projection[2] = "sunset";

            StringBuilder sb = new StringBuilder();
            sb.append("_id").append("=").append(cityId);
            cursor = this.mContext.getContentResolver().query(AttentCity.CONTENT_URI, projection, sb.toString(), null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            sunset = cursor.getString(cursor.getColumnIndex("sunset"));
            sunrise = cursor.getString(cursor.getColumnIndex("sunrise"));
        }

        if (cursor != null) {
            cursor.close();
        }

        boolean isDayTimeNow = FreemeDateUtils.isDayTimeNow(sunrise, sunset, this.getGoCityOffset(cityId));
        Log.d("WeatherDBHelper", "getCurrentCityIsDay, mSunset = " + sunset + ",mSunrise = " + sunrise + ", isDay = "
                + isDayTimeNow);

        return isDayTimeNow;
    }

    public long getFirstAttentCityId() {
        long cityId;
        boolean isUpdate = this.isUpdateLocationWeather();
        StringBuilder sb = new StringBuilder();

        if (!isUpdate) {
            sb.append("location").append('=').append("0");
        }

        Cursor cursor = this.mContext.getContentResolver().query(AttentCity.CONTENT_URI, null, sb.toString(), null,
                "sort ASC");
        if (cursor == null) {
            cityId = -1;
        } else if (cursor.getCount() < 1) {
            cityId = -1;
        } else {
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            cursor.close();
            cityId = id;
        }

        return cityId;
    }

    public String getGoCityCode(long cityId) {
        Cursor cursor = null;
        String goCityCode = null;
        String[] projection = new String[] { "go_city_code" };
        StringBuilder sb = new StringBuilder();
        sb.append("_id").append("=").append(cityId);

        try {
            cursor = this.mResolver.query(AttentCity.CONTENT_URI, projection, sb.toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            goCityCode = cursor.getString(cursor.getColumnIndex("go_city_code"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return goCityCode;
    }

    private String getGoCityName(long cityId, String[] projections, Context context) {
        Cursor cursor = null;
        String goCityName = null;
        String goCityCode = this.getGoCityCode(cityId);
        String cityName = null;
        Log.d("WeatherDBHelper", "getGoCityName goCityCode =" + goCityCode);

        if (goCityCode == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("posID").append("=").append("\'" + goCityCode + "\'");

        try {
            cursor = context.getContentResolver().query(AttentCity.GO_CITY_CONTENT_URI, projections, sb.toString(),
                    null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            cityName = cursor.getString(cursor.getColumnIndex(projections[0]));
            if (cityName == null || "".equals(cityName) || cityName.isEmpty()) {
                cityName = cursor.getString(cursor.getColumnIndex(projections[1]));
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        goCityName = cityName;
        return goCityName;
    }

    public int getGoCityOffset(long cityId) {
        Cursor cursor = null;
        int timeZone = 28800000;
        StringBuilder sb = new StringBuilder();
        sb.append("_id").append("=").append(cityId);

        try {
            cursor = this.mContext.getContentResolver().query(AttentCity.CONTENT_URI, null, sb.toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                timeZone = cursor.getInt(cursor.getColumnIndexOrThrow("time_zone"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.closeCursor(cursor);
        return timeZone;
    }

    public int getMaxSortNumber() {
        return this.getSort(true);
    }

    public AttentCity getMinSortItem() {
        return this.getCityBySortAttentCity(this.getMinSortNumber());
    }

    public int getMinSortNumber() {
        return this.getSort(false);
    }

    public AttentCity getNextCity(long cityId) {
        AttentCity city;
        if (cityId < 0) {
            city = null;
        } else {
            int sort = this.getCitySort(cityId) + 1;
            if (sort > this.getMaxSortNumber()) {
                sort = 0;
            }

            city = this.getCityBySortAttentCity(sort);
        }

        return city;
    }

    public long getNextCityId(long cityId) {
        long nextCityId;

        if (cityId < 0) {
            nextCityId = -1;
        } else {
            int sort = this.getCitySort(cityId) + 1;
            int maxSort = this.getMaxSortNumber();

            int minSort = 0;
            if (this.isUpdateLocationWeather()) {
                minSort = -1;
            }

            if (sort > maxSort) {
                sort = minSort;
            }

            nextCityId = this.getCityIdBySort(sort);
        }

        return nextCityId;
    }

    public List getOneAttentCityWeatherInfoList(int cityId) {
        ArrayList weatherInfos = null;

        if (cityId > 0) {
            Cursor cursor = this.mResolver.query(WeatherData.WEATHER_INFO_CONTENT_URI(String.valueOf(cityId)), null,
                    null, null, "date ASC");
            if (cursor != null && cursor.getCount() != 0) {
                weatherInfos = new ArrayList();
                int index = 0;
                int count = cursor.getCount();

                while (index < count) {
                    WeatherInfo weatherInfo = new WeatherInfo();
                    cursor.moveToPosition(index);
                    weatherInfo.setWeatherId(cursor.getInt(cursor.getColumnIndex("weather_id")));
                    weatherInfo.setDate(Long.valueOf(cursor.getLong(cursor.getColumnIndex("date"))));
                    weatherInfo.setCurrentWeather(cursor.getString(cursor.getColumnIndex("current_weather")));
                    weatherInfo.setCurrentTemp(cursor.getString(cursor.getColumnIndex("current_temp")));
                    weatherInfo.setCurrentWindDirect(cursor.getString(cursor.getColumnIndex("current_wind_direct")));
                    weatherInfo.setCurrentWindPower(cursor.getString(cursor.getColumnIndex("current_wind_power")));
                    weatherInfo.setCurrentHumidity(cursor.getString(cursor.getColumnIndex("current_humidity")));
                    weatherInfo.setCurrentUvIndex(cursor.getString(cursor.getColumnIndex("current_uv_index")));
                    weatherInfo.setCurrentUvDesc(cursor.getString(cursor.getColumnIndex("current_uv_desc")));
                    weatherInfo.setDayWeather(cursor.getString(cursor.getColumnIndex("day_weather")));
                    weatherInfo.setDayWindDirect(cursor.getString(cursor.getColumnIndex("day_wind_direct")));
                    weatherInfo.setDayWindPower(cursor.getString(cursor.getColumnIndex("day_wind_power")));
                    weatherInfo.setNightWeather(cursor.getString(cursor.getColumnIndex("night_weather")));
                    int v11 = cursor.getInt(cursor.getColumnIndex("day_temp"));
                    int v13 = cursor.getInt(cursor.getColumnIndex("night_temp"));
                    if (v11 < v13 && v11 != -273 && v13 != -273) {
                        int v14 = v11;
                        v11 = v13;
                        v13 = v14;
                    }

                    weatherInfo.setDayTemp(v11);
                    weatherInfo.setNightTemp(v13);
                    weatherInfos.add(weatherInfo);
                    ++index;
                }
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        if (weatherInfos != null && ((List) weatherInfos).size() != 0) {
            weatherInfos = (ArrayList) WeatherUtils.getWeatherInfoFromToday(this.mContext, weatherInfos);
        }

        return (weatherInfos);
    }

    public AttentCity getPreCity(long cityId) {
        if (cityId < 0) {
            return null;
        }

        int sort = this.getCitySort(cityId) - 1;
        int maxSort = this.getMaxSortNumber();

        if (sort < 0) {
            sort = maxSort;
        }

        return this.getCityBySortAttentCity(sort);
    }

    public long getPreCityId(long cityId) {
        long preCityId;

        if (cityId < 0) {
            preCityId = -1;
        } else {
            int sort = this.getCitySort(cityId) - 1;
            int maxSort = this.getMaxSortNumber();

            if (this.isUpdateLocationWeather()) {
                if (sort < -1) {
                    sort = maxSort;
                }
            } else if (sort >= 0) {
            } else {
                sort = maxSort;
            }

            preCityId = this.getCityIdBySort(sort);
        }

        return preCityId;
    }

    private int getSort(boolean max) {
        int result;
        String flag;

        if (max) {
            flag = "MAX(";
        } else {
            flag = "MIN(";
        }

        int sort = -1;
        if (this.getAttentCityCount(true) < 1) {
            result = sort;
        } else {
            String projection = flag + "sort" + ")";
            Cursor cursor = this.mResolver.query(AttentCity.CONTENT_URI, new String[] { projection }, null, null, null);
            if (cursor == null) {
                result = sort;
            } else {
                if (cursor.moveToFirst()) {
                    sort = cursor.getInt(cursor.getColumnIndex(projection));
                }

                if (cursor != null) {
                    cursor.close();
                }

                result = sort;
            }
        }

        return result;
    }

    public float getTimeZoneOfAttendCity(long cityId) {
        Cursor cursor = null;

        if (cityId < 0) {
            return 8f;
        }

        float timeZoneId;
        StringBuilder sb = new StringBuilder("_id").append("=").append(cityId);
        timeZoneId = 8f;

        try {
            cursor = this.mContext.getContentResolver().query(AttentCity.CONTENT_URI, null, sb.toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int timeZone = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            try {
                timeZone = cursor.getColumnIndexOrThrow("time_zone");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (timeZone >= 0) {
                timeZoneId = cursor.getFloat(timeZone);
            }
        }

        this.closeCursor(cursor);
        return timeZoneId;
    }

    public String getWeatherTypeById(int weatherId, Context context) {
        Cursor cursor = null;
        String[] projection;
        String weatherType = null;

        switch (WeatherUtils.getLocalLanguageMode(context)) {
        case 0: {
            projection = new String[] { "weather_name" };
            break;
        }
        case 1: {
            projection = new String[] { "weather_name_zhtw" };
            break;
        }
        case 3: {
            projection = new String[] { "weather_name_en" };
            break;
        }
        default: {
            projection = new String[] { "weather_name" };
            break;
        }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("_id").append("=").append(weatherId);

        try {
            cursor = context.getContentResolver().query(WeatherType.CONTENT_URI, projection, sb.toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            weatherType = cursor.getString(0);
        }

        if (cursor != null) {
            cursor.close();
        }

        return weatherType;
    }

    public boolean isCityAttented(Context context, long attentCityId) {
        boolean isCityAttented = false;

        Cursor cursor = context.getContentResolver().query(
                WeatherData.ATTENT_CITY_CONTENT_URI(String.valueOf(attentCityId)), new String[] { "_id", "city_name" },
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            isCityAttented = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        return isCityAttented;
    }

    public boolean isUpdateLocationWeather() {
        boolean isUpdate = false;
        StringBuilder sb = new StringBuilder();
        sb.append("location").append("=").append(1);
        Cursor cursor = this.mResolver.query(AttentCity.CONTENT_URI, null, sb.toString(), null, null);

        if (cursor != null && cursor.getCount() > 0) {
            isUpdate = true;
            cursor.close();
        }

        return isUpdate;
    }

    public void setCurrentCity(long attentCityId) {
        this.changeAttentCityCurrentFlage(attentCityId);
    }
}
