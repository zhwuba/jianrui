package com.wb.launcher3.liveweather;

import com.wb.launcher3.liveweather.LiveWeatherGLView.IconCallBack;

public abstract interface ILiveWeatherView {
    public abstract boolean canResponseMic();

    public abstract boolean canResponseSensor();

    public abstract void fadeIn();

    public abstract void fadeOut();

    public abstract void flingScreen(int direct, int offset, int acceleration);

    public abstract int getFadeTag();

    public abstract float getGLAlpha();

    public abstract int getVisibility();

    public abstract int getWeatherType();

    public abstract void huffMic(int level);

    public abstract void onPause();

    public abstract void onResume();

    public abstract void resetOffset();

    public abstract void setGLAlpha(float alpha);

    public abstract void setIconCallBack(IconCallBack callback);

    public abstract void setVisibility(int visibility);

    public abstract void shake();

    public abstract void slideScreen(int direct, float scrollX, boolean needResponse);

    public abstract void updateEmptyAreaMotionEvent();

    public abstract void updateMovtionEvent(float x, float y, int motionEvent);
}
