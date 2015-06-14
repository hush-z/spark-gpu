package com.wyn.research.squared;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class CPU {
    public static class TestKernel extends Kernel {
        // values
        private float values[];
        // squared values holder
        private float squared[];

        public TestKernel(int size) {
            values = new float[size];
            squared = new float[size];

            for (int i = 0; i < size; i++) {
                values[i] = i;
            }
        }

        @Override public void run() {
            int gid = getGlobalId();
            squared[gid] = values[gid] * values[gid];
        }
    }

    public static void main(String[] _args) {
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
