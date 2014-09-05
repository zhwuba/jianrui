package com.wb.launcher3.liveweather;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;

import com.wb.launcher3.weatherIcon.ThunderAnimation;

public class GLThunderShowerView extends LiveWeatherGLView {

    public GLThunderShowerView(Context context) {
        super(context);
        this.init(context);
    }

    public GLThunderShowerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context) {
        this.mWeatherType = LiveWeatherGLView.LIVE_WEATHER_TYPE_THUNDERSHOWER;
        this.mCanResponseMic = false;
        this.mCanResponseSensor = false;
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setRenderer(new GLThunderShowerRender(context, (this)));
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setZOrderOnTop(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ThunderAnimation.CHANGE_COLOR = false;
        if (this.mRenderer != null && ((this.mRenderer instanceof GLThunderShowerRender))) {
            ((GLThunderShowerRender) this.mRenderer).resetRenderOffset();
            ((GLThunderShowerRender) this.mRenderer).removeSmallIconDrops();
        }
    }
}
