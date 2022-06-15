package com.tchristofferson.mccommodities;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MCCommodities extends JavaPlugin {

    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        setupCommands();
    }

    @Override
    public void onDisable() {

    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);
    }
}
