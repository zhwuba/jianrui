package com.wb.launcher3;

import com.wb.launcher3.liveweather.LiveWeatherGLView;
import com.wb.launcher3.settings.Setting;
import com.wb.launcher3.weatherIcon.WeatherIconController;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class LiveweatherPreferenceFragment extends Fragment {
    public static final int WKS_TRANSITION_DEFAULT = 1;
    public static final String KEY_WKS_TRANSITION_EFFECT = "workspace_transition_effect";
    
    public static final String KEY_APP_TRANSITION_EFFECT = "apps_transition_effect";
    public static final int APP_TRANSITION_DEFAULT = 1;
    
    public static final int TYPE_WORKSPACE = 0;
    public static final int TYPE_APPS = 1;
    
	protected String m_previewKey;
	protected int m_previewTitleIds;
	protected CharSequence[] m_previewTitles = null;
	protected CharSequence[] m_previewSummaries = null;
	protected int mType = TYPE_WORKSPACE;
	
    protected int[] m_previewImgIds;
	private GridView m_preview=null;
	private PreviewAdapter mPreviewAdapter=null;
	LayoutInflater inflater=null;
	private static int mCurrentposition=0;
	
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
	
	private int getRealType(int pos){
		int realType = 0;
		
		switch(pos){
			case 0:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_NONE;
				break;
				/*
			case 1:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_DYNAMIC;
				break;*/
			case 1:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_SUNNY;
				break;
			case 2:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_CLOUDY;
				break;
			case 3:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_DANDELION;
				break;
			case 4:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_FOG;
				break;
			case 5:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_RAIN;
				break;
			case 6:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_THUNDERSHOWER;
				break;
			case 7:
				realType = LiveWeatherGLView.LIVE_WEATHER_TYPE_SNOW;
				break;
		}
		
		return realType;
	}
	
	public LiveweatherPreferenceFragment() {
	}
	
	public LiveweatherPreferenceFragment(int titleIds,int[] imgIds) {
		m_previewTitleIds = titleIds;
		m_previewImgIds = imgIds;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	    if(m_previewTitles == null) {
	    	m_previewTitles = getResources().getTextArray(m_previewTitleIds);
	    }
	    
	    SharedPreferences mPerferences = getActivity().getSharedPreferences("launcher_liveWeather_preferences", 0x0);
        int liveWeatherType = mPerferences.getInt("liveWeather_type", 0xc8);
        boolean usingRealWeather = mPerferences.getBoolean("usingRealWeather", false);
        if ((liveWeatherType > 0xd0) || (liveWeatherType < 0xc8)) {
            liveWeatherType = 0xc8;
        }
	    //Log.i("myl","zhangwuba ---------- onCreateView  liveWeatherType = " + liveWeatherType);
	    //mCurrentposition = com.android.launcher3.settings.Setting.getWeatherType(); 
	    mCurrentposition = getCurrentPosition(liveWeatherType);
	    
	    if(usingRealWeather){
	    	mCurrentposition = 1;
	    }
       
	    
		View content=inflater.inflate(R.layout.live_weather_content, container,false);
		m_preview=(GridView)content.findViewById(R.id.transition_content);
		mPreviewAdapter=new PreviewAdapter(getActivity());
		m_preview.setAdapter(mPreviewAdapter);
		m_preview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mPreviewAdapter.notifyDataSetChanged();
				mCurrentposition=arg2;
                
				SharedPreferences settings = LiveweatherPreferenceFragment.this.getActivity().getSharedPreferences("launcher_liveWeather_preferences", 0x0);
		        SharedPreferences.Editor editor = settings.edit();
		        
		        int realtype = getRealType(arg2);
		        
		        if(realtype == 0xc9 && (!Setting.getIsUsingRealWeather())){
		        	editor.putBoolean("usingRealWeather", true);
		        	editor.putInt("liveWeather_type", 0xc9);
		            editor.commit();
		            Setting.setIsUsingRealWeather(true);
		            Setting.setWeatherType(0xc9);
		            WeatherIconController.getInstance().setWeatherType(0xc9);
		            //Log.i("myl","zhangwuba ------ setting as realweather");
		            return;
		        }
		        
		        if(realtype == 0xc9 && (Setting.getIsUsingRealWeather())){
		        	 editor.putInt("liveWeather_type", 0xc9);
		        	 editor.putBoolean("usingRealWeather", true);
		        	 Setting.setIsUsingRealWeather(true);
		        }else{
		        	editor.putInt("liveWeather_type", realtype);
		        	editor.putBoolean("usingRealWeather", false);
		        	Setting.setIsUsingRealWeather(false);
		        }
		        
		        
		        editor.commit();
		        
		        if(realtype == 0xc9 && (Setting.getIsUsingRealWeather())){
		        	Setting.setWeatherType(0xc9);
		            WeatherIconController.getInstance().setWeatherType(0xc9);
		        }else{
		        	Setting.setWeatherType(realtype);
		            WeatherIconController.getInstance().setWeatherType(realtype);
		        }
			
			}
		});
		return content;
	}
	
	public class PreviewAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater  mLayoutInflater;
	    public PreviewAdapter(Context c) {
	    	mContext=c;
	    	mLayoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

		public int getCount() {
			int count = 0;
			if (m_previewImgIds != null) {
				count = m_previewImgIds.length;
			}

			return count;
		}

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.live_weather_item, null);
			}
			
			ImageView preview = (ImageView) convertView.findViewById(R.id.preview);
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
			TextView title = (TextView) convertView.findViewById(R.id.title);
			
			preview.setImageResource(m_previewImgIds[position]);
			title.setText(m_previewTitles[position]);
			// highlight
			if (position == mCurrentposition) {
				icon.setVisibility(View.VISIBLE);
			} else {
				icon.setVisibility(View.INVISIBLE);
			}
			
			return convertView;
		}
     }

}
