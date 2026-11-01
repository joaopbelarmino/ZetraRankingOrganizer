package com.zetramc.rankingorganizer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration cargosConfig;
    private File cargosFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.setupCargosConfig();
    }

    private void setupCargosConfig() {
        this.cargosFile = new File(this.plugin.getDataFolder(), "cargos.yml");
        if (!this.cargosFile.exists()) {
            this.plugin.saveResource("cargos.yml", false);
        }
        this.cargosConfig = YamlConfiguration.loadConfiguration(this.cargosFile);
    }

    public FileConfiguration getCargosConfig() {
        return this.cargosConfig;
    }

    public void reloadCargosConfig() {
        this.cargosConfig = YamlConfiguration.loadConfiguration(this.cargosFile);
    }

    public int getUpdateDelaySeconds() {
        return this.plugin.getConfig().getInt("update-delay-seconds", 60);
    }

    public Map<String, String> getRankings() {
        HashMap<String, String> rankings = new HashMap<String, String>();
        Set<String> keys = this.cargosConfig.getKeys(false);
        for (String key : keys) {
            String placeholder = this.cargosConfig.getString(key + ".placeholder");
            String tag = this.cargosConfig.getString(key + ".tag");
            if (placeholder == null || tag == null) continue;
            rankings.put(placeholder, tag);
        }
        return rankings;
    }
}

