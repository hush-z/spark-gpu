package com.wyn.research.spark;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;

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

        String logFile = "README.md";
        int size = 10000;
        int iterations = 1000;

        SparkConf conf = new SparkConf().setAppName("Spark-test");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> logData = sc.textFile(logFile).cache();

        logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                TestKernel kernel = new TestKernel(size);
                kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
                //System.out.println("Execution mode: " + kernel.getExecutionMode());
                kernel.execute(Range.create(size), iterations);
                kernel.dispose();
                return true;
            }
        }).collect();

        System.out.println("Size: " + size);
        System.out.println("Iterations: " + iterations);
        System.out.println("Finish: " + System.currentTimeMillis());

        sc.stop();
    }
}
