package com.wyn.research.spark;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;

public class Pure {
    public static void main(String[] _args) {
        System.out.println("Start: " + System.currentTimeMillis());

        String logFile = "README.md";
        int size = 100000;
        int iterations = 10000;

        SparkConf conf = new SparkConf().setAppName("Spark-test");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> logData = sc.textFile(logFile).cache();

        logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) {
                float [] values = new float[size];
                float [] squared = new float[size];

                for (int i = 0; i < size; i++) {
                    values[i] = i;
                }

                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < size; j++) {
                        squared[j] = values[j] * values[j];
                    }
                }

                return true;
            }
        }).collect();

        System.out.println("Size: " + size);
        System.out.println("Iterations: " + iterations);
        System.out.println("Finish: " + System.currentTimeMillis());

        sc.stop();
    }
}
