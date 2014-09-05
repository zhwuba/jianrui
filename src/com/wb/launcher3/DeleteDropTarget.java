/*
 * Copyright (C) 2011 The Android Open Source Project
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

import java.util.List;
import java.util.Set;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wb.launcher3.R;
import com.wb.launcher3.config.TydtechConfig;

public class DeleteDropTarget extends ButtonDropTarget {
    private static final String TAG = "DeleteDropTarget";
    private static int DELETE_ANIMATION_DURATION = 285;
    private static int FLING_DELETE_ANIMATION_DURATION = 350;
    private static float FLING_TO_DELETE_FRICTION = 0.035f;
    private static int MODE_FLING_DELETE_TO_TRASH = 0;
    private static int MODE_FLING_DELETE_ALONG_VECTOR = 1;

    private final int mFlingDeleteMode = MODE_FLING_DELETE_ALONG_VECTOR;

    private ColorStateList mOriginalTextColor;
    private TransitionDrawable mUninstallDrawable;
    private TransitionDrawable mRemoveDrawable;
    private TransitionDrawable mCurrentDrawable;

    private boolean mWaitingForUninstall = false;
    
    //*/zhangwuba add 2014-5-7
    private float mDeleteZonePadingTop = 0;
    private float mDeleteZoneDialogPadingTop = 0;
    //*/

    public DeleteDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeleteDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Get the drawable
        mOriginalTextColor = getTextColors();

        // Get the hover color
        Resources r = getResources();
        mHoverColor = r.getColor(R.color.delete_target_hover_tint);
        mUninstallDrawable = (TransitionDrawable) 
                r.getDrawable(R.drawable.uninstall_target_selector);
        mRemoveDrawable = (TransitionDrawable) r.getDrawable(R.drawable.remove_target_selector);

        mRemoveDrawable.setCrossFadeEnabled(true);
        mUninstallDrawable.setCrossFadeEnabled(true);

        // The current drawable is set to either the remove drawable or the uninstall drawable 
        // and is initially set to the remove drawable, as set in the layout xml.
        mCurrentDrawable = (TransitionDrawable) getCurrentDrawable();

        // Remove the text in the Phone UI in landscape
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!LauncherAppState.getInstance().isScreenLarge()) {
                setText("");
            }
        }
        
        //*/zhangwuba add 2014-5-7
        mDeleteZonePadingTop = r.getDimension(R.dimen.delete_zone_pading_top);
        mDeleteZoneDialogPadingTop = r.getDimension(R.dimen.delete_zone_dialog_padingtop);
        //*/
    }

    private boolean isAllAppsApplication(DragSource source, Object info) {
        return (source instanceof AppsCustomizePagedView) && (info instanceof AppInfo);
    }
    private boolean isAllAppsWidget(DragSource source, Object info) {
        if (source instanceof AppsCustomizePagedView) {
            if (info instanceof PendingAddItemInfo) {
                PendingAddItemInfo addInfo = (PendingAddItemInfo) info;
                switch (addInfo.itemType) {
                    case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                    case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                        return true;
                }
            }
        }
        return false;
    }
    private boolean isDragSourceWorkspaceOrFolder(DragObject d) {
        return (d.dragSource instanceof Workspace) || (d.dragSource instanceof Folder);
    }
    private boolean isWorkspaceOrFolderApplication(DragObject d) {
        return isDragSourceWorkspaceOrFolder(d) && (d.dragInfo instanceof ShortcutInfo);
    }
    private boolean isWorkspaceOrFolderWidget(DragObject d) {
        return isDragSourceWorkspaceOrFolder(d) && (d.dragInfo instanceof LauncherAppWidgetInfo);
    }
    private boolean isWorkspaceFolder(DragObject d) {
        return (d.dragSource instanceof Workspace) && (d.dragInfo instanceof FolderInfo);
    }

    private void setHoverColor() {
        mCurrentDrawable.startTransition(mTransitionDuration);
        setTextColor(mHoverColor);
    }
    private void resetHoverColor() {
        mCurrentDrawable.resetTransition();
        setTextColor(mOriginalTextColor);
    }

    @Override
    public boolean acceptDrop(DragObject d) {
    	//*/added for uninstall app zxa 20140418
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
         return true;
		 }
		//*/added for uninstall app zxa20140418
        return willAcceptDrop(d.dragInfo);
    }

    public static boolean willAcceptDrop(Object info) {
        if (info instanceof ItemInfo) {
            ItemInfo item = (ItemInfo) info;
            if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET ||
                    item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
                return true;
            }
            //modify  for uninstall app
            if (AppsCustomizePagedView.DISABLE_ALL_APPS &&
                    item.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
                return true;
            }

            if (!AppsCustomizePagedView.DISABLE_ALL_APPS &&
                    item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION &&
                    item instanceof AppInfo) {
                AppInfo appInfo = (AppInfo) info;
                return (appInfo.flags & AppInfo.DOWNLOADED_FLAG) != 0;
            }

            if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION &&
                item instanceof ShortcutInfo) {
                if (AppsCustomizePagedView.DISABLE_ALL_APPS) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) info;
                    if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
                        return true;					
					}
                    return (shortcutInfo.flags & AppInfo.DOWNLOADED_FLAG) != 0;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
        boolean isVisible = true;
        boolean useUninstallLabel = !AppsCustomizePagedView.DISABLE_ALL_APPS &&
                isAllAppsApplication(source, info);

        // If we are dragging an application from AppsCustomize, only show the control if we can
        // delete the app (it was downloaded), and rename the string to "uninstall" in such a case.
        // Hide the delete target if it is a widget from AppsCustomize.
		 //*/added for uninstall app zxa 20140418
        
        if (!willAcceptDrop(info) || isAllAppsWidget(source, info)) {/// M: Add for op09 Edit and Hide app icons.
            isVisible = false;
         }
       // Log.i("onDragStart", "!willAcceptDrop(info)=="+!willAcceptDrop(info)+"isAllAppsWidget(source, info)=="+isAllAppsWidget(source, info)
        		//+"Launcher.isInEditMode()=="+Launcher.isInEditMode());
        
        if (!TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
	        if (!willAcceptDrop(info) || isAllAppsWidget(source, info)) {/// M: Add for op09 Edit and Hide app icons.
	            isVisible = false;
	         }
        }else{
			Boolean isSystemApp = false;
	        if (info instanceof ShortcutInfo ) {
	        	//Log.i("onDragStart", "--onDragStart---------------1-----");
	        	ShortcutInfo appInfo = (ShortcutInfo) info;
	        	isSystemApp = (appInfo.flags & AppInfo.DOWNLOADED_FLAG) == 0;
	        	
	        	//*/zhangwuba 2014-9-4
	        	final ComponentName componentName = appInfo.intent.getComponent();
	     		if(componentName.getPackageName().contains("com.wb")){
	     			isSystemApp = true;
	     		}
	     		//*/
			}
	    	//Log.i("onDragStart", "----onDragStart--------isSystemApp----1-----"+isSystemApp);
	        if (isSystemApp || info instanceof FolderInfo) {
	        	//Log.i("zxa", "-----------------2-----");
	        	useUninstallLabel = true;
			}
		}
        if (!TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
        if (useUninstallLabel) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(mUninstallDrawable, null, null, null);
        } else {
            setCompoundDrawablesRelativeWithIntrinsicBounds(mRemoveDrawable, null, null, null);
        }
		}else{
		if (useUninstallLabel) {
        	 // Log.i("onDragStart", "--onDragStart---------------22-----");
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, mUninstallDrawable, null, null);
        } else {
        	 // Log.i("onDragStart", "--onDragStart---------------33-----");
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, mRemoveDrawable, null, null);
        }
		}
        mCurrentDrawable = (TransitionDrawable) getCurrentDrawable();

        mActive = isVisible;
        resetHoverColor();
        ((ViewGroup) getParent()).setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (getText().length() > 0 && !TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
            setText(useUninstallLabel ? R.string.delete_target_uninstall_label
                : R.string.delete_target_label);
        }
       
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
        	setText("");
        if (null !=  mLauncher.mLauncherView) {
        	 mLauncher.mLauncherView.setSystemUiVisibility(View.INVISIBLE);
		}
 		}
		 //*/added for uninstall app zxa 20140418
    }

    @Override
    public void onDragEnd() {
        super.onDragEnd();
        mActive = false;
		//*/added for uninstall app zxa 20140418
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
        if (null !=  mLauncher.mLauncherView) {
        	 mLauncher.mLauncherView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}
	    }
		//*/added for uninstall app zxa 20140418
    }

    public void onDragEnter(DragObject d) {
		//*/added for uninstall app zxa 20140418
        if (!TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
        super.onDragEnter(d);
		}else{
		 mSearchDropTargetBar.setDeleteDropTargetBG();
		}
		//*/added for uninstall app zxa 20140418
        //Log.i("mly", "---- delete onDragEnter ----- ");
        setHoverColor();
        //*/zhangwuba ------
        if(TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG){
        FrameLayout frameLayout = (FrameLayout)mLauncher.getWorkSpaceLayout();
        AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0F, 0.0F, frameLayout.getTop(), mDeleteZonePadingTop);
        translateAnimation.setDuration(500);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillAfter(true);
        frameLayout.startAnimation(animationSet);
        //frameLayout.setForeground(mUninstallDrawable);
        }
        //*/
    }

    public void onDragExit(DragObject d) {
        super.onDragExit(d);

        if (!d.dragComplete) {
            resetHoverColor();
        } else {
            // Restore the hover color if we are deleting
		//*/added for uninstall app zxa 20140418  
        if (!TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
            d.dragView.setColor(mHoverColor);
        }
        }
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
        mSearchDropTargetBar.setBackDeleteDropTargetBG();
        }
		//*/added for uninstall app zxa 20140418
        //*/zhangwuba ------
        if(TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG){
        FrameLayout frameLayout = (FrameLayout)mLauncher.getWorkSpaceLayout();
        AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0F, 0.0F, mDeleteZonePadingTop, frameLayout.getTop());
        translateAnimation.setDuration(500);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillAfter(true);
        frameLayout.startAnimation(animationSet);
        //frameLayout.setForeground(null);
        }
        //*/
    }

    private void animateToTrashAndCompleteDrop(final DragObject d) {
        final DragLayer dragLayer = mLauncher.getDragLayer();
        final Rect from = new Rect();
        dragLayer.getViewRectRelativeToSelf(d.dragView, from);
        final Rect to = getIconRect(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight(),
                mCurrentDrawable.getIntrinsicWidth(), mCurrentDrawable.getIntrinsicHeight());
        final float scale = (float) to.width() / from.width();

        mSearchDropTargetBar.deferOnDragEnd();
        deferCompleteDropIfUninstalling(d);

        Runnable onAnimationEndRunnable = new Runnable() {
            @Override
            public void run() {
                completeDrop(d);
		//*/added for uninstall app zxa 20140418
                mSearchDropTargetBar.onDragEnd();
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
            /*  if (!isWorkspaceOrFolderWidget(d)) {
                    mSearchDropTargetBar.onDragEnd();
                }*/
		}
		//*/added for uninstall app zxa 20140418
                mLauncher.exitSpringLoadedDragMode();
            }
        };
        dragLayer.animateView(d.dragView, from, to, scale, 1f, 1f, 0.1f, 0.1f,
                DELETE_ANIMATION_DURATION, new DecelerateInterpolator(2),
                new LinearInterpolator(), onAnimationEndRunnable,
                DragLayer.ANIMATION_END_DISAPPEAR, null);
		//*/added for uninstall app zxa 20140418
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
	        if (!(d.dragInfo instanceof LauncherAppWidgetInfo)) {   
	        	Workspace workspace = mLauncher.getWorkspace();  
	        	workspace.onRestoreIcon(d); 
	        }    		
		} 
		//*/added for uninstall app zxa 20140418
    }

    private void deferCompleteDropIfUninstalling(DragObject d) {
        mWaitingForUninstall = false;
        if (isUninstallFromWorkspace(d)) {
            if (d.dragSource instanceof Folder) {
                ((Folder) d.dragSource).deferCompleteDropAfterUninstallActivity();
            } else if (d.dragSource instanceof Workspace) {
                ((Workspace) d.dragSource).deferCompleteDropAfterUninstallActivity();
            }
            mWaitingForUninstall = true;
        }
    }

    private boolean isUninstallFromWorkspace(DragObject d) {
        if (AppsCustomizePagedView.DISABLE_ALL_APPS && isWorkspaceOrFolderApplication(d)) {
            ShortcutInfo shortcut = (ShortcutInfo) d.dragInfo;
            if (shortcut.intent != null && shortcut.intent.getComponent() != null) {
                Set<String> categories = shortcut.intent.getCategories();
                boolean includesLauncherCategory = false;
                if (categories != null) {
                    for (String category : categories) {
                        if (category.equals(Intent.CATEGORY_LAUNCHER)) {
                            includesLauncherCategory = true;
                            break;
                        }
                    }
                }
                return includesLauncherCategory;
            }
        }
        return false;
    }

    private void completeDrop(DragObject d) {
        ItemInfo item = (ItemInfo) d.dragInfo;
        boolean wasWaitingForUninstall = mWaitingForUninstall;
        mWaitingForUninstall = false;
        if (isAllAppsApplication(d.dragSource, item)) {
            // Uninstall the application if it is being dragged from AppsCustomize
            AppInfo appInfo = (AppInfo) item;
            mLauncher.startApplicationUninstallActivity(appInfo.componentName, appInfo.flags);
        } else if (isUninstallFromWorkspace(d)) {
	    //*/added for uninstall app zxa 20140418
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
		    			mRunning = true;
    			
		    //*zhangwuba add for miui theme
		    FrameLayout frameLayout = (FrameLayout)mLauncher.getDragLayer();
		    AnimationSet animationSet = new AnimationSet(false);
		    TranslateAnimation translateAnimation = new TranslateAnimation(0.0F, 0.0F,mDeleteZonePadingTop, mDeleteZoneDialogPadingTop);
		    translateAnimation.setDuration(500);
		    animationSet.addAnimation(translateAnimation);
		    animationSet.setFillAfter(true);
		    frameLayout.startAnimation(animationSet); 
		    //*/
		    		     
    			Handler handler = new Handler();
    			handler.post(new Runnable() {
    				@Override
    				public void run() {
    					addLayoutView(md, md.x, md.y); 
    				}  
    			});
    			
    			

		}else{
            ShortcutInfo shortcut = (ShortcutInfo) item;
            if (shortcut.intent != null && shortcut.intent.getComponent() != null) {
                final ComponentName componentName = shortcut.intent.getComponent();
                final DragSource dragSource = d.dragSource;
                int flags = AppInfo.initFlags(
                    ShortcutInfo.getPackageInfo(getContext(), componentName.getPackageName()));
                mWaitingForUninstall =
                    mLauncher.startApplicationUninstallActivity(componentName, flags);
                if (mWaitingForUninstall) {
                    final Runnable checkIfUninstallWasSuccess = new Runnable() {
                        @Override
                        public void run() {
                            mWaitingForUninstall = false;
                            String packageName = componentName.getPackageName();
                            List<ResolveInfo> activities =
                                    AllAppsList.findActivitiesForPackage(getContext(), packageName);
                            boolean uninstallSuccessful = activities.size() == 0;
                            if (dragSource instanceof Folder) {
                                ((Folder) dragSource).
                                    onUninstallActivityReturned(uninstallSuccessful);
                            } else if (dragSource instanceof Workspace) {
                                ((Workspace) dragSource).
                                    onUninstallActivityReturned(uninstallSuccessful);
                            }
                        }
                    };
                    mLauncher.addOnResumeCallback(checkIfUninstallWasSuccess);
                }
            }
		}
        } else if (isWorkspaceOrFolderApplication(d)) {
            LauncherModel.deleteItemFromDatabase(mLauncher, item);
        } else if (isWorkspaceFolder(d)) {
            // Remove the folder from the workspace and delete the contents from launcher model
            FolderInfo folderInfo = (FolderInfo) item;
            mLauncher.removeFolder(folderInfo);
            LauncherModel.deleteFolderContentsFromDatabase(mLauncher, folderInfo);
        } else if (isWorkspaceOrFolderWidget(d)) {
        	//*/modified by xuchao for remove private widget 20140509 
            // Remove the widget from the workspace
//            mLauncher.removeAppWidget((LauncherAppWidgetInfo) item);
//            LauncherModel.deleteItemFromDatabase(mLauncher, item);
            if(item != null)
            {
            	mLauncher.removeAppWidgetByButton((LauncherAppWidgetInfo) item);
            }
            //*/
            final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
            final LauncherAppWidgetHost appWidgetHost = mLauncher.getAppWidgetHost();
            if (appWidgetHost != null) {
                // Deleting an app widget ID is a void call but writes to disk before returning
                // to the caller...
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                    }
                }.start();
            }
        }
        if (wasWaitingForUninstall && !mWaitingForUninstall) {
            if (d.dragSource instanceof Folder) {
                ((Folder) d.dragSource).onUninstallActivityReturned(false);
            } else if (d.dragSource instanceof Workspace) {
                ((Workspace) d.dragSource).onUninstallActivityReturned(false);
            }
        }
    }

    public void onDrop(DragObject d) {
	    //*/added for uninstall app zxa 20140418
        if (TydtechConfig.TYDTECH_UNINSTALL_MIUI_FLAG) {
		        Workspace workspace = mLauncher.getWorkspace(); 
     	 Boolean isSystemApp = false; 
     	if (d.dragInfo instanceof ShortcutInfo) {  
     		ShortcutInfo mShortcutInfo = (ShortcutInfo)d.dragInfo;  
     		isSystemApp =  (mShortcutInfo.flags & AppInfo.DOWNLOADED_FLAG) == 0;
     		//Log.i(TAG, "isSystemApp=="+isSystemApp);
     		//*/zhanguwba add 2014-9-4
     		final ComponentName componentName = mShortcutInfo.intent.getComponent();
     		if(componentName.getPackageName().contains("com.wb")){
     			isSystemApp = true;
     		}
     		//*/
     	} 
     	 
     	if (isSystemApp || d.dragInfo instanceof FolderInfo) { 
     		if (d.dragInfo instanceof FolderInfo) {
     			//showToast(R.string.delete_folder_error_msg);
     			Toast toast = Toast.makeText(mLauncher, R.string.delete_folder_error_msg, Toast.LENGTH_LONG);
     			toast.setGravity(Gravity.TOP, 0, 0);
     			toast.show();
     			
 			}else {
 				//showToast(R.string.uninstall_system_app);
 				Toast toast = Toast.makeText(mLauncher, R.string.uninstall_system_app, Toast.LENGTH_LONG);
 				toast.setGravity(Gravity.TOP, 0, 0);
     			toast.show();
 			}
     			 workspace.onRestoreIcon(d); 
 		}else{   
 			animateToTrashAndCompleteDrop(d);
 			md = d;
 		}
	 }else{
	        animateToTrashAndCompleteDrop(d);	
		}

    }
    private DragObject md;
    private Boolean mRunning = false;//zxa
    private View mLocalView = null;
    private FrameLayout.LayoutParams mLocalLayoutParams = null;
    
    public void addLayoutView(DragObject d,int paramInt1,int paramInt2){
        View localView = LayoutInflater.from(mLauncher).inflate(R.layout.uninstall_apps, null);//uninstall_apps
        int i1 = mLauncher.getResources().getDimensionPixelSize(R.dimen.uninstall_shadow_height);//uninstall_shadow_height = 12px
        int i2 =LauncherAppState.getStatusBarHeight();
        
        ShortcutInfo shortcutInfo = (ShortcutInfo)d.dragInfo;  
 
        ((ViewGroup)mLauncher.findViewById(android.R.id.content)).addView(localView); 
        localView.setTag(shortcutInfo); 
        
       // mLauncher.isUninstallDialogOpen = true; 

        
        mLauncher.setLauncherInDeleteAppState(true);
        
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams(); 
        Resources localResources = mLauncher.getResources(); 
        localLayoutParams.height = (int)localResources.getDimension(R.dimen.uninstall_apps_dialog_height); 
        localView.setLayoutParams(localLayoutParams); 
        float f1 = mLauncher.getResources().getDisplayMetrics().density; 
       // Log.i("zxa", "f1===="+f1);
        if ((f1 > 1.0F) && (f1 < 2.0F))
        {
          //Log.i("DeleteDropTarget", "this background get drawable for density .. ");
          View localView2 = localView.findViewById(R.id.uninstall_apps_dlg);//uninstall_apps_dlg
          Drawable localDrawable = localResources.getDrawable(R.drawable.uninstall_shadow);
          //Log.i("XDeleteDropTarget", "d.width == " + localDrawable.getIntrinsicWidth());
          localView2.setBackgroundDrawable(localDrawable); 
        }
        ImageView localImageView = (ImageView)localView.findViewById(R.id.uninstall_image);//uninstall_image
        ComponentName localComponentName = shortcutInfo.intent.getComponent();
       // String packageName = localComponentName.getPackageName(); 
        Drawable drawable = mLauncher.getAppIcon(localComponentName);
        BitmapDrawable bd = (BitmapDrawable)drawable;
        Bitmap bitmap = bd.getBitmap();
        localImageView.setImageBitmap(bitmap);
        ((TextView)localView.findViewById(R.id.uninstall_title)).setText(mLauncher.getString(R.string.uninstall_title_key) + shortcutInfo.title);
        int l1 = localResources.getDimensionPixelSize(R.dimen.uninstall_apps_view_padding_left);//uninstall_apps_view_padding_left
        int l2 = localResources.getDimensionPixelSize(R.dimen.uninstall_apps_image_padding_top); //uninstall_apps_image_padding_top
        int i3 = bitmap.getWidth() / 2;
        int i4 = bitmap.getHeight() / 2;
         View localView1 = localView.findViewById(R.id.uninstall_image_layout);//uninstall_image_layout
        int i5 = d.x - (l1 + i3);
        int i6 = d.y - (l2 + i4);
        //TranslateAnimation localTranslateAnimation = new TranslateAnimation(i5, 0.0F, i6, 0.0F);
       // TranslateAnimation localTranslateAnimation = new TranslateAnimation(i5, 0.0F, i6, 0.0F);
       // localTranslateAnimation.setInterpolator(new OvershootInterpolator(0.7F));
        //localTranslateAnimation.setDuration(250L);
       // localView1.startAnimation(localTranslateAnimation); 
        
        mLocalView = localView;
        mLocalLayoutParams = localLayoutParams;
        
        ((Button)localView.findViewById(R.id.uninstall_cancel)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("zxa", "onclick-----uninstall Cancel");
				/*Workspace workspace = mLauncher.getWorkspace(); 
				  workspace.uninstallCancel(md); */
				//uninstallCancelListener(mLocalView, mLocalLayoutParams.height,md);
				removeLayoutView();
			}
		});
        
        ((Button)localView.findViewById(R.id.uninstall_ok)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Log.i("zxa", "onclick-----uninstall ok");
				removeLayoutView();
				
	        	ShortcutInfo mShortcutInfo = (ShortcutInfo)md.dragInfo;
	        	
					/*Log.i("zxa", "1234567121233344");
					Uri uri = Uri.parse("package:"+mShortcutInfo.getPackageName());
					Intent intent = new Intent("android.intent.action.App_DELETE", uri); 
					getContext().startActivity(intent);   */
	        	//removeApp(mShortcutInfo.getIntent().getComponent().getPackageName());
	        	removeApp(md);
	        	//Log.i("zxa", "mShortcutInfo.getIntent().getPackage()==="+mShortcutInfo.getIntent().getPackage());
			}
		});
        
   
    }
    
    /*
    class PackageDeleteObserver extends IPackageDeleteObserver.Stub{
        public void packageDeleted(String packageName, int returnCode) {
            Message msg = mHandler.obtainMessage(UNINSTALL_COMPLETE);
            msg.arg1 = returnCode;
            msg.obj = packageName;
            mHandler.sendMessage(msg);
        }
    }
    */
    
    private final int UNINSTALL_COMPLETE = 1;
    public final static int SUCCEEDED=1;
    public final static int FAILED=0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        	//Log.i("adbcd", "handleMessage---------");
            switch (msg.what) {
                case UNINSTALL_COMPLETE:
                    /*if (mLauncher.getIntent().getBooleanExtra(Intent.EXTRA_RETURN_RESULT, false)) {
                        Intent result = new Intent();
                        result.putExtra(Intent.EXTRA_INSTALL_RESULT, msg.arg1);
                        setResult(msg.arg1 == PackageManager.DELETE_SUCCEEDED
                                ? Activity.RESULT_OK : Activity.RESULT_FIRST_USER,
                                        result);
                        return;
                    }*/

                    //mResultCode = msg.arg1;
                    final String packageName = (String) msg.obj;

                    // Update the status text
                    final int statusText;
                    /*switch (msg.arg1) {
                        case PackageManager.DELETE_SUCCEEDED:
                            statusText = R.string.uninstall_done;
                            // Show a Toast and finish the activity
                            Toast.makeText(mLauncher, statusText, Toast.LENGTH_LONG).show();
                          //  setResultAndFinish(mResultCode);
                            return;
                        case PackageManager.DELETE_FAILED_DEVICE_POLICY_MANAGER:
                            Log.d(TAG, "Uninstall failed because " + packageName
                                    + " is a device admin");
                            break;
                        default:
                            Log.d(TAG, "Uninstall failed for " + packageName + " with code "
                                    + msg.arg1);
                            break;
                    }
                    break;
                default:
                    break;*/
            }
        }
    };
    
    private void removeApp(String packageName){
    	//mAllUsers = 
    	//Log.i("zxa", "packageName=="+packageName);
    	//PackageDeleteObserver observer = new PackageDeleteObserver();
	       // mLauncher.getPackageManager().deletePackage(mAppInfo.packageName, observer,
	          //      mAllUsers ? PackageManager.DELETE_ALL_USERS : 0);
       // mLauncher.getPackageManager().deletePackage(packageName, observer,
               // PackageManager.DELETE_ALL_USERS);
    }
    
    private void removeApp(DragObject d){
    	
    	removeIconAtWorkspace(d);
    }
    
    public static boolean findActivity(Context context, ComponentName component) {
        final PackageManager packageManager = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(component.getPackageName());

        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        final String className = component.getClassName();
        for (ResolveInfo info : apps) {
            final ActivityInfo activityInfo = info.activityInfo;
            if (activityInfo.name.equals(className)) {
                return true;
            }
        }
        return false;
    }
    
    private void removeIconAtWorkspace(DragObject d) {
    	ShortcutInfo mShortcutInfo = (ShortcutInfo)md.dragInfo;
        if (mShortcutInfo != null) {
            if (mShortcutInfo.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION) {
            	if (findActivity(getContext(), mShortcutInfo.getIntent().getComponent())) {
            		startApplicationUninstallActivity(mShortcutInfo);
                }else {
					removeShortcutByShortcutInfo(mShortcutInfo);
				}
            } else if (mShortcutInfo.itemType == LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT) {
            	removeShortcutByShortcutInfo(mShortcutInfo);
            }
        }
    }
    
    void startApplicationUninstallActivity(ShortcutInfo appInfo) {
        String packageName = appInfo.getIntent().getComponent().getPackageName();
        String className = appInfo.getIntent().getComponent().getClassName();
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, className));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        getContext().startActivity(intent);
    }
    
    void removeShortcutByShortcutInfo(ShortcutInfo info){
    	if (getContext() instanceof Launcher) {
            mLauncher = (Launcher) getContext();
            LauncherModel.deleteItemFromDatabase(getContext(), info);
            //getParent() is ShortcutAndWidgetContainer,getParent().getParent() is CellLayout
            ViewParent parent = getParent().getParent();
            if (parent != null && parent instanceof CellLayout) {
                ((CellLayout) parent).removeView(this);
                mLauncher.getWorkspace().stripEmptyScreens();
            }
        }
    }
    
    
    private void removeLayoutView(){
    	
        //mLauncher.isUninstallDialogOpen = false; 

        mLauncher.getWorkspace().setEnabled(true);
        
        mLauncher.setLauncherInDeleteAppState(false);
        
		((ViewGroup)mLauncher.findViewById(android.R.id.content)).removeView(mLocalView);
		//ObjectAnimator anim = ObjectAnimator.ofFloat(mLauncher.getDragLayer(), "translationY", 0);
		//anim.start(); 
		//*zhangwuba add for miui theme
		 FrameLayout frameLayout = (FrameLayout)mLauncher.getDragLayer();
	     AnimationSet animationSet = new AnimationSet(false);
	     TranslateAnimation translateAnimation = new TranslateAnimation(0.0F, 0.0F, mDeleteZoneDialogPadingTop, frameLayout.getTop());
	     translateAnimation.setDuration(500);
	     animationSet.addAnimation(translateAnimation);
	     animationSet.setFillAfter(true);
	     frameLayout.startAnimation(animationSet); 
	     //*/
    }
    
    //*/
   

    /**
     * Creates an animation from the current drag view to the delete trash icon.
     */
    private AnimatorUpdateListener createFlingToTrashAnimatorListener(final DragLayer dragLayer,
            DragObject d, PointF vel, ViewConfiguration config) {
        final Rect to = getIconRect(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight(),
                mCurrentDrawable.getIntrinsicWidth(), mCurrentDrawable.getIntrinsicHeight());
        final Rect from = new Rect();
        dragLayer.getViewRectRelativeToSelf(d.dragView, from);

        // Calculate how far along the velocity vector we should put the intermediate point on
        // the bezier curve
        float velocity = Math.abs(vel.length());
        float vp = Math.min(1f, velocity / (config.getScaledMaximumFlingVelocity() / 2f));
        int offsetY = (int) (-from.top * vp);
        int offsetX = (int) (offsetY / (vel.y / vel.x));
        final float y2 = from.top + offsetY;                        // intermediate t/l
        final float x2 = from.left + offsetX;
        final float x1 = from.left;                                 // drag view t/l
        final float y1 = from.top;
        final float x3 = to.left;                                   // delete target t/l
        final float y3 = to.top;

        final TimeInterpolator scaleAlphaInterpolator = new TimeInterpolator() {
            @Override
            public float getInterpolation(float t) {
                return t * t * t * t * t * t * t * t;
            }
        };
        return new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final DragView dragView = (DragView) dragLayer.getAnimatedView();
                float t = ((Float) animation.getAnimatedValue()).floatValue();
                float tp = scaleAlphaInterpolator.getInterpolation(t);
                float initialScale = dragView.getInitialScale();
                float finalAlpha = 0.5f;
                float scale = dragView.getScaleX();
                float x1o = ((1f - scale) * dragView.getMeasuredWidth()) / 2f;
                float y1o = ((1f - scale) * dragView.getMeasuredHeight()) / 2f;
                float x = (1f - t) * (1f - t) * (x1 - x1o) + 2 * (1f - t) * t * (x2 - x1o) +
                        (t * t) * x3;
                float y = (1f - t) * (1f - t) * (y1 - y1o) + 2 * (1f - t) * t * (y2 - x1o) +
                        (t * t) * y3;

                dragView.setTranslationX(x);
                dragView.setTranslationY(y);
                dragView.setScaleX(initialScale * (1f - tp));
                dragView.setScaleY(initialScale * (1f - tp));
                dragView.setAlpha(finalAlpha + (1f - finalAlpha) * (1f - tp));
            }
        };
    }

    /**
     * Creates an animation from the current drag view along its current velocity vector.
     * For this animation, the alpha runs for a fixed duration and we update the position
     * progressively.
     */
    private static class FlingAlongVectorAnimatorUpdateListener implements AnimatorUpdateListener {
        private DragLayer mDragLayer;
        private PointF mVelocity;
        private Rect mFrom;
        private long mPrevTime;
        private boolean mHasOffsetForScale;
        private float mFriction;

        private final TimeInterpolator mAlphaInterpolator = new DecelerateInterpolator(0.75f);

        public FlingAlongVectorAnimatorUpdateListener(DragLayer dragLayer, PointF vel, Rect from,
                long startTime, float friction) {
            mDragLayer = dragLayer;
            mVelocity = vel;
            mFrom = from;
            mPrevTime = startTime;
            mFriction = 1f - (dragLayer.getResources().getDisplayMetrics().density * friction);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final DragView dragView = (DragView) mDragLayer.getAnimatedView();
            float t = ((Float) animation.getAnimatedValue()).floatValue();
            long curTime = AnimationUtils.currentAnimationTimeMillis();

            if (!mHasOffsetForScale) {
                mHasOffsetForScale = true;
                float scale = dragView.getScaleX();
                float xOffset = ((scale - 1f) * dragView.getMeasuredWidth()) / 2f;
                float yOffset = ((scale - 1f) * dragView.getMeasuredHeight()) / 2f;

                mFrom.left += xOffset;
                mFrom.top += yOffset;
            }

            mFrom.left += (mVelocity.x * (curTime - mPrevTime) / 1000f);
            mFrom.top += (mVelocity.y * (curTime - mPrevTime) / 1000f);

            dragView.setTranslationX(mFrom.left);
            dragView.setTranslationY(mFrom.top);
            dragView.setAlpha(1f - mAlphaInterpolator.getInterpolation(t));

            mVelocity.x *= mFriction;
            mVelocity.y *= mFriction;
            mPrevTime = curTime;
        }
    };
    private AnimatorUpdateListener createFlingAlongVectorAnimatorListener(final DragLayer dragLayer,
            DragObject d, PointF vel, final long startTime, final int duration,
            ViewConfiguration config) {
        final Rect from = new Rect();
        dragLayer.getViewRectRelativeToSelf(d.dragView, from);

        return new FlingAlongVectorAnimatorUpdateListener(dragLayer, vel, from, startTime,
                FLING_TO_DELETE_FRICTION);
    }

    public void onFlingToDelete(final DragObject d, int x, int y, PointF vel) {
        final boolean isAllApps = d.dragSource instanceof AppsCustomizePagedView;

        // Don't highlight the icon as it's animating
        d.dragView.setColor(0);
        d.dragView.updateInitialScaleToCurrentScale();
        // Don't highlight the target if we are flinging from AllApps
        if (isAllApps) {
            resetHoverColor();
        }

        if (mFlingDeleteMode == MODE_FLING_DELETE_TO_TRASH) {
            // Defer animating out the drop target if we are animating to it
            mSearchDropTargetBar.deferOnDragEnd();
            mSearchDropTargetBar.finishAnimations();
        }

        final ViewConfiguration config = ViewConfiguration.get(mLauncher);
        final DragLayer dragLayer = mLauncher.getDragLayer();
        final int duration = FLING_DELETE_ANIMATION_DURATION;
        final long startTime = AnimationUtils.currentAnimationTimeMillis();

        // NOTE: Because it takes time for the first frame of animation to actually be
        // called and we expect the animation to be a continuation of the fling, we have
        // to account for the time that has elapsed since the fling finished.  And since
        // we don't have a startDelay, we will always get call to update when we call
        // start() (which we want to ignore).
        final TimeInterpolator tInterpolator = new TimeInterpolator() {
            private int mCount = -1;
            private float mOffset = 0f;

            @Override
            public float getInterpolation(float t) {
                if (mCount < 0) {
                    mCount++;
                } else if (mCount == 0) {
                    mOffset = Math.min(0.5f, (float) (AnimationUtils.currentAnimationTimeMillis() -
                            startTime) / duration);
                    mCount++;
                }
                return Math.min(1f, mOffset + t);
            }
        };
        AnimatorUpdateListener updateCb = null;
        if (mFlingDeleteMode == MODE_FLING_DELETE_TO_TRASH) {
            updateCb = createFlingToTrashAnimatorListener(dragLayer, d, vel, config);
        } else if (mFlingDeleteMode == MODE_FLING_DELETE_ALONG_VECTOR) {
            updateCb = createFlingAlongVectorAnimatorListener(dragLayer, d, vel, startTime,
                    duration, config);
        }
        deferCompleteDropIfUninstalling(d);

        Runnable onAnimationEndRunnable = new Runnable() {
            @Override
            public void run() {
                // If we are dragging from AllApps, then we allow AppsCustomizePagedView to clean up
                // itself, otherwise, complete the drop to initiate the deletion process
                if (!isAllApps) {
                    mLauncher.exitSpringLoadedDragMode();
                    completeDrop(d);
                }
                mLauncher.getDragController().onDeferredEndFling(d);
            }
        };
        dragLayer.animateView(d.dragView, updateCb, duration, tInterpolator, onAnimationEndRunnable,
                DragLayer.ANIMATION_END_DISAPPEAR, null);
    }
}
