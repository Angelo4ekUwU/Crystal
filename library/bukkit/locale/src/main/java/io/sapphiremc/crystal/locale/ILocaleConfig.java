/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public interface ILocaleConfig {

    Logger getLogger();

    @NotNull
    File getDataFolder();

    @NotNull
    String stylish(@NotNull final String s);

    @NotNull
    List<String> stylish(@NotNull final List<String> collection);

    @NotNull
    String defaultLang();

    boolean usePlayerLang();
}
