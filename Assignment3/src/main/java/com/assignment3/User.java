package com.assignment3;

public class User {


    private int id;
    private int time;
    private int stepCount;
    private int dayNumber;

    public User(int id, int time, int stepCount, int dayNumber) {
        this.id = id;
        this.time = time;
        this.stepCount = stepCount;
        this.dayNumber = dayNumber;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getDayNumber() {
        return dayNumber;
    }
}
