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
    private final Map<Integer, Action> actions = new HashMap<>();
    private Player viewer;

    public Menu(final Template template, final long clickCooldown, final Map<Integer, ItemStack> dynItems, final Map<Integer, Action> dynClicks) {
        this.template = template;
        this.clickCooldown = clickCooldown;
        this.inventory = template.getType() != null ?
            Bukkit.createInventory(this, template.getType(), template.getTitle()) :
            Bukkit.createInventory(this, template.getSize(), template.getTitle());
        template.getItems().forEach((slot, item) -> setItem(item, false, slot));
        dynItems.forEach((slot, item) -> setItem(item, false, slot));
        dynClicks.forEach((slot, action) -> setAction(action, slot));
    }

    public static Builder builder(Template template) {
        return new Builder(template);
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

    public void item(ItemStack item, int... slots) {
        setItem(item, false, slots);
    }

    public void item(ItemStack item, boolean update, int... slots) {
        setItem(item, update, slots);
    }

    public void item(Item item) {
        setItem(item.getItem(), false, item.getSlots());
    }

    public void item(Item item, boolean update) {
        setItem(item.getItem(), update, item.getSlots());
    }

    public void action(Action action, int... slots) {
            setAction(action, slots);
    }

    public void open(Player viewer) {
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

    void setAction(Action action, int... slots) {
        for (final int slot : slots) {
            actions.put(slot, action);
        }
    }

    void click(Action.Context ctx) {
        final Action action = actions.get(ctx.getSlot());

        if (action != null) {
            action.click(ctx);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public static class Builder {

        private final Template template;
        private final Map<Integer, ItemStack> dynItems = new HashMap<>();
        private final Map<Integer, Action> dynClicks = new HashMap<>();
        private long clickCooldown;

        public Builder(Template template) {
            this.template = template;
        }

        public Builder item(ItemStack item, int... slots) {
            for (int slot : slots) {
                dynItems.put(slot, item);
            }
            return this;
        }

        public Builder item(Item item) {
            final ItemStack stack = item.getItem();
            final int[] slots = item.getSlots();
            for (int slot : slots) {
                dynItems.put(slot, stack);
            }
            return this;
        }

        public Builder item(ItemStack item, Action action, int... slots) {
            for (int slot : slots) {
                dynItems.put(slot, item);
                dynClicks.put(slot, action);
            }
            return this;
        }

        public Builder item(Item item, Action action) {
            final ItemStack stack = item.getItem();
            final int[] slots = item.getSlots();
            for (int slot : slots) {
                dynItems.put(slot, stack);
                dynClicks.put(slot, action);
            }
            return this;
        }

        public Builder click(Action action, int slot) {
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
