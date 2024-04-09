/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Утилиты для работы с местоположением игрока.
 *
 * @author DenaryDev
 * @since 13:56 25.11.2023
 */
@ApiStatus.AvailableSince("2.1.0")
public final class LocationUtils {

    /**
     * Проверяет, находится ли точка в указанной зоне.
     *
     * @param loc  точка для проверки
     * @param pos1 первая точка зоны
     * @param pos2 вторая точка зоны
     * @return true, если указанная точка в зоне, иначе false
     */
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

    /**
     * Возвращает центральную позицию, но не трогает высоту.
     *
     * @param location точка
     * @return центральная позиция от этой точки
     */
    @NotNull
    @ApiStatus.AvailableSince("2.2.0")
    public static Location centerLocation(@NotNull final Location location) {
        final var centerLoc = location.clone();
        centerLoc.setX(location.getBlockX() + 0.5);
        centerLoc.setZ(location.getBlockZ() + 0.5);
        return centerLoc;
    }

    /**
     * Находит ближайшей к точке блок, и возвращает его {@link Location}
     * <p>
     * По высоте ищет в диапазоне от -1 до +1 относительно высоты точки.
     *
     * @param loc    точка, вокруг которой ищем блок
     * @param type   тип блока, который ищем
     * @param radius радиус, не рекомендуются ставить большие значения
     * @return Позиция ближайшего к точке блока, или null, если таковой не найден
     */
    @Nullable
    @ApiStatus.AvailableSince("2.2.0")
    public static Location findClosestBlock(@NotNull final Location loc, @NotNull final Material type, final int radius) {
        if (loc.getBlock().getType().equals(type)) return loc;
        Location closest = null;
        for (int y = -1; y <= 1; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    final var current = loc.clone().add(x, y, z);
                    if (current.getBlock().getType().equals(type)) {
                        if (closest == null || loc.distance(current) < loc.distance(closest)) {
                            closest = current;
                        }
                    }
                }
            }
        }
        return closest;
    }
}
