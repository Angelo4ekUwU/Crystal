/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config;

import me.denarydev.crystal.config.serializers.ItemStackSerializer;
import me.denarydev.crystal.config.serializers.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.HashMap;

public final class CrystalConfigSerializers {

    /**
     * Return serializers map.
     *
     * @return {@link HashMap}
     */
    @SuppressWarnings("rawtypes")
    public static HashMap<Class, TypeSerializer> get() {
        final var serializers = new HashMap<Class, TypeSerializer>();
        serializers.put(ItemStack.class, new ItemStackSerializer());
        serializers.put(Location.class, new LocationSerializer());
        return serializers;
    }
}
