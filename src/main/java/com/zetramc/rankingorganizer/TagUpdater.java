package com.zetramc.rankingorganizer;

import com.zetramc.rankingorganizer.ConfigManager;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TagUpdater {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private File dataFile;
    private FileConfiguration dataConfig;
    private LuckPerms luckPerms;

    public TagUpdater(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.setupDataConfig();
        this.setupLuckPerms();
    }

    private void setupDataConfig() {
        this.dataFile = new File(this.plugin.getDataFolder(), "data.yml");
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            }
            catch (IOException e) {
                this.plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.dataFile);
    }

    private void saveDataConfig() {
        try {
            this.dataConfig.save(this.dataFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    private void setupLuckPerms() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPerms = LuckPermsProvider.get();
        } else {
            this.plugin.getLogger().severe("LuckPerms n\u00e3o encontrado! O plugin n\u00e3o funcionar\u00e1 corretamente.");
        }
    }

    public void updateTags() {
        if (this.luckPerms == null) {
            return;
        }
        this.plugin.getLogger().info("Verificando e atualizando tags...");
        this.configManager.reloadCargosConfig();
        Map<String, String> rankings = this.configManager.getRankings();
        for (Map.Entry<String, String> entry : rankings.entrySet()) {
            String placeholder = entry.getKey();
            String lpGroup = entry.getValue();
            String currentHolderUUID = this.dataConfig.getString(placeholder + ".current_uuid");
            String currentHolderName = this.dataConfig.getString(placeholder + ".current_name");
            String newPlayerName = PlaceholderAPI.setPlaceholders(null, placeholder);
            if (newPlayerName == null || newPlayerName.isEmpty() || newPlayerName.equalsIgnoreCase("none")) {
                if (currentHolderUUID == null) continue;
                this.removeGroup(UUID.fromString(currentHolderUUID), lpGroup);
                this.dataConfig.set(placeholder + ".current_uuid", null);
                this.dataConfig.set(placeholder + ".current_name", null);
                this.saveDataConfig();
                continue;
            }
            OfflinePlayer newPlayer = Bukkit.getOfflinePlayer(newPlayerName);
            if (newPlayer == null || newPlayer.getUniqueId() == null) {
                this.plugin.getLogger().warning("N\u00e3o foi poss\u00edvel encontrar o jogador para o placeholder: " + placeholder + " (Nome: " + newPlayerName + ")");
                continue;
            }
            String newPlayerUUID = newPlayer.getUniqueId().toString();
            if (!newPlayerUUID.equals(currentHolderUUID)) {
                this.plugin.getLogger().info("Mudan\u00e7a detectada para o placeholder " + placeholder + ": " + currentHolderName + " -> " + newPlayerName);
                if (currentHolderUUID != null) {
                    this.removeGroup(UUID.fromString(currentHolderUUID), lpGroup);
                }
                this.addGroup(newPlayer.getUniqueId(), lpGroup);
                this.dataConfig.set(placeholder + ".current_uuid", newPlayerUUID);
                this.dataConfig.set(placeholder + ".current_name", newPlayerName);
                this.saveDataConfig();
                continue;
            }
            this.ensureGroup(newPlayer.getUniqueId(), lpGroup);
        }
    }

    private void addGroup(UUID uuid, String group) {
        CompletableFuture<User> userFuture = this.luckPerms.getUserManager().loadUser(uuid);
        userFuture.thenAcceptAsync(user -> {
            Node node = Node.builder("group." + group).build();
            if (user.data().toCollection().stream().noneMatch(n -> n.equals(node))) {
                user.data().add((Node)node);
                this.luckPerms.getUserManager().saveUser((User)user);
                this.plugin.getLogger().info("Adicionada tag LuckPerms '" + group + "' ao jogador " + user.getUsername() + ".");
            }
        }, command -> Bukkit.getScheduler().runTask((Plugin)this.plugin, command));
    }

    private void removeGroup(UUID uuid, String group) {
        CompletableFuture<User> userFuture = this.luckPerms.getUserManager().loadUser(uuid);
        userFuture.thenAcceptAsync(user -> {
            Node node = Node.builder("group." + group).build();
            if (user.data().toCollection().stream().anyMatch(n -> n.equals(node))) {
                user.data().remove((Node)node);
                this.luckPerms.getUserManager().saveUser((User)user);
                this.plugin.getLogger().info("Removida tag LuckPerms '" + group + "' do jogador " + user.getUsername() + ".");
            }
        }, command -> Bukkit.getScheduler().runTask((Plugin)this.plugin, command));
    }

    private void ensureGroup(UUID uuid, String group) {
        CompletableFuture<User> userFuture = this.luckPerms.getUserManager().loadUser(uuid);
        userFuture.thenAcceptAsync(user -> {
            Node node = Node.builder("group." + group).build();
            if (user.data().toCollection().stream().noneMatch(n -> n.equals(node))) {
                user.data().add((Node)node);
                this.luckPerms.getUserManager().saveUser((User)user);
                this.plugin.getLogger().info("Garantida tag LuckPerms '" + group + "' para o jogador " + user.getUsername() + ".");
            }
        }, command -> Bukkit.getScheduler().runTask((Plugin)this.plugin, command));
    }
}

