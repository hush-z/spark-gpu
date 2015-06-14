package com.wyn.research.blacksholes;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class CPU {
    public static class TestKernel extends Kernel {

        final float S_LOWER_LIMIT = 10.0f;
        final float S_UPPER_LIMIT = 100.0f;
        final float K_LOWER_LIMIT = 10.0f;
        final float K_UPPER_LIMIT = 100.0f;
        final float T_LOWER_LIMIT = 1.0f;
        final float T_UPPER_LIMIT = 10.0f;
        final float R_LOWER_LIMIT = 0.01f;
        final float R_UPPER_LIMIT = 0.05f;
        final float SIGMA_LOWER_LIMIT = 0.01f;
        final float SIGMA_UPPER_LIMIT = 0.10f;

        float phi(float X) {
            final float c1 = 0.319381530f;
            final float c2 = -0.356563782f;
            final float c3 = 1.781477937f;
            final float c4 = -1.821255978f;
            final float c5 = 1.330274429f;

            final float zero = 0.0f;
            final float one = 1.0f;
            final float two = 2.0f;
            final float temp4 = 0.2316419f;

            final float oneBySqrt2pi = 0.398942280f;

            float absX = abs(X);
            float t = one / (one + temp4 * absX);

            float y = one - oneBySqrt2pi * exp(-X * X / two) * t * (c1 + t * (c2 + t * (c3 + t * (c4 + t * c5))));

            float result = (X < zero) ? (one - y) : y;

            return result;
        }

        @Override public void run() {
            float d1, d2;
            float phiD1, phiD2;
            float sigmaSqrtT;
            float KexpMinusRT;

            int gid = getGlobalId();
            float two = 2.0f;
            float inRand = randArray[gid];
            float S = S_LOWER_LIMIT * inRand + S_UPPER_LIMIT * (1.0f - inRand);
            float K = K_LOWER_LIMIT * inRand + K_UPPER_LIMIT * (1.0f - inRand);
            float T = T_LOWER_LIMIT * inRand + T_UPPER_LIMIT * (1.0f - inRand);
            float R = R_LOWER_LIMIT * inRand + R_UPPER_LIMIT * (1.0f - inRand);
            float sigmaVal = SIGMA_LOWER_LIMIT * inRand + SIGMA_UPPER_LIMIT * (1.0f - inRand);

            sigmaSqrtT = sigmaVal * sqrt(T);

            d1 = (log(S / K) + (R + sigmaVal * sigmaVal / two) * T) / sigmaSqrtT;
            d2 = d1 - sigmaSqrtT;

            KexpMinusRT = K * exp(-R * T);

            phiD1 = phi(d1);
            phiD2 = phi(d2);

            call[gid] = S * phiD1 - KexpMinusRT * phiD2;

            phiD1 = phi(-d1);
            phiD2 = phi(-d2);

            put[gid] = KexpMinusRT * phiD2 - S * phiD1;
        }

        private float randArray[];

        private float put[];

        private float call[];

        public TestKernel(int size) {
            randArray = new float[size];
            call = new float[size];
            put = new float[size];

            for (int i = 0; i < size; i++) {
                randArray[i] = i * 1.0f / size;
            }
        }

        public void showArray(float ary[], String name, int count) {
            String line;
            line = name + ": ";
            for (int i = 0; i < count; i++) {
                if (i > 0)
                    line += ", ";
                line += ary[i];
            }
            System.out.println(line);
        }

        public void showResults(int count) {
            showArray(call, "Call Prices", count);
            showArray(put, "Put  Prices", count);
        }
    }

    public static void main(String[] _args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Start: " + System.currentTimeMillis());

        int size = Const.SIZE;
        int iterations = Const.ITERATIONS;

        Range range = Range.create(size);
        System.out.println("Size: " + size);
        System.out.println("Iterations: " + iterations);
        TestKernel kernel = new TestKernel(size);

        kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);

        kernel.execute(range, iterations);
        System.out.println("Average execution time: " + kernel.getAccumulatedExecutionTime() / iterations);
        System.out.println("Execution mode: " + kernel.getExecutionMode());
        //kernel.showResults(10);

        System.out.println("Finish: " + System.currentTimeMillis());

        kernel.dispose();
    }
}
