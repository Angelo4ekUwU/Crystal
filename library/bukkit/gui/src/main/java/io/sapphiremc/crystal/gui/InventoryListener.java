/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import io.sapphiremc.crystal.CrystalPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class InventoryListener implements Listener {

    private final Map<UUID, Long> activeCooldowns = new WeakHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEarlyInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLateInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu && event.getCurrentItem() != null && event.getWhoClicked() instanceof Player) {
            // Cancel the event again just in case a plugin un-cancels it
            event.setCancelled(true);

            final Menu menu = (Menu) event.getInventory().getHolder();
            if (menu == null) return;

            final int slot = event.getSlot();

            if (!menu.hasItem(slot)) return;

            final UUID uuid = event.getWhoClicked().getUniqueId();
            final Long cooldownUntil = activeCooldowns.get(uuid);
            final long now = System.currentTimeMillis();
            final long cooldown = menu.getClickCooldown();

            if (cooldown > 0) {
                if (cooldownUntil != null && cooldownUntil > now) {
                    return;
                } else {
                    activeCooldowns.put(uuid, now + cooldown);
                }
            }

            CrystalPlugin.instance().runSyncTask(() -> menu.click(event));
        }
    }

    public void onInvClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Menu && event.getPlayer() instanceof Player) {
            final Menu menu = (Menu) event.getInventory().getHolder();
            if (menu == null) return;

            CrystalPlugin.instance().runSyncTask(() -> menu.close(event));
        }
    }
}
