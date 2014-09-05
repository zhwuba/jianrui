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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wb.launcher3.R;
import com.wb.launcher3.DropTarget.DragObject;
import com.wb.launcher3.FolderInfo.FolderListener;
import com.wb.launcher3.config.TydtechConfig;
import com.wb.launcher3.weatherIcon.WeatherIcon;
import com.wb.launcher3.weatherIcon.WeatherIconDrawInfo;
import com.wb.launcher3.FastBitmapDrawable;
import com.wb.launcher3.FolderIcon;
import com.wb.launcher3.ItemInfo;
import com.wb.launcher3.LauncherSettings;
import com.wb.launcher3.Utilities;
import com.wb.launcher3.weatherIcon.SnowIconDrawInfo;
import com.wb.launcher3.weatherIcon.ThunderIconDrawInfo;
import com.wb.launcher3.DeviceProfile;

import java.util.ArrayList;

/**
 * An icon that can appear on in the workspace representing an {@link UserFolder}.
 */
//*/zhangwuba modify for live weather 2014-8-18
public class FolderIcon extends LinearLayout implements FolderListener,ShakeEditMode, WeatherIcon{
    private static final String TAG = "FolderIcon";

    private Launcher mLauncher;
    private Folder mFolder;
    private FolderInfo mInfo;
    private static boolean sStaticValuesDirty = true;

    private CheckLongPressHelper mLongPressHelper;

    //*/Modified by tyd Greg 2013-08-30,for optimized the folder's style
    // The number of icons to display in the
    private static final int NUM_ITEMS_IN_PREVIEW = 4;
    //*/
    private static final int CONSUMPTION_ANIMATION_DURATION = 100;
    private static final int DROP_IN_ANIMATION_DURATION = 400;
    private static final int INITIAL_ITEM_ANIMATION_DURATION = 350;
    private static final int FINAL_ITEM_ANIMATION_DURATION = 200;

    // The degree to which the inner ring grows when accepting drop
    private static final float INNER_RING_GROWTH_FACTOR = 0.15f;

    // The degree to which the outer ring is scaled in its natural state
    private static final float OUTER_RING_GROWTH_FACTOR = 0.3f;

    // The amount of vertical spread between items in the stack [0...1]
    private static final float PERSPECTIVE_SHIFT_FACTOR = 0.24f;

    // Flag as to whether or not to draw an outer ring. Currently none is designed.
    public static final boolean HAS_OUTER_RING = true;

    // The degree to which the item in the back of the stack is scaled [0...1]
    // (0 means it's not scaled at all, 1 means it's scaled to nothing)
    private static final float PERSPECTIVE_SCALE_FACTOR = 0.35f;

    public static Drawable sSharedFolderLeaveBehind = null;

    private ImageView mPreviewBackground;
    private BubbleTextView mFolderName;

    FolderRingAnimator mFolderRingAnimator = null;

    // These variables are all associated with the drawing of the preview; they are stored
    // as member variables for shared usage and to avoid computation on each frame
    private int mIntrinsicIconSize;
    private float mBaselineIconScale;
    private int mBaselineIconSize;
    private int mAvailableSpaceInPreview;
    private int mTotalWidth = -1;
    private int mPreviewOffsetX;
    private int mPreviewOffsetY;
    private float mMaxPerspectiveShift;
    boolean mAnimating = false;
    private Rect mOldBounds = new Rect();

    private PreviewItemDrawingParams mParams = new PreviewItemDrawingParams(0, 0, 0, 0);
    private PreviewItemDrawingParams mAnimParams = new PreviewItemDrawingParams(0, 0, 0, 0);
    private ArrayList<ShortcutInfo> mHiddenItems = new ArrayList<ShortcutInfo>();
    
    
    //*/zhangwuba live weather 2014-8-15
    private Context mContext;
    private int sIconSnowHeight;
    //*/

