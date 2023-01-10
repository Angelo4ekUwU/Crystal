/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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
        private Enchantment[] enchantments;
        private ItemFlag[] itemFlags;

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
            this.enchantments = enchantments;
            return this;
        }

        public Builder itemFlags(ItemFlag... itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        public Item build() {
            final ItemStack item = new ItemStack(type, Math.max(Math.min(amount, 64), 1));
            if (durability > 0) item.setDurability(durability);
            final ItemMeta meta = item.getItemMeta();

            if (displayname != null) {
                meta.setDisplayName(displayname);
                item.setItemMeta(meta);
            }
            if (lore != null) {
                meta.setLore(lore);
                item.setItemMeta(meta);
            }

            if (enchantments != null) {
                for (final Enchantment enchantment : enchantments) {
                    if (enchantment != null)
                        meta.addEnchant(enchantment, 1, true);
                }
            } else if (itemFlags != null) {
                item.addItemFlags(itemFlags);
            }

            return new Item(item, slots);
        }
    }
}
