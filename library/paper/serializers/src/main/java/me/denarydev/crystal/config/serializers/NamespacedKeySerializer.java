/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import org.bukkit.NamespacedKey;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * @author DenaryDev
 * @since 0:00 18.02.2024
 */
public class NamespacedKeySerializer extends ScalarSerializer<NamespacedKey> {
    public NamespacedKeySerializer() {
        super(NamespacedKey.class);
    }

    @Override
    public NamespacedKey deserialize(Type type, Object obj) throws SerializationException {
        final var key = NamespacedKey.fromString(obj.toString());
        if (key != null) return key;
        else throw new SerializationException("Invalid namespaced key");
    }

    @Override
    protected Object serialize(NamespacedKey item, Predicate<Class<?>> typeSupported) {
        return item.toString();
    }
}
