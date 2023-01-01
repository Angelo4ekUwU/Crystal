/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.WeakHashMap;

public final class InventoryListener implements Listener {

    private final Map<Player, Long> antiClickSpam = new WeakHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEarlyInvClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu
            && event.getCurrentItem() != null
            && event.getWhoClicked() instanceof Player) {

            // Cancel the event again just in case a plugin un-cancels it
            event.setCancelled(true);

            final Menu menu = (Menu) event.getInventory().getHolder();
            final int slot = event.getSlot();

            if (!menu.hasItem(slot)) return;

            final Player player = (Player) event.getWhoClicked();

            Long cooldownUntil = antiClickSpam.get(player);
            long now = System.currentTimeMillis();
            long cooldown = menu.getClickCooldown();

            if (cooldown > 0) {
                if (cooldownUntil != null && cooldownUntil > now) {
                    return;
                } else {
                    antiClickSpam.put(player, now + cooldown);
                }
            }

            final ClickContext context = new ClickContext(player, menu, slot, event);
            menu.click(event.getSlot(), context);
        }
    }
}
