package com.wb.launcher3;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;



import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.wb.launcher3.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class UnreadSupportShortcut {
    public UnreadSupportShortcut(String pkgName, String clsName, String keyString, int type) {
        mComponent = new ComponentName(pkgName, clsName);
        mKey = keyString;
        mShortcutType = type;
        mUnreadNum = 0;
    }

    ComponentName mComponent;
    String mKey;
    int mShortcutType;
    int mUnreadNum;

    @Override
    public String toString() {
        return "{UnreadSupportShortcut[" + mComponent + "], key = " + mKey + ",type = " + mShortcutType
                + ",unreadNum = " + mUnreadNum + "}";
    }
}

/**
 * M: This class is a util class, implemented to do the following two things,:
 * 
 * 1.Read config xml to get the shortcuts which support displaying unread
 * number, then get the initial value of the unread number of each component and
 * update shortcuts and folders through callbacks implemented in Launcher.
 * 
 * 2. Receive unread broadcast sent by application, update shortcuts and folders
 * in workspace, hot seat and update application icons in app customize paged
 * view.
 */
public class UnreadLoader extends BroadcastReceiver {

    public static final String TYD_ACTION_UNREAD_CHANGED = "com.tydtech.action.UNREAD_CHANGED";
    public static final String TYD_EXTRA_UNREAD_PACKAGENAME = "com.tydtech.intent.extra.UNREAD_PACKAGENAME";
    public static final String TYD_EXTRA_UNREAD_NUMBER = "com.tydtech.intent.extra.UNREAD_NUMBER";
    public static final String TYD_UNREAD_CLASS_NAME = "com.tydtech.unread.cls";

    public static final String APP_SHOW_NEW_VERSION = "com.tyd.tydservice";

    private static final String TAG = "UnreadLoader";
    private static final String TAG_UNREADSHORTCUTS = "unreadshortcuts";

    private static final ArrayList<UnreadSupportShortcut> UNREAD_SUPPORT_SHORTCUTS = new ArrayList<UnreadSupportShortcut>();

    private static int sUnreadSupportShortcutsNum = 0;
    private static final Object LOG_LOCK = new Object();

    private Context mContext;

    private WeakReference<UnreadCallbacks> mCallbacks;
    private static ContentResolver mResolver;

    public UnreadLoader(Context context) {
        mContext = context;
        mResolver = context.getContentResolver();
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
    	
    }

    /**
     * Set this as the current Launcher activity object for the loader.
     */
    public void initialize(UnreadCallbacks callbacks) {
        mCallbacks = new WeakReference<UnreadCallbacks>(callbacks);
        
    }

