package com.tchristofferson.mccommodities.listeners;

import com.tchristofferson.mccommodities.MCCommodities;
import com.tchristofferson.mccommodities.core.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener  implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || !inventory.equals(MCCommodities.getInstance().getShop().getInventory()))
            return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player))
            return;

        Shop shop = MCCommodities.getInstance().getShop();
        int clickedSlot = event.getSlot();

        Bukkit.getScheduler().runTask(MCCommodities.getInstance(), () -> {
            if (!shop.hasCategory(clickedSlot))
                return;

            shop.getCategory(clickedSlot).openItemInventory(((Player) event.getWhoClicked()).getPlayer());
        });
    }

}
