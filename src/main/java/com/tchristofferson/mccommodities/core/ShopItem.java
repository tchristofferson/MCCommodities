package com.tchristofferson.mccommodities.core;

import com.tchristofferson.mccommodities.MCCommodities;
import com.tchristofferson.mccommodities.config.MCCommoditySettings;
import com.tchristofferson.mccommodities.utils.Formatter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ShopItem {

    private final MCCommodities plugin;

    private int id;
    private int startingInventory;
    private BigDecimal startingPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int priceStepFactor;

    private int inventory;
    private BigDecimal price;
    private ItemStack itemStack;

    //Unique players involved in buying and selling this item
    private final Set<UUID> transactors = new HashSet<>();
    private int buys = 0;
    private int sells = 0;

    public ShopItem(MCCommodities plugin) {
        this.plugin = plugin;
    }

    public int adjustPrice() {
        MCCommoditySettings settings = plugin.getSettings();
        int uniqueTransactionThreshold = settings.getUniquePlayerTransactionThreshold();

        if (transactors.size() < uniqueTransactionThreshold) {
            resetStats();
            return 0;
        }

        final BigDecimal oldPrice = price;
        BigDecimal priceStep = BigDecimal.valueOf(settings.getDefaultPriceStep());
        BigDecimal priceChange = priceStep.multiply(price);

        if (buys > sells) {
            price = price.add(priceChange);
        } else if (sells > buys) {
            price = price.subtract(priceChange);
        } else {
            resetStats();
            return 0;
        }

        if (price.compareTo(minPrice) < 0) {
            price = minPrice;
        } else if (price.compareTo(maxPrice) > 0) {
            price = maxPrice;
        }

        resetStats();
        price = Formatter.format(price, settings);

        return price.compareTo(oldPrice);
    }

    public int getBuys() {
        return buys;
    }

    public int getSells() {
        return sells;
    }

    public Set<UUID> getTransactors() {
        return transactors;
    }

    public void addTransactor(Player player, int inventoryChange) {
        transactors.add(player.getUniqueId());

        if (inventoryChange < 0) {
            sells += inventoryChange;
        } else {
            buys += inventoryChange;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartingInventory() {
        return startingInventory;
    }

    public void setStartingInventory(int startingInventory) {
        this.startingInventory = startingInventory;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getPriceStepFactor() {
        return priceStepFactor;
    }

    public void setPriceStepFactor(int priceStepFactor) {
        this.priceStepFactor = priceStepFactor;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private void resetStats() {
        buys = 0;
        sells = 0;
        transactors.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopItem shopItem = (ShopItem) o;
        return id == shopItem.id && startingInventory == shopItem.startingInventory && priceStepFactor == shopItem.priceStepFactor && inventory == shopItem.inventory && Objects.equals(startingPrice, shopItem.startingPrice) && Objects.equals(minPrice, shopItem.minPrice) && Objects.equals(maxPrice, shopItem.maxPrice) && Objects.equals(price, shopItem.price) && Objects.equals(itemStack, shopItem.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startingInventory, startingPrice, minPrice, maxPrice, priceStepFactor, inventory, price, itemStack);
    }
}
