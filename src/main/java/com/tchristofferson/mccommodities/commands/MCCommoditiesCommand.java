package com.tchristofferson.mccommodities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.tchristofferson.mccommodities.MCCommodities;
import org.bukkit.entity.Player;

@CommandAlias("mccommodities|mcc")
public class MCCommoditiesCommand extends BaseCommand {

    @Default
    @CommandPermission(MCCommoditiesPermissions.USE_PERMISSION)
    public void onMcCommodities(Player player) {
        player.openInventory(MCCommodities.getInstance().getShop().getInventory());
    }

}
