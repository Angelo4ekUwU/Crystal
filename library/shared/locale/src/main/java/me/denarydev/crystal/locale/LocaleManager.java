/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("ConstantConditions")
public final class LocaleManager {

    private final Logger logger;
    private final File dataFolder;
    private final LoaderType loaderType;
    private final MessageSender sender;
    private final Pattern localePattern;
    private final Map<Locale, ConfigurationNode> locales = new HashMap<>();

    private Locale defaultLocale;

    public LocaleManager(Logger logger, File dataFolder, LoaderType loaderType, MessageSender sender) {
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.loaderType = loaderType;
        this.sender = sender;
        this.localePattern = Pattern.compile("([a-z]{2})_([a-z]{2})" + loaderType.getExtension());
    }

    public void load(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;

        logger.debug("Loading locale files...");
        final File localeDir = new File(dataFolder + File.separator + "locale");

        if (!localeDir.exists()) {
            try {
                copyLocalesFromJar(dataFolder);
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
                locales.put(new Locale(file.getName().replace(loaderType.getExtension(), "")), loader.load());
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
    public Message getMessage(@Nullable Locale locale, @NotNull String... path) {
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
    private ConfigurationNode getLocale(@Nullable Locale locale) {
        Locale key;
        if (locale != null && locales.containsKey(locale)) {
            key = locale;
        } else {
            logger.debug("Locale {} not found, try using default locale", locale);
            key = defaultLocale;
        }

        return locales.get(key);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyLocalesFromJar(File destFolder) throws IOException {
        if (!destFolder.exists())
            destFolder.mkdirs();

        final byte[] buffer = new byte[1024];

        File fullPath = null;
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            if (!path.startsWith("file"))
                path = "file://" + path;

            fullPath = new File(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final var zis = new ZipInputStream(Files.newInputStream(Objects.requireNonNull(fullPath).toPath()));

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (!entry.getName().startsWith("locale/"))
                continue;

            final var fileName = entry.getName();

            if (fileName.charAt(fileName.length() - 1) == '/') {
                File file = new File(destFolder + File.separator + fileName);
                if (file.isFile()) {
                    file.delete();
                }
                file.mkdirs();
                continue;
            }

            final File file = new File(destFolder + File.separator + fileName);
            if (file.exists())
                continue;

            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            if (!file.exists())
                file.createNewFile();
            final FileOutputStream fos = new FileOutputStream(file);

            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
        }

        zis.closeEntry();
        zis.close();
    }
}
