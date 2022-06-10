/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.hook.holo;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import io.sapphiremc.crystal.CrystalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "deprecation"})
public class CMIHologramHook extends HologramHook {
    private final CMI cmi;
    private final HashSet<String> ourHolograms = new HashSet<>();
    private HologramManager cmiHologramManager;

    private static Method cmi_CMIHologram_getLines;
    private static boolean useOldMethod;

    static {
        try {
            useOldMethod = CMIHologram.class.getDeclaredField("lines").getDeclaringClass() == String[].class;
            cmi_CMIHologram_getLines = CMIHologram.class.getMethod("getLines");
        } catch (NoSuchFieldException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public CMIHologramHook(CrystalPlugin plugin) {
        super(plugin);

        cmi = (CMI) Bukkit.getPluginManager().getPlugin("CMI");

        if (cmi != null) {
            cmiHologramManager = cmi.getHologramManager();
        }
    }

    @Override
    public @NotNull String getPluginName() {
        return "CMI";
    }

    @Override
    public boolean isEnabled() {
        return cmi != null && cmi.isEnabled();
    }

    @Override
    public double defaultHeightOffset() {
        return 1;
    }

    @Override
    public void createHologram(@NotNull String id, @NotNull Location location, @NotNull List<String> lines) {
        createAt(id, fixLocation(location), lines);
    }

    @Override
    public void updateHologram(@NotNull String id, @NotNull List<String> lines) {
        CMIHologram holo = cmiHologramManager.getByName(id);

        if (holo != null) {
            // only update if there is a change to the text
            List<String> holoLines;
            try {
                if (useOldMethod) {
                    holoLines = Arrays.asList((String[]) cmi_CMIHologram_getLines.invoke(holo));
                } else {
                    holoLines = (List<String>) cmi_CMIHologram_getLines.invoke(holo);
                }
            } catch (Exception ex) {
                plugin.logError("CMI Hologram error!", ex);
                holoLines = Collections.emptyList();
            }

            boolean isChanged = lines.size() != holoLines.size();
            if (!isChanged) {
                // double-check the lines
                for (int i = 0; !isChanged && i < lines.size(); ++i) {
                    isChanged = !holo.getLine(i).equals(lines.get(i));
                }
            }

            if (isChanged) {
                holo.setLines(lines);
                holo.update();
            }
        }
    }

    @Override
    public void bulkUpdateHolograms(Map<String, List<String>> hologramData) {
        for (Map.Entry<String, List<String>> entry : hologramData.entrySet()) {
            updateHologram(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void removeHologram(@NotNull String id) {
        CMIHologram holo = cmiHologramManager.getByName(id);

        if (holo != null) {
            cmiHologramManager.removeHolo(holo);
        }

        ourHolograms.remove(id);
    }

    @Override
    public void removeAllHolograms() {
        for (String id : ourHolograms) {
            CMIHologram holo = cmiHologramManager.getByName(id);

            if (holo != null) {
                cmiHologramManager.removeHolo(holo);
            }
        }

        ourHolograms.clear();
    }

    @Override
    public boolean isHologramLoaded(@NotNull String id) {
        return cmiHologramManager.getByName(id) != null;
    }

    private void createAt(String id, Location location, List<String> lines) {
        CMIHologram holo = new CMIHologram(id, location);
        holo.setLines(lines);

        cmiHologramManager.addHologram(holo);
        holo.update();

        ourHolograms.add(id);
    }
}
