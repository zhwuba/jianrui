/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.app.SearchManager;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import com.wb.launcher3.settings.Setting;
import com.wb.launcher3.realweather.RealWeatherController;
import java.lang.ref.WeakReference;

import com.wb.launcher3.R;
import com.wb.launcher3.config.TydtechConfig;

public class LauncherAppState {
    private static final String TAG = "LauncherAppState";
    private static final String SHARED_PREFERENCES_KEY = "com.wb.launcher3.prefs";

    private LauncherModel mModel;
    private IconCache mIconCache;
    private AppFilter mAppFilter;
    private WidgetPreviewLoader.CacheDb mWidgetPreviewCacheDb;
    private boolean mIsScreenLarge;
    private float mScreenDensity;
    private int mLongPressTimeout = 300;

    private static WeakReference<LauncherProvider> sLauncherProvider;
    private static Context sContext;

    private static LauncherAppState INSTANCE;

    private DynamicGrid mDynamicGrid;
    
    //*/zhangwuba live weather 2014-8-15
    private RealWeatherController mRealWeather;
    //*/

    public static LauncherAppState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LauncherAppState();
        }
        return INSTANCE;
    }

    public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE;
    }

    public Context getContext() {
        return sContext;
    }

    public static void setApplicationContext(Context context) {
        if (sContext != null) {
            Log.w(Launcher.TAG, "setApplicationContext called twice! old=" + sContext + " new=" + context);
        }
        sContext = context.getApplicationContext();
    }

    private LauncherAppState() {
        if (sContext == null) {
            throw new IllegalStateException("LauncherAppState inited before app context set");
        }

        Log.v(Launcher.TAG, "LauncherAppState inited");

        if (sContext.getResources().getBoolean(R.bool.debug_memory_enabled)) {
            MemoryTracker.startTrackingMe(sContext, "L");
        }

        // set sIsScreenXLarge and mScreenDensity *before* creating icon cache
        mIsScreenLarge = isScreenLarge(sContext.getResources());
        mScreenDensity = sContext.getResources().getDisplayMetrics().density;

        mWidgetPreviewCacheDb = new WidgetPreviewLoader.CacheDb(sContext);
        mIconCache = new IconCache(sContext);

        mAppFilter = AppFilter.loadByName(sContext.getString(R.string.app_filter_class));
        mModel = new LauncherModel(this, mIconCache, mAppFilter);

        // Register intent receivers
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED);
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED);
        sContext.registerReceiver(mModel, filter);
        //*/Added by tyd Greg 2014-03-07,for show unread number
        if (TydtechConfig.SHOW_UNREAD_EVENT_FLAG) {
//            mUnreadLoader = new UnreadLoader(sContext);
//            // Register unread change broadcast.
//            filter = new IntentFilter();
//            filter.addAction(Intent.MTK_ACTION_UNREAD_CHANGED);
//            filter.addAction(UnreadLoader.TYD_ACTION_UNREAD_CHANGED);
//            sContext.registerReceiver(mUnreadLoader, filter);
        }
        initScreenWidthAndHeight();
        //*/

        // Register for changes to the favorites
        ContentResolver resolver = sContext.getContentResolver();
        resolver.registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true,
                mFavoritesObserver);
        
        //*/zhangwuba live weather 2014-8-15
        initLiveWeatherType(sContext);
        mRealWeather = new RealWeatherController(sContext);
        if (Setting.getIsUsingRealWeather()) {
            	mRealWeather.registerObserverAndReceiver();
        }
        //*/
    }

    public void initScreenWidthAndHeight(){
        //for support user folder
        sScreenWidth = sContext.getResources().getDisplayMetrics().widthPixels;
        sScreenHeight = sContext.getResources().getDisplayMetrics().heightPixels;
        sStatusBarHeight = sContext.getResources().getDimensionPixelSize(R.dimen.launcher_startbar_height);
        Log.i("zxa", "sScreenWidth=="+sScreenWidth+",sScreenHeight=="+sScreenHeight);
       /* if(sScreenWidth > sScreenHeight) {
            int tmp = sScreenWidth;
            sScreenWidth = sScreenHeight;
            sScreenHeight = tmp;
        }*/
        //*/
    }
    /**
     * Call from Application.onTerminate(), which is not guaranteed to ever be called.
     */
    public void onTerminate() {
        sContext.unregisterReceiver(mModel);

        //*/Added by tyd Greg 2014-03-07,for show unread number
        if (TydtechConfig.SHOW_UNREAD_EVENT_FLAG) {
            sContext.unregisterReceiver(mUnreadLoader);
        }
        //*/
        ContentResolver resolver = sContext.getContentResolver();
        resolver.unregisterContentObserver(mFavoritesObserver);
        
        //*/zhangwuba live weather 2014-8-15
        if (Setting.getIsUsingRealWeather()) {
            mRealWeather.unregisterObserverAndReceiver();
        }
        mRealWeather = null;
        //*/
    }

    /**
     * Receives notifications whenever the user favorites have changed.
     */
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // If the database has ever changed, then we really need to force a reload of the
            // workspace on the next load
            mModel.resetLoadedState(false, true);
            mModel.startLoaderFromBackground();
        }
    };

    LauncherModel setLauncher(Launcher launcher) {
        if (mModel == null) {
            throw new IllegalStateException("setLauncher() called before init()");
        }
        mModel.initialize(launcher);
        /*/Added by tyd Greg 2014-03-07,for show unread number
        if (TydtechConfig.SHOW_UNREAD_EVENT_FLAG) {
           mUnreadLoader.initialize(launcher); 
        }
        //*/
		 //*/zhangwuba live weather 2014-8-15
        mRealWeather.initialize(launcher);
        //*/
        return mModel;
    }

    IconCache getIconCache() {
        return mIconCache;
    }

    LauncherModel getModel() {
        return mModel;
    }

    boolean shouldShowAppOrWidgetProvider(ComponentName componentName) {
        return mAppFilter == null || mAppFilter.shouldShowApp(componentName);
    }

    WidgetPreviewLoader.CacheDb getWidgetPreviewCacheDb() {
        return mWidgetPreviewCacheDb;
    }

    static void setLauncherProvider(LauncherProvider provider) {
        sLauncherProvider = new WeakReference<LauncherProvider>(provider);
    }

    static LauncherProvider getLauncherProvider() {
        return sLauncherProvider.get();
    }

    public static String getSharedPreferencesKey() {
        return SHARED_PREFERENCES_KEY;
    }

    DeviceProfile initDynamicGrid(Context context, int minWidth, int minHeight,
                                  int width, int height,
                                  int availableWidth, int availableHeight) {
        if (mDynamicGrid == null) {
            mDynamicGrid = new DynamicGrid(context,
                    context.getResources(),
                    minWidth, minHeight, width, height,
                    availableWidth, availableHeight);
        }

        // Update the icon size
        DeviceProfile grid = mDynamicGrid.getDeviceProfile();
        Utilities.setIconSize(grid.iconSizePx);
        grid.updateFromConfiguration(context.getResources(), width, height,
                availableWidth, availableHeight);
        return grid;
    }
    DynamicGrid getDynamicGrid() {
        return mDynamicGrid;
    }

    public boolean isScreenLarge() {
        return mIsScreenLarge;
    }

    // Need a version that doesn't require an instance of LauncherAppState for the wallpaper picker
    public static boolean isScreenLarge(Resources res) {
        return res.getBoolean(R.bool.is_large_tablet);
    }

    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public float getScreenDensity() {
        return mScreenDensity;
    }

    public int getLongPressTimeout() {
        return mLongPressTimeout;
    }
    //*/Added by tyd Greg 2014-03-07,for freeme 3.0
    private UnreadLoader mUnreadLoader;
    
    public UnreadLoader getUnreadLoader(){
        return mUnreadLoader;
    }
    
    //for support user folder
    private static int sScreenHeight;
    private static int sScreenWidth;
    private static int sStatusBarHeight;
    
    static int getScreenWidth() {
        return sScreenWidth;
    }
    
    static int getScreenHeight() {
        return sScreenHeight;
    }
    
    static int getStatusBarHeight() {
        return sStatusBarHeight;
    }
    
    //for swipe actions
    private boolean mSwipeDownEnable = true;
    private boolean mSwipeUpEnable = true;
    
    public void setSwipeDownEnable(boolean enable){
        mSwipeDownEnable = enable;
    }
    
    public void setSwipeUpEnable(boolean enable){
        mSwipeUpEnable = enable;
    }
    
    public boolean getSwipeDownEnable(){
        return mSwipeDownEnable;
    }
    
    public boolean getSwipeUpEnable(){
        return mSwipeUpEnable;
    }
    //*/
    
    //*/zhangwuba add liveweather 2014-8-15
    private void initLiveWeatherType(Context context) {
        SharedPreferences mPerferences = context.getSharedPreferences("launcher_liveWeather_preferences", 0x0);
        int liveWeatherType = mPerferences.getInt("liveWeather_type", 0xc8);
        if ((liveWeatherType > 0xd0) || (liveWeatherType < 0xc8)) {
            liveWeatherType = 0xc8;
        }
        Setting.setWeatherType(liveWeatherType);
        boolean usingRealWeather = mPerferences.getBoolean("usingRealWeather", false);
        Setting.setIsUsingRealWeather(usingRealWeather);
    }
    
    public RealWeatherController getRealWeatherController() {
        return mRealWeather;
    }
    //*/
    
}
