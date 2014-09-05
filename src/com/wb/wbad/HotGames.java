package com.wb.wbad;

import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;
import android.app.Activity;
import android.os.Bundle;

import com.wb.launcher3.R;

public class HotGames extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	    
		//*/zhangwuba 2014 youmi
        AdManager.getInstance(this).init("790c0b158bf10695", "23d2cb5c9c4d45aa", false);
        //*/
        
        setContentView(R.layout.jingping);
        
        DiyManager.showRecommendGameWall(this);
        finish();
        
        //Log.i("myl","zhangwuba --------- JingPing");
	}

}
