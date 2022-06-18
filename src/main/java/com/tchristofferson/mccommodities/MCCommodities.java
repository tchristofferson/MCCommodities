package com.tchristofferson.mccommodities;

import co.aikar.commands.PaperCommandManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MCCommodities extends JavaPlugin implements Listener {

    private PaperCommandManager commandManager;
    private Economy economy;
    private boolean citizensEnabled = false;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found! " +
                "Make sure you have installed a compatible economy plugin.", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupCommands();
        setupListeners();

        if (citizensDetected())
            citizensEnabled = true;
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPluginEnabled(PluginEnableEvent event) {
        if (!event.getPlugin().getName().equals("Citizens"))
            return;

        citizensEnabled = true;
    }

    public boolean isCitizensEnabled() {
        return this.citizensEnabled;
    }

    private boolean citizensDetected() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Citizens");
        return plugin != null && plugin.isEnabled();
    }

    private void setupListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null)
            return false;

        economy = rsp.getProvider();
        return economy != null;
    }
}
