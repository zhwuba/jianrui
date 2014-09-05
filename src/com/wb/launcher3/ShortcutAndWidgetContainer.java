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

import com.wb.launcher3.weatherIcon.WeatherIcon;
import com.wb.launcher3.weatherIcon.WeatherIconController;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

public class ShortcutAndWidgetContainer extends ViewGroup {
    static final String TAG = "CellLayoutChildren";

    // These are temporary variables to prevent having to allocate a new object just to
    // return an (x, y) value from helper functions. Do NOT use them to maintain other state.
    private final int[] mTmpCellXY = new int[2];

    private final WallpaperManager mWallpaperManager;

    private boolean mIsHotseatLayout;

    private int mCellWidth;
    private int mCellHeight;

    private int mWidthGap;
    private int mHeightGap;

    private int mCountX;
    private int mCountY;

    private boolean mInvertIfRtl = false;
    
    //*/zhangwuba add live weather 2014-8-18
    private WeatherIconController mWeatherIconController = null;
    //*/

    public ShortcutAndWidgetContainer(Context context) {
        super(context);
        mWallpaperManager = WallpaperManager.getInstance(context);
      //*zhangwuba add live weather 2014-8-18
        mWeatherIconController = WeatherIconController.getInstance();
        //*/
    }

    public void setCellDimensions(int cellWidth, int cellHeight, int widthGap, int heightGap,
            int countX, int countY) {
        mCellWidth = cellWidth;
        mCellHeight = cellHeight;
        mWidthGap = widthGap;
        mHeightGap = heightGap;
        mCountX = countX;
        mCountY = countY;
    }

    public View getChildAt(int x, int y) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();

