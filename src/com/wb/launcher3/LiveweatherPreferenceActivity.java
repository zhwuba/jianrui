package com.wb.launcher3;

import com.wb.launcher3.R;
import android.app.Activity;
import android.os.Bundle;

public class LiveweatherPreferenceActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setTitle(R.string.live_weather_title);
		setContentView(R.layout.liveweather_chooser);
		
        if (getFragmentManager().findFragmentById(R.id.fragment_content) == null) {
        	LiveweatherPreferenceFragment fragment = new LiveweatherPreferenceFragment(R.array.live_weather_entries,
        			new int[]{
    				R.drawable.preview_liveweather_none,
    				//R.drawable.preview_liveweather_realweather,
    				R.drawable.preview_liveweather_sunny,
    				R.drawable.preview_liveweather_cloudy,
    				R.drawable.preview_liveweather_dandelion,
    				R.drawable.preview_liveweather_fog,
    				R.drawable.preview_liveweather_rain,
    				R.drawable.preview_liveweather_thundershower,
    				R.drawable.preview_liveweather_snow,
    		});
            getFragmentManager().beginTransaction().add(R.id.fragment_content, fragment).commit();
        }
	}

}
