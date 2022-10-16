/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.configurate.CrystalConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class LocaleManager {

    private final Pattern localePattern = Pattern.compile("([a-z]{2})_([a-z]{2}).conf");
    private final Map<String, CommentedConfigurationNode> locales = new HashMap<>();

    private ILocaleConfig config;
    private String defaultLang;
    private boolean usePlayerLang;

    public void load(final ILocaleConfig config) {
        this.config = config;
        this.defaultLang = config.defaultLang();
        this.usePlayerLang = config.usePlayerLang();

        config.getLogger().debug("Loading locale files...");
        final var localesDir = new File(config.getDataFolder() + File.separator + "locales");

        if (!localesDir.exists()) {
            try {
                JarUtils.copyFolderFromJar("locales", config.getDataFolder(), JarUtils.CopyOption.COPY_IF_NOT_EXIST);
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy locales folder from plugin ", e);
            }
        }

        for (final var file : Objects.requireNonNull(localesDir.listFiles())) {
            final var loader = CrystalConfiguration.hoconLoader(file.toPath());
            if (!localePattern.matcher(file.getName()).matches()) {
                config.getLogger().debug("Skipping file " + file.getName());
                continue;
            }

            try {
                locales.put(file.getName().replace(".conf", ""), loader.load());
            } catch (ConfigurateException ex) {
                throw new RuntimeException("Failed to load locale " + file.getName(), ex);
            }
        }

        if (!locales.containsKey(defaultLang)) {
            config.getLogger().warn("The locale file " + defaultLang + ".conf does not exist in " + localesDir.getPath() + " folder, using file en_us.conf");
        }

        config.getLogger().warn("Successfully loaded " + locales.size() + " locales.");
    }

    @NotNull
    public Message getMessage(@NotNull final String key) {
        return getMessage(null, key);
    }

    @NotNull
    public Message getMessage(@Nullable final Player player, @NotNull final String key) {
        try {
            if (getDefaultLang().node(key).isList()) {
                final List<String> listMsg;
                listMsg = getLangFile(player).node(key).getList(String.class, Collections.emptyList());
                if (listMsg.isEmpty()) {
                    listMsg.addAll(getDefaultLang().node(key).getList(String.class, Collections.singletonList("<missing key: " + key + ">")));
                }

                return new Message(listMsg);
            } else {
                var stringMsg = getLangFile(player).node(key).getString(getDefaultLang().node(key).getString("<missing key: " + key + ">"));
                return new Message(stringMsg);
            }
        } catch (SerializationException ex) {
            throw new RuntimeException("Failed to get message", ex);
        }
    }

    @NotNull
    public CommentedConfigurationNode getDefaultLang() {
        return locales.get(defaultLang);
    }

    @NotNull
    private CommentedConfigurationNode getLangFile(@Nullable final Player player) {
        String langKey;
        if (player != null && usePlayerLang) {
            @SuppressWarnings("deprecation") final var playerLang = player.getLocale();
            if (locales.containsKey(playerLang)) {
                langKey = playerLang;
            } else {
                config.getLogger().debug("Cannot find language " + playerLang + " for player " + player.getName());
                langKey = defaultLang;
            }
        } else {
            langKey = defaultLang;
        }

        return locales.get(langKey);
    }

    public final class Message {
        private final MessageType type;
        private String msg;
        private List<String> listMsg;
        private List<Pair> pairs;

        private Message(@NotNull final String msg) {
            this.type = MessageType.STRING;
            this.msg = msg;
        }

        private Message(@NotNull final List<String> listMsg) {
            this.type = MessageType.LIST;
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
            if (type == MessageType.LIST) {
                receiver.sendMessage(asList().toArray(new String[0]));
            } else if (type == MessageType.STRING) {
                receiver.sendMessage(asString());
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

        private void prepareMessage() {
            if (type == MessageType.LIST) {
                this.listMsg = listMsg.stream().map(s -> {
                    for (final var pair : pairs) {
                        s = s.replace(pair.key(), pair.value());
                    }
                    return s;
                }).toList();
            } else {
                for (final var pair : pairs) {
                    msg = msg.replace(pair.key(), pair.value());
                }
                msg = config.stylish(msg);
            }
        }

        private enum MessageType {
            STRING, LIST
        }
    }
}
