package com.wb.launcher3.weatherIcon;

import com.wb.launcher3.FastBitmapDrawable;

public abstract interface WeatherIcon {
    public abstract WeatherIconDrawInfo getDrawInfo();

    public abstract FastBitmapDrawable getFastBitmapDrawable();

    public abstract void initDrawInfo(int type);

    public abstract void onDrawIcon(WeatherIconDrawInfo drawInfo);

    public abstract void removeDrawInfo();

    public abstract void setDrawInfo(WeatherIconDrawInfo drawInfo);
}
