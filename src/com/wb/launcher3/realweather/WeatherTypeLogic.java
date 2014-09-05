package com.wb.launcher3.realweather;

import android.content.Context;
import android.util.SparseIntArray;

public class WeatherTypeLogic {
    public static final int CLOUD = 3;
    public static final int CLOUD_NIGHT = 4;
    public static final int FOG = 5;
    public static final int HEAVY_RAIN = 7;
    public static final int HEAVY_SNOW = 6;
    public static final int LIGHT_RAIN = 8;
    public static final int LIGHT_SNOW = 9;

    public static final int LIVE_WEATHER_TYPE_NONE = 200;
    public static final int LIVE_WEATHER_TYPE_DYNAMIC = 201;
    public static final int LIVE_WEATHER_TYPE_SNOW = 202;
    public static final int LIVE_WEATHER_TYPE_RAIN = 203;
    public static final int LIVE_WEATHER_TYPE_FOG = 204;
    public static final int LIVE_WEATHER_TYPE_DANDELION = 205;
    public static final int LIVE_WEATHER_TYPE_SUNNY = 206;
    public static final int LIVE_WEATHER_TYPE_CLOUDY = 207;
    public static final int LIVE_WEATHER_TYPE_THUNDERSHOWER = 208;
    public static final int LIVE_WEATHER_TYPE_SANDSTORM = 209;

    public static final int LIVE_WEATHER_TYPE_NOCITY = 220;
    public static final int LIVE_WEATHER_TYPE_NODATA = 221;

    public static final int NO_CITY = 13;
    public static final int NO_DATA = 14;
    public static final int SAND_STORM = 10;
    public static final int SHADE = 11;
    public static final int SUN = 1;
    public static final int SUN_NIGHT = 2;
    public static final int THUNDERSHOWER = 12;

    public WeatherTypeLogic() {
        super();
    }

    public int conversionGoWeatherTypeId(int weatherTypeId, boolean isDay) {
        int v3 = 2;
        int[] v0 = new int[] { 14, 14, 1, 3, 11, 6, 5, 7, 12 };
        int v1 = weatherTypeId;
        if (!isDay) {
            v0[v3] = v3;
            v0[3] = 4;
        }

        if (v1 < 0 || v1 > v0.length) {
            v1 = 0;
        }

        return v0[v1];
    }

    public int conversionWeatherTypeId(Context context, int weatherTypeId, float cityTimeZone) {
        int[] v2;
        int v6 = 32;
        if (!FreemeDateUtils.isNowDayTimeInZoneTime(context, cityTimeZone)) {
            v2 = new int[] { 2, 4, 11, 8, 12, 12, 7, 8, 7, 7, 12, 12, 12, 9, 9, 6, 6, 6, 5, 8, 10, 10, 10, 10, 8, 7,
                    12, 12, 12, 9, 6, 6 };
        } else {
            v2 = new int[] { 1, 3, 11, 8, 12, 12, 7, 8, 7, 7, 12, 12, 12, 9, 9, 6, 6, 6, 5, 8, 10, 10, 10, 10, 8, 7,
                    12, 12, 12, 9, 6, 6 };
        }

        SparseIntArray v1 = new SparseIntArray();
        int v0;
        for (v0 = 0; v0 < v6; ++v0) {
            v1.put(v0 + 1, v2[v0]);
        }

        return v1.get(weatherTypeId);
    }

    public static final int getWeatherTypeIcon(int icon1, int icon2) {
        if (icon1 == -1) {
            return getWeatherTypeIcon(icon2);
        } else {
            return getWeatherTypeIcon(icon1);
        }
    }

    public static final int getWeatherTypeIcon(int icon) {
        int type = LIVE_WEATHER_TYPE_NONE;
        switch (icon) {
        case 0:
            type = LIVE_WEATHER_TYPE_SUNNY;
            break;
        case 1:
            type = LIVE_WEATHER_TYPE_CLOUDY;
            break;
        case 2:
            type = LIVE_WEATHER_TYPE_CLOUDY;
            break;
        case 3:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 4:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 5:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 6:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 7:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 8:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 9:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 10:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 11:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 12:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 13:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 14:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 15:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 16:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 17:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 18:
            type = LIVE_WEATHER_TYPE_FOG;
            break;
        case 19:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 20:
            type = LIVE_WEATHER_TYPE_SANDSTORM;
            break;
        case 21:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 22:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 23:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 24:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 25:
            type = LIVE_WEATHER_TYPE_RAIN;
            break;
        case 26:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 27:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 28:
            type = LIVE_WEATHER_TYPE_SNOW;
            break;
        case 29:
            type = LIVE_WEATHER_TYPE_SANDSTORM;
            break;
        case 30:
            type = LIVE_WEATHER_TYPE_SANDSTORM;
            break;
        case 31:
            type = LIVE_WEATHER_TYPE_SANDSTORM;
            break;
        default:
            break;
        }
        return type;
    }

    public static int getLiveWeatherType(int animationId) {
        int type = 200;
        switch (animationId) {
        case 1:
        case 2: {
            type = 206;
            break;
        }
        case 5: {
            type = 204;
            break;
        }
        case 7:
        case 8: {
            type = 203;
            break;
        }
        case 6:
        case 9: {
            type = 202;
            break;
        }
        case 10: {
            type = 209;
            break;
        }
        case 3:
        case 4:
        case 11: {
            type = 207;
            break;
        }
        case 12: {
            type = 208;
            break;
        }
        }

        return type;
    }
}
