/*
 * Copyright (c) 2022 DenaryDev
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class ItemUtils {

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount) {
        return createItem(type, amount, (short) 0, null, null, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability) {
        return createItem(type, amount, durability, null, null, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName) {
        return createItem(type, amount, (short) 0, displayName, null, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable final String displayName) {
        return createItem(type, amount, durability, displayName, null, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final Placeholder[] placeholders) {
        return createItem(type, amount, (short) 0, displayName, null, placeholders, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable final String displayName, @Nullable final Placeholder[] placeholders) {
        return createItem(type, amount, durability, displayName, null, placeholders, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore) {
        return createItem(type, amount, (short) 0, displayName, lore, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable final String displayName, @Nullable final List<String> lore) {
        return createItem(type, amount, durability, displayName, lore, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable final Placeholder[] placeholders) {
        return createItem(type, amount, (short) 0, displayName, lore, placeholders, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable final Placeholder[] placeholders) {
        return createItem(type, amount, durability, displayName, lore, placeholders, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable Enchantment[] enchantments) {
        return createItem(type, amount, (short) 0, displayName, lore, null, enchantments, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable Enchantment[] enchantments) {
        return createItem(type, amount, durability, displayName, lore, null, enchantments, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable final Placeholder[] placeholders, @Nullable Enchantment[] enchantments) {
        return createItem(type, amount, (short) 0, displayName, lore, placeholders, enchantments, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable final Placeholder[] placeholders, @Nullable Enchantment[] enchantments) {
        return createItem(type, amount, durability, displayName, lore, placeholders, enchantments, null);
    }

    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable Enchantment[] enchantments, @Nullable ItemFlag[] flags) {
        return createItem(type, amount, (short) 0, displayName, lore, null, enchantments, flags);
    }

    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable Enchantment[] enchantments, @Nullable ItemFlag[] flags) {
        return createItem(type, amount, durability, displayName, lore, null, enchantments, flags);
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static ItemStack createItem(@NotNull final Material type, final int amount, final short durability,
                                       @Nullable String displayName, @Nullable final List<String> lore,
                                       @Nullable final Placeholder[] placeholders, @Nullable Enchantment[] enchantments, @Nullable ItemFlag[] flags) {
        final ItemStack item = new ItemStack(type, Math.max(Math.min(amount, 64), 1));
        if (durability > 0) item.setDurability(durability);
        final ItemMeta meta = item.getItemMeta();

        if (displayName != null) {
            if (placeholders != null) {
                for (final Placeholder placeholder : placeholders) {
                    if (placeholder != null) {
                        displayName = displayName.replace(placeholder.key(), placeholder.value());
                    }
                }
            }
            meta.setDisplayName(TextUtils.stylish(displayName));
            item.setItemMeta(meta);
        }
        if (lore != null) {
            if (placeholders != null) {
                meta.setLore(TextUtils.stylish(lore.stream().map(s -> {
                    for (final Placeholder placeholder : placeholders) {
                        if (placeholder != null) {
                            s = s.replace(placeholder.key(), placeholder.value());
                        }
                    }
                    return s;
                }).collect(Collectors.toList())));
            } else {
                meta.setLore(TextUtils.stylish(lore));
            }
            item.setItemMeta(meta);
        }

        if (enchantments != null) {
            for (final Enchantment enchantment : enchantments) {
                if (enchantment != null)
                    meta.addEnchant(enchantment, 1, true);
            }
        } else if (flags != null) {
            //noinspection NullableProblems
            item.addItemFlags(flags);
        }

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
        GameProfile profile;

        if (texture.endsWith("=")) {
            profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalCustomHead");
            if (signature == null)
                profile.getProperties().put("textures", new Property("texture", texture.replaceAll("=", "")));
            else
                profile.getProperties().put("textures", new Property("textures", texture, signature));
        } else {
            profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalCustomHead");
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
}
