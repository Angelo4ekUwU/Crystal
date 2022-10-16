/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class InventoryListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu &&
            event.getCurrentItem() != null &&
            event.getWhoClicked() instanceof Player player) {
            int slot = event.getSlot();
            final var context = new ClickContext(player, menu, slot, event);
            event.setCancelled(true);
            menu.click(event.getSlot(), context);
        }
    }
}
