package com.wb.launcher3;

import java.util.ArrayList;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.wb.launcher3.settings.Setting;
import com.wb.launcher3.weatherIcon.IconListener;
import com.wb.launcher3.weatherIcon.WeatherIcon;
import com.wb.launcher3.weatherIcon.WeatherIconController;
import com.wb.launcher3.weatherIcon.WeatherIconDrawInfo;
import com.wb.launcher3.liveweather.ILiveWeatherView;
import com.wb.launcher3.liveweather.LiveWeatherGLView.IconCallBack;
import com.wb.launcher3.mic.HuffThread.IMicCallback;
import com.wb.launcher3.sensor.SensorUtil;
import com.wb.launcher3.sensor.SensorUtil.ISensorCallback;
import com.wb.launcher3.R;

public class LiveWeatherController implements IconListener {
    private static final int HANDLER_COMMAND_FADEIN = 256;
    private static final int HANDLER_COMMAND_FADEOUT = 257;
    private static final int HANDLER_COMMAND_FADEOUT_END = 258;

    private Context mContext;
    private ValueAnimator mFadeInAnimator;
    private ValueAnimator mFadeOutAnimator;
    private int mFadeoutEndDelay;
    private final Handler mHandler;
    private IconCallBack mIconCallBack;
    private boolean mIsCurrPrivatePage;
    private boolean mIsIconOnDrag;
    private boolean mIsInScrollState;
    private boolean mIsPreviewScreenState;
    private boolean mIsSteadyState;
    private boolean mIsToggleBarOpen;
    private IMicCallback mMicCallback;
    private ISensorCallback mSensorCallback;
    private SensorUtil mSensorDetector;
    private WeatherIconController mWeatherIconController;
    private ILiveWeatherView mWeatherView;
    private View mWeatherViewForOuter;

    public LiveWeatherController(Context context) {
        super();
        this.mContext = null;
        this.mWeatherView = null;
        this.mWeatherViewForOuter = null;
        this.mWeatherIconController = null;
        this.mFadeInAnimator = null;
        this.mFadeOutAnimator = null;
        this.mIsSteadyState = true;
        this.mIsToggleBarOpen = false;////true; //zhangwuba modify for our Launcher
        this.mIsIconOnDrag = false;
        this.mIsInScrollState = false;
        this.mIsPreviewScreenState = false;
        this.mIsCurrPrivatePage = false;
        this.mFadeoutEndDelay = 0;
        this.mSensorDetector = null;
        this.mIconCallBack = new IconCallBack() {
            @Override
            public int[] getEdgeForPos(int x, int y) {
                if (mWeatherIconController == null) {
                    return null;
                }

                return mWeatherIconController.getEdgeForPos(x, y);
            }

            @Override
            public WeatherIconDrawInfo getIconInfo(int index, int weathertype) {
                if (mWeatherIconController == null) {
                    return null;
                }

                return mWeatherIconController.getIconInfo(index, weathertype);
            }

            @Override
            public int getIconNum() {
                if (mWeatherIconController == null) {
                    return 0;
                }

                return mWeatherIconController.getIconNum();
            }

            @Override
            public ArrayList getIconRects() {
                if (mWeatherIconController == null) {
                    return null;
                }

                return mWeatherIconController.getIconRects();
            }

            @Override
            public float getStatusBarHeight() {
                if (mContext == null) {
                    return 0;
                }

                if (mContext.getResources() == null) {
                    return 0;
                }

                return mContext.getResources().getDimensionPixelOffset(R.dimen.freeme_workspace_shadow_background_top);
            }

            @Override
            public int[] getUpperEdgeForIcon(int index) {
                if (LiveWeatherController.this.mWeatherIconController == null) {
                    return null;
                }

                return mWeatherIconController.getUpperEdgeForIcon(index);
            }

            @Override
            public boolean isCurrentPrivatePage() {
                return LiveWeatherController.this.mIsCurrPrivatePage;
            }

            @Override
            public boolean isIconOnDrag() {
                return LiveWeatherController.this.mIsIconOnDrag;
            }

            @Override
            public boolean isInScrollState() {
                return LiveWeatherController.this.mIsInScrollState;
            }

            @Override
            public boolean isSteadyState() {
                if (!mIsSteadyState || (mIsPreviewScreenState)) {
                    return false;
                }

                return true;
            }

            @Override
            public boolean isToggleBarOpen() {
                return mIsToggleBarOpen;
            }

            @Override
            public void onFadeoutEnd() {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(HANDLER_COMMAND_FADEOUT_END, mFadeoutEndDelay);
                }
            }

            @Override
            public void onIconAction(int index, int weathertype) {
                if (mWeatherIconController == null) {
                    return;
                }

                mWeatherIconController.onIconAction(index, weathertype);
            }

            @Override
            public boolean reckonPosInIcon(int x, int y) {
                if (mWeatherIconController == null) {
                    return false;
                }

                return mWeatherIconController.reckonPosInIcon(x, y);
            }
        };

