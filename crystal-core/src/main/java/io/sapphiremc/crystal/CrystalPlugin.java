/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal;

import io.sapphiremc.crystal.config.ConfigManager;
import io.sapphiremc.crystal.locale.LocaleManager;
import io.sapphiremc.crystal.sql.DatabaseConnector;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CrystalPlugin extends JavaPlugin {

    @NotNull
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    @NotNull
    private final ConfigManager configManager = new ConfigManager(this);
    @NotNull
    private final LocaleManager localeManager = new LocaleManager(this);

    @NotNull
    private final DatabaseConnector databaseConnector = new DatabaseConnector(this);

    private FileConfiguration config;

    @Override
    public void onLoad() {
        try {
            if (!getDataFolder().exists() && !getDataFolder().mkdirs()) return;
            onPluginLoad();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            onPluginEnable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            onPluginDisable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Log debug message, if debug is enabled
     *
     * @param s message
     * @param args arguments
     */
    public void logDebug(@NotNull String s, @Nullable Object... args) {
        if (getConfig().getBoolean("debug", false)) getSLF4JLogger().info("DEBUG: " + s, args);
    }

    /**
     * Log info message
     *
     * @param s message
     * @param args arguments
     */
    public void logInfo(@NotNull String s, @Nullable Object... args) {
        getSLF4JLogger().info(s, args);
    }

    /**
     * Log warning message
     *
     * @param s message
     * @param args arguments
     */
    public void logWarn(@NotNull String s, @Nullable Object... args) {
        getSLF4JLogger().warn(s, args);
    }

    /**
     * Log error message with throwable.
     *
     * @param s message
     * @param t throwable
     */
    public void logError(@NotNull String s, @NotNull Throwable t) {
        getSLF4JLogger().error(s, t);
    }

    /**
     * Log error message
     *
     * @param s message
     * @param args arguments
     */
    public void logError(@NotNull String s, @Nullable Object... args) {
        getSLF4JLogger().error(s, args);
    }

    /**
     * Called when the plugin is loaded.
     */
    protected void onPluginLoad() {}

    /**
     * Called when the plugin is enabled.
     */
    protected void onPluginEnable() {}

    /**
     * Called after reloading the main config (config.yml).
     */
    protected void onConfigReload() {}

    /**
     * Called when the plugin is disabled.
     */
    protected void onPluginDisable() {}

    /**
     * Returns Bukkit's console command sender.
     *
     * @return console command sender
     * @see ConsoleCommandSender
     */
    @NotNull
    public ConsoleCommandSender getConsole() {
        return console;
    }

    /**
     * Reloads the configuration and calls
     * {@link CrystalPlugin#onConfigReload()} method.
     */
    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        onConfigReload();
    }

    /**
     * Turns off this plugin and unloads it from server memory.
     */
    public void disable() {
        getPluginManager().disablePlugin(this);
    }

    /**
     * Saves the default config to the plugin folder.
     */
    @Override
    public void saveDefaultConfig() {
        this.config = configManager.getConfig("config.yml");
    }

    /**
     * Returns the configuration file (config.yml) from plugin folder
     *
     * @return configuration file of this plugin
     * @see FileConfiguration
     */
    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    /**
     * Returns Configuration manager instance
     *
     * @return configuration manager
     * @see ConfigManager
     */
    @NotNull
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Returns localization manager instance
     *
     * @return localization manager
     * @see LocaleManager
     */
    @NotNull
    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    @NotNull
    public DatabaseConnector loadDatabaseConnector(ConfigurationSection config) {
        databaseConnector.load(config);
        return databaseConnector;
    }

    /**
     * Returns Bukkit's Plugin Manager instance
     *
     * @return Bukkit's plugin manager
     * @see PluginManager
     */
    @NotNull
    public PluginManager getPluginManager() {
        return getServer().getPluginManager();
    }

    /**
     * Register the listener for this plugin
     *
     * @param listener listener to register
     */
    public void registerListener(@NotNull Listener listener) {
        getPluginManager().registerEvents(listener, this);
    }

    /**
     * Check if command is not null and set executor for it.
     *
     * @param cmd command
     * @param executor executor for command
     */
    public void setCommandExecutor(@NotNull String cmd, @NotNull CommandExecutor executor) {
        PluginCommand command = getCommand(cmd);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            getSLF4JLogger().error("Command " + cmd + " not found in plugin " + getName());
        }
    }

    /**
     * Check if command is not null and set tab completer for it.
     *
     * @param cmd command
     * @param tabCompleter tab completer for command
     */
    public void setCommandTabCompleter(@NotNull String cmd, @NotNull TabCompleter tabCompleter) {
        PluginCommand command = getCommand(cmd);
        if (command != null) {
            command.setTabCompleter(tabCompleter);
        } else {
            getSLF4JLogger().error("Command " + cmd + " not found in plugin " + getName());
        }
    }
}
