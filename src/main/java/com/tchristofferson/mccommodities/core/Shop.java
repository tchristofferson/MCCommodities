package com.tchristofferson.mccommodities.core;

import com.tchristofferson.mccommodities.MCCommodities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Shop {

    private final Inventory inventory;
    //key=slot
    private final Map<Integer, List<ShopItem>> categories = new HashMap<>();
    private int nextShopId = 0;

    //TODO: Break into private methods
    public Shop(MCCommodities plugin) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection categoriesSection = config.getConfigurationSection("shop.categories");

        if (categoriesSection == null) {
            this.inventory = Bukkit.createInventory(null, 9, "Shop");
            return;
        }

        Set<String> categories = categoriesSection.getKeys(false);
        int rows = (int) Math.ceil(categories.size() / 9D);
        this.inventory = Bukkit.createInventory(null, rows * 9, "Shop");

        int slot = 0;
        for (String category : categories) {
            ConfigurationSection categorySection = categoriesSection.getConfigurationSection(category);

            String iconMaterialString = categorySection.getString("icon", "");
            Material iconMaterial = Material.getMaterial(iconMaterialString);

            if (iconMaterial == null) {
                Bukkit.getLogger().warning("Couldn't find icon material " + iconMaterialString + " for shop category! Skipping.");
                continue;
            }

            ItemStack icon = new ItemStack(iconMaterial, 1);
            ItemMeta itemMeta = icon.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', categorySection.getString("display")));
            icon.setItemMeta(itemMeta);

            inventory.setItem(slot, icon);
            List<ShopItem> shopItems;

            if (this.categories.containsKey(slot)) {
                shopItems = this.categories.get(slot);
            } else {
                shopItems = new ArrayList<>();
                this.categories.put(slot, shopItems);
            }

            ConfigurationSection itemsSection = categorySection.getConfigurationSection("items");

            if (itemsSection == null)
                continue;

            //Key is int index
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection section = itemsSection.getConfigurationSection(key);
                ItemStack item;

                if (section.isItemStack("item")) {
                    item = section.getItemStack("item");
                    item.setAmount(1);
                } else {
                    String materialString = section.getString("item");
                    Material material = Material.getMaterial(materialString);

                    if (material == null) {
                        Bukkit.getLogger().warning("Couldn't find material " + materialString + " for shop item! Skipping.");
                        continue;
                    }

                    item = new ItemStack(material, 1);
                }

                int inventory = section.getInt("inventory", -1);
                int startingInventory = section.getInt("starting-inventory", -1);
                BigDecimal price = BigDecimal.valueOf(section.getDouble("price"));
                BigDecimal startingPrice = BigDecimal.valueOf(section.getDouble("starting-price"));
                BigDecimal minPrice = BigDecimal.valueOf(section.getDouble("min-price", 0.01));
                BigDecimal maxPrice = BigDecimal.valueOf(section.getDouble("max-price", -1));
                int priceStepFactor = section.getInt("price-step-factor", 1);
                int buys = section.getInt("buys", 0);
                int sells = section.getInt("sells", 0);
                List<UUID> transactors = config.getStringList("transactors").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());

                ShopItem shopItem = new ShopItem(plugin);
                shopItem.setId(nextShopId++);
                shopItem.setStartingInventory(startingInventory);
                shopItem.setStartingPrice(startingPrice);
                shopItem.setMinPrice(minPrice);
                shopItem.setMaxPrice(maxPrice);
                shopItem.setPriceStepFactor(priceStepFactor);
                shopItem.setInventory(inventory);
                shopItem.setPrice(price);
                shopItem.setItemStack(item);
                shopItem.setBuys(buys);
                shopItem.setSells(sells);
                shopItem.setTransactors(transactors);

                shopItems.add(shopItem);
            }
        }
    }
}
