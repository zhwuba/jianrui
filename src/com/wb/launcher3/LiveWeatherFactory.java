package com.wb.launcher3;

import android.content.Context;

import com.wb.launcher3.settings.Setting;
import com.wb.launcher3.liveweather.GLCloudyView;
import com.wb.launcher3.liveweather.GLDandelionView;
import com.wb.launcher3.liveweather.GLRainFallView;
import com.wb.launcher3.liveweather.GLSnowFallView;
import com.wb.launcher3.liveweather.GLSunnyView;
import com.wb.launcher3.liveweather.GLThunderShowerView;
import com.wb.launcher3.liveweather.ILiveWeatherView;
import com.wb.launcher3.liveweather.LiveWeatherGLView;
import com.wb.launcher3.liveweather.SteamView;

public class LiveWeatherFactory {

    public static ILiveWeatherView creator(Context context) {
        ILiveWeatherView liveWeatherView = null;

        int type = Setting.getWeatherType();

        switch (type) {
        case LiveWeatherGLView.LIVE_WEATHER_TYPE_NONE:
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_DYNAMIC:
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_SNOW:
            liveWeatherView = new GLSnowFallView(context);
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_RAIN:
            liveWeatherView = new GLRainFallView(context);
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_FOG:
            liveWeatherView = new SteamView(context, false);
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_DANDELION:
            liveWeatherView = new GLDandelionView(context);
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_SUNNY:
            liveWeatherView = new GLSunnyView(context);
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_CLOUDY:
            liveWeatherView = new GLCloudyView(context);
            break;

        case LiveWeatherGLView.LIVE_WEATHER_TYPE_THUNDERSHOWER:
            liveWeatherView = new GLThunderShowerView(context);
            break;
        }

        return liveWeatherView;
    }
}
