/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.compatibility;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public enum ServerSoftware {
    UNKNOWN,
    CRAFTBUKKIT,
    SPIGOT,
    PAPER,
    AIRPLANE,
    TUINITY,
    PUFFERFISH,
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
            Class.forName("gg.pufferfish.pufferfish.PufferfishConfig");
            return PUFFERFISH;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("gg.airplane.AirplaneConfig");
            return AIRPLANE;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("com.tuinity.tuinity.config.TuinityConfig");
            return TUINITY;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("io.papermc.paper.configuration.PaperConfigurations");
            return PAPER;
        } catch (ClassNotFoundException ex) {
            try {
                Class.forName("com.destroystokyo.paper.PaperConfig");
                return PAPER;
            } catch (ClassNotFoundException ignored) {
            }
        }

        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return SPIGOT;
        } catch (ClassNotFoundException ignored) {
        }

        if (Bukkit.getServer().getClass().getName().contains("craftbukkit")) {
            return CRAFTBUKKIT;
        } else {
            LoggerFactory.getLogger("Crystal").error("Couldn't detect server type " + Bukkit.getServer().getName());
            return UNKNOWN;
        }
    }

    /**
     * @return Friendly name of this software
     */
    public String getFriendlyName() {
        return StringUtils.capitalize(this.name());
    }

    /**
     * Current server software
     * <p>
     * Supported software: CraftBukkit, Spigot, Paper, Airplane, Pufferfish, Purpur and Sapphire
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
