/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.gui;

import net.kyori.adventure.text.Component;
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
    private final ClickAction clickAction;
    @Nullable
    private final CloseAction closeAction;
    private final Map<Integer, ClickAction> clickActions = new HashMap<>();

    private Player viewer;

    @SuppressWarnings("NullableProblems")
    public Menu(Template template, long clickCooldown, ClickAction clickAction, CloseAction closeAction, Map<Integer, ItemStack> dynItems, Map<Integer, ClickAction> dynClicks) {
        this.template = template;
        this.clickCooldown = clickCooldown;
        this.clickAction = clickAction;
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

    public void button(@NotNull Button button) {
        setItem(button.item(), false, button.slots());
        if (button.action() != null) setAction(button.action(), button.slots());
    }

    public void button(@NotNull Button button, boolean update) {
        setItem(button.item(), update, button.slots());
        if (button.action() != null) setAction(button.action(), button.slots());
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
            clickActions.put(slot, action);
        }
    }

    void click(InventoryClickEvent event) {
        if (clickAction != null) clickAction.click(event);
        final var action = clickActions.get(event.getSlot());

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
        private final Map<Integer, ClickAction> dynActions = new HashMap<>();
        private long clickCooldown;
        private ClickAction clickAction;
        private CloseAction closeAction;

        public Builder(@NotNull Template template) {
            this.template = template;
        }

        public Builder title(Component title) {
            this.template.setTitle(title);
            return this;
        }

        public Builder size(int size) {
            this.template.setSize(size);
            return this;
        }

        public Builder rows(int rows) {
            this.template.setSize(rows * 9);
            return this;
        }

        public Builder item(@NotNull ItemStack item, int @NotNull ... slots) {
            for (final int slot : slots) {
                dynItems.put(slot, item);
            }
            return this;
        }

        public Builder item(@NotNull ItemStack item, @NotNull ClickAction action, int @NotNull ... slots) {
            for (final int slot : slots) {
                dynItems.put(slot, item);
                dynActions.put(slot, action);
            }
            return this;
        }

        public Builder button(@NotNull Button button) {
            final var stack = button.item();
            final int[] slots = button.slots();
            final var action = button.action();
            for (final int slot : slots) {
                dynItems.put(slot, stack);
                if (action != null) dynActions.put(slot, action);
            }
            return this;
        }

        public Builder clickAction(@NotNull ClickAction action, int slot) {
            dynActions.put(slot, action);
            return this;
        }

        public Builder clickCooldown(long clickCooldown) {
            this.clickCooldown = clickCooldown;
            return this;
        }

        public Builder clickAction(@Nullable ClickAction clickAction) {
            this.clickAction = clickAction;
            return this;
        }

        public Builder closeAction(@Nullable CloseAction closeAction) {
            this.closeAction = closeAction;
            return this;
        }

        public Menu build() {
            return new Menu(template, clickCooldown, clickAction, closeAction, dynItems, dynActions);
        }
    }
}
