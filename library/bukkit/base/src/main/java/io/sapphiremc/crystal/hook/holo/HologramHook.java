/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.hook.holo;

import io.sapphiremc.crystal.CrystalPlugin;
import io.sapphiremc.crystal.hook.Hook;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class HologramHook implements Hook {
    protected double xOffset = 0.5;
    protected double yOffset = 0.5;
    protected double zOffset = 0.5;

    protected final CrystalPlugin plugin;

    public HologramHook setPositionOffset(double x, double y, double z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;

        return this;
    }

    /**
     * Center and offset this location.
     *
     * @param location location to offset
     * @return copy-safe location with the applied offset.
     */
    protected final Location fixLocation(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return location.clone().add((x - (int) x) + xOffset, (y - (int) y) + yOffset + defaultHeightOffset(), (z - (int) z) + zOffset);
    }

    public abstract double defaultHeightOffset();

    public void createHologram(@NotNull String id, @NotNull Location location, @NotNull String line) {
        createHologram(id, location, Collections.singletonList(line));
    }

    public abstract void createHologram(@NotNull String id, @NotNull Location location, @NotNull List<String> lines);

    public void updateHologram(@NotNull String id, @NotNull String line) {
        updateHologram(id, Collections.singletonList(line));
    }

    public abstract void updateHologram(@NotNull String id, @NotNull List<String> lines);

    public abstract void bulkUpdateHolograms(Map<String, List<String>> hologramData);

    public abstract void removeHologram(@NotNull String id);

    public abstract void removeAllHolograms();

    public abstract boolean isHologramLoaded(@NotNull String id);
}
