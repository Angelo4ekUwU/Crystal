/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.locale;

import me.denarydev.crystal.config.CrystalConfigs;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.nio.file.Path;

public enum LoaderType {
    HOCON(".conf"),
    YAML(".yml"),
    JSON(".json");

    private final String extension;

    LoaderType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public ConfigurationLoader<? extends ConfigurationNode> getLoader(final Path path) {
        return switch (this) {
            case HOCON -> CrystalConfigs.hoconLoader(path);
            case YAML -> CrystalConfigs.yamlLoader(path);
            case JSON -> CrystalConfigs.gsonLoader(path);
        };
    }
}
