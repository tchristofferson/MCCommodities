package com.tchristofferson.mccommodities.utils;

public class NumberUtil {

    public static Double toDouble(Object obj) {
        return ((Number) obj).doubleValue();
    }

    public static Long toLong(Object obj) {
        return ((Number) obj).longValue();
    }

}
