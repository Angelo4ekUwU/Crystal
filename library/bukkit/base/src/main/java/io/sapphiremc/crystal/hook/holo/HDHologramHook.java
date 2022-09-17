/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.hook.holo;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.sapphiremc.crystal.CrystalPlugin;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class HDHologramHook extends HologramHook {

    private final Map<String, Hologram> holograms = new HashMap<>();
    private final String textLineFormat;

    public HDHologramHook(CrystalPlugin plugin) {
        super(plugin);

        String version = plugin.getPluginManager().getPlugin("HolographicDisplays").getDescription().getVersion();
        this.textLineFormat = version.startsWith("3") ? "TextLine{text=%s}" : "CraftTextLine [text=%s]";
    }

    @Override
    public @NotNull String getPluginName() {
        return "HolographicDisplays";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public double defaultHeightOffset() {
        return 1;
    }

    @Override
    public void createHologram(@NotNull String id, @NotNull Location location, @NotNull List<String> lines) {
        createAt(id, location, lines);
    }

    @Override
    public void updateHologram(@NotNull String id, @NotNull List<String> lines) {
        bulkUpdateHolograms(Collections.singletonMap(id, lines));
    }

    @Override
    public void bulkUpdateHolograms(Map<String, List<String>> hologramData) {
        for (Map.Entry<String, List<String>> entry : hologramData.entrySet()) {
            String id = entry.getKey();
            List<String> lines = entry.getValue();

            Hologram hologram = holograms.get(id);

            // only update if there is a change to the text
            boolean isChanged = lines.size() != hologram.size();

            if (!isChanged) {
                // double-check the lines
                for (int i = 0; !isChanged && i < lines.size(); ++i) {
                    isChanged = !hologram.getLine(i).toString().equals(String.format(textLineFormat, lines.get(i)));
                }
            }

            if (isChanged) {
                hologram.clearLines();

                for (String line : lines) {
                    hologram.appendTextLine(line);
                }
            }
        }
    }

    @Override
    public void removeHologram(@NotNull String id) {
        Hologram hologram = holograms.remove(id);
        if (hologram != null) {
            hologram.delete();
        }
    }

    @Override
    public void removeAllHolograms() {
        holograms.values().forEach(Hologram::delete);
        holograms.clear();
    }

    @Override
    public boolean isHologramLoaded(@NotNull String id) {
        return holograms.get(id) != null;
    }

    private void createAt(String id, Location location, List<String> lines) {
        if (holograms.containsKey(id)) {
            return;
        }

        location = fixLocation(location);
        Hologram hologram = HologramsAPI.createHologram(plugin, location);

        for (String line : lines) {
            hologram.appendTextLine(line);
        }

        holograms.put(id, hologram);
    }
}
