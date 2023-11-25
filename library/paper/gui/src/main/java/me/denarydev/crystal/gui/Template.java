/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class Template {

    private Component title;
    private int size;
    private final InventoryType type;
    private final Map<Integer, ItemStack> items;

    private Template(Component title, int size, InventoryType type, Map<Integer, ItemStack> items) {
        this.title = title;
        this.size = size;
        this.type = type;
        this.items = items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Component title() {
        return title;
    }

    public int size() {
        return size;
    }

    public InventoryType type() {
        return type;
    }

    public Map<Integer, ItemStack> items() {
        return items;
    }

    void title(Component title) {
        this.title = title;
    }

    void size(int size) {
        this.size = size;
    }

    public static class Builder {

        private Component title;
        private int size;
        private InventoryType type;
        private final Map<Integer, ItemStack> items = new HashMap<>();

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder titleRich(String title, TagResolver... resolvers) {
            this.title = MiniMessage.miniMessage().deserialize(title, resolvers);
            return this;
        }

        public Builder titlePlain(String title) {
            this.title = PlainTextComponentSerializer.plainText().deserialize(title);
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
            for (final int slot : slots) {
                items.put(slot, item);
            }
            return this;
        }

        public Template build() {
            return new Template(title, size, type, items);
        }
    }
}
