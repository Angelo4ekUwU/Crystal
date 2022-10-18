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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Menu implements InventoryHolder {

    private final Map<Integer, Template> templates = new HashMap<>();
    private final Map<Integer, ClickAction> actions = new HashMap<>();
    private Inventory inventory;
    private Player viewer;
    private int page;

    public Menu(final Template template) {
        this.templates.put(1, template);
    }

    public Menu(final Map<Integer, Template> templates) {
        this.templates.putAll(templates);
    }

    void item(int slot, ItemStack item) {
        if (inventory != null) {
            inventory.setItem(slot, item);
        }
    }

    public void open(Player viewer) {
        open(viewer, 1);
    }

    public void open(Player viewer, int page) {
        this.viewer = viewer;
        this.page = page;
        final var template = templates.get(templates.containsKey(page) ? page : 1);
        inventory = Bukkit.createInventory(this, template.getSize(), template.getTitle());
        template.getItems().forEach(inventory::setItem);
        viewer.openInventory(inventory);
    }

    public boolean hasPage(int page) {
        return templates.containsKey(page);
    }

    public boolean hasNextPage() {
        return hasPage(page + 1);
    }

    public boolean hasPreviousPage() {
        return hasPage(page - 1);
    }

    public void setPage(int page) {
        if (hasPage(page))
            open(viewer, page);
    }

    public void nextPage() {
        setPage(page + 1);
    }

    public void previousPage() {
        setPage(page - 1);
    }

    public Map<Integer, Template> getTemplates() {
        return templates;
    }

    @Nullable
    public Template getTemplate(int page) {
        return templates.getOrDefault(page, null);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Player getViewer() {
        return viewer;
    }

    public int getPage() {
        return page;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void action(int slot, ClickAction action) {
        actions.put(slot, action);
    }

    public void click(int slot, ClickContext ctx) {
        final var action = actions.get(slot);

        if (action != null) {
            action.click(ctx);
        }
    }

    public static class Builder {

        private final Map<Integer, Template> templates = new HashMap<>();
        private final Map<Integer, ItemStack> dynItems = new HashMap<>();
        private final Map<Integer, ClickAction> dynClicks = new HashMap<>();

        public Builder template(Template template) {
            this.templates.put(1, template);
            return this;
        }

        public Builder template(Template template, int page) {
            this.templates.put(page, template);
            return this;
        }

        public Builder templates(Map<Integer, Template> templates) {
            this.templates.putAll(templates);
            return this;
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
            final var menu = new Menu(templates);
            dynItems.forEach(menu::item);
            dynClicks.forEach(menu::action);
            return menu;
        }
    }
}