    /**
     * Load and initialize unread shortcuts.
     * 
     * @param context
     */
    void loadAndInitUnreadShortcuts() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... unused) {
                loadUnreadSupportShortcuts();
                initUnreadNumberFromSystem();
                return null;
            }

            @Override
            protected void onPostExecute(final Void result) {
                if (mCallbacks != null) {
                    UnreadCallbacks callbacks = mCallbacks.get();
                    if (callbacks != null) {
                        callbacks.bindUnreadInfoIfNeeded();
                    }
                }
            }
        }.execute();
    }

    /**
     * Initialize unread number by querying system settings provider.
     * 
     * @param context
     */
    private void initUnreadNumberFromSystem() {
        final ContentResolver cr = mContext.getContentResolver();
        final int shortcutsNum = sUnreadSupportShortcutsNum;
        UnreadSupportShortcut shortcut = null;
        for (int i = 0; i < shortcutsNum; i++) {
            shortcut = UNREAD_SUPPORT_SHORTCUTS.get(i);
            try {
                shortcut.mUnreadNum = android.provider.Settings.System.getInt(cr, shortcut.mKey);
               
            } catch (android.provider.Settings.SettingNotFoundException e) {
               
            }
        }
       
    }

    private void loadUnreadSupportShortcuts() {
    	
    }

    /**
     * Get unread support shortcut information, since the information are stored
     * in an array list, we may query it and modify it at the same time, a lock
     * is needed.
     * 
     * @return
     */
    private static String getUnreadSupportShortcutInfo() {
        String info = " Unread support shortcuts are ";
        synchronized (LOG_LOCK) {
            info += UNREAD_SUPPORT_SHORTCUTS.toString();
        }
        return info;
    }

    /**
     * Whether the given component support unread feature.
     * 
     * @param component
     * @return
     */
    static int supportUnreadFeature(ComponentName component) {
       
        if (component == null) {
            return -1;
        }

        final int size = UNREAD_SUPPORT_SHORTCUTS.size();
        for (int i = 0, sz = size; i < sz; i++) {
            if (UNREAD_SUPPORT_SHORTCUTS.get(i).mComponent.equals(component)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Set the unread number of the item in the list with the given unread
     * number.
     * 
     * @param index
     * @param unreadNum
     * @return
     */
    static synchronized boolean setUnreadNumberAt(int index, int unreadNum) {
        if (index >= 0 || index < sUnreadSupportShortcutsNum) {
           
            if (UNREAD_SUPPORT_SHORTCUTS.get(index).mUnreadNum != unreadNum) {
                UNREAD_SUPPORT_SHORTCUTS.get(index).mUnreadNum = unreadNum;
                return true;
            }
        }
        return false;
    }

    /**
     * Get unread number of application at the given position in the supported
     * shortcut list.
     * 
     * @param index
     * @return
     */
    static synchronized int getUnreadNumberAt(int index) {
        if (index < 0 || index >= sUnreadSupportShortcutsNum) {
            return 0;
        }
        
        return UNREAD_SUPPORT_SHORTCUTS.get(index).mUnreadNum;
    }

    /**
     * Get unread number for the given component.
     * 
     * @param component
     * @return
     */
    static int getUnreadNumberOfComponent(ComponentName component) {
        /*
         * /Modified by tyd Greg 2013-09-02,for show the new version and some
         * tyd's unread final int index = supportUnreadFeature(component);
         * return getUnreadNumberAt(index); /
         */
        if (component == null) {
            return -1;
        }
        final int index = supportUnreadFeature(component);
        int unreadNum = getUnreadNumberAt(index);
        if (unreadNum > 0 && component.getPackageName().equals(APP_SHOW_NEW_VERSION)) {
            unreadNum = 1;
        }
        // for the market will push some messages
        if (unreadNum <= 0) {
            unreadNum = getUnreadNum(component.getPackageName());
        }
        return unreadNum;
        // */
    }

    /**
     * Draw unread number for the given icon.
     * 
     * @param canvas
     * @param icon
     * @return
     */
    static void drawUnreadEventIfNeed(Canvas canvas, View icon) {
        ItemInfo info = (ItemInfo) icon.getTag();
        if (info != null && info.unreadNum > 0) {
            Resources res = icon.getContext().getResources();
            
            // / M: Meature sufficent width for unread text and background image
            Paint unreadTextNumberPaint = new Paint();
            unreadTextNumberPaint.setTextSize(res.getDimension(R.dimen.unread_text_number_size));
            unreadTextNumberPaint.setTypeface(Typeface.DEFAULT_BOLD);
            unreadTextNumberPaint.setColor(0xffffffff);
            unreadTextNumberPaint.setTextAlign(Paint.Align.CENTER);

            Paint unreadTextPlusPaint = new Paint(unreadTextNumberPaint);
            unreadTextPlusPaint.setTextSize(res.getDimension(R.dimen.unread_text_plus_size));

            String unreadTextNumber;
            String unreadTextPlus = "+";
            Rect unreadTextNumberBounds = new Rect(0, 0, 0, 0);
            Rect unreadTextPlusBounds = new Rect(0, 0, 0, 0);
           
            //unreadTextNumberPaint.getTextBounds(unreadTextNumber, 0, unreadTextNumber.length(), unreadTextNumberBounds);
            int textHeight = unreadTextNumberBounds.height();
            int textWidth = unreadTextNumberBounds.width() + unreadTextPlusBounds.width();

            // / M: Draw unread background image.
            NinePatchDrawable unreadBgNinePatchDrawable = (NinePatchDrawable) res
                    .getDrawable(R.drawable.ic_newevents_numberindication);
            int unreadBgWidth = unreadBgNinePatchDrawable.getIntrinsicWidth();
            int unreadBgHeight = unreadBgNinePatchDrawable.getIntrinsicHeight();

            int unreadMinWidth = (int) res.getDimension(R.dimen.unread_minWidth);
            if (unreadBgWidth < unreadMinWidth) {
                unreadBgWidth = unreadMinWidth;
            }
            int unreadTextMargin = (int) res.getDimension(R.dimen.unread_text_margin);
            if (unreadBgWidth < textWidth + unreadTextMargin) {
                unreadBgWidth = textWidth + unreadTextMargin;
            }
            if (unreadBgHeight < textHeight) {
                unreadBgHeight = textHeight;
            }
            Rect unreadBgBounds = new Rect(0, 0, unreadBgWidth, unreadBgHeight);
            unreadBgNinePatchDrawable.setBounds(unreadBgBounds);

            int unreadMarginTop = 0;
            int unreadMarginRight = 0;
            if (info instanceof ShortcutInfo) {
                if (info.container == (long) LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    unreadMarginTop = (int) res.getDimension(R.dimen.hotseat_unread_margin_top);
                    unreadMarginRight = (int) res.getDimension(R.dimen.hotseat_unread_margin_right);
                } else if (info.container == (long) LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    unreadMarginTop = (int) res.getDimension(R.dimen.workspace_unread_margin_top);
                    unreadMarginRight = (int) res.getDimension(R.dimen.workspace_unread_margin_right);
                } else {
                    unreadMarginTop = (int) res.getDimension(R.dimen.folder_unread_margin_top);
                    unreadMarginRight = (int) res.getDimension(R.dimen.folder_unread_margin_right);
                }
            } 

            int unreadBgPosX = icon.getScrollX() + icon.getWidth() - unreadBgWidth - unreadMarginRight;
            int unreadBgPosY = icon.getScrollY() + unreadMarginTop;

            canvas.save();
            canvas.translate(unreadBgPosX, unreadBgPosY);

            unreadBgNinePatchDrawable.draw(canvas);

            // / M: Draw unread text.
            Paint.FontMetrics fontMetrics = unreadTextNumberPaint.getFontMetrics();
           

            canvas.restore();
        }
    }

    public interface UnreadCallbacks {
        /**
         * Bind shortcuts and application icons with the given component, and
         * update folders unread which contains the given component.
         * 
         * @param component
         * @param unreadNum
         */
        void bindComponentUnreadChanged(ComponentName component, int unreadNum);

        /**
         * Bind unread shortcut information if needed, this call back is used to
         * update shortcuts and folders when launcher first created.
         */
        void bindUnreadInfoIfNeeded();
    }

    static void drawNewVersionIfNeed(Canvas canvas, View icon) {
        Resources res = icon.getContext().getResources();
        int unreadMarginTop = (int) res.getDimension(R.dimen.app_list_unread_margin_top);
        int unreadMarginRight = (int) res.getDimension(R.dimen.app_list_unread_margin_right);

        Drawable newVersion = res.getDrawable(R.drawable.unread_new_version);
        int unreadBgWidth = newVersion.getIntrinsicWidth();
        int unreadBgHeight = newVersion.getIntrinsicHeight();
        int unreadBgPosX = icon.getScrollX() + icon.getWidth() - unreadBgWidth - unreadMarginRight;
        int unreadBgPosY = icon.getScrollY() + unreadMarginTop;
        Rect unreadBgBounds = new Rect(0, 0, unreadBgWidth, unreadBgHeight);
        newVersion.setBounds(unreadBgBounds);

        canvas.save();
        canvas.translate(unreadBgPosX, unreadBgPosY);
        newVersion.draw(canvas);
        canvas.restore();
    }

    static void saveUnreadNum(Context context, String pkgName, int unread) {
        android.provider.Settings.System.putInt(context.getContentResolver(), pkgName, unread);
    }

    static int getUnreadNum(String pkgName) {
        int unread = 0;
        try {
            if (mResolver != null) {
                unread = android.provider.Settings.System.getInt(mResolver, pkgName);
            }
        } catch (android.provider.Settings.SettingNotFoundException e) {

        }
        return unread;
    }

    void clearTydtechUnreadNum(ComponentName componentName) {
        if (componentName != null) {
            final int index = supportUnreadFeature(componentName);
            if (index < 0) {
                String pkgName = componentName.getPackageName();
                if (mCallbacks != null && !TextUtils.isEmpty(pkgName)) {
                    saveUnreadNum(mContext, pkgName, 0);
                    final UnreadCallbacks callbacks = mCallbacks.get();
                    if (callbacks != null) {
                        callbacks.bindComponentUnreadChanged(componentName, 0);
                    }
                }
            }
        }
    }

}
