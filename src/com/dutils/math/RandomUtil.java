package com.dutils.math;

import java.util.Random;

public class RandomUtil {

    private int mEnd;
    private static Random mRandom;
    private int[] mSequence;
    private int mTotal;

    static {
        RandomUtil.mRandom = new Random();
    }

    public static boolean flipCoin() {
        boolean result;

        if (RandomUtil.mRandom.nextFloat() < 0.5f) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public static float floatRange(float min, float max) {
        return (max - min) * RandomUtil.mRandom.nextFloat() + min;
    }

    public static int[] getRandomSequence(int total) {
        int[] sequence = new int[total];
        int[] output = new int[total];

        RandomUtil.initSequence(sequence);
        Random random = new Random();
        int end = total - 1;

        for (int i = 0; i < total; ++i) {
            int num = random.nextInt(end + 1);
            output[i] = sequence[num];
            sequence[num] = sequence[end];
            --end;
        }

        return output;
    }

    private static void initSequence(int[] sequence) {
        int total = sequence.length;

        for (int i = 0; i < total; ++i) {
            sequence[i] = i;
        }
    }

    public static int intRange(int min, int max) {
        if (max - min != 0) {
            min += RandomUtil.mRandom.nextInt(max - min);
        }

        return min;
    }

    public int nextIntSequence() {
        if (this.mEnd < 0) {
            this.reset(this.mTotal);
        }

        int num = RandomUtil.mRandom.nextInt(this.mEnd + 1);
        int result = this.mSequence[num];
        this.mSequence[num] = this.mSequence[this.mEnd];
        --this.mEnd;
        return result;
    }

    public static boolean percent(float p) {
        boolean result;

        if (RandomUtil.mRandom.nextFloat() < p) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public void reset(int total) {
        this.mTotal = total;
        this.mEnd = total - 1;
        if (this.mSequence == null || this.mSequence.length != total) {
            this.mSequence = new int[total];
        }

        RandomUtil.initSequence(this.mSequence);
    }
}
