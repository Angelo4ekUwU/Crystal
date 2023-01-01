/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickContext {

    private final Player player;
    private final Menu menu;
    private final InventoryClickEvent event;
    private final int slot;

    public ClickContext(Player player, Menu menu, int slot, InventoryClickEvent event) {
        this.player = player;
        this.menu = menu;
        this.slot = slot;
        this.event = event;
    }

    public Player getPlayer() {
        return player;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getSlot() {
        return slot;
    }

    public InventoryClickEvent getEvent() {
        return event;
    }
}
