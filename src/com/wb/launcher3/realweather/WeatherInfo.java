package com.wb.launcher3.realweather;

import java.io.File;

import android.net.Uri;

public class WeatherInfo {
    public static final String ALERT = "alert";
    public static final String CITY_ID = "city_id";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.freeme.weather_info";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.freeme.weather_info";
    public static final Uri CONTENT_URI = Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator
            + "weather_info");
    public static final String CURRENT_HUMIDITY = "current_humidity";
    public static final String CURRENT_TEMP = "current_temp";
    public static final String CURRENT_UV_DESC = "current_uv_desc";
    public static final String CURRENT_UV_INDEX = "current_uv_index";
    public static final String CURRENT_WEATHER = "current_weather";
    public static final String CURRENT_WIND_DIRECT = "current_wind_direct";
    public static final String CURRENT_WIND_POWER = "current_wind_power";
    public static final String DATE = "date";
    public static final String DAY_TEMP = "day_temp";
    public static final String DAY_WEATHER = "day_weather";
    public static final String DAY_WEATHER_ID = "day_weather_id";
    public static final String DAY_WIND_DIRECT = "day_wind_direct";
    public static final String DAY_WIND_POWER = "day_wind_power";
    public static final String GO_CITY_CODE = "go_city_code";
    public static final String ID = "_id";
    public static final int MAX_CURRENT_TEMP_LENGTH = 5;
    public static final String NIGHT_TEMP = "night_temp";
    public static final String NIGHT_WEATHER = "night_weather";
    public static final String NIGHT_WEATHER_ID = "night_weather_id";
    public static final String NIGHT_WIND_DIRECT = "night_wind_direct";
    public static final String NIGHT_WIND_POWER = "night_wind_power";
    public static final String PIC = "pic";
    public static final String REMARK = "remark";
    public static final String REMARK2 = "remark2";
    public static final String URL = "url";
    public static final String WEATHER_ID = "weather_id";
    private String alert;
    private int cityId;
    private String currentHumidity;
    private String currentTemp;
    private String currentUvDesc;
    private String currentUvIndex;
    private String currentWeather;
    private String currentWindDirect;
    private String currentWindPower;
    private Long date;
    private int dayTemp;
    private String dayWeather;
    private String dayWindDirect;
    private String dayWindPower;
    private int id;
    private int nightTemp;
    private String nightWeather;
    private String nightWindDirect;
    private String nightWindPower;
    private String pic;
    private String remark;
    private String remark2;
    private String url;
    private int weatherId;

    public WeatherInfo() {
        super();
    }

    public String getAlert() {
        return this.alert;
    }

    public int getCityId() {
        return this.cityId;
    }

    public String getCurrentHumidity() {
        return this.currentHumidity;
    }

    public String getCurrentTemp() {
        return this.currentTemp;
    }

    public String getCurrentUvDesc() {
        return this.currentUvDesc;
    }

    public String getCurrentUvIndex() {
        return this.currentUvIndex;
    }

    public String getCurrentWeather() {
        return this.currentWeather;
    }

    public String getCurrentWindDirect() {
        return this.currentWindDirect;
    }

    public String getCurrentWindPower() {
        return this.currentWindPower;
    }

    public Long getDate() {
        return this.date;
    }

    public int getDayTemp() {
        return this.dayTemp;
    }

    public String getDayWeather() {
        return this.dayWeather;
    }

    public String getDayWindDirect() {
        return this.dayWindDirect;
    }

    public String getDayWindPower() {
        return this.dayWindPower;
    }

    public int getId() {
        return this.id;
    }

    public int getNightTemp() {
        return this.nightTemp;
    }

    public String getNightWeather() {
        return this.nightWeather;
    }

    public String getNightWindDirect() {
        return this.nightWindDirect;
    }

    public String getNightWindPower() {
        return this.nightWindPower;
    }

    public String getPic() {
        return this.pic;
    }

    public String getRemark() {
        return this.remark;
    }

    public String getRemark2() {
        return this.remark2;
    }

    public String getUrl() {
        return this.url;
    }

    public int getWeatherId() {
        return this.weatherId;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCurrentHumidity(String currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public void setCurrentUvDesc(String currentUvDesc) {
        this.currentUvDesc = currentUvDesc;
    }

    public void setCurrentUvIndex(String currentUvIndex) {
        this.currentUvIndex = currentUvIndex;
    }

    public void setCurrentWeather(String currentWeather) {
        this.currentWeather = currentWeather;
    }

    public void setCurrentWindDirect(String currentWindDirect) {
        this.currentWindDirect = currentWindDirect;
    }

    public void setCurrentWindPower(String currentWindPower) {
        this.currentWindPower = currentWindPower;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setDayTemp(int dayTemp) {
        this.dayTemp = dayTemp;
    }

    public void setDayWeather(String dayWeather) {
        this.dayWeather = dayWeather;
    }

    public void setDayWindDirect(String dayWindDirect) {
        this.dayWindDirect = dayWindDirect;
    }

    public void setDayWindPower(String dayWindPower) {
        this.dayWindPower = dayWindPower;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNightTemp(int nightTemp) {
        this.nightTemp = nightTemp;
    }

    public void setNightWeather(String nightWeather) {
        this.nightWeather = nightWeather;
    }

    public void setNightWindDirect(String nightWindDirect) {
        this.nightWindDirect = nightWindDirect;
    }

    public void setNightWindPower(String nightWindPower) {
        this.nightWindPower = nightWindPower;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    @Override
    public String toString() {
        return "WeatherInfo(id=" + this.id + " cityId=" + this.cityId + " weatherId=" + this.weatherId + " date="
                + this.date + " currentWeather=" + this.currentWeather + " currentTemp=" + this.currentTemp
                + " dayWeather=" + this.dayWeather + " dayTemp=" + this.dayTemp + " nightWeather=" + this.nightWeather
                + " nightTemp=" + this.nightTemp + ")";
    }
}
