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

import com.wb.launcher3.R;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Map.Entry;

/**
 * Cache of application icons.  Icons can be made from any thread.
 */
public class IconCache {
    @SuppressWarnings("unused")
    private static final String TAG = "Launcher.IconCache";

    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    private static class CacheEntry {
        public Bitmap icon;
        public String title;
    }

    private final Bitmap mDefaultIcon;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final HashMap<ComponentName, CacheEntry> mCache =
            new HashMap<ComponentName, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
    private int mIconDpi;

    public IconCache(Context context) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        mContext = context;
        mPackageManager = context.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();

        // need to set mIconDpi before getting default icon
        mDefaultIcon = makeDefaultIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ResolveInfo info) {
        return getFullResIcon(info.activityInfo);
    }

    public Drawable getFullResIcon(ActivityInfo info) {

        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon() {
        Drawable d = getFullResDefaultActivityIcon();
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }

    /**
     * Remove any records for the supplied ComponentName.
     */
    public void remove(ComponentName componentName) {
        synchronized (mCache) {
            mCache.remove(componentName);
        }
    }

    /**
     * Empty out the cache.
     */
    public void flush() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    /**
     * Empty out the cache that aren't of the correct grid size
     */
    public void flushInvalidIcons(DeviceProfile grid) {
        synchronized (mCache) {
            Iterator<Entry<ComponentName, CacheEntry>> it = mCache.entrySet().iterator();
            while (it.hasNext()) {
                final CacheEntry e = it.next().getValue();
                if (e.icon.getWidth() != grid.iconSizePx || e.icon.getHeight() != grid.iconSizePx) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public void getTitleAndIcon(AppInfo application, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            CacheEntry entry = cacheLocked(application.componentName, info, labelCache);

            application.title = entry.title;
            application.iconBitmap = entry.icon;
        }
    }

    public Bitmap getIcon(Intent intent) {
        synchronized (mCache) {
            final ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
            ComponentName component = intent.getComponent();

            if (resolveInfo == null || component == null) {
                return mDefaultIcon;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, null);
            return entry.icon;
        }
    }

    public Bitmap getIcon(ComponentName component, ResolveInfo resolveInfo,
            HashMap<Object, CharSequence> labelCache) {
        synchronized (mCache) {
            if (resolveInfo == null || component == null) {
                return null;
            }

            CacheEntry entry = cacheLocked(component, resolveInfo, labelCache);
            return entry.icon;
        }
    }

    public boolean isDefaultIcon(Bitmap icon) {
        return mDefaultIcon == icon;
    }

    private CacheEntry cacheLocked(ComponentName componentName, ResolveInfo info,
            HashMap<Object, CharSequence> labelCache) {
        CacheEntry entry = mCache.get(componentName);
        if (entry == null) {
            entry = new CacheEntry();

            mCache.put(componentName, entry);

            ComponentName key = LauncherModel.getComponentNameFromResolveInfo(info);
            if (labelCache != null && labelCache.containsKey(key)) {
                entry.title = labelCache.get(key).toString();
            } else {
                entry.title = info.loadLabel(mPackageManager).toString();
                if (labelCache != null) {
                    labelCache.put(key, entry.title);
                }
            }
            if (entry.title == null) {
                entry.title = info.activityInfo.name;
            }
          //*/zhangwuba 
            entry.icon = getApplicationIcon(mContext, info);
          /*
            entry.icon = Utilities.createIconBitmap(
                    getFullResIcon(info), mContext);
            //*/
        }
        return entry;
    }

    public HashMap<ComponentName,Bitmap> getAllIcons() {
        synchronized (mCache) {
            HashMap<ComponentName,Bitmap> set = new HashMap<ComponentName,Bitmap>();
            for (ComponentName cn : mCache.keySet()) {
                final CacheEntry e = mCache.get(cn);
                set.put(cn, e.icon);
            }
            return set;
        }
    }
    
    //*/zhangwuba add handle icon
    private static HashMap<String, Integer> iconsMap = null;
    
    private static int[] mDealultBgResId = new int[] {
        R.drawable.ic_app_background_01,
        R.drawable.ic_app_background_02,
        R.drawable.ic_app_background_03,
        R.drawable.ic_app_background_04,
        R.drawable.ic_app_background_05,
        R.drawable.ic_app_background_06 };
    
    private static void initBackgroundMap() {
        if(null == iconsMap){
            iconsMap = new HashMap<String, Integer>();  
            iconsMap.put("com.android.contacts", R.drawable.ic_app_contacts);
            iconsMap.put("com.android.contacts.activities.PeopleActivity", R.drawable.ic_app_contacts); 
            iconsMap.put("com.android.dialer.DialtactsActivity", R.drawable.ic_app_phone);
            iconsMap.put("com.android.phone", R.drawable.ic_app_phone);
            iconsMap.put("com.android.mms", R.drawable.ic_app_mms);
            iconsMap.put("com.android.music", R.drawable.ic_app_music);
            iconsMap.put("com.tydtech.music", R.drawable.ic_app_music);
            iconsMap.put("com.android.camera.KbStyle2Camera", R.drawable.ic_app_camera); 
            iconsMap.put("com.android.camera.CameraLauncher", R.drawable.ic_app_camera);
            iconsMap.put("com.android.calendar", R.drawable.ic_app_calendar);
            iconsMap.put("com.android.calendar.AllInOneActivity", R.drawable.ic_app_calendar);
            iconsMap.put("com.tydtech.weather", R.drawable.ic_app_weather);
            iconsMap.put("com.android.email", R.drawable.ic_app_email);
            iconsMap.put("com.android.email.activity.Welcome", R.drawable.ic_app_email);
            iconsMap.put("com.android.gallery3d", R.drawable.ic_app_gallery);
            iconsMap.put("com.android.gallery3d.app.Gallery", R.drawable.ic_app_gallery);
            iconsMap.put("com.android.gallery3d.app.GalleryActivity", R.drawable.ic_app_gallery);
            iconsMap.put("com.tydtech.videogallery", R.drawable.ic_app_video);
            iconsMap.put("com.mediatek.videoplayer", R.drawable.ic_app_video);
            iconsMap.put("com.android.gallery3d.app.MovieActivity", R.drawable.ic_app_video);   
            iconsMap.put("com.mediatek.filemanager", R.drawable.ic_app_filemanager);
            iconsMap.put("com.tyd.compass", R.drawable.ic_app_compass);
            iconsMap.put("com.android.deskclock", R.drawable.ic_app_clock);
            iconsMap.put("com.android.deskclock.DeskClock", R.drawable.ic_app_clock);
            iconsMap.put("com.tyd.calculator", R.drawable.ic_app_calculator);
            iconsMap.put("com.android.calculator2.Calculator", R.drawable.ic_app_calculator);
            iconsMap.put("com.android.torch", R.drawable.ic_app_torch);
            iconsMap.put("com.mediatek.FMRadio", R.drawable.ic_app_fmradio);
            iconsMap.put("com.android.soundrecorder", R.drawable.ic_app_soundrecorder);
            iconsMap.put("com.tyd.tydservice", R.drawable.ic_app_tydservice);
            iconsMap.put("com.android.settings.Settings", R.drawable.ic_app_settings);
            iconsMap.put("com.android.settings", R.drawable.ic_app_settings);
            iconsMap.put("com.mediatek.schpwronoff", R.drawable.ic_app_settings);
            iconsMap.put("com.tydtech.thememanager", R.drawable.ic_app_theme);
            
            iconsMap.put("com.tyd.userbook", R.drawable.ic_app_userbook);
            iconsMap.put("com.android.systemui", R.drawable.ic_app_settings);
            iconsMap.put("com.android.systemui.TorchActivity", R.drawable.ic_app_torch);
            iconsMap.put("com.android.browser", R.drawable.ic_app_browser);
            iconsMap.put("com.android.quicksearchbox", R.drawable.ic_app_search);
            
            iconsMap.put("com.zhuoyi.notes_hd", R.drawable.ic_app_note); 
            iconsMap.put("com.tyd.notes", R.drawable.ic_app_note); 
            iconsMap.put("com.tyd.book", R.drawable.ic_app_book);
            //iconsMap.put("com.zhuoyi.market", R.drawable.ic_app_kedoustore);
            //iconsMap.put("com.zhuoyi.marketHD", R.drawable.ic_app_kedoustore);
            
            //iconsMap.put("com.mediatek.contacts.ShareContactViaSDCardActivity", R.drawable.mtk_contact_sd_card_icon);
            //iconsMap.put("com.mediatek.contacts.ShareContactViaSMSActivity", R.drawable.ic_app_mms);
           
            //zhangwuba add for google
            iconsMap.put("com.google.android.gms.app.settings.GoogleSettingsActivity", R.drawable.ic_app_googlesettings);
            iconsMap.put("com.google.android.gm", R.drawable.ic_app_gmail);
            iconsMap.put("com.google.android.gm.ConversationListActivityGmail", R.drawable.ic_app_gmail);
            iconsMap.put("com.google.android.apps.genie.geniewidget", R.drawable.ic_app_news);           
            iconsMap.put("com.google.android.talk", R.drawable.ic_app_hangouts);
            iconsMap.put("com.google.android.talk.SigningInActivity", R.drawable.ic_app_hangouts);
            iconsMap.put("com.google.android.googlequicksearchbox", R.drawable.ic_app_googlesearch);
            iconsMap.put("com.google.android.apps.maps", R.drawable.ic_app_googlemap);
            iconsMap.put("com.android.vending.AssetBrowserActivity", R.drawable.ic_app_playstore);
            iconsMap.put("com.android.music.activitymanagement.TopLevelActivity", R.drawable.ic_app_playmusic);
            iconsMap.put("com.google.android.youtube.app.honeycomb.Shell$HomeActivity", R.drawable.ic_app_youtube);
            iconsMap.put("com.android.videoeditor", R.drawable.ic_app_movieedit);
            iconsMap.put("com.google.android.googlequicksearchbox.VoiceSearchActivity", R.drawable.ic_app_voicesearch);
            iconsMap.put("com.android.providers.downloads.ui", R.drawable.ic_app_download);
            iconsMap.put("com.android.providers.downloads.ui.DownloadList", R.drawable.ic_app_download);
            
            iconsMap.put("com.google.android.maps.MapsActivity", R.drawable.ic_app_googlemap);
            iconsMap.put("com.google.android.dialer.extensions.GoogleDialtactsActivity", R.drawable.ic_app_phone);
            //
            
            //*/youmi
            iconsMap.put("com.wb.wbad.JingPing", R.drawable.ic_app_launcher_folder_news);
            iconsMap.put("com.wb.wbad.HotApp", R.drawable.ic_app_hotapp);
            iconsMap.put("com.wb.wbad.HotGames", R.drawable.ic_app_launcher_folder_game);
            //*/
        }
        
    }
    
    
    private Bitmap getIconBitmapWithThemeBg(Context context, Drawable dr, String packageName,
            int resid) {
        if (dr == null) {
            return null;
        }
        Log.i("myl","zhangwuba  ------ packageName = " + packageName);
        int backgroundResId = mDealultBgResId[(packageName.length() + resid)
                % (mDealultBgResId.length)];
        Bitmap background = ((BitmapDrawable) (context.getResources()
                .getDrawable(backgroundResId))).getBitmap();

        Bitmap originalAppIcon = ((BitmapDrawable) dr).getBitmap();

        Bitmap mask = ((BitmapDrawable) (context.getResources()
                .getDrawable(R.drawable.ic_app_pack_mask))).getBitmap();

        Bitmap pattern = ((BitmapDrawable) (context.getResources()
                .getDrawable(R.drawable.ic_app_pack_pattern))).getBitmap();

        Bitmap border = ((BitmapDrawable) (context.getResources()
                .getDrawable(R.drawable.ic_app_pack_border))).getBitmap();

        Bitmap maskedAppIcon = makeMaskedIcon(mask, originalAppIcon);

        return packAppIcon(pattern, background, maskedAppIcon, border);
    }

    private Bitmap packAppIcon(Bitmap pattern, Bitmap background,
            Bitmap maskedAppIcon, Bitmap border) {
        Bitmap result = Bitmap.createBitmap(maskedAppIcon.getWidth(),
                maskedAppIcon.getHeight(), Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        mCanvas.drawBitmap(pattern, 0, 0, null);
        mCanvas.drawBitmap(background, 0, 0, null);
        mCanvas.drawBitmap(maskedAppIcon, 0, 0, null);
        mCanvas.drawBitmap(border, 0, 0, null);
        return result;
    }

    private Bitmap makeMaskedIcon(Bitmap mask, Bitmap originalAppIcon) {
        Rect rect = calcMaskRect(mask);
        Bitmap appIcon = Bitmap.createScaledBitmap(originalAppIcon,
                rect.width(), rect.height(), true);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
                Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(appIcon, rect.left, rect.top, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }
    
    private Rect calcMaskRect(Bitmap mask) {

        Rect rect = new Rect();
        int width = mask.getWidth();
        int height = mask.getHeight();
        rect.left = width;
        rect.top = height;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (mask.getPixel(x, y) != 0 ) {
                    if (x < rect.left) rect.left = x;
                    if (y < rect.top) rect.top = y;
                    
                    if (x > rect.right) rect.right = x;
                    if (y > rect.bottom) rect.bottom = y;
                }
            }
        }

        rect.left--;
        rect.top--;
        rect.right++;
        rect.bottom++;
        
        return rect;
    }
    
    private Bitmap getApplicationIcon(Context context, ResolveInfo info){
    	boolean foundFrameworkIcon = false;
    	int resID = 0;
    	if(iconsMap == null){
    		initBackgroundMap();
    	}
    	
    	 Log.i("myl","zhangwuba  ------ info.activityInfo.name = " + info.activityInfo.name);
    	
    	 Integer altResid = iconsMap.get(info.activityInfo.name);
    	 
    	 if(null != altResid && altResid.intValue() != -1){
    		 foundFrameworkIcon = true;
    		 resID = altResid.intValue();
    	 }
    	 
    	 if(foundFrameworkIcon){
    		 Drawable icon = context.getResources().getDrawable(resID);
    		 icon = handleSpecial(context,icon, info.activityInfo.name);
    		 return Utilities.createIconBitmap(icon,context);
    	 }
    	 
    	 return getIconBitmapWithThemeBg(context,getFullResIcon(info),
    			 info.activityInfo.name,0);
    	 
    	 
    }
    
    private Drawable handleSpecial(Context context, Drawable dr,String packageName)
    {
        if (!(dr instanceof BitmapDrawable))
            return dr;
            
        Drawable result = dr;        
        if ("com.android.calendar.AllInOneActivity".equals(packageName)) {
            result = generateCalendarIcon(context,((BitmapDrawable)result).getBitmap());
        }
        return result;
    }

    private Drawable generateCalendarIcon(Context context,Bitmap icon) {
    
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String monthDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        Bitmap result =Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), Config.ARGB_8888);
        Canvas canvas=new Canvas(result);
    
        canvas.drawBitmap(icon, 0, 0, null);
    
        Paint textPaint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DEV_KERN_TEXT_FLAG);
        Rect textRect = new Rect();
        canvas.save();
    
        textPaint.setColor(context.getResources().getColor(R.color.live_calendar_icon_text));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        textPaint.setTextSize(33f * context.getResources().getDisplayMetrics().density);
        textPaint.getTextBounds(monthDay, 0, monthDay.length(), textRect);
                                   
        float horizontalPercent = context.getResources().getFraction(R.dimen.live_calendar_horizontal_percent, 1, 1);
        float verticalPercent = context.getResources().getFraction(R.dimen.live_calendar_vertical_percent, 1, 1);

        canvas.drawText(monthDay, (result.getWidth() - (textRect.left * 2 + textRect.width()) ) * horizontalPercent,
            (result.getHeight()-textRect.height())* verticalPercent + textRect.height(), textPaint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
    
        return new BitmapDrawable(context.getResources(), result);
    }
    
    //*/
    
}
