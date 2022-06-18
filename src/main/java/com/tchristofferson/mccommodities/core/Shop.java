package com.tchristofferson.mccommodities.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop {

    private final Inventory inventory;

    public Shop(FileConfiguration config) {
        this.inventory = Bukkit.createInventory(null, 54, "Shop");

        ConfigurationSection categoriesSection = config.getConfigurationSection("shop.categories");

        if (categoriesSection == null)
            return;

        for (String category : categoriesSection.getKeys(false)) {
            ConfigurationSection categorySection = categoriesSection.getConfigurationSection(category);

            String materialString = categorySection.getString("icon", "");
            Material iconMaterial = Material.getMaterial(materialString);

            if (iconMaterial == null) {
                Bukkit.getLogger().warning("Couldn't find icon material " + materialString + " for shop category! Skipping.");
                continue;
            }

            ItemStack icon = new ItemStack(iconMaterial, 1);
            ItemMeta itemMeta = icon.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', categorySection.getString("display")));
            icon.setItemMeta(itemMeta);

            inventory.addItem(icon);
        }
    }
}
