/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("ConstantConditions")
public final class LocaleManager {

    private final Logger logger;
    private final File dataFolder;
    private final LoaderType loaderType;
    private final MessageSender sender;
    private final Pattern localePattern;
    private final Map<String, ConfigurationNode> locales = new HashMap<>();

    private String defaultLocale;

    public LocaleManager(Logger logger, File dataFolder, LoaderType loaderType, MessageSender sender) {
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.loaderType = loaderType;
        this.sender = sender;
        this.localePattern = Pattern.compile("([a-z]{2})_([a-z]{2})" + loaderType.getExtension());
    }

    public void load(String defaultLocale) {
        this.defaultLocale = defaultLocale;

        logger.debug("Loading locale files...");
        final File localeDir = new File(dataFolder + File.separator + "locale");

        if (!localeDir.exists()) {
            try {
                JarUtils.copyFolderFromJar("locale", dataFolder, JarUtils.CopyOption.COPY_IF_NOT_EXIST);
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy locales folder from plugin ", e);
            }
        }

        if (!localeDir.exists() || !localeDir.isDirectory()) {
            logger.warn("Failed to copy locales from plugin");
            return;
        }

        if (localeDir.listFiles() == null && localeDir.listFiles().length < 1) {
            logger.warn("Locales not found");
            return;
        }

        for (final File file : localeDir.listFiles()) {
            if (file == null) continue;
            if (!localePattern.matcher(file.getName()).matches()) {
                logger.info("Skipping file " + file.getName());
                continue;
            }

            final var loader = loaderType.getLoader(file.toPath());

            try {
                locales.put(file.getName().replace(loaderType.getExtension(), ""), loader.load());
            } catch (ConfigurateException ex) {
                throw new RuntimeException("Failed to load locale " + file.getName(), ex);
            }
        }

        if (!locales.containsKey(defaultLocale)) {
            logger.warn("The locale file " + defaultLocale + loaderType.getExtension() + " does not exist in " + localeDir.getPath() + " folder, try using en_us" + loaderType.getExtension());
        }

        logger.warn("Successfully loaded " + locales.size() + " locales.");
    }

    @NotNull
    public Message getMessage(@NotNull String... path) {
        return getMessage(null, path);
    }

    @NotNull
    public Message getMessage(@Nullable String locale, @NotNull String... path) {
        try {
            final Object[] finalPath = path.length == 1 ? path[0].split("\\.") : path;
            if (getDefaultLocale().node(finalPath).isList()) {
                final var msg = getLocale(locale).node(finalPath).getList(String.class, Collections.emptyList());
                if (msg.isEmpty()) {
                    msg.addAll(getDefaultLocale().node(finalPath).getList(String.class, Collections.singletonList("<missing path: " + Arrays.toString(finalPath) + ">")));
                }

                return new Message(sender, msg);
            } else {
                final var msg = getLocale(locale).node(finalPath).getString(getDefaultLocale().node(finalPath).getString("<missing path: " + Arrays.toString(finalPath) + ">"));
                return new Message(sender, msg);
            }
        } catch (SerializationException ex) {
            throw new RuntimeException("Failed to get message", ex);
        }
    }

    @NotNull
    public ConfigurationNode getDefaultLocale() {
        return locales.get(defaultLocale);
    }

    @NotNull
    private ConfigurationNode getLocale(@Nullable final String locale) {
        String key;
        if (locale != null && locales.containsKey(locale)) {
            key = locale;
        } else {
            logger.debug("Locale {} not found, try using default locale", locale);
            key = defaultLocale;
        }

        return locales.get(key);
    }
}
