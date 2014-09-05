package com.wb.launcher3.weatherIcon;

public abstract interface IconListener {
    public abstract void onClearIcons();

    public abstract void onRegisterIcon(WeatherIcon weatherIcon);

    public abstract void onUnregisterIcon(WeatherIcon weatherIcon);

    public abstract void onUpdateIcon(WeatherIcon weatherIcon);
}
