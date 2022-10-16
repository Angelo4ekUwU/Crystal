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

    private final Template tmpl;
    private final Inventory inventory;
    private final Map<Integer, ClickAction> actions;
    private Player viewer;

    public Menu(Template tmpl) {
        this.tmpl = tmpl;
        this.actions = new HashMap<>();
        inventory = Bukkit.createInventory(this, tmpl.getSize(), tmpl.getTitle());
        tmpl.getItems().forEach(inventory::setItem);
    }

    public Template getTemplate() {
        return tmpl;
    }

    public Player getViewer() {
        return viewer;
    }

    void setItem(int slot, ItemStack item) {
        if (inventory != null) {
            inventory.setItem(slot, item);
        }
    }

    void setAction(int slot, ClickAction action) {
        actions.put(slot, action);
    }

    void click(int slot, ClickContext ctx) {
        final var action = actions.get(slot);

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

        private final Template tmpl;
        private final Map<Integer, ItemStack> dynItems;
        private final Map<Integer, ClickAction> dynClicks;

        public Builder(Template tmpl) {
            this.tmpl = tmpl;
            this.dynItems = new HashMap<>();
            this.dynClicks = new HashMap<>();
        }

        public Builder item(int slot, ItemStack item) {
            dynItems.put(slot, item);
            return this;
        }

        public Builder item(ItemStack item, int from, int to) {
            for (int i = from; i <= to; i++) {
                dynItems.put(i, item);
            }
            return this;
        }

        public Builder item(int slot, ItemStack item, ClickAction action) {
            dynItems.put(slot, item);
            dynClicks.put(slot, action);
            return this;
        }

        public Builder click(int slot, ClickAction action) {
            dynClicks.put(slot, action);
            return this;
        }

        public Menu build() {
            Menu menu = new Menu(tmpl);
            dynItems.forEach(menu::setItem);
            dynClicks.forEach(menu::setAction);
            return menu;
        }
    }
}
