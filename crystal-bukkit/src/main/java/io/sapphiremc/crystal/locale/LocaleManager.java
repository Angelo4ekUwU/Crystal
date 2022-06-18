/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.CrystalPlugin;
import io.sapphiremc.crystal.util.JarUtils;
import io.sapphiremc.crystal.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class LocaleManager {

    private final CrystalPlugin plugin;
    private final Pattern localePattern = Pattern.compile("([a-z]{2})_([a-z]{2}).yml");
    private final Map<String, FileConfiguration> locales = new HashMap<>();

    private String defaultLang;
    private boolean usePlayerLang;

    public void load(ConfigurationSection config) {
        this.defaultLang = config.getString("default", "en_us");
        this.usePlayerLang = config.getBoolean("use-player-lang", false);

        plugin.logDebug("Loading locale files...");
        File langDir = new File(plugin.getDataFolder() + File.separator + "lang");

        if (!langDir.exists()) {
            try {
                JarUtils.copyFolderFromJar("lang", plugin.getDataFolder(), JarUtils.CopyOption.COPY_IF_NOT_EXIST);
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy locales folder from plugin ", e);
            }
        }

        for (File file : langDir.listFiles()) {
            if (!localePattern.matcher(file.getName()).matches()) {
                plugin.logDebug("Skipping file " + file.getName());
                continue;
            }

            locales.put(file.getName().replace(".yml", ""), plugin.getConfigManager().getConfig(file));
        }

        if (!locales.keySet().contains(defaultLang)) {
            plugin.logWarn("The locale file " + defaultLang + ".yml does not exist in " + langDir.getPath() + " folder, using file en_us.yml");
        }

        plugin.logDebug("Successfully loaded " + locales.size() + " locales.");
    }

    @NotNull
    public Message getMessage(@NotNull String key) {
        return getMessage(null, key);
    }

    @NotNull
    public Message getMessage(@Nullable Player player, @NotNull String key) {
        if (getDefaultLang().isString(key)) {
            String stringMsg = getLangFile(player).getString(key, getDefaultLang().getString(key));
            if (stringMsg == null || stringMsg.isEmpty()) {
                stringMsg = getDefaultLang().getString(key);
                if (stringMsg == null || stringMsg.isEmpty()) {
                    stringMsg = "&c<missing key: " + key + ">";
                }
            }

            return new Message(stringMsg);
        } else {
            List<String> listMsg = getLangFile(player).getStringList(key);
            if (listMsg.isEmpty()) {
                listMsg = getDefaultLang().getStringList(key);
                if (listMsg.isEmpty()) {
                    listMsg = List.of("&c<missing key: " + key + ">");
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
    private FileConfiguration getLangFile(@Nullable Player player) {
        String langKey;
        if (player != null && usePlayerLang) {
            @SuppressWarnings("deprecation")
            String playerLang = player.getLocale();
            if (locales.containsKey(playerLang)) {
                langKey = playerLang;
            } else {
                plugin.logDebug("Cannot find language " + playerLang + " for player " + player.getName());
                langKey = defaultLang;
            }
        } else {
            langKey = defaultLang;
        }

        return locales.get(langKey);
    }

    public static class Message {
        private final MessageType type;
        private String msg;
        private List<String> listMsg;

        private Message(String msg) {
            this.type = MessageType.STRING;
            this.msg = msg;
        }

        private Message(List<String> listMsg) {
            this.type = MessageType.LIST;
            this.listMsg = listMsg;
        }

        public Message processPlaceholder(String placeholder, String replacement) {
            if (type == MessageType.LIST) {
                List<String> processed = new ArrayList<>();
                for (String s : listMsg) {
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

        public void send(CommandSender receiver) {
            if (type == MessageType.LIST) {
                receiver.sendMessage(asList().toArray(new String[0]));
            } else if (type == MessageType.STRING) {
                receiver.sendMessage(asString());
            }
        }

        public String asString() {
            return TextUtils.stylish(msg);
        }

        public List<String> asList() {
            return TextUtils.stylish(listMsg);
        }

        private enum MessageType {
            STRING, LIST
        }
    }
}
