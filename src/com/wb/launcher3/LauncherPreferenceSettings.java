package com.wb.launcher3;

import java.util.Calendar;
import java.util.Date;

import com.wb.launcher3.R;
import com.wb.launcher3.LauncherDataBackupHelper.OnBackupAndRestoreListener;
import com.wb.launcher3.config.TydtechConfig;
import com.wb.launcher3.liveweather.LiveWeatherGLView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class LauncherPreferenceSettings extends PreferenceActivity {

    private LauncherDataBackupHelper mDataBackupHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.menu_launcher_settings);
        super.onCreate(savedInstanceState);
        mDataBackupHelper = new LauncherDataBackupHelper(this);
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        getFragmentManager().beginTransaction().replace(android.R.id.content, new LauncherPrefsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    class LauncherPrefsFragment extends PreferenceFragment implements OnPreferenceClickListener,
            OnBackupAndRestoreListener, OnPreferenceChangeListener {

        private static final String KEY_BACKUP_DATA = "key_backup_data";
        private static final String KEY_RESTORE_DATA = "key_restore_data";
        private static final String KEY_RESET_DATA = "key_reset_data";
        
        private static final String KEY_DEBUG_CATEGORY = "key_debug_category";
        
        private static final String KEY_DEBUG_TEXT_STYLE = "key_debug_text_style";
        
        private static final String KEY_SWIPE_CATEGORY = "key_swipe_category";
        private static final String KEY_SWIPE_UP_ACTIONS = "key_swipe_up_actions";
        private static final String KEY_SWIPE_DOWN_ACTIONS = "key_swipe_down_actions";

        public static final String KEY_WKS_TRANSITION_EFFECT = "workspace_transition_effect";
        public static final String KEY_APP_TRANSITION_EFFECT = "apps_transition_effect";

        public static final int WKS_TRANSITION_TYPE_MAX = 10;
        public static final int WKS_TRANSITION_DEFAULT = 1;

        public static final int APP_TRANSITION_TYPE_MAX = 11;
        public static final int APP_TRANSITION_DEFAULT = 1;

        private Preference mBackupDataPreference;
        private Preference mRestoreDataPreference;
        private Preference mResetDataPreference;

        private Preference mWksTransitionEffect;
        private Preference mAppTransitionEffect;

        private Calendar mDummyDate;
        
        
        private PreferenceCategory mDebugCategory;
        private CheckBoxPreference mDebugTextStyle;
        private SharedPreferences mSharedPrefs;
        
        private PreferenceCategory mSwipeCategory;
        private CheckBoxPreference mSwipeUpActions;
        private CheckBoxPreference mSwipeDownActions;
        
        //*/zhangwuba add live weather 2014-8-19
        public static final String KEY_LIVE_WEATHER_PREFERCE = "workspace_liveweather_key";
        private Preference mLiveWeatherPreferce;
        //*/

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preference_settings);

            mDummyDate = Calendar.getInstance();

            mBackupDataPreference = findPreference(KEY_BACKUP_DATA);
            mRestoreDataPreference = findPreference(KEY_RESTORE_DATA);
            mResetDataPreference = findPreference(KEY_RESET_DATA);

            mWksTransitionEffect = findPreference(KEY_WKS_TRANSITION_EFFECT);
            mAppTransitionEffect = findPreference(KEY_APP_TRANSITION_EFFECT);
            
            mDebugCategory = (PreferenceCategory) findPreference(KEY_DEBUG_CATEGORY);
            mSwipeCategory = (PreferenceCategory) findPreference(KEY_SWIPE_CATEGORY);
            
            mSharedPrefs = getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
                    Context.MODE_PRIVATE);
            
            if (TydtechConfig.SWIPE_GESTURE_ENABLED) {
                mSwipeUpActions = (CheckBoxPreference) findPreference(KEY_SWIPE_UP_ACTIONS);
                mSwipeDownActions = (CheckBoxPreference) findPreference(KEY_SWIPE_DOWN_ACTIONS);
                mSwipeDownActions.setOnPreferenceChangeListener(this);
                mSwipeUpActions.setOnPreferenceChangeListener(this);
            }else {
                getPreferenceScreen().removePreference(mSwipeCategory);
            }
            
            if (TydtechConfig.DEBUG_TEXT_STYLE_ENABLE) {
                mDebugTextStyle = (CheckBoxPreference) findPreference(KEY_DEBUG_TEXT_STYLE);
                mDebugTextStyle.setOnPreferenceChangeListener(this);
            }else {
                getPreferenceScreen().removePreference(mDebugCategory);
            }

            
            mBackupDataPreference.setOnPreferenceClickListener(this);
            mRestoreDataPreference.setOnPreferenceClickListener(this);
            mResetDataPreference.setOnPreferenceClickListener(this);

            mDataBackupHelper.setOnBackupAndRestoreListener(this);
            
            //*/zhangwuba live weather 2014-8-19
            mLiveWeatherPreferce = findPreference(KEY_LIVE_WEATHER_PREFERCE);
            //*/

        }
        
        //*/zhangwuba live weather 2014-8-19
        private int getCurrentPosition(int realtyle){
    		int position = 0;
    		
    		switch(realtyle){
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_NONE:
    				position = 0;
    				break;
    			/*
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_DYNAMIC:
    				position = 1;
    				break;*/
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_SUNNY:
    				position = 1;
    				break;
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_CLOUDY:
    				position = 2;
    				break;
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_DANDELION:
    				position = 3;
    				break;
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_FOG:
    				position = 4;
    				break;
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_RAIN:
    				position = 5;
    				break;
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_THUNDERSHOWER:
    				position = 6;
    				break;
    			case LiveWeatherGLView.LIVE_WEATHER_TYPE_SNOW:
    				position = 7;
    				break;
    		}
    		
    		return position;
    	}
        
        private void updateLiveWeatherDescription(){
        	Preference preference;
            String summary;
            
            SharedPreferences mPerferences = getActivity().getSharedPreferences("launcher_liveWeather_preferences", 0x0);
            int liveWeatherType = mPerferences.getInt("liveWeather_type", 0xc8);
            boolean usingRealWeather = mPerferences.getBoolean("usingRealWeather", false);
            if ((liveWeatherType > 0xd0) || (liveWeatherType < 0xc8)) {
                liveWeatherType = 0xc8;
            }
    	    //Log.i("myl","zhangwuba ---------- onCreateView  liveWeatherType = " + liveWeatherType);
    	    //mCurrentposition = com.android.launcher3.settings.Setting.getWeatherType(); 
    	    int currValue = getCurrentPosition(liveWeatherType);
    	    
    	    if(usingRealWeather){
    	    	currValue = 1;
    	    }
    	    

            if (currValue < 0 || currValue >= 9) {
                summary = "";
            } else {
                final CharSequence[] entries = getResources().getTextArray(R.array.live_weather_entries);
                summary = entries[currValue].toString();
            }
            
            if(mLiveWeatherPreferce != null)
            	mLiveWeatherPreferce.setSummary(summary);
            
        }
        //*/

        @Override
        public void onResume() {
            super.onResume();
            updatePreferenceState();
            updateTransitionEffectDescription(KEY_WKS_TRANSITION_EFFECT);
            updateTransitionEffectDescription(KEY_APP_TRANSITION_EFFECT);
            
            if (TydtechConfig.SWIPE_GESTURE_ENABLED) {
                updateSwipeActions();
            }
            if (TydtechConfig.DEBUG_TEXT_STYLE_ENABLE) {
                mDebugTextStyle.setChecked(isEnableTextStyle());
            }
            
            //*/zhangwuba live weather 2014-8-19
            updateLiveWeatherDescription();
            //*/
        }

        private void updateSwipeActions() {
            mSwipeUpActions.setChecked(mSharedPrefs.getBoolean(Launcher.SWIPE_UP_ACTIONS_EXTRA, true));
            mSwipeDownActions.setChecked(mSharedPrefs.getBoolean(Launcher.SWIPE_DOWN_ACTIONS_EXTRA, true));
        }

        private void updateTransitionEffectDescription(String key) {
            Preference preference;
            String summary;
            int id;
            int count;

            if (KEY_WKS_TRANSITION_EFFECT.equals(key)) {
                preference = mWksTransitionEffect;
                id = R.array.workspace_transition_effect_entries;
                count = WKS_TRANSITION_TYPE_MAX;
            } else {
                preference = mAppTransitionEffect;
                id = R.array.apps_transition_effect_entries;
                count = APP_TRANSITION_TYPE_MAX;
            }

            int currValue = getTransitionEffect(key);

            if (currValue < 0 || currValue >= count) {
                summary = "";
            } else {
                final CharSequence[] entries = getResources().getTextArray(id);
                summary = entries[currValue].toString();
            }

            preference.setSummary(summary);
        }

        private int getTransitionEffect(String key) {
            int result = -1;

            if (KEY_WKS_TRANSITION_EFFECT.equals(key)) {
                result = Settings.System.getInt(getActivity().getContentResolver(), KEY_WKS_TRANSITION_EFFECT,
                        WKS_TRANSITION_DEFAULT);
            } else {
                result = Settings.System.getInt(getActivity().getContentResolver(), KEY_APP_TRANSITION_EFFECT,
                        APP_TRANSITION_DEFAULT);
            }
            return result;
        }

        private String getDateFormat() {
            return Settings.System.getString(getContentResolver(), Settings.System.DATE_FORMAT);
        }

        private boolean is24Hour() {
            return DateFormat.is24HourFormat(getActivity());
        }

        public String getTimeAndDateDisplay(Context context, long time) {
            java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(context);
            final Calendar now = Calendar.getInstance();
            now.setTimeInMillis(time);
            mDummyDate.setTimeZone(now.getTimeZone());
            // We use December 31st because it's unambiguous when demonstrating
            // the date format.
            // We use 13:00 so we can demonstrate the 12/24 hour options.
            mDummyDate.set(now.get(Calendar.YEAR), 11, 31, 13, 0, 0);
            Date dummyDate = mDummyDate.getTime();

            StringBuilder builder = new StringBuilder();
            builder.append(shortDateFormat.format(now.getTime()));
            builder.append(" ");
            builder.append(DateFormat.getTimeFormat(getActivity()).format(now.getTime()));

            return builder.toString();
        }

        public void updatePreferenceState() {
            long lastedBackupTime = mDataBackupHelper.getLastestRestoreTime();
            if (lastedBackupTime > 0) {
                mRestoreDataPreference.setEnabled(true);
                // CharSequence time = DateFormat.format("yyyy-MM-dd h:mm:ss",
                // lastedBackupTime);
                mBackupDataPreference.setSummary(getString(R.string.launcher_has_backup_summary,
                        getTimeAndDateDisplay(getActivity(), lastedBackupTime)));
            } else {
                mRestoreDataPreference.setEnabled(false);
                mBackupDataPreference.setSummary(R.string.launcher_no_backup_summary);

            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setNegativeButton(
                    android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

            if (preference == mBackupDataPreference) {
                builder.setTitle(R.string.launcher_settings_dialog_tips)
                        .setMessage(R.string.launcher_settings_dialog_backup_msg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mDataBackupHelper.dataBackup();
                            }
                        });
            } else if (preference == mRestoreDataPreference) {
                builder.setTitle(R.string.launcher_settings_dialog_tips)
                        .setMessage(R.string.launcher_settings_dialog_restore_msg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mDataBackupHelper.dataRecover();
                            }
                        });
            } else if (preference == mResetDataPreference) {
                builder.setTitle(R.string.launcher_settings_dialog_tips)
                        .setMessage(R.string.launcher_settings_dialog_reset_msg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mDataBackupHelper.dataReset();
                            }
                        });

            }
            builder.create().show();
            return false;
        }

        @Override
        public void onBckup(boolean sucess) {
            updatePreferenceState();
        }

        @Override
        public void onRestore(boolean sucess) {
            if (sucess) {
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void onReset(boolean sucess) {
            if (sucess) {
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public boolean onPreferenceChange(Preference arg0, Object arg1) {
            if (arg0 == mDebugTextStyle) {
                enableTextStyle((Boolean)arg1);
            }else if (arg0 == mSwipeDownActions) {
                writePrefers(Launcher.SWIPE_DOWN_ACTIONS_EXTRA, (Boolean)arg1);
                LauncherAppState.getInstance().setSwipeDownEnable((Boolean)arg1);
                updateSwipeActions();//bugfix fix for update checkbox immediately zxa
            }else if (arg0 == mSwipeUpActions) {
                writePrefers(Launcher.SWIPE_UP_ACTIONS_EXTRA, (Boolean)arg1);
                LauncherAppState.getInstance().setSwipeUpEnable((Boolean)arg1);
                updateSwipeActions();//bugfix fix for update checkbox immediately zxa
            }
            return false;
        }
        
        public boolean isEnableTextStyle(){
            return mSharedPrefs.getBoolean(Launcher.DEBUG_TEXT_STYLE_EXTRA, false);
        }
        
        public void enableTextStyle(boolean enable){
            writePrefers(Launcher.DEBUG_TEXT_STYLE_EXTRA, enable);
        }

        public void writePrefers(String extra,boolean enable) {
            Editor editor = mSharedPrefs.edit();
            editor.putBoolean(extra, enable);
            editor.commit();
        }
        
        
    }
}
