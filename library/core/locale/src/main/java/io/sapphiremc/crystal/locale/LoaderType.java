/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.configurate.CrystalConfig;
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
        switch (this) {
            case HOCON: return CrystalConfig.hoconLoader(path);
            case YAML: return CrystalConfig.yamlLoader(path);
            case JSON: return CrystalConfig.gsonLoader(path);
            default: return null;
        }
    }
}
