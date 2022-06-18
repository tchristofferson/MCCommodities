package com.tchristofferson.mccommodities.config;

public class MCCommoditySettings {

    private final int decimalPlaces;
    private final boolean priceRounding;
    private final double defaultPriceStep;
    private final double buySellDifference;
    private final long shopResetInterval;
    private final int uniquePlayerTransactionThreshold;

    public MCCommoditySettings(int decimalPlaces, boolean priceRounding, double defaultPriceStep,
                               double buySellDifference, long shopResetInterval, int uniquePlayerTransactionThreshold) {
        this.decimalPlaces = decimalPlaces;
        this.priceRounding = priceRounding;
        this.defaultPriceStep = defaultPriceStep;
        this.buySellDifference = buySellDifference;
        this.shopResetInterval = shopResetInterval;
        this.uniquePlayerTransactionThreshold = uniquePlayerTransactionThreshold;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public boolean isPriceRounding() {
        return priceRounding;
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
