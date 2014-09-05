package com.wb.launcher3;

import java.util.Random;

import com.wb.launcher3.DragLayer.ViewDraw;



import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimationDrawable extends ValueAnimator implements ViewDraw{
	
	private Rect mRect;
	
	private static final float RADIUS = Math.round(4*2.5);
	private static final float MINRADOM = Math.round(2*2.5);
	private static final float MAXRADOM = Math.round(6*2.5);
	private Paint mPaint = new Paint();
	
	private AnmationSet[] mAniset = new AnmationSet[64];
	
	 private View mView;
	
	public AnimationDrawable(View view, Bitmap bitmap, Rect rect){
		
		mRect = new Rect(rect);
		
		Random random = new Random(System.currentTimeMillis());
		
		int i = bitmap.getWidth() / 10;
	    int j = bitmap.getHeight() / 10;
	    for (int k = 0; k < 8; ++k){
	        for (int l = 0; l < 8; ++l){
	        	mAniset[(l + k * 8)] = createAnimationSet(bitmap.getPixel(i * (l + 1), j * (k + 1)),random);
	        }
	    }
	    
	    mView = view;
	    
	    setFloatValues(new float[] { 0.0F, 1.2F });
	    setInterpolator(new DecelerateInterpolator());
		
	}
	
	 
	 
	 public void start(){
	    super.start();
	    mView.invalidate(mRect);
	  }
	
	private AnmationSet createAnimationSet(int pix, Random random)
	  {
		AnmationSet anmset = new AnmationSet(this);
		anmset.color = pix;
		anmset.radius = RADIUS;
		anmset.radiusAdjust = Math.max(MINRADOM, MAXRADOM * random.nextFloat());
		anmset.y_axis = (mRect.height() * (0.15F + 0.33F * random.nextFloat()));
		anmset.x_axis = (0.4F * (mRect.width() * (random.nextFloat() - 0.5F)));
		anmset.slope = (6.0F * anmset.y_axis / anmset.x_axis);
		anmset.xParameters = (-anmset.slope / anmset.x_axis);
	    float centerX = mRect.centerX();
	    anmset.centerX = centerX;
	    anmset.x = centerX;
	    float centerY = mRect.centerY();
	    anmset.centerY = centerY;
	    anmset.y = centerY;
	    anmset.alpha = 1.0F;
	    return anmset;
	  }
	
	class AnmationSet {
		  float alpha;
		  int color;
		  float radius;
		  float radiusAdjust;
		  float x_axis;
		  float y_axis;
		  float centerX;
		  float centerY;
		  float xParameters;
		  float slope;
		  float x;
		  float y;
		  
		  public AnmationSet(AnimationDrawable animationDrawble){
			  
		  }
		  
		  public void update(float value){
			  float f1 = value / 1.2F;
			  float f2;
			  
			  if(f1 < 0.5f){
				  f2 = 0.0f;
			  }else{
				  f2 = (f1 - 0.5F) / 0.5F;
			  }
			  
			  alpha = (1.0F - f2);
		      float f3 = value * x_axis;
		      x = (f3 + centerX);
		      y = (float)(centerY - xParameters * Math.pow(f3, 2.0D) - f3 * slope);
		      radius = value * radiusAdjust;
		  }
	}

	
	@Override
	public boolean viewDraw(Canvas canvas) {
		if(isStarted()){
		//	Log.i("myl","zhangwuba --------- MyImageView draw viewDraw");
			 for (AnmationSet aniset : mAniset){
				 Log.i("myl","zhangwuba --------- MyImageView draw viewDraw");
				aniset.update(((Float)getAnimatedValue()).floatValue());
		        mPaint.setColor(aniset.color);
		        mPaint.setAlpha((int)(Color.alpha(aniset.color) * aniset.alpha));
		        canvas.drawCircle(aniset.x, aniset.y, aniset.radius, mPaint);
		        mView.invalidate(mRect);
		      }
		 }
		 
		return false;
	}
	
	

}
