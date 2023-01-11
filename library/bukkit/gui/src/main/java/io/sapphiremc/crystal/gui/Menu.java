/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Menu implements InventoryHolder {

    private final Template template;
    private final long clickCooldown;
    private final Inventory inventory;
    @Nullable
    private final CloseAction closeAction;
    private final Map<Integer, ClickAction> clicks = new HashMap<>();

    private Player viewer;

    public Menu(final Template template, final long clickCooldown, final CloseAction closeAction, final Map<Integer, ItemStack> dynItems, final Map<Integer, ClickAction> dynClicks) {
        this.template = template;
        this.clickCooldown = clickCooldown;
        this.closeAction = closeAction;
        this.inventory = template.getType() != null ?
            Bukkit.createInventory(this, template.getType(), template.getTitle()) :
            Bukkit.createInventory(this, template.getSize(), template.getTitle());
        template.getItems().forEach((slot, item) -> setItem(item, false, slot));
        dynItems.forEach((slot, item) -> setItem(item, false, slot));
        dynClicks.forEach((slot, action) -> setAction(action, slot));
    }

    public static Builder builder(@NotNull Template template) {
        return new Builder(template);
    }

    @NotNull
    public Template getTemplate() {
        return template;
    }

    public long getClickCooldown() {
        return clickCooldown;
    }

    @Nullable
    public Player getViewer() {
        return viewer;
    }

    public boolean hasItem(int slot) {
        return inventory.getItem(slot) != null;
    }

    public void item(@NotNull ItemStack item, int @NotNull ... slots) {
        setItem(item, false, slots);
    }

    public void item(@NotNull ItemStack item, boolean update, int @NotNull ... slots) {
        setItem(item, update, slots);
    }

    public void item(@NotNull Item item) {
        setItem(item.getItem(), false, item.getSlots());
    }

    public void item(@NotNull Item item, boolean update) {
        setItem(item.getItem(), update, item.getSlots());
    }

    public void action(@NotNull ClickAction action, int @NotNull ... slots) {
        setAction(action, slots);
    }

    public void open(@NotNull Player viewer) {
        this.viewer = viewer;
        viewer.openInventory(inventory);
    }

    public void update() {
        if (viewer == null) return;
        viewer.updateInventory();
    }

    void setItem(ItemStack item, boolean update, int... slots) {
        if (inventory != null) {
            for (final int slot : slots) {
                inventory.setItem(slot, item);
            }
            if (update) update();
        }
    }

    void setAction(ClickAction action, int... slots) {
        for (final int slot : slots) {
            clicks.put(slot, action);
        }
    }

    void click(InventoryClickEvent event) {
        final ClickAction action = clicks.get(event.getSlot());

        if (action != null) {
            action.click(event);
        }
    }

    void close(InventoryCloseEvent event) {
        if (closeAction != null) {
            closeAction.close(event);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public static class Builder {

        private final Template template;
        private final Map<Integer, ItemStack> dynItems = new HashMap<>();
        private final Map<Integer, ClickAction> dynClicks = new HashMap<>();
        private long clickCooldown;
        private CloseAction closeAction;

        public Builder(@NotNull Template template) {
            this.template = template;
        }

        public Builder item(@NotNull ItemStack item, int @NotNull ... slots) {
            for (int slot : slots) {
                dynItems.put(slot, item);
            }
            return this;
        }

        public Builder item(@NotNull Item item) {
            final ItemStack stack = item.getItem();
            final int[] slots = item.getSlots();
            for (int slot : slots) {
                dynItems.put(slot, stack);
            }
            return this;
        }

        public Builder item(@NotNull ItemStack item, @NotNull ClickAction action, int @NotNull ... slots) {
            for (int slot : slots) {
                dynItems.put(slot, item);
                dynClicks.put(slot, action);
            }
            return this;
        }

        public Builder item(@NotNull Item item, @NotNull ClickAction action) {
            final ItemStack stack = item.getItem();
            final int[] slots = item.getSlots();
            for (int slot : slots) {
                dynItems.put(slot, stack);
                dynClicks.put(slot, action);
            }
            return this;
        }

        public Builder click(@NotNull ClickAction action, int slot) {
            dynClicks.put(slot, action);
            return this;
        }

        public Builder clickCooldown(long clickCooldown) {
            this.clickCooldown = clickCooldown;
            return this;
        }

        public Builder closeAction(@Nullable CloseAction closeAction) {
            this.closeAction = closeAction;
            return this;
        }

        public Menu build() {
            final Menu menu = new Menu(template, clickCooldown, closeAction, dynItems, dynClicks);
            return menu;
        }
    }
}
