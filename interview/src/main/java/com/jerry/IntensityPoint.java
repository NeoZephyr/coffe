package com.jerry;

public class IntensityPoint {

    int position;
    int value;

    public IntensityPoint(int position, int value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", position, value);
    }
}
