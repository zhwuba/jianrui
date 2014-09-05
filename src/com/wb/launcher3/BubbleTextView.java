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

import java.util.ArrayList;
import java.util.HashSet;

import com.wb.launcher3.R;
import com.wb.launcher3.config.TydtechConfig;
import com.wb.launcher3.weatherIcon.WeatherIcon;
import com.wb.launcher3.weatherIcon.WeatherIconDrawInfo;
import com.wb.launcher3.FastBitmapDrawable;
import com.wb.launcher3.ItemInfo;
import com.wb.launcher3.LauncherSettings;
import com.wb.launcher3.Utilities;
import com.wb.launcher3.weatherIcon.SnowIconDrawInfo;
import com.wb.launcher3.weatherIcon.ThunderIconDrawInfo;
import com.wb.launcher3.settings.Setting;
import com.wb.launcher3.weatherIcon.WeatherIconController;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
//*/zhangwuba add live weather 2014-8-15
@SuppressLint("DrawAllocation")
public class BubbleTextView extends TextView implements ShakeEditMode, WeatherIcon {
    static final float SHADOW_LARGE_RADIUS = 4.0f;
    static final float SHADOW_SMALL_RADIUS = 1.75f;
    static final float SHADOW_Y_OFFSET = 2.0f;
    static final int SHADOW_LARGE_COLOUR = 0xDD000000;
    static final int SHADOW_SMALL_COLOUR = 0xCC000000;
    static final float PADDING_H = 8.0f;
    static final float PADDING_V = 3.0f;

    private int mPrevAlpha = -1;

    private HolographicOutlineHelper mOutlineHelper;
    private final Canvas mTempCanvas = new Canvas();
    private final Rect mTempRect = new Rect();
    private boolean mDidInvalidateForPressedState;
    private Bitmap mPressedOrFocusedBackground;
    private int mFocusedOutlineColor;
    private int mFocusedGlowColor;
    private int mPressedOutlineColor;
    private int mPressedGlowColor;

    private int mTextColor;
    //*/Modified by tyd Greg 2014-03-28,for use ourselves' style
    private boolean mShadowsEnabled = false;
    //*/
    private boolean mIsTextVisible;

    private boolean mBackgroundSizeChanged;
    private Drawable mBackground;

    private boolean mStayPressed;
    private CheckLongPressHelper mLongPressHelper;
    
    //*/zhangwuba 2014-8-15 live weather
    private int mIconSnowHeight;
    //*/

