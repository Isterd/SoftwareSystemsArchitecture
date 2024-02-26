package com.example.architecture.utils;

import lombok.Getter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Device {
    public Map<Integer, List<Double>> timeRequestsByDevice;
    double[] array;
    public double[] workTime;
    double deltaY = 5;
    double alpha;
    double beta;


    public Device(int deviceNum, int sourceNum, double alpha, double beta) {
        array = new double[deviceNum];
        workTime = new double[deviceNum];
        this.alpha = alpha;
        this.beta = beta;
        timeRequestsByDevice = new HashMap<>();
        for (int i = 0; i < sourceNum; i++) {
            timeRequestsByDevice.put(i, new ArrayList<>());
        }
    }

    public boolean hasEmpty() {
        return findFirstEmptyIndex() != null;
    }

    public boolean isEmpty() {
        for (double d : array) {
            if (d != 0) {
                return false;
            }
        }
        return true;
    }

    public Integer findFirstEmptyIndex() {
        Integer index = null;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int addToDevice(double request) {
        Integer emptyIndex;                                                 //целая часть - номер источника,
        if ((emptyIndex = findFirstEmptyIndex()) != null) {                 //дробная - номер заявки (пакета)
            array[emptyIndex] = request;
        } else {
            return -1;
        }
        return emptyIndex;
    }

    public double flush(int deviceNumber) {
        double req = array[deviceNumber];
        array[deviceNumber] = 0;
        return req;
    }


    public double calculateDeltaTimeEvenly() {
        return Math.random() * (beta - alpha) + alpha;
    }

    public int size() {
        return array.length;
    }

    public void sumWorkTime(int devNum, int srcNum, double time) {
        workTime[devNum] += time;
        timeRequestsByDevice.get(srcNum).add(time);
    }
}