        this.mMicCallback = new IMicCallback() {
            @Override
            public void detectSound(float db) {
                huffMic(((int) db));
            }
        };

        this.mSensorCallback = new ISensorCallback() {
            @Override
            public void detectShake() {
                shake();
            }
        };

        this.mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case HANDLER_COMMAND_FADEIN:
                    if (mWeatherView == null) {
                        return;
                    }

                    mWeatherView.fadeIn();
                    break;
                case HANDLER_COMMAND_FADEOUT:
                    if (mWeatherView == null) {
                        return;
                    }

                    mWeatherView.onResume();
                    mWeatherView.fadeOut();
                    break;
                case HANDLER_COMMAND_FADEOUT_END:
                    onPause();
                    if (mFadeoutEndDelay != 401) {
                        return;
                    }

                    mWeatherView.setVisibility(8);
                    break;
                }
            }
        };

        this.init(context);
    }

    public void changeWeather() {
        this.mWeatherView = LiveWeatherFactory.creator(this.mContext);
        if (this.mWeatherView == null) {
            return;
        }

        this.mWeatherView.setIconCallBack(this.mIconCallBack);
        if (this.mWeatherView.canResponseSensor()) {
            this.generateSensorDetector();
        } else {
            this.destroySensorDetector();
        }
    }

    public void destroy() {
        Object v1 = null;
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(v1);
        }

        this.mWeatherIconController.setIconListener(((IconListener) v1));
        this.mWeatherView = ((ILiveWeatherView) v1);
        this.destroySensorDetector();
    }

    private void destroySensorDetector() {
        if (this.mSensorDetector != null) {
            this.mSensorDetector.closeSensor();
            this.mSensorDetector = null;
        }
    }

    public void fadeIn(int delay) {
        int v3 = HANDLER_COMMAND_FADEIN;
        if (this.mWeatherView == null) {
            return;
        }

        if (this.mHandler == null) {
            return;
        }

        this.onResume();
        if (this.mWeatherView.getGLAlpha() == 1f && this.mWeatherView.getVisibility() == 0) {
            Log.i("LiveWeatherController", "fadeIn return 111  getGLAlpha = " + this.mWeatherView.getGLAlpha());
            return;
        }

        if (this.mWeatherView.getFadeTag() == 1) {
            Log.i("LiveWeatherController", "fadeIn return 222  getFadeTag = " + this.mWeatherView.getFadeTag());
            return;
        }

        if (this.mWeatherView.getVisibility() != 0) {
            this.mWeatherView.setGLAlpha(0f);
            this.mWeatherView.setVisibility(View.VISIBLE);
        }

        this.mWeatherView.resetOffset();
        if (this.mWeatherIconController != null) {
            this.mWeatherIconController.showAnimation();
        }

        this.mHandler.removeMessages(v3);
        if (delay > 0 && this.mWeatherView.getFadeTag() == -1) {
            this.mHandler.sendEmptyMessageDelayed(v3, delay);
            return;
        }

        this.mWeatherView.fadeIn();
    }

    public void fadeOut(int startDelay, int endDelay) {
        if (this.mWeatherView != null && this.mHandler != null) {
            this.mHandler.removeMessages(HANDLER_COMMAND_FADEIN);
            if (this.mWeatherView.getVisibility() != 0) {
                Log.i("LiveWeatherController",
                        "fadeOut return 111 getVisibility() = " + this.mWeatherView.getVisibility());
            } else if (this.mWeatherView.getFadeTag() == -1) {
                Log.i("LiveWeatherController", "fadeOut return 222  getFadeTag = " + this.mWeatherView.getFadeTag());
            } else {
                this.mFadeoutEndDelay = endDelay;
                if (startDelay <= 0) {
                    this.mWeatherView.onResume();
                    this.mWeatherView.fadeOut();
                } else {
                    this.mHandler.sendEmptyMessageDelayed(HANDLER_COMMAND_FADEOUT, (startDelay));
                }

                if (this.mWeatherView.getGLAlpha() == 0f && this.mWeatherView.getFadeTag() != 1) {
                    Log.i("LiveWeatherController", "fadeOut return 333  getGLAlpha = " + this.mWeatherView.getGLAlpha());
                    return;
                }

                if (this.mWeatherIconController == null) {
                    return;
                }

                this.mWeatherIconController.hideAnimation();
            }
        }
    }

    public void flingScreen(int direct, int offset, int acceleration) {
        if (this.mWeatherView != null) {
            this.mWeatherView.flingScreen(direct, offset, acceleration);
        }
    }

    private void generateSensorDetector() {
        if (this.mWeatherView == null) {
            return;
        }

        if (!this.mWeatherView.canResponseSensor()) {
            return;
        }

        if (this.mSensorDetector != null) {
            return;
        }

        this.mSensorDetector = SensorUtil.getInstance(this.mContext);
        this.mSensorDetector.setSensorCallback(this.mSensorCallback);
        this.mSensorDetector.openSensor();
    }

    public float getGLAlpha() {
        return this.mWeatherView.getGLAlpha();
    }

    public int getWeatherType() {
        return this.mWeatherView.getWeatherType();
    }

    public View getWeatherView() {
        this.mWeatherViewForOuter = (View) this.mWeatherView;
        return this.mWeatherViewForOuter;
    }

    public void hide() {
        if (this.mWeatherView != null) {
            this.mWeatherView.setVisibility(View.INVISIBLE);
            this.onPause();
        }

        if (this.mWeatherIconController == null) {
            return;
        }

        this.mWeatherIconController.hideAnimation();
    }

    public void huffMic(int level) {
        if (this.mWeatherView == null) {
            return;
        }

        this.mWeatherView.huffMic(level);
    }

    protected void init(Context context) {
        this.mContext = context;
        this.mWeatherView = LiveWeatherFactory.creator(context);
        this.mWeatherView.setIconCallBack(this.mIconCallBack);
        this.generateSensorDetector();
        this.mWeatherIconController = WeatherIconController.getInstance();
        this.mWeatherIconController.setIconListener((this));
        this.mWeatherIconController.setWeatherType(Setting.getWeatherType());
    }

    @Override
    public void onClearIcons() {
    }

    public void onPause() {
        this.onPause(true);
    }

    public void onPause(boolean isPauseIcon) {
        this.destroySensorDetector();
        if (this.mWeatherView != null && this.mWeatherView.getFadeTag() != -1) {
            this.mWeatherView.onPause();
        }

        if (this.mWeatherIconController != null && (isPauseIcon)) {
            this.mWeatherIconController.pauseAnimation();
        }
    }

    @Override
    public void onRegisterIcon(WeatherIcon weatherIcon) {
    }

    public void onResume() {
        if (this.mWeatherView != null) {
            this.mHandler.removeMessages(HANDLER_COMMAND_FADEOUT);
            this.mHandler.removeMessages(HANDLER_COMMAND_FADEOUT_END);
            this.mWeatherView.onResume();
        }

        this.mWeatherIconController.reLoadWeatherIcon();
        if (this.mWeatherIconController != null && !this.mIsToggleBarOpen) {
            this.mWeatherIconController.resumeAnimation();
        }

        this.generateSensorDetector();
    }

    @Override
    public void onUnregisterIcon(WeatherIcon weatherIcon) {
    }

    @Override
    public void onUpdateIcon(WeatherIcon weatherIcon) {
    }

    public void restart() {
    }

    public void setGLAlpha(float alpha) {
        this.mWeatherView.setGLAlpha(alpha);
    }

    public void setGlAlpha(float alpha) {
        this.mWeatherView.setGLAlpha(alpha);
    }

    public void setIsCurrPrivatePage(boolean isCurrPrivatePage) {
        this.mIsCurrPrivatePage = isCurrPrivatePage;
    }

    public void setIsIconOnDrag(boolean isOnDrag) {
        this.mIsIconOnDrag = isOnDrag;
    }

    public void setIsInScrollState(boolean isInScrollState) {
        this.mIsInScrollState = isInScrollState;
    }

    public void setIsToggleBarOpen(boolean isOpen) {
        this.mIsToggleBarOpen = isOpen;
    }

    public void setPreviewScreenState(boolean isPreview) {
        this.mIsPreviewScreenState = isPreview;
    }

    public void setScreenMovingState(boolean isMoving) {
        this.mIsSteadyState = isMoving ? false : true;
    }

    public void shake() {
        if (this.mWeatherView != null) {
            this.mWeatherView.shake();
        }
    }

    public void show() {
        if (this.mWeatherView != null) {
            if (this.mFadeOutAnimator != null && (this.mFadeOutAnimator.isRunning())) {
                this.mFadeOutAnimator.cancel();
            }

            this.mWeatherView.setVisibility(View.VISIBLE);
            this.onResume();
        }

        if (this.mWeatherIconController != null) {
            this.mWeatherIconController.showAnimation();
        }
    }

    public void slideScreen(int direct, float scrollX, boolean needResponse) {
        if (this.mWeatherView != null) {
            this.mWeatherView.slideScreen(direct, scrollX, needResponse);
        }
    }

    public void updateEmptyAreaMotionEvent() {
        if (this.mWeatherView != null) {
            this.mWeatherView.updateEmptyAreaMotionEvent();
        }
    }

    public void updateMotionEvent(float x, float y, int motionEvent) {
        if (this.mWeatherView == null) {
            return;
        }

        this.mWeatherView.updateMovtionEvent(x, y, motionEvent);
    }
}
