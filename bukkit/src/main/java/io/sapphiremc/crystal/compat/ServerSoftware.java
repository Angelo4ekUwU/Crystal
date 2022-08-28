/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.compat;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

public enum ServerSoftware {
    UNKNOWN,
    CRAFTBUKKIT,
    SPIGOT,
    PAPER,
    PURPUR,
    SAPPHIRE;

    private static final ServerSoftware SOFTWARE = checkSoftware();

    private static ServerSoftware checkSoftware() {
        String path = Bukkit.getServer().getClass().getName();

        try {
            Class.forName("io.sapphiremc.sapphire.SapphireConfig");
            return SAPPHIRE;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
            return PURPUR;
        } catch (ClassNotFoundException ex) {
            try {
                Class.forName("net.pl3x.purpur.PurpurConfig");
                return PURPUR;
            } catch (ClassNotFoundException ignored) {
            }
        }

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return PAPER;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return SPIGOT;
        } catch (ClassNotFoundException ignored) {
        }

        return Bukkit.getServer().getClass().getName().contains("craftbukkit") ? CRAFTBUKKIT : UNKNOWN;
    }

    /**
     * Current server software
     * <p>
     * Supported software: CraftBukkit, Spigot, Paper, Purpur and Sapphire
     *
     * @return current {@link ServerSoftware}
     */
    public static ServerSoftware getCurrentSoftware() {
        return SOFTWARE;
    }

    public static boolean isServer(ServerSoftware software) {
        return SOFTWARE == software;
    }

    public static boolean isServer(ServerSoftware... software) {
        return ArrayUtils.contains(software, SOFTWARE);
    }

    public boolean isCurrent() {
        return SOFTWARE == this;
    }
}
