/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class Template {

    private final String title;
    private final int size;
    private final InventoryType type;
    private final Map<Integer, ItemStack> items;

    private Template(String title, int size, InventoryType type, Map<Integer, ItemStack> items) {
        this.title = title;
        this.size = size;
        this.type = type;
        this.items = items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public InventoryType getType() {
        return type;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public static class Builder {

        private String title;
        private int size;
        private InventoryType type;
        private final Map<Integer, ItemStack> items = new HashMap<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder size(int size) {

            this.size = size;
            return this;
        }

        public Builder rows(int rows) {
            this.size = rows * 9;
            return this;
        }

        public Builder type(InventoryType type) {
            this.type = type;
            return this;
        }

        public Builder item(ItemStack item, int... slots) {
            for (int slot : slots) {
                items.put(slot, item);
            }
            return this;
        }

        public Builder item(Item item) {
            final ItemStack stack = item.getItem();
            final int[] slots = item.getSlots();
            for (int slot : slots) {
                items.put(slot, stack);
            }
            return this;
        }

        public Template build() {
            return new Template(title, size, type, items);
        }
    }
}
