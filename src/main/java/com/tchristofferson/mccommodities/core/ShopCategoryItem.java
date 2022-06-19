package com.tchristofferson.mccommodities.core;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopCategoryItem implements ConfigurationSerializable {

    private int id;
    private final ItemStack itemStack;
    private final List<ShopItem> shopItems;

    public ShopCategoryItem(int id, ItemStack itemStack, List<ShopItem> shopItems) {
        this.id = id;
        this.itemStack = itemStack;
        this.shopItems = new ArrayList<>(shopItems);
    }

    //Used for deserialization
    public ShopCategoryItem(Map<String, Object> map) {
        this.id = (int) map.get("id");
        this.itemStack = (ItemStack) map.get("itemStack");
        //noinspection unchecked
        this.shopItems = (List<ShopItem>) map.get("shopItems");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", id);
        map.put("itemStack", itemStack);
        map.put("shopItems", shopItems);

        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopCategoryItem that = (ShopCategoryItem) o;
        return id == that.id && Objects.equals(itemStack, that.itemStack) && Objects.equals(shopItems, that.shopItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemStack, shopItems);
    }
}
