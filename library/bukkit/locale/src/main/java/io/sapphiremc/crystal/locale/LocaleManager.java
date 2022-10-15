/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static io.sapphiremc.crystal.locale.ICrystalLocaleModule.LOGGER;

@SuppressWarnings("unused")
public final class LocaleManager {

    private final Pattern localePattern = Pattern.compile("([a-z]{2})_([a-z]{2}).yml");
    private final Map<String, FileConfiguration> locales = new HashMap<>();

    private ICrystalLocaleModule config;
    private String defaultLang;
    private boolean usePlayerLang;

    public void load(final ICrystalLocaleModule config) {
        this.config = config;
        this.defaultLang = config.defaultLang();
        this.usePlayerLang = config.usePlayerLang();

        LOGGER.debug("Loading locale files...");
        final var langDir = new File(config.getDataFolder() + File.separator + "lang");

        if (!langDir.exists()) {
            try {
                JarUtils.copyFolderFromJar("lang", config.getDataFolder(), JarUtils.CopyOption.COPY_IF_NOT_EXIST);
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy locales folder from plugin ", e);
            }
        }

        for (final var file : langDir.listFiles()) {
            if (!localePattern.matcher(file.getName()).matches()) {
                LOGGER.debug("Skipping file " + file.getName());
                continue;
            }

            locales.put(file.getName().replace(".yml", ""), config.getFileConfig(file));
        }

        if (!locales.containsKey(defaultLang)) {
            LOGGER.warn("The locale file " + defaultLang + ".yml does not exist in " + langDir.getPath() + " folder, using file en_us.yml");
        }

        LOGGER.warn("Successfully loaded " + locales.size() + " locales.");
    }

    @NotNull
    public Message getMessage(@NotNull final String key) {
        return getMessage(null, key);
    }

    @NotNull
    public Message getMessage(@Nullable final Player player, @NotNull final String key) {
        if (getDefaultLang().isString(key)) {
            var stringMsg = getLangFile(player).getString(key, getDefaultLang().getString(key));
            if (stringMsg == null || stringMsg.isEmpty()) {
                stringMsg = getDefaultLang().getString(key);
                if (stringMsg == null || stringMsg.isEmpty()) {
                    stringMsg = "<missing key: " + key + ">";
                }
            }

            return new Message(stringMsg);
        } else {
            final var listMsg = getLangFile(player).getStringList(key);
            if (listMsg.isEmpty()) {
                listMsg.addAll(getDefaultLang().getStringList(key));
                if (listMsg.isEmpty()) {
                    listMsg.add("<missing key: " + key + ">");
                }
            }

            return new Message(listMsg);
        }
    }

    @NotNull
    public FileConfiguration getDefaultLang() {
        return locales.get(defaultLang);
    }

    @NotNull
    private FileConfiguration getLangFile(@Nullable final Player player) {
        String langKey;
        if (player != null && usePlayerLang) {
            @SuppressWarnings("deprecation")
            final var playerLang = player.getLocale();
            if (locales.containsKey(playerLang)) {
                langKey = playerLang;
            } else {
                LOGGER.debug("Cannot find language " + playerLang + " for player " + player.getName());
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

        private Message(@NotNull final String msg) {
            this.type = MessageType.STRING;
            this.msg = msg;
        }

        private Message(@NotNull final List<String> listMsg) {
            this.type = MessageType.LIST;
            this.listMsg = listMsg;
        }

        public Message processPlaceholder(@NotNull final String placeholder, @NotNull final String replacement) {
            if (type == MessageType.LIST) {
                final var processed = new ArrayList<String>();
                for (final var s : listMsg) {
                    if (s.contains(placeholder)) {
                        processed.add(s.replace(placeholder, replacement));
                    } else {
                        processed.add(s);
                    }
                }
                this.listMsg = processed;
            } else {
                msg = msg.replace(placeholder, replacement);
            }

            return this;
        }

        public void send(@NotNull final CommandSender receiver) {
            if (type == MessageType.LIST) {
                receiver.sendMessage(asList().toArray(new String[0]));
            } else if (type == MessageType.STRING) {
                receiver.sendMessage(asString());
            }
        }

        @NotNull
        public String asString() {
            return config.stylish(msg);
        }

        @NotNull
        public List<String> asList() {
            return config.stylish(listMsg);
        }

        private enum MessageType {
            STRING, LIST
        }
    }
}
