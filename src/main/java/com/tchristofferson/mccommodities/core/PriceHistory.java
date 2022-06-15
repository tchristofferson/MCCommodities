package com.tchristofferson.mccommodities.core;

import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

public class PriceHistory {

    private final ItemStack itemStack;
    private Map<Period, BigDecimal> priceHistory;

    public PriceHistory(ItemStack itemStack) {
        itemStack.setAmount(1);
        this.itemStack = itemStack;
        priceHistory = new HashMap<>();
    }
}
