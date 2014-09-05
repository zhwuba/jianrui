package com.wb.launcher3.realweather;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class RealWeatherController {

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(WeatherUtils.WEATHER_DATA_CHANGE)) {
                mHandler.sendEmptyMessage(MSG_UPDATE_WEATHER);
            }
        }
    };

    public abstract interface RealWeatherCallbacks {
        public abstract void bindWeatherTypeChanged(int arg0);
    }

    private static final boolean DEBUG = false;
    private static final int MSG_UPDATE_WEATHER = 1;
    private static final String TAG = "RealWeatherController";
    private WeakReference mCallbacks;
    private Context mContext;
    private long mCurrentCityId;
    private WeatherInfo mCurrentWeatherInfo;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_WEATHER: {
                RealWeatherController.this.UpdateWeatherView();
                break;
            }
            }
        }
    };
    private int mLiveWeathertype = 200;
    private boolean mPaused = false;
    private WeatherDataHelper mWeatherDBHelper;
    private static Handler sWorker;
    private static HandlerThread sWorkerThread = new HandlerThread("unreader-loader");

    public RealWeatherController(Context context) {
        super();
        this.mContext = context;

        sWorkerThread.start();
        sWorker = new Handler(RealWeatherController.sWorkerThread.getLooper());
    }

    public void UpdateWeatherType() {
        ContentResolver resolver = mContext.getContentResolver();

        Cursor cursor = resolver.query(WeatherUtils.CITY_URI, WeatherUtils.CITY_INDEX_QUERY, WeatherUtils.DISPLAY
                + " = 1", null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int code = cursor.getInt(cursor.getColumnIndex(WeatherUtils.CODE));
            Weather weather = Weather.readWeatherFromDatabase(resolver, code);
            if (weather != null) {
                mLiveWeathertype = WeatherTypeLogic.getWeatherTypeIcon(weather.getIcon1(), weather.getIcon2());
            } else {
                mLiveWeathertype = WeatherTypeLogic.LIVE_WEATHER_TYPE_NODATA;
            }
        } else {
            mLiveWeathertype = WeatherTypeLogic.LIVE_WEATHER_TYPE_NOCITY;
        }

        Log.e(TAG, "UpdateWeatherType mLiveWeathertype = " + mLiveWeathertype);
    }

    public void UpdateWeatherView() {
        this.UpdateWeatherType();
        RealWeatherController.sWorker.post(new Runnable() {
            @Override
            public void run() {
                if (RealWeatherController.this.mCallbacks != null) {
                    Object callback = RealWeatherController.this.mCallbacks.get();
                    if (callback != null) {
                        ((RealWeatherCallbacks) callback)
                                .bindWeatherTypeChanged(RealWeatherController.this.mLiveWeathertype);
                    }
                }
            }
        });
    }

    private int getAttentCityCount() {
        int cityCount;
        if (WeatherUtils.isEnableLocalWeather(this.mContext)) {
            cityCount = this.mWeatherDBHelper.getAttentCityCount(true);
        } else {
            cityCount = this.mWeatherDBHelper.getAttentCityCount(false);
        }

        return cityCount;
    }

    private void haveCityInit() {
        this.loadCityWeatherInfo();
    }

    public void initialize(RealWeatherCallbacks callbacks) {
        this.mCallbacks = new WeakReference(callbacks);
    }

    private void loadCityWeatherInfo() {
        this.mCurrentCityId = this.mWeatherDBHelper.getCurrentCityId(this.mContext);
        if (this.mCurrentCityId < 0) {
            this.mCurrentCityId = this.mWeatherDBHelper.getFirstAttentCityId();
        }

        this.showCurrentCityWeatherInfo(this.mCurrentCityId);
    }

    public void onDateOrTimeChange() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
    }

    public void onPause() {
        this.mPaused = true;
    }

    public void onResume() {
        this.mPaused = false;
    }

    public void onUpdateCompoleted(long success) {
        if (success == 1 && !this.mPaused) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
        }
    }

    public void registerObserverAndReceiver() {
        this.registerReceiver();
    }

    protected void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WeatherUtils.WEATHER_DATA_CHANGE);

        try {
            this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCurrentCityWeather(long mCurrentCityId) {
        mCurrentWeatherInfo = mWeatherDBHelper.getCurrentCityGoWeather(mCurrentCityId);
        if (mCurrentWeatherInfo != null) {
            int typeId = new WeatherTypeLogic().conversionGoWeatherTypeId(this.mCurrentWeatherInfo.getWeatherId(),
                    this.mWeatherDBHelper.getCurrentCityIsDay(mCurrentCityId));
            Log.i("RealWeatherController", "show weather mCurrentWeatherInfo.getWeatherId() = "
                    + this.mCurrentWeatherInfo.getWeatherId());
            mLiveWeathertype = WeatherTypeLogic.getLiveWeatherType(typeId);
        } else {
            mLiveWeathertype = 221;
        }
    }

    private void showCurrentCityWeatherInfo(long mCurrentCityId) {
        this.showCurrentCityWeather(mCurrentCityId);
    }

    public void unregisterObserverAndReceiver() {
        this.unregisterReceiver();
    }

    protected void unregisterReceiver() {
        try {
            this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
