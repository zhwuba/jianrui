package com.dutils.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.dutils.math.Vector3f;

public class IBufferFactory {

    public static void fillBuffer(FloatBuffer fb, Vector3f v) {
        fb.put(v.x);
        fb.put(v.y);
        fb.put(v.z);
    }

    public static void fillBuffer(FloatBuffer fb, Vector3f v, int limit) {
        fb.put(v.x);
        fb.put(1f - v.y);
        if (limit != 2) {
            fb.put(v.z);
        }
    }

    public static void fillBuffer(ShortBuffer sb, int[] data) {
        for (int i = 0; i < data.length; ++i) {
            sb.put(((short) data[i]));
        }
    }

    public static ByteBuffer newByteBuffer(int numElements) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(numElements);
        buffer.order(ByteOrder.nativeOrder());
        buffer.position(0);
        return buffer;
    }

    public static FloatBuffer newFloatBuffer(int numElements) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(numElements * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static IntBuffer newIntBuffer(int numElements) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(numElements * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.position(0);
        return intBuffer;
    }

    public static ShortBuffer newShortBuffer(int numElements) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(numElements * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.position(0);
        return shortBuffer;
    }

    public static void read(FloatBuffer fb, Vector3f v) {
        v.x = fb.get();
        v.y = fb.get();
        v.z = fb.get();
    }
}
