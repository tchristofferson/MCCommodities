package com.tchristofferson.mccommodities.core;

import com.tchristofferson.mccommodities.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.*;

public class Shop implements ConfigurationSerializable {

    private final Inventory inventory;
    //key=slot
    private final Map<Integer, ShopCategoryItem> categories;
    private BigDecimal balance = BigDecimal.valueOf(-1);

    public Shop() {
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "MCCommodities");
        this.categories = new TreeMap<>();
    }

    public Shop(Map<String, Object> map) {
        this();
        //noinspection unchecked
        this.categories.putAll((Map<Integer, ShopCategoryItem>) map.get("categories"));
        this.balance = BigDecimal.valueOf(NumberUtil.toDouble(map.get("balance")));

        this.categories.forEach((slot, shopCategoryItem) -> {
            if (slot < 54) {
                this.inventory.setItem(slot, shopCategoryItem.getItemStack());
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public void addCategory(ItemStack itemStack, String displayName) {
        int slot = this.inventory.firstEmpty();

        if (slot == -1)
            throw new IllegalStateException("The inventory has no more slots available!");

        ItemStack clone = itemStack.clone();
        clone.setAmount(1);
        ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        clone.setItemMeta(itemMeta);

        this.inventory.setItem(slot, clone);
    }

    //Removed category and re-assigns ids and closes empty space in inventory
    public void removeCategory(int id) {
        this.categories.remove(id);

        List<ShopCategoryItem> shopCategoryItems = new LinkedList<>(this.categories.values());
        this.categories.clear();
        this.inventory.clear();

        shopCategoryItems.forEach(shopCategoryItem -> {
            int slot = this.inventory.firstEmpty();
            shopCategoryItem.setId(slot);
            this.categories.put(slot, shopCategoryItem);
        });
    }

    public List<ShopCategoryItem> getCategories() {
        return new ArrayList<>(this.categories.values());
    }

    public ShopCategoryItem getCategory(int id) {
        return this.categories.get(id);
    }

    public boolean hasCategory(int id) {
        return this.categories.containsKey(id);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("categories", categories);
        map.put("balance", balance.doubleValue());

        return map;
    }
}
