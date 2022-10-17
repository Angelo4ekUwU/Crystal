package io.sapphiremc.crystal.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class CrystalPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getSLF4JLogger().info("This is a plugin library!");
    }
}
