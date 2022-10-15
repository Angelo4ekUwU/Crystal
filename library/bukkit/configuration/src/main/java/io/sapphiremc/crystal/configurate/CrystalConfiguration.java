/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.configurate;

import io.sapphiremc.crystal.configurate.serializers.ItemStackSerializer;
import io.sapphiremc.crystal.configurate.serializers.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.CheckedFunction;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CrystalConfiguration {

    /**
     * Returns hocon config loader with custom serializers.
     *
     * @param path Path to configuration file
     * @return {@link HoconConfigurationLoader}
     */
    public static HoconConfigurationLoader hoconLoader(@NotNull final Path path) {
        return HoconConfigurationLoader.builder()
            .path(path)
            .defaultOptions(options -> options.serializers(builder ->
                builder.register(ItemStack.class, new ItemStackSerializer())
                    .register(Location.class, new LocationSerializer())))
            .emitJsonCompatible(false)
            .build();
    }

    /**
     * Load configuration from file.
     *
     * @param path Path to configuration file
     * @param clazz Configuration class type
     * @param refreshNode refresh node or not
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path, @NotNull final Class<T> clazz, final boolean refreshNode) throws ConfigurateException {
        final var creator = creator(clazz, refreshNode);
        final var loader = hoconLoader(path);

        final ConfigurationNode node;
        if (Files.exists(path)) {
            node = loader.load();
        } else {
            node = CommentedConfigurationNode.root(loader.defaultOptions());
        }
        final var instance = creator.apply(node);
        loader.save(node);
        return instance;
    }

    @NotNull
    private static <T> CheckedFunction<ConfigurationNode, T, SerializationException> creator(@NotNull Class<T> type, boolean refreshNode) {
        return node -> {
            T instance = node.require(type);
            if (refreshNode) {
                node.set(type, instance);
            }
            return instance;
        };
    }
}
