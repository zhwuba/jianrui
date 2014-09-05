package com.wb.wbad;


import com.wb.launcher3.R;

import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class JingPing extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
	    
		//*/zhangwuba 2014 youmi
        AdManager.getInstance(this).init("790c0b158bf10695", "23d2cb5c9c4d45aa", false);
        //*/
        
        setContentView(R.layout.jingping);
        
        DiyManager.showRecommendWall(this);
        finish();
        
        //Log.i("myl","zhangwuba --------- JingPing");
	}

}
