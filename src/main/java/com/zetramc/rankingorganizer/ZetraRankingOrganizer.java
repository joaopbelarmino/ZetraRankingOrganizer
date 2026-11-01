package com.zetramc.rankingorganizer;

import com.zetramc.rankingorganizer.ConfigManager;
import com.zetramc.rankingorganizer.TagUpdater;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ZetraRankingOrganizer
extends JavaPlugin {
    private ConfigManager configManager;
    private TagUpdater tagUpdater;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        this.saveResource("cargos.yml", false);
        this.configManager = new ConfigManager(this);
        this.tagUpdater = new TagUpdater(this, this.configManager);
        long delay = 20L * (long)this.configManager.getUpdateDelaySeconds();
        new BukkitRunnable(){

            @Override
            public void run() {
                ZetraRankingOrganizer.this.tagUpdater.updateTags();
            }
        }.runTaskTimerAsynchronously(this, delay, delay);
        this.getLogger().info("ZetraRankingOrganizer habilitado!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("ZetraRankingOrganizer desabilitado!");
    }
}

