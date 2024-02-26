package com.example.architecture.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Source {
    public int[] countRequests;
    public double[] workTime;
    public double[] waitTime;
    public double[] startTime;
    public Map<Integer, List<Double>> timeRequestsBySource;
    double deltaY = 5;
    double lm;


    public Source(int sourceNum, double lambda) {
        countRequests = new int[sourceNum];
        workTime = new double[sourceNum];
        waitTime = new double[sourceNum];
        startTime = new double[sourceNum];
        lm = lambda;

        timeRequestsBySource = new HashMap<>();
        for (int i = 0; i < sourceNum; i++) {
            timeRequestsBySource.put(i, new ArrayList<>());
        }
    }

    public double createRequest(int sourceNumber, double currentTime) {
        double requestNumber = ++countRequests[sourceNumber];
        while (requestNumber >= 1) {
            requestNumber /= 10;
        }
        startTime[sourceNumber] = currentTime;
        return (double) sourceNumber + requestNumber;
    }

    public double deltaTimePoisson() { // должен генерировать в среднем раз в 2.5 минуты
        return -1 / lm * Math.log(Math.random());// r -> [0; 1]
    }

    public int size() {
        return countRequests.length;
    }

    public void sumWorkTime(int toolNum, double time) {
        workTime[toolNum] += time;
    }

    public int getNumSource(double sourceNumber) {
        return (int) sourceNumber;
    }


    public void sumWaitTime(int toolNum, double time) {
        waitTime[toolNum] += time;
        timeRequestsBySource.get(toolNum).add(time);
    }
}
