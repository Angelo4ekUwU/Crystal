/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui.events;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class GuiClickEvent {
    private final ClickType clickType;
    private final int slot;
    @Nullable
    private final ItemStack currentItem;

    private boolean close = true;
    private boolean destroy = true;
    private boolean cancelled = true;

    public GuiClickEvent(final ClickType clickType, int slot, @Nullable ItemStack currentItem) {
        this.clickType = clickType;
        this.slot = slot;
        this.currentItem = currentItem;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public int getSlot() {
        return this.slot;
    }

    @Nullable
    public ItemStack getCurrentItem() {
        return this.currentItem;
    }

    public boolean isCloseInventory() {
        return this.close;
    }

    public void setCloseInventory(final boolean close) {
        this.close = close;
    }

    public boolean isDestroyInventory() {
        return this.destroy;
    }

    public void setDestroyInventory(final boolean destroy) {
        this.destroy = destroy;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