    public FolderIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        //*/zhangwuba live weather 2014-8-15
        mContext = context;
        //*/zhangwuba live weather 2014-8-15
        init();
    }

    public FolderIcon(Context context) {
        super(context);
        //*/zhangwuba live weather 2014-8-15
        mContext = context;
        //*/zhangwuba live weather 2014-8-15
        init();
    }

    private void init() {
        mLongPressHelper = new CheckLongPressHelper(this);
        
      //*/zhangwuba live weather 2014-8-15
        sIconSnowHeight = mContext.getResources().getInteger(R.integer.config_IconSnowHeight);
        //*/
    }

    public boolean isDropEnabled() {
        final ViewGroup cellLayoutChildren = (ViewGroup) getParent();
        final ViewGroup cellLayout = (ViewGroup) cellLayoutChildren.getParent();
        final Workspace workspace = (Workspace) cellLayout.getParent();
        return !workspace.isSmall();
    }

    static FolderIcon fromXml(int resId, Launcher launcher, ViewGroup group,
            FolderInfo folderInfo, IconCache iconCache) {
        @SuppressWarnings("all") // suppress dead code warning
        final boolean error = INITIAL_ITEM_ANIMATION_DURATION >= DROP_IN_ANIMATION_DURATION;
        if (error) {
            throw new IllegalStateException("DROP_IN_ANIMATION_DURATION must be greater than " +
                    "INITIAL_ITEM_ANIMATION_DURATION, as sequencing of adding first two items " +
                    "is dependent on this");
        }

        FolderIcon icon = (FolderIcon) LayoutInflater.from(launcher).inflate(resId, group, false);
        icon.setClipToPadding(false);
        icon.mFolderName = (BubbleTextView) icon.findViewById(R.id.folder_icon_name);
        icon.mFolderName.setText(folderInfo.title);
        icon.mPreviewBackground = (ImageView) icon.findViewById(R.id.preview_background);
        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
        // Offset the preview background to center this view accordingly
        LinearLayout.LayoutParams lp =
                (LinearLayout.LayoutParams) icon.mPreviewBackground.getLayoutParams();
        lp.topMargin = grid.folderBackgroundOffset;
        lp.width = grid.folderIconSizePx;
        lp.height = grid.folderIconSizePx;

        icon.setTag(folderInfo);
        icon.setOnClickListener(launcher);
        icon.mInfo = folderInfo;
        icon.mLauncher = launcher;
        icon.setContentDescription(String.format(launcher.getString(R.string.folder_name_format),
                folderInfo.title));
        Folder folder = Folder.fromXml(launcher);
        folder.setDragController(launcher.getDragController());
        folder.setFolderIcon(icon);
        folder.bind(folderInfo);
        icon.mFolder = folder;

        icon.mFolderRingAnimator = new FolderRingAnimator(launcher, icon);
        
        //*/zhangwuba add live weather 2014-8-18
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) launcher.getResources().getDrawable(
                    R.drawable.ic_app_launcher_folder);
            int width = bitmapDrawable.getBitmap().getWidth();//Utilities.getIconWidth();
            Bitmap bitmap = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), width, width, true);
            FastBitmapDrawable folderBg = new FastBitmapDrawable(bitmap);
            icon.mPreviewBackground.setImageDrawable(folderBg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //*/
        
        folderInfo.addListener(icon);

        return icon;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        sStaticValuesDirty = true;
        return super.onSaveInstanceState();
    }

    public static class FolderRingAnimator {
        public int mCellX;
        public int mCellY;
        private CellLayout mCellLayout;
        public float mOuterRingSize;
        public float mInnerRingSize;
        public FolderIcon mFolderIcon = null;
        public static Drawable sSharedOuterRingDrawable = null;
        public static Drawable sSharedInnerRingDrawable = null;
        public static int sPreviewSize = -1;
        public static int sPreviewPadding = -1;

        private ValueAnimator mAcceptAnimator;
        private ValueAnimator mNeutralAnimator;
        
        //*/Modified by tyd Greg 2013-08-30,for optimized the folder's style
        public static int sPreviewSubIconGap;
        public static int sPreviewTopPadding;
        public static int sPreviewOffsetX;
        public static int sPreviewOffsetY;
        //*/

        public FolderRingAnimator(Launcher launcher, FolderIcon folderIcon) {
            mFolderIcon = folderIcon;
            Resources res = launcher.getResources();

            // We need to reload the static values when configuration changes in case they are
            // different in another configuration
            if (sStaticValuesDirty) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    throw new RuntimeException("FolderRingAnimator loading drawables on non-UI thread "
                            + Thread.currentThread());
                }
                
 		boolean needGetDefaultValue = true;
                /*/Modified by tyd Greg 2013-08-30,for get the folder's value from theme
               
                String folderPreviewValue = launcher.getResources().getThemeString("folder_preview_values");
                if (TydtechConfig.TYDTECH_DEBUG_FLAG) {
                    Log.i("Greg", "the folderPreviewValue from them: " + folderPreviewValue);
                }
                if (!TextUtils.isEmpty(folderPreviewValue)) {
                    String[] strs = folderPreviewValue.split(",");
                    if (strs.length > 2) {
                        try {
                            float padding = Float.parseFloat(strs[0]);
                            float iconGap = Float.parseFloat(strs[1]);
                            float offsetX = Float.parseFloat(strs[2]);
                            float offsetY = Float.parseFloat(strs[3]);

                            needGetDefaultValue = false;
                            float density = launcher.getResources().getDisplayMetrics().density;
                            sPreviewPadding = (int) (padding * density);
                            sPreviewSubIconGap = (int) (iconGap * density);
                            sPreviewOffsetX = (int) (offsetX * density);
                            sPreviewOffsetY = (int) (offsetY * density);
                        } catch (Exception e) {
                        }
                    }
                }
                //*/

                LauncherAppState app = LauncherAppState.getInstance();
                DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
                sPreviewSize = grid.folderIconSizePx;
                sPreviewPadding = res.getDimensionPixelSize(R.dimen.folder_preview_padding);
                /*/Modified by tyd Greg,for optimized the folder's style
                sSharedOuterRingDrawable = res.getDrawable(R.drawable.portal_ring_outer_holo);
                //*/
                sSharedOuterRingDrawable = res.getDrawable(R.drawable.ic_app_launcher_folder);
                //*/
                
                sSharedInnerRingDrawable = res.getDrawable(R.drawable.portal_ring_inner_nolip_holo);
                sSharedFolderLeaveBehind = res.getDrawable(R.drawable.portal_ring_rest);
                sStaticValuesDirty = false;
                
                /*/Modified by tyd Greg,for optimized the folder's style
                sPreviewTopPadding = res.getDimensionPixelSize(R.dimen.app_icon_padding_top);
                //*/
                if (needGetDefaultValue) {
                    sPreviewPadding = res.getDimensionPixelSize(R.dimen.folder_preview_padding);
                    sPreviewSubIconGap = res.getDimensionPixelSize(R.dimen.folder_preview_subicon_gap);
                    sPreviewOffsetX = res.getDimensionPixelSize(R.dimen.folder_preview_offsetX);
                    sPreviewOffsetY = res.getDimensionPixelSize(R.dimen.folder_preview_offsetY);
                }
                //*/
            }
        }

        public void animateToAcceptState() {
            if (mNeutralAnimator != null) {
                mNeutralAnimator.cancel();
            }
            mAcceptAnimator = LauncherAnimUtils.ofFloat(mCellLayout, 0f, 1f);
            mAcceptAnimator.setDuration(CONSUMPTION_ANIMATION_DURATION);

            final int previewSize = sPreviewSize;
            mAcceptAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float percent = (Float) animation.getAnimatedValue();
                    mOuterRingSize = (1 + percent * OUTER_RING_GROWTH_FACTOR) * previewSize;
                    mInnerRingSize = (1 + percent * INNER_RING_GROWTH_FACTOR) * previewSize;
                    if (mCellLayout != null) {
                        mCellLayout.invalidate();
                    }
                }
            });
            mAcceptAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mFolderIcon != null) {
                        mFolderIcon.mPreviewBackground.setVisibility(INVISIBLE);
                    }
                }
            });
            mAcceptAnimator.start();
        }

        public void animateToNaturalState() {
            if (mAcceptAnimator != null) {
                mAcceptAnimator.cancel();
            }
            mNeutralAnimator = LauncherAnimUtils.ofFloat(mCellLayout, 0f, 1f);
            mNeutralAnimator.setDuration(CONSUMPTION_ANIMATION_DURATION);

            final int previewSize = sPreviewSize;
            mNeutralAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float percent = (Float) animation.getAnimatedValue();
                    mOuterRingSize = (1 + (1 - percent) * OUTER_RING_GROWTH_FACTOR) * previewSize;
                    mInnerRingSize = (1 + (1 - percent) * INNER_RING_GROWTH_FACTOR) * previewSize;
                    if (mCellLayout != null) {
                        mCellLayout.invalidate();
                    }
                }
            });
            mNeutralAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mCellLayout != null) {
                        mCellLayout.hideFolderAccept(FolderRingAnimator.this);
                    }
                    if (mFolderIcon != null) {
                        mFolderIcon.mPreviewBackground.setVisibility(VISIBLE);
                    }
                }
            });
            mNeutralAnimator.start();
        }

        // Location is expressed in window coordinates
        public void getCell(int[] loc) {
            loc[0] = mCellX;
            loc[1] = mCellY;
        }

        // Location is expressed in window coordinates
        public void setCell(int x, int y) {
            mCellX = x;
            mCellY = y;
        }

        public void setCellLayout(CellLayout layout) {
            mCellLayout = layout;
        }

        public float getOuterRingSize() {
            return mOuterRingSize;
        }

        public float getInnerRingSize() {
            return mInnerRingSize;
        }
    }

    Folder getFolder() {
        return mFolder;
    }

    FolderInfo getFolderInfo() {
        return mInfo;
    }

    private boolean willAcceptItem(ItemInfo item) {
        final int itemType = item.itemType;
        return ((itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
                itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) &&
                !mFolder.isFull() && item != mInfo && !mInfo.opened);
    }

    public boolean acceptDrop(Object dragInfo) {
        final ItemInfo item = (ItemInfo) dragInfo;
        return !mFolder.isDestroyed() && willAcceptItem(item);
    }

    public void addItem(ShortcutInfo item) {
        mInfo.add(item);
    }

    public void onDragEnter(Object dragInfo) {
        if (mFolder.isDestroyed() || !willAcceptItem((ItemInfo) dragInfo)) return;
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) getLayoutParams();
        CellLayout layout = (CellLayout) getParent().getParent();
        mFolderRingAnimator.setCell(lp.cellX, lp.cellY);
        mFolderRingAnimator.setCellLayout(layout);
        mFolderRingAnimator.animateToAcceptState();
        layout.showFolderAccept(mFolderRingAnimator);
    }

    public void onDragOver(Object dragInfo) {
    }

    public void performCreateAnimation(final ShortcutInfo destInfo, final View destView,
            final ShortcutInfo srcInfo, final DragView srcView, Rect dstRect,
            float scaleRelativeToDragLayer, Runnable postAnimationRunnable) {

        // These correspond two the drawable and view that the icon was dropped _onto_
        Drawable animateDrawable = ((TextView) destView).getCompoundDrawables()[1];
        computePreviewDrawingParams(animateDrawable.getIntrinsicWidth(),
                destView.getMeasuredWidth());

        // This will animate the first item from it's position as an icon into its
        // position as the first item in the preview
        animateFirstItem(animateDrawable, INITIAL_ITEM_ANIMATION_DURATION, false, null);
        addItem(destInfo);

        // This will animate the dragView (srcView) into the new folder
        onDrop(srcInfo, srcView, dstRect, scaleRelativeToDragLayer, 1, postAnimationRunnable, null);
    }

    public void performDestroyAnimation(final View finalView, Runnable onCompleteRunnable) {
        Drawable animateDrawable = ((TextView) finalView).getCompoundDrawables()[1];
        computePreviewDrawingParams(animateDrawable.getIntrinsicWidth(), 
                finalView.getMeasuredWidth());

        // This will animate the first item from it's position as an icon into its
        // position as the first item in the preview
        animateFirstItem(animateDrawable, FINAL_ITEM_ANIMATION_DURATION, true,
                onCompleteRunnable);
    }

    public void onDragExit(Object dragInfo) {
        onDragExit();
    }

    public void onDragExit() {
        mFolderRingAnimator.animateToNaturalState();
    }

    private void onDrop(final ShortcutInfo item, DragView animateView, Rect finalRect,
            float scaleRelativeToDragLayer, int index, Runnable postAnimationRunnable,
            DragObject d) {
        item.cellX = -1;
        item.cellY = -1;

        // Typically, the animateView corresponds to the DragView; however, if this is being done
        // after a configuration activity (ie. for a Shortcut being dragged from AllApps) we
        // will not have a view to animate
        if (animateView != null) {
            DragLayer dragLayer = mLauncher.getDragLayer();
            Rect from = new Rect();
            dragLayer.getViewRectRelativeToSelf(animateView, from);
            Rect to = finalRect;
            if (to == null) {
                to = new Rect();
                Workspace workspace = mLauncher.getWorkspace();
                // Set cellLayout and this to it's final state to compute final animation locations
                workspace.setFinalTransitionTransform((CellLayout) getParent().getParent());
                float scaleX = getScaleX();
                float scaleY = getScaleY();
                setScaleX(1.0f);
                setScaleY(1.0f);
                scaleRelativeToDragLayer = dragLayer.getDescendantRectRelativeToSelf(this, to);
                // Finished computing final animation locations, restore current state
                setScaleX(scaleX);
                setScaleY(scaleY);
                workspace.resetTransitionTransform((CellLayout) getParent().getParent());
            }

            int[] center = new int[2];
            float scale = getLocalCenterForIndex(index, center);
            center[0] = (int) Math.round(scaleRelativeToDragLayer * center[0]);
            center[1] = (int) Math.round(scaleRelativeToDragLayer * center[1]);

            to.offset(center[0] - animateView.getMeasuredWidth() / 2,
                      center[1] - animateView.getMeasuredHeight() / 2);

            float finalAlpha = index < NUM_ITEMS_IN_PREVIEW ? 0.5f : 0f;

            float finalScale = scale * scaleRelativeToDragLayer;
            dragLayer.animateView(animateView, from, to, finalAlpha,
                    1, 1, finalScale, finalScale, DROP_IN_ANIMATION_DURATION,
                    new DecelerateInterpolator(2), new AccelerateInterpolator(2),
                    postAnimationRunnable, DragLayer.ANIMATION_END_DISAPPEAR, null);
            addItem(item);
            mHiddenItems.add(item);
            mFolder.hideItem(item);
            postDelayed(new Runnable() {
                public void run() {
                    mHiddenItems.remove(item);
                    mFolder.showItem(item);
                    invalidate();
                }
            }, DROP_IN_ANIMATION_DURATION);
        } else {
            addItem(item);
        }
    }

    public void onDrop(DragObject d) {
        ShortcutInfo item;
        if (d.dragInfo instanceof AppInfo) {
            // Came from all apps -- make a copy
            item = ((AppInfo) d.dragInfo).makeShortcut();
        } else {
            item = (ShortcutInfo) d.dragInfo;
        }
        mFolder.notifyDrop();
        onDrop(item, d.dragView, null, 1.0f, mInfo.contents.size(), d.postAnimationRunnable, d);
    }

    private void computePreviewDrawingParams(int drawableSize, int totalSize) {
        if (mIntrinsicIconSize != drawableSize || mTotalWidth != totalSize) {
            LauncherAppState app = LauncherAppState.getInstance();
            DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();

            mIntrinsicIconSize = drawableSize;
            mTotalWidth = totalSize;

            final int previewSize = mPreviewBackground.getLayoutParams().height;
            final int previewPadding = FolderRingAnimator.sPreviewPadding;

            mAvailableSpaceInPreview = (previewSize - 2 * previewPadding);
            // cos(45) = 0.707  + ~= 0.1) = 0.8f
            int adjustedAvailableSpace = (int) ((mAvailableSpaceInPreview / 2) * (1 + 0.8f));

            int unscaledHeight = (int) (mIntrinsicIconSize * (1 + PERSPECTIVE_SHIFT_FACTOR));
            mBaselineIconScale = (1.0f * adjustedAvailableSpace / unscaledHeight);

            mBaselineIconSize = (int) (mIntrinsicIconSize * mBaselineIconScale);
            mMaxPerspectiveShift = mBaselineIconSize * PERSPECTIVE_SHIFT_FACTOR;

            mPreviewOffsetX = (mTotalWidth - mAvailableSpaceInPreview) / 2;
            mPreviewOffsetY = previewPadding + grid.folderBackgroundOffset;
        }
    }

    private void computePreviewDrawingParams(Drawable d) {
        /*/Modified by tyd Greg 2013-08-30,for optimized the folder's style
        computePreviewDrawingParams(d.getIntrinsicWidth(), getMeasuredWidth());
        /*/
        computePreviewDrawingParams(d.getIntrinsicWidth(), getMeasuredWidth(),getMeasuredHeight());
        //*/
    }

    class PreviewItemDrawingParams {
        PreviewItemDrawingParams(float transX, float transY, float scale, int overlayAlpha) {
            this.transX = transX;
            this.transY = transY;
            this.scale = scale;
            this.overlayAlpha = overlayAlpha;
        }
        float transX;
        float transY;
        float scale;
        int overlayAlpha;
        Drawable drawable;
    }

    private float getLocalCenterForIndex(int index, int[] center) {
        mParams = computePreviewItemDrawingParams(Math.min(NUM_ITEMS_IN_PREVIEW, index), mParams);

        mParams.transX += mPreviewOffsetX;
        mParams.transY += mPreviewOffsetY;
        float offsetX = mParams.transX + (mParams.scale * mIntrinsicIconSize) / 2;
        float offsetY = mParams.transY + (mParams.scale * mIntrinsicIconSize) / 2;

        center[0] = (int) Math.round(offsetX);
        center[1] = (int) Math.round(offsetY);
        return mParams.scale;
    }

    /*/Modified by tyd Greg 2013-08-30,for optimized the folder's style
    private PreviewItemDrawingParams computePreviewItemDrawingParams(int index,
            PreviewItemDrawingParams params) {
        index = NUM_ITEMS_IN_PREVIEW - index - 1;
        float r = (index * 1.0f) / (NUM_ITEMS_IN_PREVIEW - 1);
        float scale = (1 - PERSPECTIVE_SCALE_FACTOR * (1 - r));

        float offset = (1 - r) * mMaxPerspectiveShift;
        float scaledSize = scale * mBaselineIconSize;
        float scaleOffsetCorrection = (1 - scale) * mBaselineIconSize;

        // We want to imagine our coordinates from the bottom left, growing up and to the
        // right. This is natural for the x-axis, but for the y-axis, we have to invert things.
        float transY = mAvailableSpaceInPreview - (offset + scaledSize + scaleOffsetCorrection) + getPaddingTop();
        float transX = offset + scaleOffsetCorrection;
        float totalScale = mBaselineIconScale * scale;
        final int overlayAlpha = (int) (80 * (1 - r));

        if (params == null) {
            params = new PreviewItemDrawingParams(transX, transY, totalScale, overlayAlpha);
        } else {
            params.transX = transX;
            params.transY = transY;
            params.scale = totalScale;
            params.overlayAlpha = overlayAlpha;
        }
        return params;
    }
    /*/
    public PreviewItemDrawingParams computePreviewItemDrawingParams(int index,
            PreviewItemDrawingParams params) {
        int row = index / 2;
        int cloum = index % 2;
        int k = FolderRingAnimator.sPreviewSubIconGap;
        int tranX = cloum * (k + this.mBaselineIconSize);
        int tranY = row * (k + this.mBaselineIconSize);
        if (params == null) {
            params = new PreviewItemDrawingParams(tranX, tranY, mBaselineIconScale, 1);
        }else{
            params.transX = tranX;
            params.transY = tranY;
            params.scale = this.mBaselineIconScale;
            params.overlayAlpha = 1;
        }
        return params;
    }
    //*/

    private void drawPreviewItem(Canvas canvas, PreviewItemDrawingParams params) {
        canvas.save();
        canvas.translate(params.transX + mPreviewOffsetX, params.transY + mPreviewOffsetY);
        canvas.scale(params.scale, params.scale);
        Drawable d = params.drawable;
        //*/Modified by tyd Greg 2013-08-30,for optimized the folder's style
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        //*/
        if (d != null) {
            mOldBounds.set(d.getBounds());
            d.setBounds(0, 0, mIntrinsicIconSize, mIntrinsicIconSize);
            d.setFilterBitmap(true);
            d.setColorFilter(Color.argb(params.overlayAlpha, 255, 255, 255),
                    PorterDuff.Mode.SRC_ATOP);
            d.draw(canvas);
            d.clearColorFilter();
            d.setFilterBitmap(false);
            d.setBounds(mOldBounds);
        }
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mFolder == null) return;
        if (mFolder.getItemCount() == 0 && !mAnimating) return;

        ArrayList<View> items = mFolder.getItemsInReadingOrder();
        Drawable d;
        TextView v;

        // Update our drawing parameters if necessary
        if (mAnimating) {
            computePreviewDrawingParams(mAnimParams.drawable);
        } else {
            v = (TextView) items.get(0);
            d = v.getCompoundDrawables()[1];
            computePreviewDrawingParams(d);
        }

        int nItemsInPreview = Math.min(items.size(), NUM_ITEMS_IN_PREVIEW);
        if (!mAnimating) {
            for (int i = nItemsInPreview - 1; i >= 0; i--) {
                v = (TextView) items.get(i);
                if (!mHiddenItems.contains(v.getTag())) {
                    d = v.getCompoundDrawables()[1];
                    mParams = computePreviewItemDrawingParams(i, mParams);
                    mParams.drawable = d;
                    drawPreviewItem(canvas, mParams);
                }
            }
        } else {
            drawPreviewItem(canvas, mAnimParams);
        }
        
        //*/Added by tyd Greg 2014-03-07,for support unread feature
        if (TydtechConfig.SHOW_UNREAD_EVENT_FLAG) {
            UnreadLoader.drawUnreadEventIfNeed(canvas, this);
        }
        //*/
    }

    private void animateFirstItem(final Drawable d, int duration, final boolean reverse,
            final Runnable onCompleteRunnable) {
        final PreviewItemDrawingParams finalParams = computePreviewItemDrawingParams(0, null);

        final float scale0 = 1.0f;
        final float transX0 = (mAvailableSpaceInPreview - d.getIntrinsicWidth()) / 2;
        final float transY0 = (mAvailableSpaceInPreview - d.getIntrinsicHeight()) / 2 + getPaddingTop();
        mAnimParams.drawable = d;

        ValueAnimator va = LauncherAnimUtils.ofFloat(this, 0f, 1.0f);
        va.addUpdateListener(new AnimatorUpdateListener(){
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (Float) animation.getAnimatedValue();
                if (reverse) {
                    progress = 1 - progress;
                    mPreviewBackground.setAlpha(progress);
                }

                mAnimParams.transX = transX0 + progress * (finalParams.transX - transX0);
                mAnimParams.transY = transY0 + progress * (finalParams.transY - transY0);
                mAnimParams.scale = scale0 + progress * (finalParams.scale - scale0);
                invalidate();
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                if (onCompleteRunnable != null) {
                    onCompleteRunnable.run();
                }
            }
        });
        va.setDuration(duration);
        va.start();
    }

    public void setTextVisible(boolean visible) {
        if (visible) {
            mFolderName.setVisibility(VISIBLE);
        } else {
            mFolderName.setVisibility(INVISIBLE);
        }
    }

    public boolean getTextVisible() {
        return mFolderName.getVisibility() == VISIBLE;
    }

    public void onItemsChanged() {
        invalidate();
        requestLayout();
    }

    public void onAdd(ShortcutInfo item) {
        invalidate();
        requestLayout();
    }

    public void onRemove(ShortcutInfo item) {
        invalidate();
        requestLayout();
    }

    public void onTitleChanged(CharSequence title) {
        mFolderName.setText(title.toString());
        setContentDescription(String.format(getContext().getString(R.string.folder_name_format),
                title));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Call the superclass onTouchEvent first, because sometimes it changes the state to
        // isPressed() on an ACTION_UP
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLongPressHelper.postCheckForLongPress();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLongPressHelper.cancelLongPress();
                break;
        }
        return result;
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mLongPressHelper.cancelLongPress();
    }
    
    //*/Added by tyd Greg 2014-03-07
    /**
     * M: Update the unread message number of the shortcut with the given value.
     *
     * @param unreadNum the number of the unread message.
     */
    public void setFolderUnreadNum(int unreadNum) {
        
        if (unreadNum <= 0) {
            mInfo.unreadNum = 0;
        } else {
            mInfo.unreadNum = unreadNum;
        }
    }
    
    /**
     * M: Update unread number of the folder, the number is the total unread number
     * of all shortcuts in folder, duplicate shortcut will be only count once.
     */
    public void updateFolderUnreadNum() {
        final ArrayList<ShortcutInfo> contents = mInfo.contents;
        final int contentsCount = contents.size();
        int unreadNumTotal = 0;
        final ArrayList<ComponentName> components = new ArrayList<ComponentName>();
        ShortcutInfo shortcutInfo = null;
        ComponentName componentName = null;
        int unreadNum = 0;
        for (int i = 0; i < contentsCount; i++) {
            shortcutInfo = contents.get(i);
            componentName = shortcutInfo.intent.getComponent();
            unreadNum = UnreadLoader.getUnreadNumberOfComponent(componentName);
            if (unreadNum > 0) {
                shortcutInfo.unreadNum = unreadNum;
                int j = 0;
                for (j = 0; j < components.size(); j++) {
                    if (componentName != null && componentName.equals(components.get(j))) {
                        break;
                    }
                }
               
                if (j >= components.size()) {
                    components.add(componentName);
                    unreadNumTotal += unreadNum;
                }
            }
        }
       
        setFolderUnreadNum(unreadNumTotal);
    }

    /**
     * M: Update the unread message of the shortcut with the given information.
     *
     * @param unreadNum the number of the unread message.
     */
    public void updateFolderUnreadNum(ComponentName component, int unreadNum) {
        final ArrayList<ShortcutInfo> contents = mInfo.contents;
        final int contentsCount = contents.size();
        int unreadNumTotal = 0;
        ShortcutInfo appInfo = null;
        ComponentName name = null;
        final ArrayList<ComponentName> components = new ArrayList<ComponentName>();
        for (int i = 0; i < contentsCount; i++) {
            appInfo = contents.get(i);
            name = appInfo.intent.getComponent();
            if (name != null && component != null) {
                if (component.getClassName().equals(UnreadLoader.TYD_UNREAD_CLASS_NAME)) {
                    if (name.getPackageName().equals(component.getPackageName())) {
                        appInfo.unreadNum = unreadNum;
                    }
                } else {
                    if (name.equals(component)) {
                        appInfo.unreadNum = unreadNum;
                    }
                }
            }
            if (appInfo.unreadNum > 0) {
                int j = 0;
                for (j = 0; j < components.size(); j++) {
                    if (name != null && name.equals(components.get(j))) {
                        break;
                    }
                }
                
                if (j >= components.size()) {
                    components.add(name);
                    unreadNumTotal += appInfo.unreadNum;
                }
            }
        }
       
        setFolderUnreadNum(unreadNumTotal);
    }

    //for support edit mode
    private int mDirection = 1;
    private float mInitAngle = 0;
    
    private boolean mEditMode = false;

    @Override
    public void enterEditMode() {
        mEditMode = true;
    }

    @Override
    public void exitEditMode() {
        mEditMode = false;
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
    
    //for optimized the folder's style    
    private int mTotalHeight = -1;
    private int mPreviewTopMargin = -1;
    
    private void computePreviewDrawingParams(int drawableSize, int totalSize,int totalHeight) {
        if (mIntrinsicIconSize != drawableSize || mTotalWidth != totalSize || mTotalHeight != totalHeight) {
            mIntrinsicIconSize = drawableSize;
            mTotalWidth = totalSize;
            mTotalHeight = totalHeight;

            final int previewSize = FolderRingAnimator.sPreviewSize;
            final int previewPadding = FolderRingAnimator.sPreviewPadding;
            final int previewSubIconGap = FolderRingAnimator.sPreviewSubIconGap;
            final int previewTopPadding = FolderRingAnimator.sPreviewTopPadding;
            final int offsetX = FolderRingAnimator.sPreviewOffsetX;
            final int offsetY = FolderRingAnimator.sPreviewOffsetY;
            
            mAvailableSpaceInPreview = (previewSize - 2 * previewPadding);
            mBaselineIconScale = (1.0F * (int)((this.mAvailableSpaceInPreview - previewSubIconGap) / 2.0F) / this.mIntrinsicIconSize);
            mBaselineIconSize = (int)(mIntrinsicIconSize * mBaselineIconScale);
            mPreviewTopMargin = ((ViewGroup.MarginLayoutParams)mPreviewBackground.getLayoutParams()).topMargin;
            mPreviewOffsetX = (offsetX + (mTotalWidth - mAvailableSpaceInPreview) / 2);
            mPreviewOffsetY = (offsetY + (previewPadding + (previewTopPadding + mPreviewTopMargin)));
        }
    }
    
    public int getFolderPreviewHeight(){
        return mPreviewBackground.getHeight();
    }
   
    public int getFolderPreviewBottom(){
        return getBottom();
    }
    
    public int getTitleBottom() {
        if (mFolderName != null){
            return mFolderName.getTop() + mFolderName.getHeight();
        }
        return 0;
    }
    
    public void setTitle(CharSequence title) {
        if (mFolderName != null){
            mFolderName.setText(title);
        }
    }
    
    public void onOpen() {
        setEnabled(false);
    }
    
    public void onClose() {
        setEnabled(true);
    }
    
    public void refreshBubbleText(){
        mFolderName.refreshShadowText();
    }
    //*/

	@Override
	public void shakeOnceTime() {
		// TODO Auto-generated method stub
		
	}

	//*/zhangwuba add live weather 2014-8-18
	private Rect mGlobalVisibleRect = new Rect();
	@Override
	public WeatherIconDrawInfo getDrawInfo() {
		FastBitmapDrawable drawable = getFastBitmapDrawable();
		Log.i("myl","zhangwuba ---------- folder getDrawInfo drawable = " + drawable);
        if (drawable != null) {
        	Log.i("myl","zhangwuba ---------- folder getDrawInfo drawable info = " + drawable.getWeatherIconDrawInfo());
            return drawable.getWeatherIconDrawInfo();
        }
        return null;
	}

	@Override
	public FastBitmapDrawable getFastBitmapDrawable() {
		Drawable drawable = mPreviewBackground.getDrawable();
        if (drawable != null) {
            return (FastBitmapDrawable) drawable;
        }
        Log.e(TAG, "getFastBitmapDrawable return null !!");
        return null;
	}

	@Override
	public void initDrawInfo(int type) {
		WeatherIconDrawInfo weatherIconDrawInfo = mapWeatherIconDrawInfo(getDrawInfo(), type);
		 //Log.i("myl","zhangwuba ---------- folder initDrawInfo = " + weatherIconDrawInfo);
        setDrawInfo(weatherIconDrawInfo);
	}

	@Override
	public void onDrawIcon(WeatherIconDrawInfo drawInfo) {
		 FastBitmapDrawable drawable = getFastBitmapDrawable();
		 //Log.i("myl","zhangwuba ---------- folder onDrawIcon 111 drawable = " + drawable);
	     if ((drawable != null) && (drawInfo != null)) {
	    	 //Log.i("myl","zhangwuba ---------- folder onDrawIcon");
	            drawable.updateWeatherIconDrawInfo(drawInfo);
	     }
	     mPreviewBackground.postInvalidate();
	}

	@Override
	public void removeDrawInfo() {
		setDrawInfo(null);
        mPreviewBackground.postInvalidate();
	}

	@Override
	public void setDrawInfo(WeatherIconDrawInfo drawInfo) {
		FastBitmapDrawable drawable = getFastBitmapDrawable();
		//Log.i("myl","zhangwuba ---------- folder setDrawInfo = " + drawInfo);
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
        Object tag = this.getTag();
        ItemInfo item = null;
        if ((tag instanceof ItemInfo)) {
            item = (ItemInfo) tag;
        }

        if (item != null && weatherIconDrawInfo != null) {
            Resources res = getContext().getResources();
            int cellX = item.cellX;
            int cellY;
            if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                cellY = item.cellY;//CELLY_OF_DOCKABR;
            } else {
                cellY = item.cellY;
            }

            weatherIconDrawInfo.setCellXY(cellX, cellY);
            if (getGlobalVisibleRect(mGlobalVisibleRect)) {
                weatherIconDrawInfo.setCellRect(mGlobalVisibleRect);
            }

            int width = 133;//Utilities.getIconWidth();
            weatherIconDrawInfo.setIconWH(width, width);
            weatherIconDrawInfo.setPaddingTop(res.getDimensionPixelSize(R.dimen.app_icon_padding_top));
            weatherIconDrawInfo.setDrawablePadding(res.getDimensionPixelSize(R.dimen.app_icon_drawablePadding));
            if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                weatherIconDrawInfo.setIsDockBar(true);
            }

        }
        if ((weatherIconDrawInfo instanceof SnowIconDrawInfo)) {
            int height = this.getDrawInfoHeight();
            SnowIconDrawInfo snowIconDrawInfo = (SnowIconDrawInfo) weatherIconDrawInfo;
            if (height > 0) {
                snowIconDrawInfo.setMaxThickness(height / sIconSnowHeight);
            } else {
                Log.w(TAG, "fillDrawInfo getDrawInfoHeight return 0!");
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
                return weatherIconDrawInfo;
            } else {
                weatherIconDrawInfo = new WeatherIconDrawInfo(getDrawInfo());
            }
            break;
        case 202:
            if (weatherIconDrawInfo == null) {
                weatherIconDrawInfo = new SnowIconDrawInfo(this);
                fillDrawInfo(weatherIconDrawInfo);
            } else {
                weatherIconDrawInfo = new SnowIconDrawInfo(getDrawInfo());
            }
            int maxThickness = getDrawInfoHeight();
            if (maxThickness > 0) {
                ((SnowIconDrawInfo) weatherIconDrawInfo).setMaxThickness(maxThickness / sIconSnowHeight);
            } else {
                Log.w(TAG, "mapWeatherIconDrawInfo getDrawInfoHeight return 0!");
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
