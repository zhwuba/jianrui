package com.wb.launcher3.weatherIcon;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import com.wb.launcher3.LauncherAppState;
import com.wb.launcher3.R;

public class IconSnowEffect {
    private Integer LOCK;
    private static final int SNOWFLAKE_TYPES = 6;
    private static int SNOWTHICKNESS = 0;
    private static final int SNOW_THICKNESS_MAX = 20;

    private static Context mContext;
    private Random mRandom;
    private static float mScreenDensity;
    private Bitmap mSnowDownBitmap;
    private Bitmap mSnowUpBitmap;
    private Bitmap[] mSnowflake;

    public IconSnowEffect() {
        this.mSnowflake = new Bitmap[SNOWFLAKE_TYPES];
        this.mSnowUpBitmap = null;
        this.mSnowDownBitmap = null;
        this.mRandom = new Random();
        this.LOCK = Integer.valueOf(0);

        Resources resource = IconSnowEffect.mContext.getResources();
        IconSnowEffect.mScreenDensity = LauncherAppState.getInstance().getScreenDensity();

        int snowThickness = IconSnowEffect.mScreenDensity < 3 ? 10 : SNOW_THICKNESS_MAX;
        IconSnowEffect.SNOWTHICKNESS = snowThickness;

        this.mSnowflake[0] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_1)).getBitmap();
        this.mSnowflake[1] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_2)).getBitmap();
        this.mSnowflake[2] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_3)).getBitmap();
        this.mSnowflake[3] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_4)).getBitmap();
        this.mSnowflake[4] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_5)).getBitmap();
        this.mSnowflake[5] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_6)).getBitmap();
    }

    public IconSnowEffect(Bitmap iconBitmap) {
        this.mSnowflake = new Bitmap[SNOWFLAKE_TYPES];
        this.mSnowUpBitmap = null;
        this.mSnowDownBitmap = null;
        this.mRandom = new Random();
        this.LOCK = Integer.valueOf(0);

        Resources resource = IconSnowEffect.mContext.getResources();
        IconSnowEffect.mScreenDensity = LauncherAppState.getInstance().getScreenDensity();

        int snowThickness = IconSnowEffect.mScreenDensity < 3 ? 10 : SNOW_THICKNESS_MAX;
        IconSnowEffect.SNOWTHICKNESS = snowThickness;

        this.mSnowflake[0] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_1)).getBitmap();
        this.mSnowflake[1] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_2)).getBitmap();
        this.mSnowflake[2] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_3)).getBitmap();
        this.mSnowflake[3] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_4)).getBitmap();
        this.mSnowflake[4] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_5)).getBitmap();
        this.mSnowflake[5] = ((BitmapDrawable) resource.getDrawable(R.drawable.snowflake_6)).getBitmap();
    }

    public void makeSnowBitmap(Bitmap iconBitmap) {
        int snowThickness = IconSnowEffect.SNOWTHICKNESS;
        Random random = this.mRandom;
        int startHeight = 0;
        int endHeight = 0;

        int width = iconBitmap.getWidth();
        int height = iconBitmap.getHeight();

        int[] edgeHeight = new int[width];
        int i;

        for (i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (iconBitmap.getPixel(i, j) != 0) {
                    if (startHeight == 0) {
                        startHeight = i;
                    } else {
                        endHeight = i;
                    }

                    edgeHeight[i] = j;
                    break;
                }
            }
        }

        this.mSnowUpBitmap = Bitmap.createBitmap(width, height, iconBitmap.getConfig());
        this.mSnowDownBitmap = Bitmap.createBitmap(width, height, iconBitmap.getConfig());

        Canvas upCanvas = new Canvas(this.mSnowUpBitmap);
        Canvas downCanvas = new Canvas(this.mSnowDownBitmap);

        int index = 1;
        int distance = endHeight - startHeight;
        boolean flag = random.nextBoolean();

        while (index < distance) {
            int snowFlakeIndex = random.nextInt(6);
            Bitmap snowFlakeBitmap = this.mSnowflake[snowFlakeIndex].copy(this.mSnowflake[snowFlakeIndex].getConfig(),
                    true);
            int offerY = ((int) (Math.random() * ((snowThickness))));
            int w = this.mSnowflake[snowFlakeIndex].getWidth();
            int h = this.mSnowflake[snowFlakeIndex].getHeight();

            i = flag ? index + startHeight : endHeight - index;

            if (distance - index < w) {
                int randomWidth = random.nextInt(distance - index);
                Rect src = new Rect(0, 0, randomWidth, h);
                Rect dst = new Rect();

                if (flag) {
                    dst.set(i, edgeHeight[i] + offerY, i + randomWidth, edgeHeight[i] + offerY + src.bottom);
                } else {
                    dst.set(i - randomWidth, edgeHeight[i] + offerY, i, edgeHeight[i] + offerY + src.bottom);
                }

                upCanvas.drawBitmap(snowFlakeBitmap, src, dst, null);
                downCanvas.drawBitmap(snowFlakeBitmap, src, dst, null);
                return;
            }

            upCanvas.drawBitmap(snowFlakeBitmap, flag ? i : i - w, (edgeHeight[i] + offerY * 2), null);
            downCanvas.drawBitmap(snowFlakeBitmap, flag ? i : i - w, (edgeHeight[i] + offerY), null);

            if (random.nextBoolean()) {
                continue;
            }

            index += random.nextInt(2) + 1;
        }
    }

    public void resetThickness() {
        synchronized (this.LOCK) {
            if (this.mSnowUpBitmap != null) {
                this.mSnowUpBitmap.recycle();
                this.mSnowUpBitmap = null;
            }
            if (this.mSnowDownBitmap != null) {
                this.mSnowDownBitmap.recycle();
                this.mSnowDownBitmap = null;
            }
            return;
        }
    }

    public static void setContext(Context context) {
        IconSnowEffect.mContext = context;
    }

    public Bitmap updateSnow(Bitmap iconBitmap, int upThickness, int upAlpha, int downAlpha) {
        Paint paint;
        Bitmap bitmap = null;

        if (iconBitmap != null) {
            synchronized (this.LOCK) {
                if (upThickness <= 0) {
                    try {
                        bitmap = iconBitmap.copy(iconBitmap.getConfig(), true);
                        return bitmap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (this.mSnowUpBitmap == null || this.mSnowDownBitmap == null) {
                        this.makeSnowBitmap(iconBitmap);
                    }

                    paint = new Paint();

                    if (upThickness > IconSnowEffect.SNOWTHICKNESS) {
                        upThickness = IconSnowEffect.SNOWTHICKNESS;
                    }

                    if (upAlpha >= 256) {
                        upAlpha = 255;
                    }

                    if (downAlpha >= 256) {
                        downAlpha = 255;
                    }

                    try {
                        bitmap = Bitmap.createBitmap(iconBitmap.getWidth(), iconBitmap.getHeight() + upThickness,
                                iconBitmap.getConfig());
                        Canvas canvas = new Canvas(bitmap);
                        paint.setAlpha(upAlpha);
                        canvas.drawBitmap(this.mSnowUpBitmap, 0f, 0f, paint);
                        canvas.drawBitmap(iconBitmap, 0f, upThickness, null);
                        paint.setAlpha(downAlpha);
                        canvas.drawBitmap(this.mSnowDownBitmap, 0f, upThickness - 6, paint);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return bitmap;
    }

    public void updateSnow(Canvas canvas, Bitmap iconBitmap, int x, int y, Paint paint, int upThickness, int upAlpha,
            int downAlpha) {
        Paint newPaint;

        if (iconBitmap != null) {
            synchronized (this.LOCK) {
                if (upThickness <= 0) {
                    float left = x;
                    float top = y;

                    try {
                        canvas.drawBitmap(iconBitmap, left, top, paint);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (this.mSnowUpBitmap == null || this.mSnowDownBitmap == null) {
                        this.makeSnowBitmap(iconBitmap);
                    }

                    if (upThickness > IconSnowEffect.SNOWTHICKNESS) {
                        upThickness = IconSnowEffect.SNOWTHICKNESS;
                    }

                    newPaint = new Paint();
                    if (upAlpha >= 256) {
                        upAlpha = 255;
                    }

                    if (downAlpha >= 256) {
                        downAlpha = 255;
                    }

                    try {
                        newPaint.setAlpha(upAlpha);
                        canvas.drawBitmap(this.mSnowUpBitmap, x, y - upThickness, newPaint);
                        canvas.drawBitmap(iconBitmap, x, y, paint);
                        newPaint.setAlpha(downAlpha);
                        canvas.drawBitmap(this.mSnowDownBitmap, x, y - 6, newPaint);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
