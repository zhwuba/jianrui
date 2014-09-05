package com.dutils.math;

public class MathUtil {

    public MathUtil() {
        super();
    }

    public static boolean isPowerOf2(int n) {
        boolean isPowerOf2;
        if (n <= 0 || (n - 1 & n) != 0) {
            isPowerOf2 = false;
        } else {
            isPowerOf2 = true;
        }

        return isPowerOf2;
    }

    public static int midPointIterator(int i) {
        int result;
        if (i != 0) {
            result = (i - 1) / 2 + 1;
            if ((i - 1) % 2 != 0) {
                result = -result;
            }
        } else {
            result = 0;
        }

        return result;
    }

    public static int nextPowerOf2(int n) {
        --n;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        return (n | n >>> 16) + 1;
    }

    public static int prevPowerOf2(int n) {
        if (!MathUtil.isPowerOf2(n)) {
            n = MathUtil.nextPowerOf2(n) / 2;
        }

        return n;
    }
}
