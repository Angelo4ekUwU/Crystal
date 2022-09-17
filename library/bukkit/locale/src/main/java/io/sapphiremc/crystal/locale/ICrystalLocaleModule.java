/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public interface ICrystalLocaleModule {

    Logger LOGGER = LoggerFactory.getLogger("Crystal Locale");

    File getDataFolder();

    String stylish(String s);

    List<String> stylish(List<String> collection);

    FileConfiguration getFileConfig(File file);

    String defaultLang();

    boolean usePlayerLang();
}
