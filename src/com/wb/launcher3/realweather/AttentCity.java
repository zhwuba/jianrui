package com.wb.launcher3.realweather;

import java.io.File;
import java.util.List;

import android.net.Uri;

public class AttentCity {
    public static final String CITY_CODE = "city_code";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";
    public static final String CITY_NAME_EN = "city_name_en";
    public static final String CITY_NAME_TW = "city_name_tw";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.freeme.city";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.freeme.city";
    public static final Uri CONTENT_URI = Uri.parse("content://" + "com.freeme.provider.weather" + File.separator
            + "attent_city");
    public static final String CURRENT = "current";
    public static final Uri CURRENT_CONTENT_URI = Uri.parse("content://" + "com.freeme.weather.provider.data"
            + File.separator + "attent_city" + File.separator + "current");
    public static final int CURRENT_DEFAULT = 1;
    public static final String GO_CITY_CODE = "go_city_code";
    public static final Uri GO_CITY_CONTENT_URI = Uri.parse(new StringBuffer("content://")
            .append("com.freeme.weather.provider.data").append(File.separator).append("go_city").toString());
    public static final String ID = "_id";
    public static final String ISUPDATED = "is_updated";
    public static final String LOCALE = "locale";
    public static final String LOCATION = "location";
    public static final String REMARK = "remark";
    public static final String SORT = "sort";
    public static final String SUNRISE = "sunrise";
    public static final String SUNSET = "sunset";
    public static final String TIME_ZONE = "time_zone";
    public static final String UPDATE_TIME = "update_time";
    public static final Uri URI_ATTENT_CITY_CURRENT_RESET = Uri.parse("content://" + "com.freeme.weather.provider.data"
            + File.separator + "attent_city" + File.separator + "attent_city_reset_current");
    private String cityCode;
    private int cityId;
    private String cityName;
    private int current;
    private int id;
    private int isUpdate;
    private int location;
    private String remark;
    private int sort;
    private String timeZone;
    private String updateTime;
    private List weatherInfoList;

    public AttentCity() {
        super();
        this.weatherInfoList = null;
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public int getCityId() {
        return this.cityId;
    }

    public String getCityName() {
        return this.cityName;
    }

    public int getCurrent() {
        return this.current;
    }

    public int getId() {
        return this.id;
    }

    public String getRemark() {
        return this.remark;
    }

    public int getSort() {
        return this.sort;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public List getWeatherInfoList() {
        return this.weatherInfoList;
    }

    public int isUpdate() {
        return this.isUpdate;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setWeatherInfoList(List arg1) {
        this.weatherInfoList = arg1;
    }
}
