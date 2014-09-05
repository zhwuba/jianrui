/*
 * Copyright (C) 2009 The Android Open Source Project
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

import java.lang.reflect.Method;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.wb.launcher3.R;
import com.wb.launcher3.DragLayer.TouchCompleteListener;

/**
 * {@inheritDoc}
 */
public class LauncherAppWidgetHostView extends AppWidgetHostView implements TouchCompleteListener,ShakeEditMode,View.OnClickListener {
    private final static String TAG = "LauncherAppWidgetHostView";

    private CheckLongPressHelper mLongPressHelper;
    private LayoutInflater mInflater;
    private Context mContext;
    private int mPreviousOrientation;
    private DragLayer mDragLayer;

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        mContext = context;
        mLongPressHelper = new CheckLongPressHelper(this);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDragLayer = ((Launcher) context).getDragLayer();
        /*/Added by tyd Greg 2013-08-20,for eidt mode
        Resources res = context.getResources();
        mAppwidgetFrame = res.getDrawable(R.drawable.appwidget_frame);
        mRemoveButtonSize = res.getDimensionPixelSize(R.dimen.appwidget_remove_btn_size);
        
        setClipChildren(false);
        setClipToPadding(false);
        //*/
    }

    @Override
    protected View getErrorView() {
        /*/Added by tyd Greg 2013-10-21,for support private widget
        mErrorView = mInflater.inflate(R.layout.appwidget_error, this, false);
        return mErrorView;
        /*/
        return mInflater.inflate(R.layout.appwidget_error, this, false);
        //*/
    }

    @Override
    public void updateAppWidget(RemoteViews remoteViews) {
        // Store the orientation in which the widget was inflated
        mPreviousOrientation = mContext.getResources().getConfiguration().orientation;
        super.updateAppWidget(remoteViews);
    }

    public boolean orientationChangedSincedInflation() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (mPreviousOrientation != orientation) {
           return true;
       }
       return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Consume any touch events for ourselves after longpress is triggered
        if (mLongPressHelper.hasPerformedLongPress()) {
            mLongPressHelper.cancelLongPress();
            return true;
        }

        // Watch for longpress events at this level to make sure
        // users can always pick up this widget
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLongPressHelper.postCheckForLongPress();
                mDragLayer.setTouchCompleteListener(this);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLongPressHelper.cancelLongPress();
                break;
        }

        // Otherwise continue letting touch events fall through to children
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        /*/Remarkded by tyd Greg 2013-08-20,for eidt mode
        // If the widget does not handle touch, then cancel
        // long press when we release the touch
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLongPressHelper.cancelLongPress();
                break;
        }
        return false;
        //*/
        if (mEditMode) {
            boolean isLongClick = false;
            int action = ev.getAction() & MotionEvent.ACTION_MASK;
            switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mLastDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                long timeInterval = System.currentTimeMillis() - mLastDownTime;
                if (timeInterval > DURATION_BEFORE_DRAG) {
                    isLongClick = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isLongClick = false;
                break;
            default:
                break;
            }
            return isLongClick;
        } else {
            return super.onTouchEvent(ev);
        }
        //*/
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        mLongPressHelper.cancelLongPress();
    }

    @Override
    public void onTouchComplete() {
        mLongPressHelper.cancelLongPress();
    }

    @Override
    public int getDescendantFocusability() {
        return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
    }

    //*/Added by tyd Greg 2013-08-20,for eidt mode
    private final long DURATION_BEFORE_DRAG = 100;
    private Rect mRect = new Rect();
    private Rect mRemoveRect = new Rect();
    private boolean mEditMode = false;
    private Drawable mAppwidgetFrame = null;
    private ImageView mRemoveButton = null;
    private int mRemoveButtonSize = 20;
    private Launcher mLauncher;
    private float mLastMotionX;
    private float mLastMotionY;
    private long mLastDownTime;
    
    private int mDirection = 1;
    private float mInitAngle = 0;
    
    @Override
    public void enterEditMode() {
        if (!mEditMode) {
            mEditMode = true;
            if (mRemoveButton == null) {
                mRemoveButton = new ImageView(getContext());
                mRemoveButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mRemoveButton.setImageResource(R.drawable.app_remove_btn); 
            }
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mRemoveButtonSize, mRemoveButtonSize);
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.leftMargin = -getPaddingLeft();
            layoutParams.topMargin = -getPaddingTop();
            addView(mRemoveButton, layoutParams);
            mRemoveButton.setOnClickListener(this);
            getRootView().setEnabled(false);
        }
    }

    @Override
    public void exitEditMode() {
        mEditMode = false;
        removeView(mRemoveButton);
        getRootView().setEnabled(true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(mEditMode){
            mRect.left = 1;
            mRect.top = 1;
            mRect.right = getMeasuredWidth() - 1;
            mRect.bottom = getMeasuredHeight() - 1;
            mAppwidgetFrame.setBounds(mRect);
            mAppwidgetFrame.draw(canvas);
        }
        super.dispatchDraw(canvas);

    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mEditMode) {
            mRemoveButton.getHitRect(mRemoveRect);
            if (mRemoveRect.contains((int) ev.getX(), (int) ev.getY())) {
                return mRemoveButton.dispatchTouchEvent(ev);
            } else {
                return onTouchEvent(ev);
            }
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public void onClick(View v) {
        if (mLauncher != null) {
            LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) getTag();
            if (info != null) {
                exitEditMode();
                mLauncher.removeAppWidgetByButton(info);
            }
        }
    }
    
    @Override
    public boolean isRemoveable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRippleInfo(int direction, float angle) {
        mDirection = direction;
        mInitAngle = angle;
    }

    @Override
    public int getDirection() {
        return mDirection;
    }
    
    @Override
    public float getInitAngle() {
        return mInitAngle;
    }
    
    public void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }
    
    //for support private widget
    private View mErrorView;

    public void setOppoWidget() {
        if (mErrorView != null) {
            mErrorView.setVisibility(INVISIBLE);
            setPadding(0, 0, 0, 0);
        }
    }
    
    boolean mIsPrivateWiget = false;
    private View mChildView = null;

    public View getChildView() {
        return this.mChildView;
    }

    public void setChildView(View paramView) {
        this.mChildView = paramView;
    }
    
    public void setIsPrivateWidget(boolean isPrivate){
        mIsPrivateWiget = isPrivate;
    }
    
    public boolean getIsPrivateWidget(){
        return mIsPrivateWiget;
    }
    
    
    public Object command(int paramInt, int[] paramArrayOfInt, Object paramObject) {
        Log.d("Ben", "command mChildView= " + this.mChildView);
        Class<?> cls = null;
        if (mChildView != null) {
            cls = mChildView.getClass();
            try {
                Class<?>[] clsArgs = new Class[3];
                clsArgs[0] = Integer.TYPE;
                clsArgs[1] = new int[2].getClass();
                clsArgs[2] = new Object().getClass();

                Method method = cls.getDeclaredMethod("onCommand", clsArgs);
                Object[] obj = new Object[3];
                obj[0] = Integer.valueOf(paramInt);
                obj[1] = paramArrayOfInt;
                obj[2] = paramObject;

                return method.invoke(mChildView, obj);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

	@Override
	public void shakeOnceTime() {
		// TODO Auto-generated method stub
		
	}

}
