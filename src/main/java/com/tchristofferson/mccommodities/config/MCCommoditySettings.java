package com.tchristofferson.mccommodities.config;

import java.math.RoundingMode;

public class MCCommoditySettings {

    private final int decimalPlaces;
    private final RoundingMode roundingMode;
    private final double defaultPriceStep;
    private final double buySellDifference;
    private final long shopResetInterval;
    private final int uniquePlayerTransactionThreshold;

    public MCCommoditySettings(int decimalPlaces, boolean priceRounding, double defaultPriceStep,
                               double buySellDifference, long shopResetInterval, int uniquePlayerTransactionThreshold) {
        this.decimalPlaces = decimalPlaces;
        this.roundingMode = priceRounding ? RoundingMode.HALF_EVEN : RoundingMode.DOWN;
        this.defaultPriceStep = defaultPriceStep;
        this.buySellDifference = buySellDifference;
        this.shopResetInterval = shopResetInterval;
        this.uniquePlayerTransactionThreshold = uniquePlayerTransactionThreshold;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public double getDefaultPriceStep() {
        return defaultPriceStep;
    }

    public double getBuySellDifference() {
        return buySellDifference;
    }

    public long getShopResetInterval() {
        return shopResetInterval;
    }

    public int getUniquePlayerTransactionThreshold() {
        return uniquePlayerTransactionThreshold;
    }
}
