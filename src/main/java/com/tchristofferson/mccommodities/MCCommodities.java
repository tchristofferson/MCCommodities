package com.tchristofferson.mccommodities;

import co.aikar.commands.PaperCommandManager;
import com.tchristofferson.configupdater.ConfigUpdater;
import com.tchristofferson.mccommodities.config.MCCommoditySettings;
import com.tchristofferson.mccommodities.core.Shop;
import com.tchristofferson.mccommodities.core.ShopCategoryItem;
import com.tchristofferson.mccommodities.core.ShopItem;
import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class MCCommodities extends JavaPlugin implements Listener {

    private static MCCommodities instance;

    //Auto set by Spigot
    private String spigotUser = "%%__USER__%%";
    private String pluginResourceId = "%%__RESOURCE__%%";

    private PaperCommandManager commandManager;
    private Economy economy;
    private PagedInventoryAPI pagedInventoryAPI;
    private MCCommoditySettings settings;
    private Shop shop;
    private boolean citizensEnabled = false;
    private long pluginStartTime;

    @Override
    public void onEnable() {
        instance = this;
        String pluginName = getDescription().getName();
        Bukkit.getLogger().info("Enabling " + pluginName + ". . .");
        saveDefaultConfig();
        updateConfig();
        saveResource("shop.yml", false);

        if (!setupEconomy()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found! " +
                "Make sure you have installed a compatible economy plugin.", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerConfigObjects();
        setupCommands();
        setupListeners();
        pagedInventoryAPI = new PagedInventoryAPI(this);
        shop = loadShop();
        citizensEnabled = citizensDetected();

        Bukkit.getLogger().info(pluginName + " Enabled!");
        Bukkit.getLogger().info(pluginName + " registered to Spigot user " + spigotUser);
        pluginStartTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        saveConfig();
        updateConfig();

        FileConfiguration shopSave = new YamlConfiguration();
        shopSave.set("shop", shop);

        try {
            shopSave.save(new File(getDataFolder(), "shop.yml"));
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save to shop.yml! Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPluginEnableEvent(PluginEnableEvent event) {
        if (!event.getPlugin().getName().equals("Citizens"))
            return;

        citizensEnabled = true;
    }

    public static MCCommodities getInstance() {
        return instance;
    }

    public Shop getShop() {
        return shop;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PagedInventoryAPI getPagedInventoryAPI() {
        return pagedInventoryAPI;
    }

    public boolean isCitizensEnabled() {
        return this.citizensEnabled;
    }

    public MCCommoditySettings getSettings() {
        if (settings == null)
            settings = getCurrentSettings();

        return settings;
    }

    private void registerConfigObjects() {
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(ShopCategoryItem.class);
        ConfigurationSerialization.registerClass(Shop.class);
    }

    private Shop loadShop() {
        FileConfiguration shopSave = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "shop.yml"));
        return shopSave.getObject("shop", Shop.class);
    }

    private void updateConfig() {
        try {
            ConfigUpdater.update(this, "config.yml", new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MCCommoditySettings reloadSettings() {
        reloadConfig();
        settings = getCurrentSettings();

        return settings;
    }

    @SuppressWarnings("ConstantConditions")
    private MCCommoditySettings getCurrentSettings() {
        ConfigurationSection configSettings = getConfig().getConfigurationSection("settings");

        return new MCCommoditySettings(
            configSettings.getString("money-symbol", "$").charAt(0),
            configSettings.getInt("decimal-places", 2),
            configSettings.getBoolean("price-rounding", false),
            configSettings.getDouble("default-price-step", 0.1),
            configSettings.getDouble("buy-sell-difference", 0),
            configSettings.getLong("shop-reset-interval", 1800),
            configSettings.getInt("unique-player-transaction-threshold", 3)
        );
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
