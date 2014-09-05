package com.wb.launcher3.liveweather;

import java.io.InputStream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.wb.launcher3.liveweather.LiveWeatherGLView.IconCallBack;
import com.wb.launcher3.R;

public class SteamView extends View implements ILiveWeatherView {
    private static final float ERROR_VALUE = 0f;
    private static final int FADEAWAY_VIEW = 257;
    private static final int SHAKE_DELAY = 1000;
    private static final int STATE_FADE_IN = 101;
    private static final int STATE_FADE_NORMAL = 102;
    private static final int STATE_FADE_OUT = 100;
    public static final int TAG_ALPHA_FADEIN = 1;
    public static final int TAG_ALPHA_FADEOUT = -1;
    public static final int TAG_ALPHA_NORMAL = 0;
    public static final int TAG_SHAKE_END = 101;
    public static final int TAG_SHAKE_ING = 100;
    public static final int TAG_SHAKE_NORMAL = 102;
    private static final float TOUCH_TOLERANCE = 0f;
    private static final int VIEW_SHOW = 256;

    private int SCREEN_H;
    private int SCREEN_W;
    private boolean canShake;
    float density;
    private float fingerSize;
    private boolean isCanDraw;
    private AnimatorSet mAnimatorSet;
    private Bitmap mBackground;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mClearPaint;
    private Context mContext;
    private int mFadeTag;
    private Handler mHandler;
    protected IconCallBack mIconCallBack;
    private float mOldX;
    private float mOldY;
    private Paint mPaint;
    private Paint mPaintForDrawCircle;
    private Path mPath;
    private Rect mRect;
    private boolean mShakeInterrupt;
    private int mShakeTag;
    private boolean mToughWiping;
    private int mViewState;
    private int mWeatherType;

