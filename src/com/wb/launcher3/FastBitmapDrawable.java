/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wb.launcher3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.wb.launcher3.weatherIcon.IconSnowEffect;
import com.wb.launcher3.weatherIcon.SnowIconDrawInfo;
import com.wb.launcher3.weatherIcon.ThunderAnimation;
import com.wb.launcher3.weatherIcon.ThunderIconDrawInfo;
import com.wb.launcher3.weatherIcon.WeatherIconController;
import com.wb.launcher3.weatherIcon.WeatherIconDrawInfo;

public class FastBitmapDrawable extends Drawable {
    static final boolean DEBUG = false;
    static final String TAG = "FastBitmapDrawable";
    ColorMatrix cm;
    private float[] colorArray;
    private boolean hasChangeColor;
    private int mAlpha;
    private Bitmap mBitmap;
    private int mHeight;
    private IconSnowEffect mIconSnowEffect;
    private final Paint mPaint;
    private WeatherIconDrawInfo mWeatherIconDrawInfo;
    private int mWidth;
    ColorFilter normalColorFilter;
    private float[] resetArray;
    private static PorterDuffColorFilter mDefultColorFilter = new PorterDuffColorFilter(0x64000000,
            PorterDuff.Mode.SRC_ATOP);

    FastBitmapDrawable(Bitmap b) {
        super();
        mPaint = new Paint();
        mWeatherIconDrawInfo = null;
        colorArray = new float[] { -1f, 0f, 0f, 0f, 255f, 0f, -1f, 0f, 0f, 255f, 0f, 0f, -1f, 0f, 255f, 0f, 0f, 0f, 1f,
                0f };
        resetArray = new float[] { 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f };
        hasChangeColor = false;
        cm = new ColorMatrix();
        normalColorFilter = null;
        mAlpha = 255;
        mBitmap = b;
        mIconSnowEffect = new IconSnowEffect();
        if (b != null) {
            mWidth = mBitmap.getWidth();
            mHeight = mBitmap.getHeight();
        } else {
            mHeight = 0;
            mWidth = 0;
        }

        mPaint.setFilterBitmap(true);
        normalColorFilter = mPaint.getColorFilter();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect r = getBounds();
        if ((mBitmap != null) && (!mBitmap.isRecycled())) {
            if ((mWeatherIconDrawInfo != null) && (WeatherIconController.getInstance().getHideFlag() == 0)) {
                if (mWeatherIconDrawInfo instanceof SnowIconDrawInfo) {
                    SnowIconDrawInfo snowIconDrawInfo = (SnowIconDrawInfo) mWeatherIconDrawInfo;
                    mIconSnowEffect.updateSnow(canvas, mBitmap, r.left, r.top, mPaint,
                            (int) snowIconDrawInfo.getThickness(), snowIconDrawInfo.getUpAlpha(),
                            snowIconDrawInfo.getDownAlpha());
                } else if (mWeatherIconDrawInfo instanceof ThunderIconDrawInfo) {
                    if (ThunderAnimation.CHANGE_COLOR) {
                        if (!hasChangeColor) {
                            cm.set(colorArray);
                            hasChangeColor = true;
                        } else {
                            cm.set(resetArray);
                            hasChangeColor = false;
                        }
                        mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
                    } else if (mPaint.getColorFilter() != mDefultColorFilter) {
                        mPaint.setColorFilter(normalColorFilter);
                    }
                    canvas.drawBitmap(mBitmap, r.left, r.top, mPaint);
                } else {
                    if (mPaint.getColorFilter() != mDefultColorFilter) {
                        mPaint.setColorFilter(normalColorFilter);
                    }
                    canvas.drawBitmap(mBitmap, r.left, r.top, mPaint);
                }
            } else {
                if (mPaint.getColorFilter() != mDefultColorFilter) {
                    mPaint.setColorFilter(normalColorFilter);
                }
                canvas.drawBitmap(mBitmap, r.left, r.top, mPaint);
            }
        }
        Log.e(TAG, " draw --- but null == mBitmap.");
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return -0x3;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        mPaint.setAlpha(alpha);
    }

    public void setFilterBitmap(boolean filterBitmap) {
        mPaint.setFilterBitmap(filterBitmap);
    }

    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public int getMinimumWidth() {
        return mWidth;
    }

    @Override
    public int getMinimumHeight() {
        return mHeight;
    }

    public void setBitmap(Bitmap b) {
        mBitmap = b;
        if (b != null) {
            mWidth = mBitmap.getWidth();
            mHeight = mBitmap.getHeight();
        } else {
            mWidth = mHeight = 0;
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setColorFilterDark() {
        mPaint.setColorFilter(mDefultColorFilter);
        invalidateSelf();
    }

    public void setAlphaDark() {
        setAlpha(0xc8);
        invalidateSelf();
    }

    public void setAlphaDefault() {
        setAlpha(0xff);
        invalidateSelf();
    }

    public void setWeatherIconDrawInfo(WeatherIconDrawInfo weatherIconDrawInfo) {
        mWeatherIconDrawInfo = weatherIconDrawInfo;
        if ((weatherIconDrawInfo instanceof SnowIconDrawInfo)) {
            Bitmap b = mBitmap.copy(mBitmap.getConfig(), false);
            mIconSnowEffect.makeSnowBitmap(b);
            b.recycle();
        } else {
            mIconSnowEffect.resetThickness();
        }
    }

    public WeatherIconDrawInfo getWeatherIconDrawInfo() {
        return mWeatherIconDrawInfo;
    }

    public void updateWeatherIconDrawInfo(WeatherIconDrawInfo newInfo) {
        if (mWeatherIconDrawInfo != null) {
            mWeatherIconDrawInfo.updateDrawInfo(newInfo);
        }
    }
}
