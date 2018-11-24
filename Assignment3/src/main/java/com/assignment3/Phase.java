package com.assignment3;

public class Phase {

    private String name;
    private int start;
    private int end;

    private int threadNumber;

    public Phase(String name, int start, int end, int threadNumber) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.threadNumber = threadNumber;
    }

    public String getName() {
        return name;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getThreadNumber() {
        return threadNumber;
    }
}
