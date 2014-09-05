package com.wb.launcher3.liveweather;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;

public class GLRainFallView extends LiveWeatherGLView {

    public GLRainFallView(Context context) {
        super(context);
        this.init(context);
    }

    public GLRainFallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context) {
        this.mWeatherType = LiveWeatherGLView.LIVE_WEATHER_TYPE_RAIN;
        this.mCanResponseMic = false;
        this.mCanResponseSensor = false;
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setRenderer(new GLRainFallRender(context, (this)));
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.mRenderer == null) {
            return;
        }

        if (!(this.mRenderer instanceof GLRainFallRender)) {
            return;
        }

        ((GLRainFallRender) this.mRenderer).resetRenderOffset();
        ((GLRainFallRender) this.mRenderer).removeSmallIconDrops();
    }
}
