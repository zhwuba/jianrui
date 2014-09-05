package com.wb.launcher3.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Gallery;
import android.widget.Scroller;

class PreviewGallery extends Gallery {
    private static final boolean DEBUG = false;
    private static final int INVALID_POINTER = -1;
    private static final String TAG = "PreviewGallery";
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    public static final int TYPE_APPLICATION_SHORTCUT = 3;
    public static final int TYPE_APPWIDGET = 2;
    public static final int TYPE_FOLDER = 1;
    private boolean bFristScroll = false;
    private int mActivePointerId = -1;
    private float mLastMotionX;
    private Scroller mScroller;
    private int mTouchSlop;
    private int mTouchState = 0;

    public PreviewGallery(Context context) {
        this(context, null);
    }

    public PreviewGallery(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setUnselectedAlpha(1.0F);
    }

    public PreviewGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUnselectedAlpha(1.0F);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    public void SrcollToChild(int curpos, int newpos) {
        setSelection(newpos, true);
    }

    public boolean isGalleryScrolling() {
        return mTouchState == 1;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityX > 0.0F) {
            return super.onKeyDown(21, null);
        }
        return super.onKeyDown(22, null);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if ((action == 2) && (mTouchState != 0)) {
            return true;
        }

        switch (action & 0xFF) {
        case 0:
            mLastMotionX = ev.getX();
            mActivePointerId = ev.getPointerId(0);
            if (mScroller.isFinished()) {
                mTouchState = 0;
            } else {
                mTouchState = 1;
            }
            break;
        case 1:
            break;
        case 2:
            int pointerIndex = ev.findPointerIndex(mActivePointerId);
            if (pointerIndex != -1) {
                float x = ev.getX(pointerIndex);
                int xDiff = (int) Math.abs((x - mLastMotionX));
                if (xDiff > mTouchSlop / 2) {
                    bFristScroll = true;
                    mTouchState = 1;
                    mLastMotionX = x;
                }
            }
            break;
        case 3:
            mTouchState = 0;
            mActivePointerId = -1;
            break;
        }

        if (mTouchState == 0) {
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (bFristScroll) {
            bFristScroll = false;
            return super.onDown(e2);
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }
}
