package com.wb.launcher3.realweather;

import java.io.File;

import android.net.Uri;

public class City {
    public static final String CITY_CODE = "city_code";
    public static final String CITY_COUNTRY = "city_country";
    public static final String CITY_COUNTRY_ENGL = "city_country_english";
    public static final String CITY_COUNTRY_ZHTW = "city_country_zhtw";
    public static final String CITY_INCHINA = "city_inchina";
    public static final String CITY_NAME = "city_name";
    public static final String CITY_NAME_ZHTW = "city_name_zhtw";
    public static final String CITY_PARENT_CODE = "city_parent_code";
    public static final String CITY_PINYIN = "city_pinyin";
    public static final String CITY_PREFECTURE = "city_prefecture";
    public static final String CITY_PREFECTURE_ENGL = "city_prefecture_english";
    public static final String CITY_PREFECTURE_ZHTW = "city_prefecture_zhtw";
    public static final String CITY_PROVINCE = "city_province";
    public static final String CITY_PROVINCE_ENGL = "city_province_english";
    public static final String CITY_PROVINCE_ZHTW = "city_province_zhtw";
    public static final String CITY_SHORT = "city_short";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.freeme.city_list";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.freeme.city_list";
    public static final Uri CONTENT_URI = Uri.parse("content://" + "com.freeme.weather.provider.data" + File.separator
            + "city_list");
    public static final String ID = "_id";
    private String cityCode;
    private String cityCountry;
    private String cityCountryEndglish;
    private String cityCountryZhtw;
    private String cityInchina;
    private String cityName;
    private String cityNameZhtw;
    private String cityParentCode;
    private String cityPinyin;
    private String cityPrefecture;
    private String cityPrefectureEnglish;
    private String cityPrefectureZhtw;
    private String cityProvince;
    private String cityProvinceEnglish;
    private String cityProvinceZhtw;
    private String cityShort;
    private int id;

    public City() {
        super();
    }

    public String getCityCode() {
        return this.cityCode;
    }

    public String getCityCountry() {
        return this.cityCountry;
    }

    public String getCityCountryEndglish() {
        return this.cityCountryEndglish;
    }

    public String getCityCountryZhtw() {
        return this.cityCountryZhtw;
    }

    public String getCityInchina() {
        return this.cityInchina;
    }

    public String getCityName() {
        return this.cityName;
    }

    public String getCityNameZhtw() {
        return this.cityNameZhtw;
    }

    public String getCityParentCode() {
        return this.cityParentCode;
    }

    public String getCityPinyin() {
        return this.cityPinyin;
    }

    public String getCityPrefecture() {
        return this.cityPrefecture;
    }

    public String getCityPrefectureEnglish() {
        return this.cityPrefectureEnglish;
    }

    public String getCityPrefectureZhtw() {
        return this.cityPrefectureZhtw;
    }

    public String getCityProvince() {
        return this.cityProvince;
    }

    public String getCityProvinceEnglish() {
        return this.cityProvinceEnglish;
    }

    public String getCityProvinceZhtw() {
        return this.cityProvinceZhtw;
    }

    public String getCityShort() {
        return this.cityShort;
    }

    public int getId() {
        return this.id;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityCountry(String cityCountry) {
        this.cityCountry = cityCountry;
    }

    public void setCityCountryEndglish(String cityCountryEndglish) {
        this.cityCountryEndglish = cityCountryEndglish;
    }

    public void setCityCountryZhtw(String cityCountryZhtw) {
        this.cityCountryZhtw = cityCountryZhtw;
    }

    public void setCityInchina(String cityInchina) {
        this.cityInchina = cityInchina;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityNameZhtw(String cityNameZhtw) {
        this.cityNameZhtw = cityNameZhtw;
    }

    public void setCityParentCode(String cityParentCode) {
        this.cityParentCode = cityParentCode;
    }

    public void setCityPinyin(String cityPinyin) {
        this.cityPinyin = cityPinyin;
    }

    public void setCityPrefecture(String cityPrefecture) {
        this.cityPrefecture = cityPrefecture;
    }

    public void setCityPrefectureEnglish(String cityPrefectureEnglish) {
        this.cityPrefectureEnglish = cityPrefectureEnglish;
    }

    public void setCityPrefectureZhtw(String cityPrefectureZhtw) {
        this.cityPrefectureZhtw = cityPrefectureZhtw;
    }

    public void setCityProvince(String cityProvince) {
        this.cityProvince = cityProvince;
    }

    public void setCityProvinceEnglish(String cityProvinceEnglish) {
        this.cityProvinceEnglish = cityProvinceEnglish;
    }

    public void setCityProvinceZhtw(String cityProvinceZhtw) {
        this.cityProvinceZhtw = cityProvinceZhtw;
    }

    public void setCityShort(String cityShort) {
        this.cityShort = cityShort;
    }

    public void setId(int id) {
        this.id = id;
    }
}
