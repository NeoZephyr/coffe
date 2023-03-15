package com.pain.apple.lab.time;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtils {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter1 = DateTimeFormat.forPattern(STANDARD_FORMAT);

    public static Date strToDate(String dateTimeStr, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static Date strToDate(String dateTimeStr) {
        DateTime dateTime = dateTimeFormatter1.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date, String format) {
        if (null == date) {
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static String dateToStr(Date date) {
        if (null == date) {
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
