package com.tchristofferson.mccommodities.core;

import com.tchristofferson.mccommodities.MCCommodities;
import com.tchristofferson.mccommodities.config.MCCommoditySettings;
import com.tchristofferson.mccommodities.utils.Formatter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ShopItem implements ConfigurationSerializable {

    private int id;
    private int startingInventory;
    private BigDecimal startingPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private int inventory;
    private BigDecimal price;
    private ItemStack itemStack;

    //Unique players involved in buying and selling this item
    private final Set<UUID> transactors;
    private int buys = 0;
    private int sells = 0;

    public ShopItem() {
        this.transactors = new HashSet<>();
    }

    //Used for deserialization
    public ShopItem(Map<String, Object> map) {
        this.id = (int) map.get("id");
        this.startingInventory = (int) map.get("startingInventory");
        this.startingPrice = BigDecimal.valueOf((double) map.get("startingPrice"));
        this.minPrice = BigDecimal.valueOf((double) map.get("minPrice"));
        this.maxPrice = BigDecimal.valueOf((double) map.get("maxPrice"));
        this.inventory = (int) map.get("inventory");
        this.price = BigDecimal.valueOf((double) map.get("price"));
        this.itemStack = (ItemStack) map.get("itemStack");

        //noinspection unchecked
        List<String> transactorUuidStringList = (List<String>) map.get("transactors");
        this.transactors = transactorUuidStringList.stream()
            .map(UUID::fromString)
            .collect(Collectors.toCollection(HashSet::new));

        this.buys = (int) map.get("buys");
        this.sells = (int) map.get("sells");
    }

    public int adjustPrice() {
        MCCommoditySettings settings = MCCommodities.getInstance().getSettings();
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

    public void setTransactors(List<UUID> transactors) {
        this.transactors.clear();
        this.transactors.addAll(transactors);
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

    public void setBuys(int buys) {
        this.buys = buys;
    }

    public void setSells(int sells) {
        this.sells = sells;
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
        return id == shopItem.id && startingInventory == shopItem.startingInventory && inventory == shopItem.inventory && Objects.equals(startingPrice, shopItem.startingPrice) && Objects.equals(minPrice, shopItem.minPrice) && Objects.equals(maxPrice, shopItem.maxPrice) && Objects.equals(price, shopItem.price) && Objects.equals(itemStack, shopItem.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startingInventory, startingPrice, minPrice, maxPrice, inventory, price, itemStack);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", id);
        map.put("startingInventory", startingInventory);
        map.put("startingPrice", startingPrice.doubleValue());
        map.put("minPrice", minPrice.doubleValue());
        map.put("maxPrice", maxPrice.doubleValue());
        map.put("inventory", inventory);
        map.put("price", price.doubleValue());
        map.put("itemStack", itemStack);
        map.put("transactors", transactors.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("buys", buys);
        map.put("sells", sells);

        return map;
    }
}
