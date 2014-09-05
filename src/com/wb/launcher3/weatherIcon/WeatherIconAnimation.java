package com.wb.launcher3.weatherIcon;

import android.os.HandlerThread;

public abstract interface WeatherIconAnimation {
    public abstract void onHideAnimation();

    public abstract void onPauseAnimation();

    public abstract void onResumeAnimation();

    public abstract void onShowAnimation();

    public abstract void onStartAnimation(HandlerThread workThread);

    public abstract void onStopAnimation();
}
