package com.wb.wbad;

import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.wb.launcher3.Launcher;
import com.wb.launcher3.R;

public class HotApp extends Activity {
	
	 private final Handler mHandler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	    
		//*/zhangwuba 2014 youmi
        AdManager.getInstance(this).init("790c0b158bf10695", "23d2cb5c9c4d45aa", false);
        //*/
        
        setContentView(R.layout.jingping);
        
        //*/youmi
        // 加载插播资源
   		SpotManager.getInstance(this)
   				.loadSpotAds();
   		// 设置展示超时时间，加载超时则不展示广告，默认0，代表不设置超时时间
   		SpotManager.getInstance(this)
   				.setSpotTimeout(5000);// 设置5秒
   		SpotManager.getInstance(this)
   				.setShowInterval(20);// 设置20秒的显示时间间隔
   	// 如需要使用自动关闭插屏功能，请取消注释下面方法
  		 //SpotManager.getInstance(this)
  		// .setAutoCloseSpot(true);// 设置自动关闭插屏开关
  		 //SpotManager.getInstance(this)
  		 //.setCloseTime(6000); // 设置关闭插屏时间
   		
      //*/
        
        //DiyManager.showRecommendAppWall(this);
       // finish();
        
        //Log.i("myl","zhangwuba --------- JingPing");
  		 
  		//*/youmi
				
		//*/
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//SpotManager.getInstance(HotApp.this).showSpotAds(HotApp.this);
				}
			}, 10);
			//finish();
			
	}
	
	 @Override
	 protected void onResume() {
		 super.onResume();
		 
		 mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					SpotManager.getInstance(HotApp.this).showSpotAds(HotApp.this);
				}
			}, 100);
	 }
	
	@Override
	public void onBackPressed() {
		// 如果有需要，可以点击后退关闭插播广告。
		if (!SpotManager
				.getInstance(this)
				.disMiss(true)) {
			super.onBackPressed();
		}
		DiyManager.showRecommendAppWall(this);
	}
	
	@Override
	protected void onStop() {
		// 如果不调用此方法，则按home键的时候会出现图标无法显示的情况。
		SpotManager.getInstance(this)
				.disMiss(false);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		SpotManager.getInstance(this)
				.unregisterSceenReceiver();
		super.onDestroy();
	}

}
