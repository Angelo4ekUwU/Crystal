/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import me.denarydev.crystal.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var material = Material.matchMaterial(node.node("material").getString("AIR"));
        if (material == null)
            throw new SerializationException("Item material cannot be null at " + Arrays.toString(node.path().array()));
        final int amount = node.node("amount").getInt(1);
        final var stack = new ItemStack(material, amount);

        final var meta = stack.getItemMeta();

        if (node.hasChild("name"))
            meta.setDisplayName(TextUtils.stylish(node.node("name").getString("empty")));
        if (node.hasChild("lore"))
            meta.setLore(TextUtils.stylish(node.node("lore").getList(String.class, Collections.emptyList())));

        meta.setUnbreakable(node.node("unbreakable").getBoolean(false));
        if (node.hasChild("flags"))
            node.node("flags").getList(String.class, Collections.emptyList()).stream()
                .map(this::parseFlag)
                .filter(Objects::nonNull)
                .forEach(meta::addItemFlags);

        if (node.hasChild("custom-model-data"))
            meta.setCustomModelData(node.node("custom-model-data").getInt(0));
        if (meta instanceof Damageable damageable) damageable.setDamage(node.node("damage").getInt(0));

        if (node.hasChild("enchants")) {
            node.node("enchants").childrenMap().forEach(((key, value) -> {
                final var ench = Enchantment.getByKey(NamespacedKey.minecraft(key.toString().toLowerCase()));
                if (ench == null) return;
                final int level = value.getInt();
                meta.addEnchant(ench, level, true);
            }));
        }

        stack.setItemMeta(meta);

        return stack;
    }

    private ItemFlag parseFlag(String s) {
        try {
            return ItemFlag.valueOf(s);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack stack, ConfigurationNode node) throws SerializationException {
        if (stack != null) {
            node.node("material").set(stack.getType());
            if (stack.getAmount() > 1) node.node("amount").set(stack.getAmount());

            if (stack.hasItemMeta()) {
                final var meta = stack.getItemMeta();

                if (meta.hasDisplayName()) node.node("name").set(meta.getDisplayName().replaceAll("§", "&"));
                if (meta.hasLore())
                    node.node("lore").setList(String.class, meta.getLore().stream().map(s -> s.replaceAll("§", "&")).collect(Collectors.toList()));

                if (meta.isUnbreakable()) node.node("unbreakable").set(true);
                if (meta.hasCustomModelData()) node.node("custom-model-data").set(meta.getCustomModelData());
                final var flags = new ArrayList<>(meta.getItemFlags());
                if (flags.size() > 0) node.node("flags").setList(ItemFlag.class, flags);

                if (meta instanceof final Damageable damageable) {
                    if (damageable.hasDamage()) {
                        node.node("damage").set(damageable.getDamage());
                    }
                }

                if (meta.hasEnchants())
                    meta.getEnchants().forEach(((enchantment, level) -> node.node("enchants", enchantment.getKey().getKey())));
            }
        }
    }
}
