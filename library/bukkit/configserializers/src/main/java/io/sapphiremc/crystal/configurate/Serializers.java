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
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.Map;

@SuppressWarnings("unused")
public final class Serializers {

    /**
     * Return serializers map.
     *
     * @return {@link Map<Class, TypeSerializer>}
     */
    public static Map<Class, TypeSerializer> get() {
        return Map.of(
            ItemStack.class, new ItemStackSerializer(),
            Location.class, new LocationSerializer()
        );
    }
}
