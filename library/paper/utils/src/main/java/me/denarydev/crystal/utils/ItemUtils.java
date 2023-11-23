/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class ItemUtils {

    /**
     * @return new {@link Builder}, that allows you to create
     * an {@link ItemStack} with custom parameters
     */
    public static Builder itemBuilder() {
        return new Builder();
    }

    @NotNull
    public static ItemStack getCustomHead(final String texture) {
        return getCustomHead(null, texture, 1);
    }

    @NotNull
    public static ItemStack getCustomHead(final String texture, final int amount) {
        return getCustomHead(null, texture, amount);
    }

    @NotNull
    public static ItemStack getCustomHead(final String signature, final String texture) {
        return getCustomHead(signature, texture, 1);
    }

    @NotNull
    public static ItemStack getCustomHead(final String signature, final String texture, final int amount) {
        final var head = new ItemStack(Material.PLAYER_HEAD, Math.max(Math.min(amount, 64), 1));
        if (texture == null)
            return head;

        final var skullMeta = (SkullMeta) head.getItemMeta();
        final var profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalCustomHead");

        if (texture.endsWith("=")) {
            if (signature == null) {
                profile.setProperty(new ProfileProperty("textures", texture.replaceAll("=", "")));
            } else {
                profile.setProperty(new ProfileProperty("textures", texture, signature));
            }
        } else {
            final byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            profile.setProperty(new ProfileProperty("textures", new String(encodedData)));
        }

        skullMeta.setPlayerProfile(profile);
        head.setItemMeta(skullMeta);
        return head;
    }

    /**
     * Builder for {@link ItemStack}
     */
    public static class Builder {
        private ItemStack item;

        private Material type;
        private String texture;
        private int amount;
        private Component displayname;
        private List<Component> lore;
        private final Map<Enchantment, Integer> enchantments = new HashMap<>();
        private ItemFlag[] itemFlags;
        private boolean unbreakable;
        private Integer customModelData;
        private int damage;
        private Consumer<? super ItemMeta> metaEditor;
        private final Map<NamespacedKey, Object> persistentData = new HashMap<>();

        protected Builder() {
            // Do nothing
        }

        public Builder type(Material type) {
            this.type = type;
            return this;
        }

        public Builder texture(String texture) {
            this.texture = texture;
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
            this.enchantments.putAll(enchantments);
            return this;
        }

        public Builder enchantment(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public Builder itemFlags(ItemFlag... flags) {
            this.itemFlags = flags;
            return this;
        }

        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        public Builder customModelData(Integer data) {
            this.customModelData = data;
            return this;
        }

        public Builder damage(int damage) {
            this.damage = damage;
            return this;
        }

        public Builder metaEditor(Consumer<? super ItemMeta> editor) {
            this.metaEditor = editor;
            return this;
        }

        public Builder persistentData(NamespacedKey key, Object value) {
            this.persistentData.put(key, value);
            return this;
        }

        public ItemStack build() {
            if (item == null) {
                amount = Math.max(Math.min(amount, 64), 1);
                if (texture != null) {
                    item = getCustomHead(texture, amount);
                }
                item = new ItemStack(type, amount);
            }

            if (metaEditor != null)
                item.editMeta(metaEditor);

            final var meta = item.getItemMeta();

            if (displayname != null)
                meta.displayName(displayname);
            if (lore != null)
                meta.lore(lore);

            if (!enchantments.isEmpty()) {
                enchantments.forEach((ench, lvl) -> {
                    if (ench != null && !meta.hasEnchant(ench) && lvl > 0) meta.addEnchant(ench, lvl, true);
                });
            }

            if (itemFlags != null)
                meta.addItemFlags(itemFlags);

            if (customModelData != null)
                meta.setCustomModelData(customModelData);

            if (damage > 0 && meta instanceof Damageable damageable)
                damageable.setDamage(damage);

            meta.setUnbreakable(unbreakable);

            if (!persistentData.isEmpty()) {
                final var container = meta.getPersistentDataContainer();
                persistentData.forEach((key, value) -> {
                    if (value instanceof String s) container.set(key, PersistentDataType.STRING, s);
                    else if (value instanceof Byte b) container.set(key, PersistentDataType.BYTE, b);
                    else if (value instanceof Short s) container.set(key, PersistentDataType.SHORT, s);
                    else if (value instanceof Integer i) container.set(key, PersistentDataType.INTEGER, i);
                    else if (value instanceof Long l) container.set(key, PersistentDataType.LONG, l);
                    else if (value instanceof Float f) container.set(key, PersistentDataType.FLOAT, f);
                    else if (value instanceof Double d) container.set(key, PersistentDataType.DOUBLE, d);
                    else if (value instanceof byte[] ba) container.set(key, PersistentDataType.BYTE_ARRAY, ba);
                    else if (value instanceof int[] ia) container.set(key, PersistentDataType.INTEGER_ARRAY, ia);
                    else if (value instanceof long[] la) container.set(key, PersistentDataType.LONG_ARRAY, la);
                });
            }

            item.setItemMeta(meta);
            return item;
        }
    }
}
