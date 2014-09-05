package com.wb.launcher3.settings;

public class Setting {
    private static final boolean DEBUG = false;
    public static final String DESK_EFFECT_KEY = "desk_effect";
    public static final String LAUNCHERSETTING_PREFERENCE_NAME = "com.freeme.launcher_preferences";
    public static final String LIVEWEATHER_PREFERENCE_NAME = "launcher_liveWeather_preferences";
    public static final String LIVEWEATHER_TYPE = "liveWeather_type";
    public static final int LIVE_WEATHER_TYPE_CLOUDY = 207;
    public static final int LIVE_WEATHER_TYPE_DANDELION = 205;
    public static final int LIVE_WEATHER_TYPE_DYNAMIC = 201;
    public static final int LIVE_WEATHER_TYPE_FOG = 204;
    public static final int LIVE_WEATHER_TYPE_NOCITY = 220;
    public static final int LIVE_WEATHER_TYPE_NODATA = 221;
    public static final int LIVE_WEATHER_TYPE_NONE = 200;
    public static final int LIVE_WEATHER_TYPE_RAIN = 203;
    public static final int LIVE_WEATHER_TYPE_SANDSTORM = 209;
    public static final int LIVE_WEATHER_TYPE_SNOW = 202;
    public static final int LIVE_WEATHER_TYPE_SUNNY = 206;
    public static final int LIVE_WEATHER_TYPE_THUNDERSHOWER = 208;
    public static final String MAINMENU_EFFECT_KEY = "mainmenu_effect";
    public static final String MAINMENU_INOUT_EFFECT_KEY = "mainmenu_inout_effect";
    public static final int MAINMENU_SCROLL_SEEK_BAR = 102;
    public static final String SLANT_EFFECT_CLASS_NAME = "com.freeme.launcher.effect.agent.SlantEffectAgent";
    private static final String TAG = "Setting";
    public static final String USING_REALWEATHER = "usingRealWeather";
    public static final int WORKSPACE_SCROLL_SEEK_BAR = 102;
    private static boolean mIsUsingRealWeather = false;
    private static String mMainMenuEffectClassName = "";
    private static String mMainMenuIOEffectClassName = "";
    private static String mWorkspaceEffectClassName = "";
    private static int mWeatherType = 200;

    public static boolean getIsUsingRealWeather() {
        return mIsUsingRealWeather;
    }

    public static String getMainMenuEffectClassName() {
        return mMainMenuEffectClassName;
    }

    public static String getMainMenuIOEffectClassName() {
        return mMainMenuIOEffectClassName;
    }

    public static int getWeatherType() {
        return mWeatherType;
    }

    public static String getWorkSpaceEffectClassName() {
        return mWorkspaceEffectClassName;
    }

    public static boolean isWorkspaceNeedAnimateToNormal() {
        return mWorkspaceEffectClassName.equals("com.freeme.launcher.effect.agent.SlantEffectAgent");
    }

    public static void setIsUsingRealWeather(boolean using) {
        mIsUsingRealWeather = using;
    }

    public static void setMainMenuEffectClassName(String str) {
        mMainMenuEffectClassName = str;
    }

    public static void setMainMenuIOEffectClassName(String str) {
        mMainMenuIOEffectClassName = str;
    }

    public static void setWeatherType(int type) {
        mWeatherType = type;
    }

    public static void setWorkspaceEffectClassName(String str) {
        mWorkspaceEffectClassName = str;
    }
}
