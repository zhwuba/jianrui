package com.dutils.bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class BitmapUtil {

    public static Bitmap decodeFile(String fileName) {
        return BitmapFactory.decodeFile(fileName);
    }

    public static void encodeFile(Bitmap bitmap, String fileName) {
        BufferedOutputStream outputStream = null;

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(new File(
                    Environment.getExternalStorageDirectory(), fileName)));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, (outputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap filterColor(Bitmap src, int ignoreColor, int filterColor, int destColor) {
        int mask = 16777215;

        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] pixelBuffer = new int[width * height];
        src.getPixels(pixelBuffer, 0, width, 0, 0, width, height);

        int length = width * height;
        for (int i = 0; i < length; ++i) {
            if ((pixelBuffer[i] & mask) == ignoreColor || (pixelBuffer[i] & mask) < filterColor) {
                pixelBuffer[i] = 0;
            } else {
                pixelBuffer[i] = destColor;
            }
        }

        bitmap.setPixels(pixelBuffer, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap reverseAlphaAdnColor(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] pixelBuffer = new int[width * height];
        src.getPixels(pixelBuffer, 0, width, 0, 0, width, height);

        int length = width * height;
        ;
        for (int i = 0; i < length; ++i) {
            int v8 = pixelBuffer[i] >> 24 & 255;
            pixelBuffer[i] = (pixelBuffer[i] >> 16 & 255) << 24 & -16777216 | v8 << 16 & 16711680 | v8 << 8 & 65280
                    | v8 << 0 & 255;
        }

        bitmap.setPixels(pixelBuffer, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
