package com.wb.launcher3.weatherIcon;

import java.util.ArrayList;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public abstract class AnimationThread implements WeatherIconAnimation, Runnable {
    private static final String TAG = "AnimationThread";

    protected boolean mEnableCircled;
    protected int mFrameTime;
    private boolean mIsRun;
    protected int mMaxFrame;
    private boolean mPauseFlag;
    private Object mPauseLock;
    private Handler mWorker;
    private HandlerThread mWorkerThread;

    public AnimationThread() {
        super();
        this.mFrameTime = 0;
        this.mMaxFrame = 0;
        this.mEnableCircled = false;
        this.mPauseLock = null;
        this.mPauseFlag = false;
        this.mIsRun = false;
        this.mWorkerThread = null;
        this.mWorker = null;
        this.mPauseLock = new Object();
        this.mPauseFlag = false;
    }

    protected void AnimationSleep(int time) {
        if (time < 1) {
            if (this.mWorkerThread != null) {
                Thread.yield();
            }
            return;
        }

        this.threadSleep((time));
    }

    public abstract int getAnimationMaxFrame();

    public abstract WeatherIconController getIconController();

    public abstract ArrayList<WeatherIconDrawInfo> invokeFrame(int frame);

    @Override
    public void onPauseAnimation() {
        synchronized (mPauseLock) {
            this.mPauseFlag = true;
        }
    }

    @Override
    public void onResumeAnimation() {
        synchronized (mPauseLock) {
            this.mPauseFlag = false;
            this.mPauseLock.notifyAll();
        }
    }

    @Override
    public void onStartAnimation(HandlerThread workThread) {
        if (!this.mIsRun) {
            this.mIsRun = true;
            if (this.mWorkerThread == null) {
                this.mWorkerThread = workThread;
                this.mWorker = new Handler(this.mWorkerThread.getLooper());
            }

            this.mWorker.post(this);
        }
    }

    @Override
    public void onStopAnimation() {
        this.mIsRun = false;
        if (this.mPauseFlag) {
            this.onResumeAnimation();
        }
    }

    private void pauseThread() {
        synchronized (mPauseLock) {
            if (this.mPauseFlag) {
                try {
                    this.mPauseLock.wait();
                } catch (Exception v0) {
                    Log.w(AnimationThread.TAG, "pauseThread fails");
                }
            }
        }
    }

    public void preDrawIcons(ArrayList<WeatherIconDrawInfo> icons) {
        WeatherIconController iconController = this.getIconController();

        if (iconController != null) {
            iconController.onPreDrawIcons(icons);
        } else {
            Log.w(AnimationThread.TAG, "preDrawIcons controllerInstance is null !");
        }
    }

    @Override
    public void run() {
        ArrayList<WeatherIconDrawInfo> newDrawInfoList;
        this.mMaxFrame = this.getAnimationMaxFrame();

        do {
            for (int i = 0; i <= this.mMaxFrame; ++i) {
                this.pauseThread();

                if (!this.mIsRun) {
                    return;
                }

                synchronized (this.getIconController().getRegisterIcons()) {
                    newDrawInfoList = this.invokeFrame(i);
                }

                if (newDrawInfoList != null) {
                    this.preDrawIcons(newDrawInfoList);
                }

                this.AnimationSleep(this.mFrameTime);
            }
        } while (this.mEnableCircled);
    }

    public void setEnableCircled(boolean enableCircled) {
        this.mEnableCircled = enableCircled;
    }

    public void setFrameTime(int frame) {
        this.mFrameTime = frame;
    }

    protected void threadSleep(long time) {
        try {
            if (this.mWorkerThread == null) {
                return;
            }

            Thread.sleep(time, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
