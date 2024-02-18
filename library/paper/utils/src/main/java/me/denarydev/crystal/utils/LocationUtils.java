/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * @author DenaryDev
 * @since 13:56 25.11.2023
 */
public final class LocationUtils {

    public static boolean inArea(@NotNull final Location loc, @NotNull final Location pos1, @NotNull final Location pos2) {
        final double x1 = Math.min(pos1.getX(), pos2.getX());
        final double y1 = Math.min(pos1.getY(), pos2.getY());
        final double z1 = Math.min(pos1.getZ(), pos2.getZ());

        final double x2 = Math.max(pos1.getX(), pos2.getX());
        final double y2 = Math.max(pos1.getY(), pos2.getY());
        final double z2 = Math.max(pos1.getZ(), pos2.getZ());

        return loc.getX() >= x1 && loc.getX() <= x2 &&
            loc.getY() >= y1 && loc.getY() <= y2 &&
            loc.getZ() >= z1 && loc.getZ() <= z2;
    }
}
