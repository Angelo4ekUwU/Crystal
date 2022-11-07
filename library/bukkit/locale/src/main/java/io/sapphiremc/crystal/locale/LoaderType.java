/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.configurate.CrystalConfiguration;
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
            case HOCON -> CrystalConfiguration.hoconLoader(path);
            case YAML -> CrystalConfiguration.yamlLoader(path);
            case JSON -> CrystalConfiguration.gsonLoader(path);
        };
    }
}
