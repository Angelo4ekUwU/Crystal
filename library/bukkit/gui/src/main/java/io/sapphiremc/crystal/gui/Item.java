/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sapphiremc.crystal.gui.interfaces.ClickHandler;
import io.sapphiremc.crystal.utils.ItemUtils;
import io.sapphiremc.crystal.utils.Placeholder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class Item {
    private final String id;
    private final int[] slots;
    private final ItemStack itemStack;
    private final ClickHandler clickHandler;

    private Item(final String id, final int[] slots, final ItemStack itemStack, final ClickHandler clickHandler) {
        this.id = id;
        this.slots = slots;
        this.itemStack = itemStack;
        this.clickHandler = clickHandler;
    }

    private Item(final String id, final int[] slots, final Material material, final int amount, String displayName, List<String> lore, Placeholder[] placeholders, final Enchantment[] enchantments, final ItemFlag[] flags, final ClickHandler clickHandler) {
        this.id = id;
        this.slots = slots;

        final var nbtItem = new NBTItem(ItemUtils.createItem(material, amount, displayName, lore, placeholders, enchantments, flags));
        nbtItem.setString("ItemId", id);
        this.itemStack = nbtItem.getItem();

        this.clickHandler = clickHandler;
    }

    public static ItemBuilder builder(final String itemId) {
        return new ItemBuilder(itemId);
    }

    public static Item fromStack(final String id, final ItemStack stack, final int... slots) {
        return new Item(id, slots, stack, null);
    }

    public static Item fromStack(final String id, final ItemStack stack, final ClickHandler clickHandler, final int... slots) {
        return new Item(id, slots, stack, clickHandler);
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int @NotNull [] getSlots() {
        return slots;
    }

    @NotNull
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Nullable
    public ClickHandler getClickHandler() {
        return this.clickHandler;
    }

    public static class ItemBuilder {

        private final String id;
        private int[] slots;
        private Material type;
        private int amount;
        private String displayName;
        private List<String> lore;
        private Placeholder[] placeholders;
        private Enchantment[] enchantments;
        private ItemFlag[] flags;
        private ClickHandler onClick;

        private ItemBuilder(final String id) {
            this.id = id;
        }

        public ItemBuilder slots(final int... slots) {
            this.slots = slots;
            return this;
        }

        public ItemBuilder range(final int start, final int end) {
            this.slots = IntStream.rangeClosed(start, end).toArray();
            return this;
        }

        public ItemBuilder type(final Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder amount(final int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder displayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public ItemBuilder lore(final List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder placeholders(final Placeholder... placeholders) {
            this.placeholders = placeholders;
            return this;
        }

        public ItemBuilder enchantments(final Enchantment... enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        public ItemBuilder flags(final ItemFlag... flags) {
            this.flags = flags;
            return this;
        }

        public ItemBuilder clickHandler(final ClickHandler onClick) {
            this.onClick = onClick;
            return this;
        }

        public ItemBuilder fromItemStack(final ItemStack item) {
            this.type = item.getType();
            this.amount = item.getAmount();

            if (item.hasItemMeta()) {
                final var meta = item.getItemMeta();
                if (meta.hasDisplayName()) displayName = meta.getDisplayName();
                if (meta.hasLore()) lore = meta.getLore();
            }

            if (item.hasEnchants()) enchantments = item.getEnchants().keySet().toArray(new Enchantment[0]);
            this.flags = item.getItemFlags().toArray(new ItemFlag[0]);

            return this;
        }

        public Item build() {
            if (id == null || type == null) return null;

            return new Item(id, slots, type, amount, displayName, lore, placeholders, enchantments, flags, onClick);
        }
    }
}
