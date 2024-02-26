package com.example.architecture.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TimeAction implements Comparable<TimeAction> {
    public Double time;
    public Integer action;
    public int toolNum;

    @Override
    public int compareTo(TimeAction o) {
        if (this.time.compareTo(o.time) == 0) {
            return -this.action.compareTo(o.action);
        }
        return this.time.compareTo(o.time);
    }
}