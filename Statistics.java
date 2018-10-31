package com.patientkeeper;

import java.util.ArrayList;
import java.util.Arrays;

public class Statistics {

    double[] data;
    int size;

    public Statistics(double[] data) {
        size = data.length;
        this.data = data;
    }

    public Statistics(ArrayList<Case> cases) {
        size = cases.size();
        data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i] = cases.get(i).getAge(); // Maybe someday track other stats?
        }
    }


    double getMean() {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/size;
    }

    double getVariance() {
        double mean = getMean();
        double temp = 0;
        for(double a :data)
            temp += (a-mean)*(a-mean);
        return temp/(size-1);
    }

    double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public double median() {
        Arrays.sort(data);
        if (data.length % 2 == 0)
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        return data[data.length / 2];
    }

    public String toString() {
        String str = "Size: " + size;
        str += "\n[";
        for (int i = 0; i < size; i++) {
            str += data[i];
        }
        str += "]";
        return str;
    }
}
