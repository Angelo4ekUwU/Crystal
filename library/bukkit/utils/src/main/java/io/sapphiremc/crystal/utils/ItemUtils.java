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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public final class ItemUtils {

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount) {
        return createItem(type, amount, null, null, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName) {
        return createItem(type, amount, displayName, null, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final Placeholder[] placeholders) {
        return createItem(type, amount, displayName, null, placeholders, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, @Nullable final String displayName,
                                       @Nullable final List<String> lore) {
        return createItem(type, amount, displayName, lore, null, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount, @Nullable final String displayName,
                                       @Nullable final List<String> lore, @Nullable final Placeholder[] placeholders) {
        return createItem(type, amount, displayName, lore, placeholders, null, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable Enchantment[] enchantments) {
        return createItem(type, amount, displayName, lore, null, enchantments, null);
    }

    @NotNull
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable final Placeholder[] placeholders, @Nullable Enchantment[] enchantments) {
        return createItem(type, amount, displayName, lore, placeholders, enchantments, null);
    }

    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable final String displayName, @Nullable final List<String> lore,
                                       @Nullable Enchantment[] enchantments, @Nullable ItemFlag[] flags) {
        return createItem(type, amount, displayName, lore, null, enchantments, flags);
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static ItemStack createItem(@NotNull final Material type, final int amount,
                                       @Nullable String displayName, @Nullable final List<String> lore,
                                       @Nullable final Placeholder[] placeholders, @Nullable Enchantment[] enchantments, @Nullable ItemFlag[] flags) {
        final var item = new ItemStack(type, Math.max(Math.min(amount, 64), 1));
        final var meta = item.getItemMeta();

        if (displayName != null) {
            if (placeholders != null) {
                for (final var placeholder : placeholders) {
                    if (placeholder != null) {
                        displayName = displayName.replace(placeholder.key(), placeholder.value());
                    }
                }
            }
            item.setDisplayName(TextUtils.stylish(displayName));
            item.setItemMeta(meta);
        }
        if (lore != null) {
            if (placeholders != null) {
                meta.setLore(TextUtils.stylish(lore.stream().map(s -> {
                    for (final var placeholder : placeholders) {
                        if (placeholder != null) {
                            s = s.replace(placeholder.key(), placeholder.value());
                        }
                    }
                    return s;
                }).toList()));
            } else {
                meta.setLore(TextUtils.stylish(lore));
            }
            item.setItemMeta(meta);
        }

        if (enchantments != null) {
            for (final var enchantment : enchantments) {
                if (enchantment != null)
                    item.addEnchant(enchantment, 1, true);
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
        final var head = new ItemStack(Material.PLAYER_HEAD);
        if (texture == null)
            return head;

        final var skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile;

        if (texture.endsWith("=")) {
            profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalCustomHead");
            if (signature == null)
                profile.getProperties().put("textures", new Property("texture", texture.replaceAll("=", "")));
            else
                profile.getProperties().put("textures", new Property("textures", texture, signature));
        } else {
            profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CrystalCustomHead");
            final var encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        }

        try {
            final var profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
            head.setItemMeta(skullMeta);
            return head;
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
            throw new RuntimeException("Reflection error while setting head texture", ex);
        }
    }
}
