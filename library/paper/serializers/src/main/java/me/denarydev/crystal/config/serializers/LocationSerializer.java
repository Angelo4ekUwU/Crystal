/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class LocationSerializer implements TypeSerializer<Location> {

    public static final TypeToken<Location> TYPE = TypeToken.get(Location.class);

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var s = node.getString();
        if (s != null) {
            final String[] loc = s.split(";");
            if (loc.length == 3) { // X;Y;Z
                return new Location(null,
                    Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]));
            } else if (loc.length == 4) { // WORLD;X;Y;Z
                return new Location(Bukkit.getWorld(loc[0]),
                    Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
            } else if (loc.length == 6) { // WORLD;X;Y;Z;YAW;PITCH
                return new Location(Bukkit.getWorld(loc[0]),
                    Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]),
                    Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
            } else {
                throw new SerializationException("Invalid location format!");
            }
        } else {
            throw new SerializationException("Location string is null!");
        }
    }

    @Override
    public void serialize(Type type, @Nullable Location loc, ConfigurationNode node) throws SerializationException {
        if (loc != null) {
            final var builder = new StringBuilder();

            if (loc.isWorldLoaded()) builder.append(loc.getWorld().getName()).append(";"); // World

            builder.append(loc.getX()).append(";"); // X
            builder.append(loc.getY()).append(";"); // Y
            builder.append(loc.getZ()); // Z

            if (loc.getYaw() > 0 || loc.getPitch() > 0) {
                builder.append(";").append(loc.getYaw()); // Yaw
                builder.append(";").append(loc.getPitch()); // Pitch
            }

            node.set(builder.toString());
        }
    }
}
