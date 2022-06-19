package com.tchristofferson.mccommodities.core;

import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.navigationitems.CloseNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NextNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.PreviousNavigationItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

public class Shop implements ConfigurationSerializable {

    private final Inventory inventory;
    //key=slot
    private final Map<Integer, ShopCategoryItem> categories;

    public Shop() {
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "MCCommodities");
        this.categories = new TreeMap<>();
    }

    public Shop(Map<String, Object> map) {
        this();
        //noinspection unchecked
        this.categories.putAll((Map<Integer, ShopCategoryItem>) map.get("categories"));

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

    @SuppressWarnings("ConstantConditions")
    private NavigationRow getNavigationRow() {
        ItemStack nextButton = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta nextSkullMeta = (SkullMeta) nextButton.getItemMeta();
        //MHF_ArrowRight
        nextSkullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("50c8510b-5ea0-4d60-be9a-7d542d6cd156")));
        nextSkullMeta.setDisplayName(ChatColor.GRAY + "Next Page -->");
        nextButton.setItemMeta(nextSkullMeta);
        NextNavigationItem nextNavigationItem = new NextNavigationItem(nextButton);

        ItemStack previousButton = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta previousSkullMeta = (SkullMeta) previousButton.getItemMeta();
        //MHF_ArrowLeft
        previousSkullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("a68f0b64-8d14-4000-a95f-4b9ba14f8df9")));
        previousSkullMeta.setDisplayName(ChatColor.GRAY + "<-- Previous Page");
        previousButton.setItemMeta(previousSkullMeta);
        PreviousNavigationItem previousNavigationItem = new PreviousNavigationItem(previousButton);

        ItemStack closeButton = new ItemStack(Material.BARRIER, 1);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        closeButton.setItemMeta(closeMeta);
        CloseNavigationItem closeNavigationItem = new CloseNavigationItem(closeButton);

        return new NavigationRow(nextNavigationItem, previousNavigationItem, closeNavigationItem);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("categories", categories);

        return map;
    }
}
