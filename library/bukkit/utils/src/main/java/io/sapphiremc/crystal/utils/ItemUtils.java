/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.sapphiremc.crystal.compatibility.ServerVersion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ItemUtils {

    /**
     * @return new {@link ItemBuilder}, that allows you to create
     * an {@link ItemStack} with custom parameters
     */
    public static ItemBuilder itemBuilder() {
        return new ItemBuilder();
    }

    @NotNull
    @SuppressWarnings("deprecation")
    private static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                        @Nullable String displayname, @Nullable final List<String> lore,
                                        @Nullable final Map<Enchantment, Integer> enchantments, @Nullable final ItemFlag[] itemFlags,
                                        final boolean unbreakable, @Nullable final Integer customModelData) {
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
        return item;
    }

    @NotNull
    public static ItemStack getCustomHead(final String texture) {
        return getCustomHead(null, texture);
    }

    @NotNull
    public static ItemStack getCustomHead(final String signature, final String texture) {
        final ItemStack head = new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.v1_13_R1) ? Material.PLAYER_HEAD : Material.getMaterial("SKULL"));
        if (texture == null)
            return head;

        final SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalCustomHead");

        if (texture.endsWith("=")) {
            if (signature == null) {
                profile.getProperties().put("textures", new Property("texture", texture.replaceAll("=", "")));
            } else {
                profile.getProperties().put("textures", new Property("textures", texture, signature));
            }
        } else {
            final byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        }

        try {
            final Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
            head.setItemMeta(skullMeta);
            return head;
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
            throw new RuntimeException("Reflection error while setting head texture", ex);
        }
    }

    /**
     * Builder for {@link ItemStack}
     */
    public static class ItemBuilder {
        private Material type;
        private int amount;
        private short durability;
        private String displayname;
        private List<String> lore;
        private Map<Enchantment, Integer> enchantments = new HashMap<>();
        private ItemFlag[] itemFlags;
        private boolean unbreakable;
        private Integer customModelData;

        public ItemBuilder type(Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder durability(short durability) {
            this.durability = durability;
            return this;
        }

        public ItemBuilder displayname(String displayname) {
            this.displayname = displayname;
            return this;
        }

        public ItemBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder enchantments(Enchantment... enchantments) {
            for (final Enchantment enchantment : enchantments) {
                this.enchantments.put(enchantment, 1);
            }
            return this;
        }

        public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        public ItemBuilder enchantment(Enchantment enchantment, int level) {
            this.enchantments.put(enchantment, level);
            return this;
        }

        public ItemBuilder itemFlags(ItemFlag... itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        public ItemBuilder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        /**
         * @since Minecraft 1.14
         */
        public ItemBuilder customModelData(Integer customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public ItemStack build() {
            return createItem(type, amount, durability, displayname, lore, enchantments, itemFlags, unbreakable, customModelData);
        }
    }
}