            if ((lp.cellX <= x) && (x < lp.cellX + lp.cellHSpan) &&
                    (lp.cellY <= y) && (y < lp.cellY + lp.cellVSpan)) {
                return child;
            }
        }
        return null;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        @SuppressWarnings("all") // suppress dead code warning
        final boolean debug = false;
        if (debug) {
            // Debug drawing for hit space
            Paint p = new Paint();
            p.setColor(0x6600FF00);
            for (int i = getChildCount() - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                final CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();

                canvas.drawRect(lp.x, lp.y, lp.x + lp.width, lp.y + lp.height, p);
            }
        }
        //*/Added by tyd Greg 2013-08-20,for eidt mode
        if (mEditMode) {
            float t = 0;
            t = computeRippleTimeLine();
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child instanceof ShakeEditMode) {
                    if (child.getVisibility() == VISIBLE) {
                        int d = ((ShakeEditMode) child).getDirection();
                        float angle = ((ShakeEditMode) child).getInitAngle();
                        float tx = (float) (CIRCLE_R * Math.cos(angle + 360 * t * Math.PI / 180));
                        float ty = (float) (d * CIRCLE_R * Math.sin(angle + 360 * t * Math.PI / 180));
                        // can not used child.setTranslationX(tx) and
                        // child.setTranslationY(ty)
                        canvas.save();
                        canvas.translate(tx, ty);
                        drawChild(canvas, child, getDrawingTime());
                        canvas.restore();
                    }
                }
            }

            invalidate();
            return;
        }
        //*/
        
        //*/zhangwuba add 2014-5-6
        if(mShakeOnce){
            float t = 0;
            t = computeRippleTimeLine();
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child instanceof ShakeEditMode) {
                    if (child.getVisibility() == VISIBLE) {
                        int d = ((ShakeEditMode) child).getDirection();
                        float angle = ((ShakeEditMode) child).getInitAngle();
                        float tx = (float) (CIRCLE_R * Math.cos(angle + 360 * t * Math.PI / 180));
                        float ty = (float) (d * CIRCLE_R * Math.sin(angle + 360 * t * Math.PI / 180));
                        // can not used child.setTranslationX(tx) and
                        // child.setTranslationY(ty)
                        canvas.save();
                        canvas.translate(tx, ty);
                        drawChild(canvas, child, getDrawingTime());
                        canvas.restore();
                    }
                }
            }
            invalidate();
            return;
        }
        //*/
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSpecSize, heightSpecSize);

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child);
            }
        }
    }

    public void setupLp(CellLayout.LayoutParams lp) {
        lp.setup(mCellWidth, mCellHeight, mWidthGap, mHeightGap, invertLayoutHorizontally(),
                mCountX);
    }

    // Set whether or not to invert the layout horizontally if the layout is in RTL mode.
    public void setInvertIfRtl(boolean invert) {
        mInvertIfRtl = invert;
    }

    public void setIsHotseat(boolean isHotseat) {
        mIsHotseatLayout = isHotseat;
    }

    int getCellContentWidth() {
        final LauncherAppState app = LauncherAppState.getInstance();
        final DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
        return Math.min(getMeasuredHeight(), mIsHotseatLayout ?
                grid.hotseatCellWidthPx: grid.cellWidthPx);
    }

    int getCellContentHeight() {
        final LauncherAppState app = LauncherAppState.getInstance();
        final DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
        return Math.min(getMeasuredHeight(), mIsHotseatLayout ?
                grid.hotseatCellHeightPx : grid.cellHeightPx);
    }

    public void measureChild(View child) {
        final LauncherAppState app = LauncherAppState.getInstance();
        final DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
        final int cellWidth = mCellWidth;
        final int cellHeight = mCellHeight;
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
        if (!lp.isFullscreen) {
            lp.setup(cellWidth, cellHeight, mWidthGap, mHeightGap, invertLayoutHorizontally(),
                    mCountX);

            if (child instanceof LauncherAppWidgetHostView) {
                // Widgets have their own padding, so skip
            } else {
                // Otherwise, center the icon
                int cHeight = getCellContentHeight();
                int cellPaddingY = (int) Math.max(0, ((lp.height - cHeight) / 2f));
                int cellPaddingX = (int) (grid.edgeMarginPx / 2f);
                child.setPadding(cellPaddingX, cellPaddingY, cellPaddingX, 0);
            }
        } else {
            lp.x = 0;
            lp.y = 0;
            lp.width = getMeasuredWidth();
            lp.height = getMeasuredHeight();
        }
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
        int childheightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height,
                MeasureSpec.EXACTLY);
        child.measure(childWidthMeasureSpec, childheightMeasureSpec);
    }

    private boolean invertLayoutHorizontally() {
        return mInvertIfRtl && isLayoutRtl();
    }

    public boolean isLayoutRtl() {
        return (getLayoutDirection() == LAYOUT_DIRECTION_RTL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
                int childLeft = lp.x;
                int childTop = lp.y;
                child.layout(childLeft, childTop, childLeft + lp.width, childTop + lp.height);

                if (lp.dropped) {
                    lp.dropped = false;

                    final int[] cellXY = mTmpCellXY;
                    getLocationOnScreen(cellXY);
                    mWallpaperManager.sendWallpaperCommand(getWindowToken(),
                            WallpaperManager.COMMAND_DROP,
                            cellXY[0] + childLeft + lp.width / 2,
                            cellXY[1] + childTop + lp.height / 2, 0, null);
                }
            }
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (child != null) {
            Rect r = new Rect();
            child.getDrawingRect(r);
            requestRectangleOnScreen(r);
        }
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        // Cancel long press for all children
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.cancelLongPress();
        }
    }

    @Override
    protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            view.setDrawingCacheEnabled(enabled);
            // Update the drawing caches
            if (!view.isHardwareAccelerated() && enabled) {
                view.buildDrawingCache(true);
            }
        }
    }

    @Override
    protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
        super.setChildrenDrawnWithCacheEnabled(enabled);
    }
    
    //*/Added by tyd Greg 2013-08-20,for eidt mode
    private boolean mEditMode = false;
    
    private long mStartTime;
    
    private static final float TIME_1_CIRCLE = 700; // 360 angle/TIME_1_CIRCLE ms
    private static final float CIRCLE_R = 1.4f;
    private static final int CLOCKWISE = 1;
    private static final int COUNTERCLOCKWISE = -1;
    private static final int CIRCLE_DIRECTIONS[] = {CLOCKWISE, COUNTERCLOCKWISE};

    public void enterEditMode() {
        mEditMode = true;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ShakeEditMode) {
                int index = (int) (Math.random() * CIRCLE_DIRECTIONS.length);
                float angle = (float) (Math.random() * 360);
                ShakeEditMode editMode = (ShakeEditMode)childView;
                editMode.setRippleInfo(CIRCLE_DIRECTIONS[index], angle);
                editMode.enterEditMode();
            }
        }
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        invalidate();
    }
    
    public void exitEditMode() {
        mEditMode = false;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ShakeEditMode) {
                ((ShakeEditMode) childView).exitEditMode();
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.setPivotX(0);
            child.setPivotY(0);
            child.setRotation(0);
        }
    }
    
    //*/zhangwuba add shake once
    private boolean mShakeOnce = false;
    
    private Handler myHandle = new Handler();
    
    public void shakeOnceTime(){
    	/*
    	mShakeOnce = true;
    	int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ShakeEditMode) {
                int index = (int) (Math.random() * CIRCLE_DIRECTIONS.length);
                float angle = (float) (Math.random() * 360);
                ShakeEditMode editMode = (ShakeEditMode)childView;
                editMode.setRippleInfo(CIRCLE_DIRECTIONS[index], angle);
                //editMode.enterEditMode();
            }
        }
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        invalidate();
     
        myHandle.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mShakeOnce = false;
			}
		}, 600);
		*/
    	
    	int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView instanceof ShakeEditMode) {
                //int index = (int) (Math.random() * CIRCLE_DIRECTIONS.length);
                //float angle = (float) (Math.random() * 360);
                //ShakeEditMode editMode = (ShakeEditMode)childView;
                //editMode.setRippleInfo(CIRCLE_DIRECTIONS[index], angle);
                //editMode.enterEditMode();
            	playanimation(childView);
            }
        }
    }
    
    private void playanimation(View view){
    	view.clearAnimation();
        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 1.05F, 1.0F, 1.05F, 1, 0.5F, 1, 0.5F);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(2);
        scaleAnimation.setDuration(200L);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        animationSet.addAnimation(scaleAnimation);
        view.startAnimation(animationSet);
    }
    //*/
    
    
    
    private float computeRippleTimeLine() {
        int timePassed = (int)(AnimationUtils.currentAnimationTimeMillis() - mStartTime);
        float t = timePassed / TIME_1_CIRCLE;
        
        if (timePassed >= TIME_1_CIRCLE) {
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            t = 1;
        }
        return t;
    }
    //*/
    
    //*/zhangwuba add live weather 2014-8-18
  //*/zhangwuba add live weather 2014-8-18
    @Override
    public void addView(final View child, final int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        Log.i("myl","zhangwuba ------------ ShortcutAndWidgetContainer add view child = " +child);
        if ((child instanceof WeatherIcon)) {
            final WeatherIcon weatherIcon = (WeatherIcon) child;
            post(new Runnable() {
                @Override
                public void run() {
                	if(mIsHotseatLayout){
                		mWeatherIconController.registerIconForDockbar(weatherIcon);
                	}else{
                		mWeatherIconController.registerIcon(weatherIcon);
                	}
                }
            });
        }
    }

    @Override
    public void removeView(View view) {
        if (view != null && ((view instanceof WeatherIcon))) {
            WeatherIcon weatherIcon = (WeatherIcon) view;
            if(mIsHotseatLayout){
            	mWeatherIconController.unregisterIconForDockbar(weatherIcon);
            }else{
            	mWeatherIconController.unregisterIcon(weatherIcon);
            }
        }
        super.removeView(view);
    }

    @Override
    public void removeViewInLayout(View view) {
        if (view != null && ((view instanceof WeatherIcon))) {
            WeatherIcon weatherIcon = (WeatherIcon) view;
            if(mIsHotseatLayout){
            	mWeatherIconController.unregisterIconForDockbar(weatherIcon);
            }else{
            	mWeatherIconController.unregisterIcon(weatherIcon);
            }
        }

        super.removeViewInLayout(view);
    }

    @Override
    public void removeViewsInLayout(int start, int count) {
        View view = null;
        for (int i = start; i < (start + count); i = i + 0x1) {
            view = getChildAt(i);
            if ((view != null) && (view instanceof WeatherIcon)) {
                WeatherIcon weatherIcon = (WeatherIcon) view;
                if(mIsHotseatLayout){
                	mWeatherIconController.unregisterIconForDockbar(weatherIcon);
                }else{
                	mWeatherIconController.unregisterIcon(weatherIcon);
                }
            }
        }
        super.removeViewsInLayout(start, count);
    }

    @Override
    public void removeViewAt(int index) {
        View view = getChildAt(index);
        if ((view != null) && (view instanceof WeatherIcon)) {
            WeatherIcon weatherIcon = (WeatherIcon) view;
            if(mIsHotseatLayout){
            	mWeatherIconController.unregisterIconForDockbar(weatherIcon);
            }else{
            	mWeatherIconController.unregisterIcon(weatherIcon);
            }
        }
        super.removeViewAt(index);
    }

    @Override
    public void removeViews(int start, int count) {
        View view = null;
        for (int i = start; i < (start + count); i = i + 0x1) {
            view = getChildAt(i);
            if ((view != null) && (view instanceof WeatherIcon)) {
                WeatherIcon weatherIcon = (WeatherIcon) view;
                if(mIsHotseatLayout){
                	mWeatherIconController.unregisterIconForDockbar(weatherIcon);
                }else{
                	mWeatherIconController.unregisterIcon(weatherIcon);
                }
            }
        }
        super.removeViews(start, count);
    }

    @Override
    public void removeAllViews() {
        View view = null;
        int count = getChildCount();
        for (int i = 0x0; i < count; i = i + 0x1) {
            view = getChildAt(i);
            if ((view != null) && (view instanceof WeatherIcon)) {
                WeatherIcon weatherIcon = (WeatherIcon) view;
                if(mIsHotseatLayout){
                	mWeatherIconController.unregisterIconForDockbar(weatherIcon);
                }else{
                	mWeatherIconController.unregisterIcon(weatherIcon);
                }
            }
        }
        super.removeAllViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        View view = null;
        int count = getChildCount();
        for (int i = 0x0; i < count; i = i + 0x1) {
            view = getChildAt(i);
            if ((view != null) && (view instanceof WeatherIcon)) {
                WeatherIcon weatherIcon = (WeatherIcon) view;
                if(mIsHotseatLayout){
                	mWeatherIconController.unregisterIconForDockbar(weatherIcon);
                }else{
                	mWeatherIconController.unregisterIcon(weatherIcon);
                }
            }
        }
        super.removeAllViewsInLayout();
    }
    //*/
    //*/
}
