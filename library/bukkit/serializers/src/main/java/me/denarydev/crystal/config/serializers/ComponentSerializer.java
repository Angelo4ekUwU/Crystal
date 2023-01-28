/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ComponentSerializer implements TypeSerializer<Component> {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public Component deserialize(Type type, ConfigurationNode node) {
        final var s = node.getString();
        if (s != null) {
            return MINI_MESSAGE.deserialize(s);
        }
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable Component obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.set(MINI_MESSAGE.serialize(obj));
        }
    }
}
