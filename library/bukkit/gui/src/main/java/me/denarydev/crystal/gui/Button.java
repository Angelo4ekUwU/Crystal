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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Button(ItemStack item, int[] slots, @Nullable ClickAction action) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ItemStack item;

        private int[] slots;
        private ClickAction action;

        private Material type;
        private int amount;
        private Component displayname;
        private List<Component> lore;
        private Map<Enchantment, Integer> enchantments = new HashMap<>();
        private ItemFlag[] itemFlags;
        private boolean unbreakable;
        private Integer customModelData;
        private int damage;

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

        public Builder action(ClickAction action) {
            this.action = action;
            return this;
        }

        public Builder itemStack(ItemStack item) {
            this.item = item;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder displayname(Component displayname) {
            this.displayname = displayname;
            return this;
        }

        public Builder displaynameRich(String displayname, TagResolver... tags) {
            this.displayname = MiniMessage.miniMessage().deserialize(displayname, tags);
            return this;
        }

        public Builder displaynamePlain(String displayname) {
            this.displayname = PlainTextComponentSerializer.plainText().deserialize(displayname);
            return this;
        }

        public Builder lore(List<Component> lore) {
            this.lore = lore;
            return this;
        }

        public Builder loreRich(List<String> lore, TagResolver... tags) {
            this.lore = lore.stream().map(s -> MiniMessage.miniMessage().deserialize(s, tags)).toList();
            return this;
        }

        public Builder lorePlain(List<String> lore) {
            this.lore = new ArrayList<>(lore.stream().map(s -> PlainTextComponentSerializer.plainText().deserialize(s)).toList());
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

        public Builder customModelData(Integer customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder damage(int damage) {
            this.damage = damage;
            return this;
        }

        public Button build() {
            if (item == null) item = new ItemStack(type, Math.max(Math.min(amount, 64), 1));
            final var meta = item.getItemMeta();

            if (displayname != null)
                meta.displayName(displayname);
            if (lore != null)
                meta.lore(lore);

            if (enchantments != null) {
                enchantments.forEach((ench, lvl) -> {
                    if (ench != null && lvl > 0) meta.addEnchant(ench, lvl, true);
                });
            }

            if (itemFlags != null)
                item.addItemFlags(itemFlags);

            if (customModelData != null)
                meta.setCustomModelData(customModelData);

            if (damage > 0 && meta instanceof Damageable damageable)
                damageable.setDamage(damage);

            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);

            return new Button(item, slots, action);
        }
    }
}
