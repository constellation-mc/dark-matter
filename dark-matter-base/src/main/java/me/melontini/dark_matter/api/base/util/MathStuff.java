package me.melontini.dark_matter.api.base.util;

import java.util.Random;

public class MathStuff {
    private MathStuff() {
        throw new UnsupportedOperationException();
    }
    //"doesn't MathHelper already add this?" - yes, but it uses Mogang's random, and I don't like it.
    public static double nextDouble(Random random, double min, double max) {
        return min >= max ? min : random.nextDouble() * (max - min) + min;
    }

    public static int nextInt(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    public static long round(double value) {
        long x = (long)value;
        if ((value - x) >= 0.5d) {
            return x + 1L;
        } else {
            return (value - x) < -0.5d ? x - 1L : x;
        }
    }

    public static int round(float value) {
        int x = (int)value;
        if ((value - x) >= 0.5f) {
            return x + 1;
        } else {
            return (value - x) < -0.5f ? x - 1 : x;
        }
    }

    public static int fastCeil(double value) {
        return (int) (value - 1024.0) + 1024;
    }

    public static int fastFloor(double value) {
        return (int) (value + 1024.0) - 1024;
    }

    public static double fastSqrt(double x, int numIterations) {
        return inverseSqrt(x, numIterations) * x;
    }

    public static double fastSqrt(double x) {
        return inverseSqrt(x) * x;
    }

    public static float fastSqrt(float x, int numIterations) {
        return inverseSqrt(x, numIterations) * x;
    }

    public static float fastSqrt(float x) {
        return inverseSqrt(x) * x;
    }

    public static double inverseSqrt(double x, int numIterations) {
        MakeSure.isFalse(x < 0, "Negative numbers cannot have a square root.");

        double xhalf = 0.5 * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);

        for (int j = 0; j < numIterations; j++) {
            x = x * (1.5 - xhalf * x * x);
        }

        return x;
    }

    public static double inverseSqrt(double x) {
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        return x * (1.5 - (0.5 * x) * x * x);
    }


    public static float inverseSqrt(float x, int numIterations) {
        MakeSure.isFalse(x < 0, "Negative numbers cannot have a square root.");

        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);

        for (int j = 0; j < numIterations; j++) {
            x = x * (1.5f - xhalf * x * x);
        }

        return x;
    }

    public static float inverseSqrt(float x) {
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        return x * (1.5f - (0.5f * x) * x * x);
    }
}
