package com.tchristofferson.mccommodities.utils;

import com.tchristofferson.mccommodities.config.MCCommoditySettings;

import java.math.BigDecimal;

public class Formatter {

    public static BigDecimal format(BigDecimal bigDecimal, MCCommoditySettings settings) {
        return bigDecimal.setScale(settings.getDecimalPlaces(), settings.getRoundingMode());
    }

}
