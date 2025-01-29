package com.jerry;

import java.util.ArrayList;
import java.util.List;

public class IntensitySegments {

    private List<IntensityPoint> data = new ArrayList<>();

    public void add(int from, int to, int amount) {
        if (amount == 0) {
            return;
        }

        List<IntensityPoint> newData = new ArrayList<>();
        int i = 0;

        // from 之前的 segment 保持不变
        while (i < data.size() && data.get(i).position < from) {
            newData.add(data.get(i++));
        }

        // from 出现在尾部
        if (i == data.size()) {
            append(newData, from, amount);
            append(newData, to, 0);
            data = newData;
            return;
        }

        // 处理 from point
        if (data.get(i).position == from) {
            int newValue = amount + data.get(i++).value;
            append(newData, from, newValue);
        } else {
            int newValue = newData.isEmpty() ? amount : amount + data.get(i - 1).value;
            append(newData, from, newValue);
        }

        // to 之前的 segment 增加 amount
        while (i < data.size() && data.get(i).position < to) {
            IntensityPoint point = data.get(i++);
            append(newData, point.position, point.value + amount);
        }

        // to 出现在尾部
        if (i == data.size()) {
            append(newData, to, 0);
            data = newData;
            return;
        }

        // 处理 to 阶段
        if (data.get(i).position == to) {
            append(newData, data.get(i++));
        } else {
            append(newData, to, data.get(i - 1).value);
        }

        // to 之后的 segment 保持不变
        while (i < data.size()) {
            append(newData, data.get(i++));
        }

        data = newData;
    }

    public void set(int from, int to, int amount) {
        List<IntensityPoint> newData = new ArrayList<>();
        int i = 0;

        // from 之前的 segment 保持不变
        while (i < data.size() && data.get(i).position < from) {
            newData.add(data.get(i++));
        }

        // from 出现在尾部
        if (i == data.size()) {
            append(newData, from, amount);
            append(newData, to, 0);
            data = newData;
            return;
        }

        // 处理 from point
        if (data.get(i).position == from) {
            append(newData, from, amount);
            i++;
        } else {
            append(newData, from, amount);
        }

        // to 之前的 segment 增加 amount
        while (i < data.size() && data.get(i).position < to) {
            IntensityPoint point = data.get(i++);
            append(newData, point.position, amount);
        }

        // to 出现在尾部
        if (i == data.size()) {
            append(newData, to, 0);
            data = newData;
            return;
        }

        // 处理 to 阶段
        if (data.get(i).position == to) {
            append(newData, data.get(i++));
        } else {
            append(newData, to, data.get(i - 1).value);
        }

        // to 之后的 segment 保持不变
        while (i < data.size()) {
            append(newData, data.get(i++));
        }

        data = newData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (IntensityPoint point : data) {
            sb.append(point.toString());
            sb.append(',');
        }

        if (!data.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(']');
        return sb.toString();
    }

    private void append(List<IntensityPoint> data, int position, int value) {
        append(data, new IntensityPoint(position, value));
    }

    private void append(List<IntensityPoint> data, IntensityPoint point) {
        int size = data.size();

        if (size == 0 && point.value == 0) {
            return;
        }

        if (size != 0 && data.get(size - 1).value == point.value) {
            return;
        }

        data.add(point);
    }
}
