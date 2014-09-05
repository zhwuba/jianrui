package com.wb.launcher3;

import android.app.Activity;
import android.os.Bundle;

public class AppsTransitionEffectChooser extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setTitle(R.string.launcher_settings_apps_transition_effect);
        setContentView(R.layout.transition_chooser);
		
        if (getFragmentManager().findFragmentById(R.id.fragment_content) == null) {
        	WorkspaceTransitionEffectFragment fragment = new WorkspaceTransitionEffectFragment(R.array.apps_transition_effect_entries,
        			new int[]{
    				R.drawable.wks_style_1,
    				R.drawable.wks_style_2,
    				R.drawable.wks_style_4,
    				R.drawable.wks_style_5,
    				R.drawable.wks_style_6,
    				R.drawable.wks_style_7,
    				R.drawable.wks_style_8,
                    R.drawable.apps_style_2,
                    R.drawable.apps_style_1,
    				R.drawable.wks_style_9,
    				R.drawable.wks_style_10,
    		},WorkspaceTransitionEffectFragment.TYPE_APPS);
            getFragmentManager().beginTransaction().add(R.id.fragment_content, fragment).commit();
        }
	}

}
