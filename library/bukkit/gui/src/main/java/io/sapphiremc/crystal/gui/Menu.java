/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Menu implements InventoryHolder {

    private final Template template;
    private final long clickCooldown;
    private final Inventory inventory;
    private final Map<Integer, ClickAction> actions = new HashMap<>();
    private Player viewer;

    public Menu(final Template template, final long clickCooldown, final Map<Integer, ItemStack> dynItems, final Map<Integer, ClickAction> dynClicks) {
        this.template = template;
        this.clickCooldown = clickCooldown;
        this.inventory = template.getType() != null ?
            Bukkit.createInventory(this, template.getType(), template.getTitle()) :
            Bukkit.createInventory(this, template.getSize(), template.getTitle());
        template.getItems().forEach(this::setItem);
        dynItems.forEach(this::setItem);
        dynClicks.forEach(this::setAction);
    }

    public Template getTemplate() {
        return template;
    }

    public long getClickCooldown() {
        return clickCooldown;
    }

    public Player getViewer() {
        return viewer;
    }

    public boolean hasItem(int slot) {
        return inventory.getItem(slot) != null;
    }

    public void setItem(int slot, ItemStack item) {
        if (inventory != null) {
            inventory.setItem(slot, item);
        }
    }

    public void setAction(int slot, ClickAction action) {
        actions.put(slot, action);
    }

    public void click(int slot, ClickContext ctx) {
        final ClickAction action = actions.get(slot);

        if (action != null) {
            action.click(ctx);
        }
    }

    public void open(Player viewer) {
        this.viewer = viewer;
        viewer.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public static Builder builder(Template template) {
        return new Builder(template);
    }

    public static class Builder {

        private final Template template;
        private final Map<Integer, ItemStack> dynItems = new HashMap<>();
        private final Map<Integer, ClickAction> dynClicks = new HashMap<>();
        private long clickCooldown;

        public Builder(Template template) {
            this.template = template;
        }

        public Builder item(int slot, ItemStack item) {
            dynItems.put(slot, item);
            return this;
        }

        public Builder item(final ItemStack item, final int... slots) {
            for (int slot : slots) {
                dynItems.put(slot, item);
            }
            return this;
        }

        public Builder item(ItemStack item, int slot, ClickAction action) {
            dynItems.put(slot, item);
            dynClicks.put(slot, action);
            return this;
        }

        public Builder click(ClickAction action, int slot) {
            dynClicks.put(slot, action);
            return this;
        }

        public Builder clickCooldown(long clickCooldown) {
            this.clickCooldown = clickCooldown;
            return this;
        }

        public Menu build() {
            final Menu menu = new Menu(template, clickCooldown, dynItems, dynClicks);
            return menu;
        }
    }
}
