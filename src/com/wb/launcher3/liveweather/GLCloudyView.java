package com.wb.launcher3.liveweather;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;

public class GLCloudyView extends LiveWeatherGLView {

    public GLCloudyView(Context context) {
        super(context);
        this.init(context);
    }

    public GLCloudyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context) {
        this.mWeatherType = LIVE_WEATHER_TYPE_CLOUDY;
        this.mCanResponseMic = false;
        this.mCanResponseSensor = false;
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setRenderer(new GLCloudyRender(context, (this)));
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.mRenderer != null && ((this.mRenderer instanceof GLCloudyRender))) {
            ((GLCloudyRender) this.mRenderer).onPause();
        }
    }
}
