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
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.util.CheckedFunction;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

@SuppressWarnings("unused")
public final class CrystalConfiguration {

    /**
     * Returns hocon configuration loader with custom serializers.
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
     * Returns yaml configuration loader with custom serializers.
     *
     * @param path Path to configuration file
     * @return {@link YamlConfigurationLoader}
     */
    public static YamlConfigurationLoader yamlLoader(@NotNull final Path path) {
        return YamlConfigurationLoader.builder()
            .path(path)
            .defaultOptions(options -> options.serializers(builder ->
                builder.register(ItemStack.class, new ItemStackSerializer())
                    .register(Location.class, new LocationSerializer())))
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .build();
    }

    /**
     * Returns json configuration loader with custom serializers.
     *
     * @param path Path to configuration file
     * @return {@link GsonConfigurationLoader}
     */
    public static GsonConfigurationLoader gsonLoader(@NotNull final Path path) {
        return GsonConfigurationLoader.builder()
            .path(path)
            .defaultOptions(options -> options.serializers(builder ->
                builder.register(ItemStack.class, new ItemStackSerializer())
                    .register(Location.class, new LocationSerializer())))
            .indent(2)
            .build();
    }

    /**
     * Load configuration from file using hocon loader.
     *
     * @param path        Path to configuration file
     * @param clazz       Configuration class type
     * @param refreshNode refresh node or not
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final Path path, @NotNull final Class<T> clazz,
                                   final boolean refreshNode) throws ConfigurateException {
        return loadConfig(hoconLoader(path), clazz, refreshNode);
    }

    /**
     * Load configuration from file using specified loader.
     *
     * @param loader      Configuration loader
     * @param clazz       Configuration class type
     * @param refreshNode refresh node or not
     * @return Configuration class instance with values
     * @throws ConfigurateException if configuration loading failed
     */
    public static <T> T loadConfig(@NotNull final ConfigurationLoader<? extends ConfigurationNode> loader, @NotNull final Class<T> clazz,
                                   final boolean refreshNode) throws ConfigurateException {
        final var creator = creator(clazz, refreshNode);

        final ConfigurationNode node;
        if (loader.canLoad()) {
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
