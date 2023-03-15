package com.pain.apple.lab.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CopyUtils {

    public static <T> T copy(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

        T destination = null;

        try {
            destination = clazz.newInstance();
        } catch (Exception e) {
            log.warn("copy failed", e);
            return null;
        }

        BeanUtils.copyProperties(source, destination);
        return destination;
    }

    public static <T> List<T> copy(List sourceList, Class<T> clazz) {
        List<T> destinationList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(sourceList)) {
            for (Object source : sourceList) {
                T destination = copy(source, clazz);
                destinationList.add(destination);
            }
        }

        return destinationList;
    }
}