    public BubbleTextView(Context context) {
        super(context);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void onFinishInflate() {
        super.onFinishInflate();

        // Ensure we are using the right text size
        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
        setTextSize(TypedValue.COMPLEX_UNIT_SP, grid.iconTextSize);
        setTextColor(getResources().getColor(R.color.workspace_icon_text_color));
    }

    private void init() {
        mLongPressHelper = new CheckLongPressHelper(this);
        mBackground = getBackground();

        mOutlineHelper = HolographicOutlineHelper.obtain(getContext());

        final Resources res = getContext().getResources();
        mFocusedOutlineColor = mFocusedGlowColor = mPressedOutlineColor = mPressedGlowColor =
            res.getColor(R.color.outline_color);
        mDelIconTopPadding = Utilities.getUnistallBitmapTop(getContext());
        mDelIconLeftPadding = Utilities.getUnistallBitmapLeft(getContext());
        /*/Remarked by tyd Greg 2014-03-28,for use ourselves' style
        setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        //*/
        
        //*/Modified by tyd Greg 2013-12-11,for new install
        mNewInstallPrefix = Utilities.getNewInstallPrefix(getContext());
        //*/
        
        //*/zhangwuba live weather 2014-8-15
        mIconSnowHeight = res.getInteger(R.integer.config_IconSnowHeight);
        //*/
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        Bitmap b = info.getIcon(iconCache);
        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();

        setCompoundDrawables(null,
                Utilities.createIconDrawable(b), null, null);
        /*/Modified by tyd Greg 2013-12-11,for new install
        setCompoundDrawablePadding((int) ((grid.folderIconSizePx - grid.iconSizePx) / 2f));
        setText(info.title);
        //*/
        updateTitle(info);
        //*/
        setTag(info);
    }
    

    @Override
    protected boolean setFrame(int left, int top, int right, int bottom) {
        if (getLeft() != left || getRight() != right || getTop() != top || getBottom() != bottom) {
            mBackgroundSizeChanged = true;
        }
        return super.setFrame(left, top, right, bottom);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBackground || super.verifyDrawable(who);
    }

    @Override
    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
            //*/Added by tyd Greg 2014-03-10,for uninstall quickly
            if (tag instanceof ShortcutInfo) {
                initUninstallState((ShortcutInfo)tag);
            }
            //*/
        }
        super.setTag(tag);
    }

    @Override
    protected void drawableStateChanged() {
        if (isPressed()) {
            // In this case, we have already created the pressed outline on ACTION_DOWN,
            // so we just need to do an invalidate to trigger draw
            if (!mDidInvalidateForPressedState) {
                setCellLayoutPressedOrFocusedIcon();
            }
        } else {
            // Otherwise, either clear the pressed/focused background, or create a background
            // for the focused state
            final boolean backgroundEmptyBefore = mPressedOrFocusedBackground == null;
            if (!mStayPressed) {
                mPressedOrFocusedBackground = null;
            }
            if (isFocused()) {
                if (getLayout() == null) {
                    // In some cases, we get focus before we have been layed out. Set the
                    // background to null so that it will get created when the view is drawn.
                    mPressedOrFocusedBackground = null;
                } else {
                    mPressedOrFocusedBackground = createGlowingOutline(
                            mTempCanvas, mFocusedGlowColor, mFocusedOutlineColor);
                }
                mStayPressed = false;
                setCellLayoutPressedOrFocusedIcon();
            }
            final boolean backgroundEmptyNow = mPressedOrFocusedBackground == null;
            if (!backgroundEmptyBefore && backgroundEmptyNow) {
                setCellLayoutPressedOrFocusedIcon();
            }
        }

        Drawable d = mBackground;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
        super.drawableStateChanged();
    }

    /**
     * Draw this BubbleTextView into the given Canvas.
     *
     * @param destCanvas the canvas to draw on
     * @param padding the horizontal and vertical padding to use when drawing
     */
    private void drawWithPadding(Canvas destCanvas, int padding) {
        final Rect clipRect = mTempRect;
        getDrawingRect(clipRect);

        // adjust the clip rect so that we don't include the text label
        clipRect.bottom =
            getExtendedPaddingTop() - (int) BubbleTextView.PADDING_V + getLayout().getLineTop(0);

        // Draw the View into the bitmap.
        // The translate of scrollX and scrollY is necessary when drawing TextViews, because
        // they set scrollX and scrollY to large values to achieve centered text
        destCanvas.save();
        destCanvas.scale(getScaleX(), getScaleY(),
                (getWidth() + padding) / 2, (getHeight() + padding) / 2);
        destCanvas.translate(-getScrollX() + padding / 2, -getScrollY() + padding / 2);
        destCanvas.clipRect(clipRect, Op.REPLACE);
        draw(destCanvas);
        destCanvas.restore();
    }

    /**
     * Returns a new bitmap to be used as the object outline, e.g. to visualize the drop location.
     * Responsibility for the bitmap is transferred to the caller.
     */
    private Bitmap createGlowingOutline(Canvas canvas, int outlineColor, int glowColor) {
        final int padding = mOutlineHelper.mMaxOuterBlurRadius;
        final Bitmap b = Bitmap.createBitmap(
                getWidth() + padding, getHeight() + padding, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(b);
        drawWithPadding(canvas, padding);
        mOutlineHelper.applyExtraThickExpensiveOutlineWithBlur(b, canvas, glowColor, outlineColor);
        canvas.setBitmap(null);

        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the superclass onTouchEvent first, because sometimes it changes the state to
        // isPressed() on an ACTION_UP
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //*/Added by tyd Greg 2014-03-10,for uninstall quickly
                if (checkUninstallPressed((int)event.getX(), (int)event.getY())) {
                    this.mPressedDelIcon = true;
                    cancelLongPress();
                    break;
                }
                //*/
                
                // So that the pressed outline is visible immediately when isPressed() is true,
                // we pre-create it on ACTION_DOWN (it takes a small but perceptible amount of time
                // to create it)
                if (mPressedOrFocusedBackground == null) {
                    mPressedOrFocusedBackground = createGlowingOutline(
                            mTempCanvas, mPressedGlowColor, mPressedOutlineColor);
                }
                // Invalidate so the pressed state is visible, or set a flag so we know that we
                // have to call invalidate as soon as the state is "pressed"
                if (isPressed()) {
                    mDidInvalidateForPressedState = true;
                    setCellLayoutPressedOrFocusedIcon();
                } else {
                    mDidInvalidateForPressedState = false;
                }

                mLongPressHelper.postCheckForLongPress();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //*/Added by tyd Greg 2014-03-10,for uninstall quickly
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mPressedDelIcon) {
                        post(new Runnable() {
                            public void run() {
                                removeIconAtWorkspace();
                            }
                        });
                    }
                }
                mPressedDelIcon = false;
                //*/
                // If we've touched down and up on an item, and it's still not "pressed", then
                // destroy the pressed outline
                if (!isPressed()) {
                    mPressedOrFocusedBackground = null;
                }

                mLongPressHelper.cancelLongPress();
                break;
        }
        return result;
    }

    void setStayPressed(boolean stayPressed) {
        mStayPressed = stayPressed;
        if (!stayPressed) {
            mPressedOrFocusedBackground = null;
        }
        setCellLayoutPressedOrFocusedIcon();
    }

    void setCellLayoutPressedOrFocusedIcon() {
        if (getParent() instanceof ShortcutAndWidgetContainer) {
            ShortcutAndWidgetContainer parent = (ShortcutAndWidgetContainer) getParent();
            if (parent != null) {
                CellLayout layout = (CellLayout) parent.getParent();
                layout.setPressedOrFocusedIcon((mPressedOrFocusedBackground != null) ? this : null);
            }
        }
    }

    void clearPressedOrFocusedBackground() {
        mPressedOrFocusedBackground = null;
        setCellLayoutPressedOrFocusedIcon();
    }

    Bitmap getPressedOrFocusedBackground() {
        return mPressedOrFocusedBackground;
    }

    int getPressedOrFocusedBackgroundPadding() {
        return mOutlineHelper.mMaxOuterBlurRadius / 2;
    }

    @Override
    public void draw(Canvas canvas) {
        if (!mShadowsEnabled) {
            super.draw(canvas);
            //*/Added by tyd Greg 2014-03-07,for support unread feature
            if (TydtechConfig.SHOW_UNREAD_EVENT_FLAG) {
                UnreadLoader.drawUnreadEventIfNeed(canvas, this);
            }
            //*/
            return;
        }

        final Drawable background = mBackground;
        if (background != null) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();

            if (mBackgroundSizeChanged) {
                background.setBounds(0, 0,  getRight() - getLeft(), getBottom() - getTop());
                mBackgroundSizeChanged = false;
            }

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }

        // If text is transparent, don't draw any shadow
        if (getCurrentTextColor() == getResources().getColor(android.R.color.transparent)) {
            getPaint().clearShadowLayer();
            super.draw(canvas);
            //*/Added by tyd Greg 2014-03-07,for support unread feature
            if (TydtechConfig.SHOW_UNREAD_EVENT_FLAG) {
                UnreadLoader.drawUnreadEventIfNeed(canvas, this);
            }
            //*/
            return;
        }

        // We enhance the shadow by drawing the shadow twice
        getPaint().setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        super.draw(canvas);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(),
                getScrollX() + getWidth(),
                getScrollY() + getHeight(), Region.Op.INTERSECT);
        getPaint().setShadowLayer(SHADOW_SMALL_RADIUS, 0.0f, 0.0f, SHADOW_SMALL_COLOUR);
        super.draw(canvas);
        canvas.restore();
        
        //*/Added by tyd Greg 2014-03-07,for support unread feature
        if (TydtechConfig.SHOW_UNREAD_EVENT_FLAG) {
            UnreadLoader.drawUnreadEventIfNeed(canvas, this);
        }
        //*/
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBackground != null) mBackground.setCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBackground != null) mBackground.setCallback(null);
    }

    @Override
    public void setTextColor(int color) {
        mTextColor = color;
        super.setTextColor(color);
    }

    public void setShadowsEnabled(boolean enabled) {
        mShadowsEnabled = enabled;
        getPaint().clearShadowLayer();
        invalidate();
    }

    public void setTextVisibility(boolean visible) {
        Resources res = getResources();
        if (visible) {
            super.setTextColor(mTextColor);
        } else {
            super.setTextColor(res.getColor(android.R.color.transparent));
        }
        mIsTextVisible = visible;
    }

    public boolean isTextVisible() {
        return mIsTextVisible;
    }

    @Override
    protected boolean onSetAlpha(int alpha) {
        if (mPrevAlpha != alpha) {
            mPrevAlpha = alpha;
            super.onSetAlpha(alpha);
        }
        return true;
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }

    //*/Added by tyd Greg 2014-03-10
    //for uninstall quickly
    static final String TAG = "BubbleTextView";
    
    ShortcutInfo mShortcutInfo = null;
    Launcher mLauncher = null;
    
    private int mDelIconLeftPadding;
    private int mDelIconTopPadding;
    
    private boolean mPressedDelIcon = false;
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDelIcon(canvas);
    }
    
    public void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }
    
    private void initUninstallState(ShortcutInfo info) {
        mShortcutInfo = info;
        if(mShortcutInfo.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION){
            mShortcutInfo.uninstallable = ((mShortcutInfo.flags & AppInfo.DOWNLOADED_FLAG) == 1);
        }else if(mShortcutInfo.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT){
            mShortcutInfo.uninstallable = true;
        }
    }
    
    private void removeIconAtWorkspace() {
        if (mShortcutInfo != null) {
            if (mShortcutInfo.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION) {
                startApplicationUninstallActivity(mShortcutInfo);
            } else if (mShortcutInfo.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT) {
                if (getContext() instanceof Launcher) {
                    mLauncher = (Launcher) getContext();
                    LauncherModel.deleteItemFromDatabase(getContext(), mShortcutInfo);
                    //getParent() is ShortcutAndWidgetContainer,getParent().getParent() is CellLayout
                    ViewParent parent = getParent().getParent();
                    if (parent != null && parent instanceof CellLayout) {
                        ((CellLayout) parent).removeView(this);
                        mLauncher.getWorkspace().stripEmptyScreens();
                    }
                }
            }
        }
    }
    
    void startApplicationUninstallActivity(ShortcutInfo appInfo) {
       
        String packageName = appInfo.getIntent().getComponent().getPackageName();
        String className = appInfo.getIntent().getComponent().getClassName();
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, className));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getContext().startActivity(intent);
    }
    
    private void drawDelIcon(Canvas canvas) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        ShortcutInfo appInfo = mShortcutInfo;
        if (uninstallEnable()) {
            boolean focused = isFocused();
            Bitmap uninstallBitmap = Utilities.getUnistallBitmap(getContext());
            if (uninstallBitmap != null) {
                canvas.save();
                canvas.translate((float) scrollX, (float) scrollY);
                canvas.drawBitmap(uninstallBitmap, (float) mDelIconLeftPadding, (float) mDelIconTopPadding, null);
                canvas.translate((float) -scrollX, (float) -scrollY);
                canvas.restore();
            } else {
                
            }
        }
    }
    
    private boolean checkUninstallPressed(int x, int y) {
        if (!uninstallEnable()) {
            return false;
        }
        int left =mDelIconLeftPadding;// 0x0;
        int right = mDelIconLeftPadding + Utilities.getUnistallBitmapWidth(getContext());
        int top = mDelIconTopPadding;//0;//Utilities.getUnistallBitmapTop(getContext());
        int bottom = mDelIconTopPadding + Utilities.getUnistallBitmapHeight(getContext());
        return (x <= right) && (x >= 0) && (y <= bottom) && (y >= 0);
    }
    
    protected boolean uninstallEnable() {
        return (this.mShortcutInfo != null) && (this.mShortcutInfo.uninstallable) && mEditMode;
    }
    
    //for edit mode
    private boolean mEditMode = false;
    @Override
    public void enterEditMode() {
        mEditMode = true;
        setEnabled(false);
    }

    @Override
    public void exitEditMode() {
        mEditMode = false;
        setEnabled(true);
    }

    private int mDirection = 1;
    private float mInitAngle = 0;

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

    @Override
    public boolean isRemoveable() {
        // TODO Auto-generated method stub
        return false;
    }
    
    //for new install
    private Drawable mNewInstallPrefix;
    public void updateTitle(ShortcutInfo info) {
        if (info.newinstalled == 1) {
            SpannableString sp = new SpannableString(" " + info.title);
            sp.setSpan(new ImageSpan(mNewInstallPrefix, 0x1), 0x0, 0x1, 0x11);
            setText(sp);
        }else {
            setText(info.title);
        }
    }
    
    public void updateText(ShortcutInfo info) {
        if (TydtechConfig.SHOW_NEW_INSTALL_FLAG) {
            if (info.newinstalled == 1) {
                SpannableString sp = new SpannableString(" " + info.title);
                sp.setSpan(new ImageSpan(mNewInstallPrefix, 0x1), 0x0, 0x1, 0x11);
                setText(sp);
            } else {
                setText(info.title);
            }
        } else {
            setText(info.title);
        }
    }
    
    //for debug text style
    SharedPreferences mSharedPrefs;

    public void refreshShadowText() {
        Context context = getContext();
        if (mSharedPrefs == null) {
            mSharedPrefs = context.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
                    Context.MODE_PRIVATE);
        }
        float shadowRadius = mSharedPrefs.getFloat(Launcher.DEBUG_TEXT_SHADOW_RADIUS_EXTRA,
                Float.valueOf(context.getResources().getString(R.string.workspace_shadow_radius)));
        float shadowOffsetX = mSharedPrefs.getFloat(Launcher.DEBUG_TEXT_SHADOW_OFFSET_X_EXTRA,
                Float.valueOf(context.getResources().getString(R.string.workspace_shadow_offset_x)));
        float shadowOffsetY = mSharedPrefs.getFloat(Launcher.DEBUG_TEXT_SHADOW_OFFSET_Y_EXTRA,
                Float.valueOf(context.getResources().getString(R.string.workspace_shadow_offset_y)));

        setShadowLayer(shadowRadius, shadowOffsetX, shadowOffsetY,
                context.getResources().getColor(R.color.workspace_shadow_color));
        invalidate();
    }
    //*/

	@Override
	public void shakeOnceTime() {
		// TODO Auto-generated method stub
		
	}
	
	//*/zhangwuba add live weather 2014-8-15
	private Rect mGlobalVisibleRect = new Rect();
	
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final BubbleTextView icon = this;
        Log.i("myl","zhangwuba ------------- onLayout BubbleTextView");
        post(new Runnable() {

            @Override
            public void run() {
                WeatherIconDrawInfo weatherIconDrawInfo = getDrawInfo();
                Log.i("myl","zhangwuba ------------- onLayout BubbleTextView weatherIconDrawInfo = " + weatherIconDrawInfo);
                if (weatherIconDrawInfo != null) {
                	 Log.i("myl","zhangwuba ------------- onLayout BubbleTextView 22222");
                    WeatherIconDrawInfo newWeatherIconDrawInfo = mapWeatherIconDrawInfo(weatherIconDrawInfo,
                            Setting.getWeatherType());
                    fillDrawInfo(newWeatherIconDrawInfo);
                    if (!newWeatherIconDrawInfo.equals(weatherIconDrawInfo)) {
                        WeatherIconController.getInstance().updateIcon(icon, newWeatherIconDrawInfo);
                    }
                }
            }
        });
    }
	
	@Override
	public WeatherIconDrawInfo getDrawInfo() {
		 FastBitmapDrawable drawable = getFastBitmapDrawable();
		 Log.i("myl","zhangwuba -------- getDrawInfo = " + drawable);
	        if (drawable != null) {
	            return drawable.getWeatherIconDrawInfo();
	        }
	        return null;
	}

	@Override
	public FastBitmapDrawable getFastBitmapDrawable() {
		Drawable[] drawable = getCompoundDrawables();
        return (FastBitmapDrawable) drawable[0x1];
	}

	@Override
	public void initDrawInfo(int type) {
		WeatherIconDrawInfo weatherIconDrawInfo = mapWeatherIconDrawInfo(getDrawInfo(), type);
        setDrawInfo(weatherIconDrawInfo);
	}

	@Override
	public void onDrawIcon(WeatherIconDrawInfo drawInfo) {
		FastBitmapDrawable drawable = getFastBitmapDrawable();
        if ((drawable != null) && (drawInfo != null)) {
            drawable.updateWeatherIconDrawInfo(drawInfo);
        }
        postInvalidate();
	}

	@Override
	public void removeDrawInfo() {
		setDrawInfo(null);
        postInvalidate();
	}

	@Override
	public void setDrawInfo(WeatherIconDrawInfo drawInfo) {
		FastBitmapDrawable drawable = getFastBitmapDrawable();
        if (drawable != null) {
            drawable.setWeatherIconDrawInfo(drawInfo);
        }
	}
	
	 protected int getDrawInfoHeight() {
	        FastBitmapDrawable drawable = getFastBitmapDrawable();
	        Bitmap bitamp = drawable.getBitmap();
	        if ((bitamp != null) && (!bitamp.isRecycled())) {
	            return bitamp.getHeight();
	        }
	        return 0x0;
	    }
	
	protected void fillDrawInfo(WeatherIconDrawInfo weatherIconDrawInfo) {
		Log.i("myl","zhangwuba ------  bubbletextview ----  fillDrawInfo");
        Object tag = getTag();
        ItemInfo item = null;
        if (tag instanceof ItemInfo) {
            item = (ItemInfo) tag;
        }
        Resources res;
        if ((item != null) && (weatherIconDrawInfo != null)) {
            res = getContext().getResources();
            weatherIconDrawInfo.setCellXY(item.cellX, item.cellY);
            if (getGlobalVisibleRect(this.mGlobalVisibleRect)) {
                weatherIconDrawInfo.setCellRect(this.mGlobalVisibleRect);
            } else {
                Log.w(TAG, "getGlobalVisibleRect false!!");
            }

            int width = 133;//Utilities.getIconWidth();
            weatherIconDrawInfo.setIconWH(width, width);
            weatherIconDrawInfo.setPaddingTop(res.getDimensionPixelSize(R.dimen.app_icon_padding_top));
            weatherIconDrawInfo.setDrawablePadding(res.getDimensionPixelSize(R.dimen.app_icon_drawablePadding));
            if (LauncherSettings.Favorites.CONTAINER_HOTSEAT == item.container) {
                weatherIconDrawInfo.setIsDockBar(true);
            }
            if ((weatherIconDrawInfo instanceof SnowIconDrawInfo)) {
                int height = getDrawInfoHeight();
                if (height > 0) {
                    ((SnowIconDrawInfo) weatherIconDrawInfo).setMaxThickness(height / mIconSnowHeight);
                } else {
                    Log.e(TAG, "fillDrawInfo getDrawInfoHeight return 0!");
                }
            }
        }
    }
	
	protected WeatherIconDrawInfo mapWeatherIconDrawInfo(WeatherIconDrawInfo oldDrawInfo, int weatherType) {
        WeatherIconDrawInfo weatherIconDrawInfo = oldDrawInfo;
        switch (weatherType) {
        case 200:
            if (weatherIconDrawInfo == null) {
                weatherIconDrawInfo = new WeatherIconDrawInfo(this);
                fillDrawInfo(weatherIconDrawInfo);
            } else {
                weatherIconDrawInfo = new WeatherIconDrawInfo(getDrawInfo());
            }
            break;
        case 201:
            if (weatherIconDrawInfo == null) {
                weatherIconDrawInfo = new WeatherIconDrawInfo(this);
                fillDrawInfo(weatherIconDrawInfo);
            } else {
                weatherIconDrawInfo = new WeatherIconDrawInfo(getDrawInfo());
            }
            break;
        case 208:
            if (weatherIconDrawInfo == null) {
                weatherIconDrawInfo = new ThunderIconDrawInfo(this);
                fillDrawInfo(weatherIconDrawInfo);
            } else {
                weatherIconDrawInfo = new ThunderIconDrawInfo(getDrawInfo());
            }
            break;
        case 202:
            if (weatherIconDrawInfo == null) {
                weatherIconDrawInfo = new SnowIconDrawInfo(this);
                fillDrawInfo(weatherIconDrawInfo);
            } else {
                weatherIconDrawInfo = new SnowIconDrawInfo(getDrawInfo());
                int maxThickness = getDrawInfoHeight();
                if (maxThickness > 0) {
                    ((SnowIconDrawInfo) weatherIconDrawInfo).setMaxThickness(maxThickness / mIconSnowHeight);
                } else {
                    Log.w("BubbleTextView", "mapWeatherIconDrawInfo getDrawInfoHeight return 0!");
                }
            }
            break;
        case 203:
        case 204:
        case 205:
        case 206:
        case 207:
            if (weatherIconDrawInfo == null) {
                weatherIconDrawInfo = new WeatherIconDrawInfo(this);
                fillDrawInfo(weatherIconDrawInfo);
            } else {
                weatherIconDrawInfo = new WeatherIconDrawInfo(getDrawInfo());
            }
            break;
        }

        return weatherIconDrawInfo;
    }
	//*/
    
}
