/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.utils.JarUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "ConstantConditions"})
public final class LocaleManager {

    private final Plugin plugin;
    private final LoaderType loaderType;
    private final Pattern localePattern;
    private final Map<String, ConfigurationNode> locales = new HashMap<>();

    private String defaultLang;
    private boolean usePlayerLang;

    public LocaleManager(final Plugin plugin, final LoaderType loaderType) {
        this.plugin = plugin;
        this.loaderType = loaderType;
        this.localePattern = Pattern.compile("([a-z]{2})_([a-z]{2})" + loaderType.getExtension());
    }

    public void load(final String defaultLang, final boolean usePlayerLang) {
        this.defaultLang = defaultLang;
        this.usePlayerLang = usePlayerLang;

        plugin.getSLF4JLogger().debug("Loading locale files...");
        final var localeDir = new File(plugin.getDataFolder() + File.separator + "locale");

        if (!localeDir.exists()) {
            try {
                JarUtils.copyFolderFromJar("locale", plugin.getDataFolder(), JarUtils.CopyOption.COPY_IF_NOT_EXIST);
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy locales folder from plugin ", e);
            }
        }

        if (!localeDir.exists() || !localeDir.isDirectory()) {
            plugin.getSLF4JLogger().warn("Failed to copy locales from plugin");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        if (localeDir.listFiles() == null && localeDir.listFiles().length < 1) {
            plugin.getSLF4JLogger().warn("Locales not found");
            return;
        }

        for (final var file : localeDir.listFiles()) {
            if (file == null) continue;
            if (!localePattern.matcher(file.getName()).matches()) {
                plugin.getSLF4JLogger().info("Skipping file " + file.getName());
                continue;
            }

            final var loader = loaderType.getLoader(file.toPath());

            try {
                locales.put(file.getName().replace(loaderType.getExtension(), ""), loader.load());
            } catch (ConfigurateException ex) {
                throw new RuntimeException("Failed to load locale " + file.getName(), ex);
            }
        }

        if (!locales.containsKey(defaultLang)) {
            plugin.getSLF4JLogger().warn("The locale file " + defaultLang + loaderType.getExtension() + " does not exist in " + localeDir.getPath() + " folder, try using en_us" + loaderType.getExtension());
        }

        plugin.getSLF4JLogger().warn("Successfully loaded " + locales.size() + " locales.");
    }

    @NotNull
    public Message getMessage(@NotNull final String... path) {
        return getMessage(null, path);
    }

    @NotNull
    public Message getMessage(@Nullable final Player player, @NotNull final String... path) {
        try {
            final var finalPath = (Object[]) (path.length == 1 ? path[0].split("\\.") : path);
            if (getDefaultLocale().node(finalPath).isList()) {
                final List<String> listMsg;
                listMsg = getLangFile(player).node(finalPath).getList(String.class, Collections.emptyList());
                if (listMsg.isEmpty()) {
                    listMsg.addAll(getDefaultLocale().node(finalPath).getList(String.class, Collections.singletonList("<missing path: " + Arrays.toString(finalPath) + ">")));
                }

                return new Message(listMsg);
            } else {
                var stringMsg = getLangFile(player).node(finalPath).getString(getDefaultLocale().node(finalPath).getString("<missing path: " + Arrays.toString(finalPath) + ">"));
                return new Message(stringMsg);
            }
        } catch (SerializationException ex) {
            throw new RuntimeException("Failed to get message", ex);
        }
    }

    @NotNull
    public ConfigurationNode getDefaultLocale() {
        return locales.get(defaultLang);
    }

    @NotNull
    private ConfigurationNode getLangFile(@Nullable final Player player) {
        String langKey;
        if (player != null && usePlayerLang) {
            @SuppressWarnings("deprecation") final var playerLang = player.getLocale();
            if (locales.containsKey(playerLang)) {
                langKey = playerLang;
            } else {
                plugin.getSLF4JLogger().debug("Cannot find language " + playerLang + " for player " + player.getName());
                langKey = defaultLang;
            }
        } else {
            langKey = defaultLang;
        }

        return locales.get(langKey);
    }
}
