/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.configurate.serializers;

import io.sapphiremc.crystal.compatibility.ServerVersion;
import io.sapphiremc.crystal.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final Material material = Material.matchMaterial(node.node("material").getString("AIR"));
        if (material == null)
            throw new SerializationException("Item material cannot be null at " + Arrays.toString(node.path().array()));
        final int amount = node.node("amount").getInt(1);
        final ItemStack stack = new ItemStack(material, amount);
        if (ServerVersion.isServerVersionBelow(ServerVersion.v1_13_R1)) {
            final short durability = (short) node.node("durability").getInt(0);
            if (durability > 0) stack.setDurability(durability);
        }

        if (node.hasChild("meta")) {
            final ConfigurationNode nMeta = node.node("meta");
            final ItemMeta meta = stack.getItemMeta();

            if (nMeta.hasChild("display-name"))
                meta.setDisplayName(TextUtils.stylish(nMeta.node("display-name").getString("empty")));
            if (nMeta.hasChild("lore"))
                meta.setLore(TextUtils.stylish(nMeta.node("lore").getList(String.class, Collections.emptyList())));

            meta.setUnbreakable(nMeta.node("unbreakable").getBoolean(false));
            if (nMeta.hasChild("item-flags"))
                nMeta.node("item-flags").getList(String.class, Collections.emptyList()).stream()
                    .map(this::parseFlag)
                    .filter(Objects::nonNull)
                    .forEach(meta::addItemFlags);

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.v1_13_R1)) {
                if (nMeta.hasChild("custom-model-data"))
                    meta.setCustomModelData(nMeta.node("custom-model-data").getInt(0));
                if (meta instanceof Damageable) ((Damageable) meta).setDamage(nMeta.node("item-damage").getInt(0));
            }

            if (nMeta.hasChild("enchants")) {
                nMeta.node("enchants").childrenMap().forEach(((key, value) -> {
                    final Enchantment ench = Enchantment.getByName(key.toString());
                    if (ench == null) return;
                    final int level = value.getInt();
                    meta.addEnchant(ench, level, true);
                }));
            }

            stack.setItemMeta(meta);
        }

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
            if (ServerVersion.isServerVersionBelow(ServerVersion.v1_13_R1) && stack.getDurability() > 0)
                node.node("durability").set(stack.getDurability());

            if (stack.hasItemMeta()) {
                final ConfigurationNode nMeta = node.node("meta");
                final ItemMeta meta = stack.getItemMeta();

                if (meta.hasDisplayName()) nMeta.node("display-name").set(meta.getDisplayName().replaceAll("§", "&"));
                if (meta.hasLore())
                    nMeta.node("lore").setList(String.class, meta.getLore().stream().map(s -> s.replaceAll("§", "&")).collect(Collectors.toList()));

                if (meta.isUnbreakable()) nMeta.node("unbreakable").set(true);
                if (meta.hasCustomModelData()) nMeta.node("custom-model-data").set(meta.getCustomModelData());
                final List<ItemFlag> flags = new ArrayList<>(meta.getItemFlags());
                if (flags.size() > 0) nMeta.setList(ItemFlag.class, flags);

                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    if (damageable.hasDamage()) {
                        nMeta.node("damage").set(damageable.getDamage());
                    }
                }

                if (meta.hasEnchants())
                    meta.getEnchants().forEach(((enchantment, level) -> node.node("enchantments", enchantment.getKey().getKey())));
            }
        }
    }
}
