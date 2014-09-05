package com.wb.launcher3.liveweather;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;

public class GLSnowFallView extends LiveWeatherGLView {
    GLSnowFallRender mSnowRender;

    public GLSnowFallView(Context context) {
        super(context);
        this.mSnowRender = null;
        this.init(context);
    }

    public GLSnowFallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSnowRender = null;
        this.init(context);
    }

    private void init(Context context) {
        this.mWeatherType = 202;
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.mSnowRender = new GLSnowFallRender(context, (this));
        this.setRenderer(this.mSnowRender);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mSnowRender.unregisterHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mSnowRender.unregisterHandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mSnowRender.registerHandler();
    }

    @Override
    public void shake() {
        if (this.mSnowRender != null) {
            this.mSnowRender.shake();
        }
    }
}
