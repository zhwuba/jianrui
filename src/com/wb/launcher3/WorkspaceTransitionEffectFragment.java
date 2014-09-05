package com.wb.launcher3;

import com.wb.launcher3.R;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkspaceTransitionEffectFragment extends Fragment {
	
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
	
	public WorkspaceTransitionEffectFragment() {
	}
	
	public WorkspaceTransitionEffectFragment(int titleIds,int[] imgIds,int type) {
		m_previewTitleIds = titleIds;
		m_previewImgIds = imgIds;
		mType = type;
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
	    if (mType == TYPE_WORKSPACE) {
	        mCurrentposition = Settings.System.getInt(getActivity().getContentResolver(), 
	                KEY_WKS_TRANSITION_EFFECT, WKS_TRANSITION_DEFAULT); 
        }else {
            mCurrentposition = Settings.System.getInt(getActivity().getContentResolver(), 
                    KEY_APP_TRANSITION_EFFECT, APP_TRANSITION_DEFAULT);
        }
	    
		View content=inflater.inflate(R.layout.workspacetransition_effect_content, container,false);
		m_preview=(GridView)content.findViewById(R.id.transition_content);
		mPreviewAdapter=new PreviewAdapter(getActivity());
		m_preview.setAdapter(mPreviewAdapter);
		m_preview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mCurrentposition=arg2;
                if (mType == TYPE_WORKSPACE) {
                    Settings.System.putInt(getActivity().getContentResolver(),
                            KEY_WKS_TRANSITION_EFFECT, mCurrentposition);
                } else {
                    Settings.System.putInt(getActivity().getContentResolver(),
                            KEY_APP_TRANSITION_EFFECT, mCurrentposition);
                }
				
				mPreviewAdapter.notifyDataSetChanged();
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
						R.layout.workspacetransition_effect_item, null);
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
