package com.wb.launcher3.liveweather;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.wb.launcher3.weatherIcon.WeatherIconDrawInfo;

public class LiveWeatherGLView extends GLSurfaceView implements ILiveWeatherView {
    public abstract interface IconCallBack {
        public abstract int[] getEdgeForPos(int x, int y);

        public abstract WeatherIconDrawInfo getIconInfo(int index, int weatherType);

        public abstract int getIconNum();

        public abstract ArrayList<Rect> getIconRects();

        public abstract float getStatusBarHeight();

        public abstract int[] getUpperEdgeForIcon(int index);

        public abstract boolean isCurrentPrivatePage();

        public abstract boolean isIconOnDrag();

        public abstract boolean isInScrollState();

        public abstract boolean isSteadyState();

        public abstract boolean isToggleBarOpen();

        public abstract void onFadeoutEnd();

        public abstract void onIconAction(int index, int weatherType);

        public abstract boolean reckonPosInIcon(int x, int y);
    }

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

    protected boolean mCanResponseMic;
    protected boolean mCanResponseSensor;
    protected float mHuffDb;
    protected float mLastScrollX;
    protected LiveWeatherGLRender mRenderer;
    protected int mWeatherType;

    public LiveWeatherGLView(Context context) {
        super(context);

        this.mLastScrollX = -1f;
        this.mWeatherType = LIVE_WEATHER_TYPE_NONE;
        this.mCanResponseMic = true;
        this.mCanResponseSensor = true;
        this.init(context);
    }

    public LiveWeatherGLView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mLastScrollX = -1f;
        this.mWeatherType = LIVE_WEATHER_TYPE_NONE;
        this.mCanResponseMic = true;
        this.mCanResponseSensor = true;
        this.init(context);
    }

    @Override
    public boolean canResponseMic() {
        return this.mCanResponseMic;
    }

    @Override
    public boolean canResponseSensor() {
        return this.mCanResponseSensor;
    }

    @Override
    public void fadeIn() {
        if (this.mRenderer != null) {
            this.mRenderer.fadeIn();
        }
    }

    @Override
    public void fadeOut() {
        if (this.mRenderer != null) {
            this.mRenderer.fadeOut();
        }
    }

    @Override
    public void flingScreen(int direct, int offset, int acceleration) {
        if (this.mRenderer == null) {
            return;
        }

        if (acceleration == 0) {
            return;
        }

        this.mRenderer.setAcceleration(acceleration);
    }

    @Override
    public int getFadeTag() {
        return mRenderer != null ? mRenderer.getFadeTag() : 0;
    }

    @Override
    public float getGLAlpha() {
        return mRenderer != null ? mRenderer.getGLAlpha() : 0;
    }

    @Override
    public int getWeatherType() {
        return this.mWeatherType;
    }

    public void hide() {
    }

    @Override
    public void huffMic(int level) {
    }

    private void init(Context context) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.mRenderer != null) {
            this.mRenderer.resetLastDrawTime();
        }
    }

    public void requestRenderDelayed(long delay) {
        try {
            Thread.sleep(delay);
            this.requestRender();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetOffset() {
        this.mLastScrollX = 0f;
        if (this.mRenderer != null) {
            this.mRenderer.resetOffset();
        }
    }

    public void restart() {
    }

    @Override
    public void setGLAlpha(final float alpha) {
        if (this.mRenderer != null) {
            this.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (LiveWeatherGLView.this.mRenderer != null) {
                        LiveWeatherGLView.this.mRenderer.setGLAlpha(alpha);
                    }
                }
            });
        }
    }

    @Override
    public void setIconCallBack(IconCallBack iconCallBack) {
        this.mRenderer.setIconCallBack(iconCallBack);
    }

    public void setRenderer(LiveWeatherGLRender renderer) {
        super.setRenderer((renderer));
        this.mRenderer = renderer;
        this.setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void shake() {
    }

    public void show() {
    }

    @Override
    public void slideScreen(int direct, float scrollX, boolean needResponse) {
        if (this.mLastScrollX > 0f) {
            float newScrollX = this.mLastScrollX - scrollX;
            if (needResponse && Math.abs(newScrollX) < this.mRenderer.getWidth() * 0.5f) {
                this.updateOffset(newScrollX);
            }
        }

        this.mLastScrollX = scrollX;
    }

    @Override
    public void updateEmptyAreaMotionEvent() {
        if (this.mRenderer != null) {
            this.mRenderer.updateEmptyAreaMotionEvent();
        }
    }

    @Override
    public void updateMovtionEvent(float x, float y, int motionEvent) {
        if (this.mRenderer != null) {
            this.mRenderer.updateMovtionEvent(x, y, motionEvent);
        }
    }

    public void updateOffset(final float offset) {
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (LiveWeatherGLView.this.mRenderer != null) {
                    LiveWeatherGLView.this.mRenderer.setOffset(offset);
                }
            }
        });
    }
}
