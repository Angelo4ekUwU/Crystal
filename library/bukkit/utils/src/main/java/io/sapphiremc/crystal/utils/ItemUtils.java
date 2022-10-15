/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
}
