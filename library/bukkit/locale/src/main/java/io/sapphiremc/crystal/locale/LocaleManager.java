/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.configurate.CrystalConfiguration;
import io.sapphiremc.crystal.utils.JarUtils;
import io.sapphiremc.crystal.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
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
        this.localePattern = Pattern.compile("([a-z]{2})_([a-z]{2})." + loaderType.getFormat());
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

        if (localeDir.listFiles() == null || localeDir.listFiles().length < 1) {
            plugin.getSLF4JLogger().warn("Locales not found");
            return;
        }

        for (final var file : Objects.requireNonNull(localeDir.listFiles())) {
            if (!localePattern.matcher(file.getName()).matches()) {
                plugin.getSLF4JLogger().debug("Skipping file " + file.getName());
                continue;
            }

            final var loader = loaderType.getLoader(file.toPath());

            try {
                locales.put(file.getName().replace(".conf", ""), loader.load());
            } catch (ConfigurateException ex) {
                throw new RuntimeException("Failed to load locale " + file.getName(), ex);
            }
        }

        if (!locales.containsKey(defaultLang)) {
            plugin.getSLF4JLogger().warn("The locale file " + defaultLang + ".conf does not exist in " + localeDir.getPath() + " folder, using file en_us.conf");
        }

        plugin.getSLF4JLogger().warn("Successfully loaded " + locales.size() + " locales.");
    }

    @NotNull
    public Message getMessage(@NotNull final String key) {
        return getMessage(null, key);
    }

    @NotNull
    public Message getMessage(@Nullable final Player player, @NotNull final String key) {
        try {
            if (getDefaultLocale().node(key).isList()) {
                final List<String> listMsg;
                listMsg = getLangFile(player).node(key).getList(String.class, Collections.emptyList());
                if (listMsg.isEmpty()) {
                    listMsg.addAll(getDefaultLocale().node(key).getList(String.class, Collections.singletonList("<missing key: " + key + ">")));
                }

                return new Message(listMsg);
            } else {
                var stringMsg = getLangFile(player).node(key).getString(getDefaultLocale().node(key).getString("<missing key: " + key + ">"));
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

    public static final class Message {
        private final boolean isString;
        private String msg;
        private List<String> listMsg;
        private List<Pair> pairs;

        private Message(@NotNull final String msg) {
            this.isString = true;
            this.msg = msg;
        }

        private Message(@NotNull final List<String> listMsg) {
            this.isString = false;
            this.listMsg = listMsg;
        }

        /**
         * Adds placeholders for this message
         *
         * @param placeholders Array of placeholders
         * @return this message
         */
        public Message placeholders(final Pair... placeholders) {
            Collections.addAll(pairs, placeholders);
            return this;
        }

        /**
         * Send message to target {@link CommandSender}
         *
         * @param receiver target
         */
        public void send(@NotNull final CommandSender receiver) {
            prepareMessage();
            if (this.isString) {
                receiver.sendMessage(asString());
            } else {
                receiver.sendMessage(asList().toArray(new String[0]));
            }
        }

        /**
         * @return Formatted string
         */
        @NotNull
        public String asString() {
            prepareMessage();
            return msg;
        }

        /**
         * @return Formatted list
         */
        @NotNull
        public List<String> asList() {
            prepareMessage();
            return listMsg;
        }

        /**
         * Replace all placeholders and process colors and gradients.
         */
        private void prepareMessage() {
            if (isString) {
                for (final var pair : pairs) {
                    msg = msg.replace(pair.key(), pair.value());
                }
                msg = TextUtils.stylish(msg);
            } else {
                this.listMsg = listMsg.stream().map(s -> {
                    for (final var pair : pairs) {
                        s = s.replace(pair.key(), pair.value());
                    }
                    return TextUtils.stylish(s);
                }).toList();
            }
        }
    }

    public record Pair(String key, Object obj) {

        public static Pair of(final String key, final Object value) {
            return new Pair(key, value);
        }

        @Override
        public String key() {
            return "%" + key + "%";
        }

        public String value() {
            return obj.toString();
        }
    }

    public enum LoaderType {
        HOCON("conf"),
        YAML("yml"),
        JSON("json");

        private final String format;

        LoaderType(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public ConfigurationLoader<? extends ConfigurationNode> getLoader(final Path path) {
            return switch (this) {
                case HOCON -> CrystalConfiguration.hoconLoader(path);
                case YAML -> CrystalConfiguration.yamlLoader(path);
                case JSON -> CrystalConfiguration.gsonLoader(path);
            };
        }
    }
}
