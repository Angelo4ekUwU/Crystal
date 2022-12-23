/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.configurate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.util.CheckedFunction;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings({"unused", "rawtypes"})
public final class CrystalConfig {

    /**
     * Returns hocon configuration loader.
     *
     * @param path Path to configuration file
     * @return {@link HoconConfigurationLoader}
     */
    public static HoconConfigurationLoader hoconLoader(@NotNull final Path path) {
        return hoconLoader(path, null);
    }

    /**
     * Returns json configuration loader with custom serializers.
     *
     * @param path Path to configuration file
     * @param serializers custom serializers
     * @return {@link HoconConfigurationLoader}
     */
    public static HoconConfigurationLoader hoconLoader(@NotNull final Path path, @Nullable Map<Class, TypeSerializer> serializers) {
        final HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
            .path(path)
            .emitJsonCompatible(false);

        if (serializers != null) {
            final TypeSerializerCollection.Builder collectionBuilder = TypeSerializerCollection.builder();
            serializers.forEach(collectionBuilder::register);
            return builder.defaultOptions(options -> options.serializers(collectionBuilder.build())).build();
        } else {
            return builder.build();
        }
    }

    /**
     * Returns yaml configuration loader.
     *
     * @param path Path to configuration file
     * @return {@link YamlConfigurationLoader}
     */
    public static YamlConfigurationLoader yamlLoader(@NotNull final Path path) {
        return yamlLoader(path, null);
    }

    /**
     * Returns yaml configuration loader with custom serializers.
     *
     * @param path Path to configuration file
     * @param serializers custom serializers
     * @return {@link YamlConfigurationLoader}
     */
    public static YamlConfigurationLoader yamlLoader(@NotNull final Path path, @Nullable Map<Class, TypeSerializer> serializers) {
        final YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder()
            .path(path)
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2);

        if (serializers != null) {
            final TypeSerializerCollection.Builder collectionBuilder = TypeSerializerCollection.builder();
            serializers.forEach(collectionBuilder::register);
            return builder.defaultOptions(options -> options.serializers(collectionBuilder.build())).build();
        } else {
            return builder.build();
        }
    }

    /**
     * Returns json configuration loader.
     *
     * @param path Path to configuration file
     * @return {@link GsonConfigurationLoader}
     */
    public static GsonConfigurationLoader gsonLoader(@NotNull final Path path) {
        return gsonLoader(path, null);
    }

    /**
     * Returns json configuration loader with custom serializers.
     *
     * @param path Path to configuration file
     * @param serializers custom serializers
     * @return {@link GsonConfigurationLoader}
     */
    public static GsonConfigurationLoader gsonLoader(@NotNull final Path path, @Nullable Map<Class, TypeSerializer> serializers) {
        final GsonConfigurationLoader.Builder builder = GsonConfigurationLoader.builder()
            .path(path)
            .indent(2);

        if (serializers != null) {
            final TypeSerializerCollection.Builder collectionBuilder = TypeSerializerCollection.builder();
            serializers.forEach(collectionBuilder::register);
            return builder.defaultOptions(options -> options.serializers(collectionBuilder.build())).build();
        } else {
            return builder.build();
        }
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
        final CheckedFunction<ConfigurationNode, T, SerializationException> creator = creator(clazz, refreshNode);

        final ConfigurationNode node;
        if (loader.canLoad()) {
            node = loader.load();
        } else {
            node = CommentedConfigurationNode.root(loader.defaultOptions());
        }
        final T instance = creator.apply(node);
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
