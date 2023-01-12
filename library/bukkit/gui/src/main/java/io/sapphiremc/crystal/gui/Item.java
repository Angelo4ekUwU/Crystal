/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import io.sapphiremc.crystal.compatibility.ServerVersion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item {

    private final ItemStack item;
    private final int[] slots;

    public Item(ItemStack item, int[] slots) {
        this.item = item;
        this.slots = slots;
    }

    public static Builder builder() {
        return new Builder();
    }

    ItemStack getItem() {
        return item;
    }

    int[] getSlots() {
        return slots;
    }

    public static class Builder {
        private int[] slots;

        private Material type;
        private int amount;
        private short durability;
        private String displayname;
        private List<String> lore;
        private Map<Enchantment, Integer> enchantments = new HashMap<>();
        private ItemFlag[] itemFlags;
        private boolean unbreakable;
        private Integer customModelData;

        public Builder type(Material type) {
            this.type = type;
            return this;
        }

        public Builder slot(int slot) {
            this.slots = new int[]{slot};
            return this;
        }

        public Builder slots(int... slots) {
            this.slots = slots;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder durability(short durability) {
            this.durability = durability;
            return this;
        }

        public Builder displayname(String displayname) {
            this.displayname = displayname;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder enchantments(Enchantment... enchantments) {
            for (final Enchantment enchantment : enchantments) {
                this.enchantments.put(enchantment, 1);
            }
            return this;
        }

        public Builder enchantments(Map<Enchantment, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        public Builder enchantment(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public Builder itemFlags(ItemFlag... itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        /**
         * @since Minecraft 1.14
         */
        public Builder customModelData(Integer customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        @SuppressWarnings("deprecation")
        public Item build() {
            final ItemStack item = new ItemStack(type, Math.max(Math.min(amount, 64), 1));
            final ItemMeta meta = item.getItemMeta();

            if (durability > 0)
                item.setDurability(durability);

            if (displayname != null)
                meta.setDisplayName(displayname);
            if (lore != null)
                meta.setLore(lore);

            if (enchantments != null) {
                for (final Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    if (entry != null)
                        meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }

            if (itemFlags != null)
                item.addItemFlags(itemFlags);

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.v1_14_R1) && customModelData != null)
                meta.setCustomModelData(customModelData);

            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);

            return new Item(item, slots);
        }
    }
}
