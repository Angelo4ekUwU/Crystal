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
        if (event.getInventory() != null
            && event.getInventory().getHolder() instanceof Menu
            && event.getCurrentItem() != null
            && event.getWhoClicked() instanceof Player) {
            final Player player = (Player) event.getWhoClicked();
            final int slot = event.getSlot();
            final Menu menu = (Menu) event.getInventory().getHolder();
            final ClickContext context = new ClickContext(player, menu, slot, event);
            event.setCancelled(true);
            menu.click(event.getSlot(), context);
        }
    }
}
