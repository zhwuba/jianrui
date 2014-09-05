package com.wb.launcher3.liveweather;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;

public class GLDandelionView extends LiveWeatherGLView {
    private GLDandelionRender mDandelionRender;

    public GLDandelionView(Context context) {
        super(context);
        this.init(context);
    }

    public GLDandelionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context) {
        this.mWeatherType = LiveWeatherGLView.LIVE_WEATHER_TYPE_DANDELION;
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.mDandelionRender = new GLDandelionRender(context, (this));
        this.setRenderer(this.mDandelionRender);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mDandelionRender.unregisterHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mDandelionRender.unregisterHandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mDandelionRender.registerHandler();
    }

    @Override
    public void shake() {
        if (this.mDandelionRender == null) {
            return;
        }

        this.mDandelionRender.shake();
    }
}
