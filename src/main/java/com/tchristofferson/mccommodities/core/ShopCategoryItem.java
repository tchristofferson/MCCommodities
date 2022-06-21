package com.tchristofferson.mccommodities.core;

import com.tchristofferson.mccommodities.MCCommodities;
import com.tchristofferson.mccommodities.config.MCCommoditySettings;
import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.PageModifier;
import com.tchristofferson.pagedinventories.navigationitems.CloseNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NextNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.PreviousNavigationItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ShopCategoryItem implements ConfigurationSerializable {

    private int id;
    private final IPagedInventory pagedInventory;
    private final ItemStack itemStack;
    private final List<ShopItem> shopItems;

    public ShopCategoryItem(int id, ItemStack itemStack, List<ShopItem> shopItems) {
        this.id = id;
        this.pagedInventory = createPagedInventory();
        this.itemStack = itemStack;
        this.shopItems = new ArrayList<>(shopItems);
    }

    //Used for deserialization
    public ShopCategoryItem(Map<String, Object> map) {
        MCCommoditySettings settings = MCCommodities.getInstance().getSettings();
        this.id = (int) map.get("id");
        this.pagedInventory = createPagedInventory();
        this.itemStack = (ItemStack) map.get("itemStack");
        //noinspection unchecked
        this.shopItems = (List<ShopItem>) map.get("shopItems");

        int nextSlot = 0;
        int pageIndex = 0;

        for (ShopItem shopItem : this.shopItems) {
            Bukkit.getLogger().info("nextSlot: " + nextSlot);
            Bukkit.getLogger().info("PageIndex: " + pageIndex);
            Bukkit.getLogger().info("PagedInventory size: " + pagedInventory.getSize());
            if (pageIndex + 1 > pagedInventory.getSize())
                pagedInventory.addPage(Bukkit.createInventory(null, 54, "Shop - " + this.itemStack.getItemMeta().getDisplayName()));

            Bukkit.getLogger().info("PagedInventory size: " + pagedInventory.getSize());
            PageModifier pageModifier = pagedInventory.getPageModifier(pageIndex);
            ItemStack itemStack = shopItem.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new LinkedList<>();
            lore.add("Buy: " + ChatColor.GREEN + ChatColor.BOLD + settings.getMoneySymbol() + shopItem.getBuyPrice());
            lore.add("Sell: " + ChatColor.RED + ChatColor.BOLD + settings.getMoneySymbol() + shopItem.getSellPrice());

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            pageModifier.setItem(nextSlot++, itemStack);

            if (nextSlot >= 45) {
                nextSlot = 0;
                pageIndex++;
            }
        }
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

    public void addShopItem(ShopItem shopItem) {

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

    private IPagedInventory createPagedInventory() {
        return MCCommodities.getInstance()
            .getPagedInventoryAPI()
            .createPagedInventory(getNavigationRow());
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
}