    public SteamView(Context context, boolean paramBoolean) {
        super(context);
        this.mClearPaint = new Paint();
        this.mToughWiping = false;
        this.density = 0f;
        this.canShake = true;
        this.mContext = null;
        this.mFadeTag = 0;
        this.mShakeTag = TAG_SHAKE_NORMAL;
        this.mWeatherType = LiveWeatherGLView.LIVE_WEATHER_TYPE_FOG;
        this.mOldX = -100f;
        this.mOldY = -100f;
        this.isCanDraw = false;
        this.mRect = new Rect();
        this.fingerSize = 0f;
        this.mAnimatorSet = null;
        this.mShakeInterrupt = false;
        this.mViewState = STATE_FADE_NORMAL;

        this.mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case VIEW_SHOW:
                    SteamView.this.shakeInView();
                    break;
                }
            }
        };

        this.mContext = context;
        this.setFocusable(true);
        this.init();
        this.drawCover();
    }

    @Override
    public boolean canResponseMic() {
        return false;
    }

    @Override
    public boolean canResponseSensor() {
        return true;
    }

    public void clear() {
        this.mBackground = this.genBitmap(R.drawable.fog_blank);
        this.mClearPaint.setShader(new BitmapShader(this.mBackground, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR));
        this.mClearPaint.setStyle(Paint.Style.FILL);
        this.mClearPaint.setAlpha(255);
        this.mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        this.mCanvas.drawRect(0f, 0f, this.getWidth(), this.getHeight(), this.mClearPaint);
        this.invalidate();
    }

    public void drawCover() {
        this.mCanvas.drawBitmap(this.genBitmap(R.drawable.fog_blank), 0f, 0f, null);
    }

    public void drawToPoint(float oldX, float oldY, float newX, float newY, float fingerSize, int alpha) {
    }

    @Override
    public void fadeIn() {
        if (this.mShakeTag == TAG_SHAKE_ING) {
            return;
        }

        if (this.mAnimatorSet != null) {
            this.mAnimatorSet.cancel();
            this.mAnimatorSet = null;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", new float[] { 0f, 1f });
        animator.setDuration(500);
        this.mAnimatorSet = new AnimatorSet();

        this.mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SteamView.this.setFadeTag(0);
                SteamView.this.mViewState = STATE_FADE_IN;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                SteamView.this.setFadeTag(1);
            }
        });

        this.mAnimatorSet.play(animator);
        this.mAnimatorSet.start();
    }

    @Override
    public void fadeOut() {
        if (this.mAnimatorSet != null) {
            this.mAnimatorSet.cancel();
            this.mAnimatorSet = null;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", new float[] { this.getAlpha(), 0f });
        animator.setDuration(500);
        this.mAnimatorSet = new AnimatorSet();

        this.mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SteamView.this.setFadeTag(0);
                SteamView.this.mViewState = STATE_FADE_OUT;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                SteamView.this.setFadeTag(-1);
            }
        });

        this.mAnimatorSet.play(animator);
        this.mAnimatorSet.start();
    }

    @Override
    public void flingScreen(int direct, int offset, int acceleration) {
    }

    public Bitmap genBitmap(int res) {
        Bitmap bitmap = null;
        InputStream inputStream;

        try {
            inputStream = this.mContext.getResources().openRawResource(res);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("SteamView", "createImage() is close error.");
        }

        Bitmap result = Bitmap.createScaledBitmap(bitmap, this.SCREEN_W, this.SCREEN_H, true);
        bitmap.recycle();

        return result;
    }

    @Override
    public int getFadeTag() {
        return this.mFadeTag;
    }

    @Override
    public float getGLAlpha() {
        return this.getAlpha();
    }

    @Override
    public int getWeatherType() {
        return this.mWeatherType;
    }

    @Override
    public void huffMic(int level) {
    }

    private void init() {
        new DisplayMetrics();
        DisplayMetrics v0 = this.getResources().getDisplayMetrics();
        this.SCREEN_W = v0.widthPixels;
        this.SCREEN_H = v0.heightPixels;
        this.fingerSize = 0.046296f * this.SCREEN_W;
        this.mPaint = new Paint();
        this.mPaint.setAlpha(0);
        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(this.fingerSize);
        this.mPaintForDrawCircle = new Paint();
        this.mPaintForDrawCircle.setAlpha(0);
        this.mPaintForDrawCircle.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.mPaintForDrawCircle.setAntiAlias(true);
        this.mPaintForDrawCircle.setDither(true);
        this.mPaintForDrawCircle.setStyle(Paint.Style.FILL);
        this.mPaintForDrawCircle.setStrokeJoin(Paint.Join.ROUND);
        this.mPaintForDrawCircle.setStrokeCap(Paint.Cap.ROUND);
        this.mPaintForDrawCircle.setStrokeWidth(this.fingerSize / 2f);
        this.mPath = new Path();
        this.mBitmap = Bitmap.createBitmap(this.SCREEN_W, this.SCREEN_H, Bitmap.Config.ARGB_8888);
        this.mCanvas = new Canvas();
        this.mCanvas.setBitmap(this.mBitmap);
    }

    private boolean isToggleBarOpen() {
        if (this.mIconCallBack == null) {
            Log.e("SteamView", "error mIconCallBack = null");
            return false;
        }

        return this.mIconCallBack.isToggleBarOpen();
    }

    public boolean isToughWiping() {
        return this.mToughWiping;
    }

    @Override
    protected void onAttachedToWindow() {
        this.setAlpha(0f);
        super.onAttachedToWindow();
        this.shakeInView();
        this.mViewState = 102;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.recycle();
        this.mViewState = STATE_FADE_NORMAL;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, 0f, 0f, null);
        if (this.mPath == null || this.mPaint == null) {
            super.onDraw(canvas);
        } else {
            this.mCanvas.drawPath(this.mPath, this.mPaint);
        }
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    public void recycle() {
        Bitmap v1 = null;
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
            this.mBitmap = v1;
        }

        if (this.mBackground != null) {
            this.mBackground.recycle();
            this.mBackground = v1;
        }
    }

    @Override
    public void resetOffset() {
    }

    private void setFadeTag(int fadeTag) {
        this.mFadeTag = fadeTag;
    }

    @Override
    public void setGLAlpha(float alpha) {
        this.setAlpha(alpha);
    }

    @Override
    public void setIconCallBack(IconCallBack iconCallBack) {
        this.mIconCallBack = iconCallBack;
    }

    @Override
    public void shake() {
        if (!this.isToggleBarOpen() && (this.canShake) && this.mViewState != 100) {
            this.mShakeInterrupt = false;
            this.shakeOutView();
        }
    }

    private void shakeInView() {
        if (this.mAnimatorSet != null) {
            this.mAnimatorSet.cancel();
            this.mAnimatorSet = null;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", new float[] { 0f, 1f });
        animator.setDuration(2500);
        this.mAnimatorSet = new AnimatorSet();

        this.mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                SteamView.this.mAnimatorSet = null;
                SteamView.this.canShake = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SteamView.this.canShake = true;
                SteamView.this.mShakeTag = TAG_SHAKE_END;
                SteamView.this.setFadeTag(0);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                SteamView.this.setFadeTag(1);
            }
        });

        this.mAnimatorSet.play(animator);
        this.mAnimatorSet.start();
    }

    private void shakeOutView() {
        if (this.mAnimatorSet != null) {
            this.mAnimatorSet.cancel();
            this.mAnimatorSet = null;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", new float[] { this.getAlpha(), 0f });
        animator.setDuration(2500);
        this.mAnimatorSet = new AnimatorSet();

        this.mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                SteamView.this.mShakeInterrupt = true;
                SteamView.this.canShake = true;
                SteamView.this.mShakeTag = TAG_SHAKE_END;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!SteamView.this.mShakeInterrupt) {
                    SteamView.this.clear();
                    SteamView.this.mHandler.sendEmptyMessageDelayed(VIEW_SHOW, SHAKE_DELAY);
                }

                SteamView.this.mViewState = STATE_FADE_NORMAL;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                SteamView.this.canShake = false;
                SteamView.this.mShakeTag = TAG_SHAKE_ING;
            }
        });

        this.mAnimatorSet.play(animator);
        this.mAnimatorSet.start();
    }

    @Override
    public void slideScreen(int direct, float scrollX, boolean needResponse) {
    }

    private void touchMove(float x, float y) {
        float deltaX = Math.abs(x - this.mOldX);
        float deltaY = Math.abs(y - this.mOldY);

        if (deltaX >= 4f || deltaY >= 4f) {
            this.mPath.quadTo(this.mOldX, this.mOldY, (this.mOldX + x) / 2f, (this.mOldY + y) / 2f);
        } else {
            this.mPath.lineTo(x, y);
        }

        this.mOldX = x;
        this.mOldY = y;
    }

    private void touchStart(float x, float y) {
        this.mOldX = x;
        this.mOldY = y;
        this.mPath.reset();
        this.mPath.moveTo(this.mOldX, this.mOldY);
        this.mCanvas.drawCircle(x, y, this.fingerSize / 2f, this.mPaintForDrawCircle);
    }

    private void touchUp(float x, float y) {
        this.mPath.lineTo(this.mOldX, this.mOldY);
        this.mCanvas.drawPath(this.mPath, this.mPaint);
        this.mCanvas.drawCircle(x, y, this.fingerSize / 2f, this.mPaintForDrawCircle);
        this.mPath.reset();
    }

    @Override
    public void updateEmptyAreaMotionEvent() {
    }

    @Override
    public void updateMovtionEvent(float x, float y, int event) {
        if (this.getAlpha() >= 1f) {
            this.updateRect(this.mRect, x, y, this.fingerSize);
            switch (event) {
            case 0:
                this.touchStart(x, y);
                this.invalidate(this.mRect);
                this.isCanDraw = true;
                break;
            case 1:
                if (this.isCanDraw) {
                    this.touchUp(x, y);
                    this.invalidate(this.mRect);
                    this.isCanDraw = false;
                }
                break;
            case 2:
                if (this.isCanDraw) {
                    this.touchMove(x, y);
                    this.invalidate(this.mRect);
                }
                break;
            }
        }
    }

    private void updateRect(Rect rect, float x, float y, float fingerSize) {
        if (x - fingerSize < ((rect.left))) {
            rect.left = (int) (x - fingerSize);
        }

        if (y - fingerSize < ((rect.top))) {
            rect.top = (int) (y - fingerSize);
        }

        if (x + fingerSize > ((rect.right))) {
            rect.right = (int) (x + fingerSize);
        }

        if (y + fingerSize > ((rect.bottom))) {
            rect.bottom = (int) (y + fingerSize);
        }
    }
}
