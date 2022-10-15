/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.configurate.serializers;

import io.sapphiremc.crystal.utils.TextUtils;
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

@SuppressWarnings("deprecation")
public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var material = node.node("material").get(Material.class);
        if (material == null)
            throw new SerializationException("Item material cannot be null at " + Arrays.toString(node.path().array()));
        final var amount = node.node("amount").getInt(1);
        final var stack = new ItemStack(material, amount);

        if (node.hasChild("meta")) {
            final var nMeta = node.node("meta");
            final var meta = stack.getItemMeta();

            if (node.hasChild("display-name"))
                meta.setDisplayName(TextUtils.stylish(nMeta.node("display-name").getString(null)));
            if (node.hasChild("lore"))
                meta.setLore(TextUtils.stylish(nMeta.node("lore").getList(String.class, Collections.emptyList())));

            meta.setUnbreakable(nMeta.node("unbreakable").getBoolean(false));
            if (nMeta.hasChild("custom-model-data")) meta.setCustomModelData(nMeta.node("custom-model-data").getInt(0));
            if (nMeta.hasChild("item-flags"))
                nMeta.node("item-flags").getList(ItemFlag.class, Collections.emptyList()).forEach(meta::addItemFlags);

            if (meta instanceof Damageable damageable) damageable.setDamage(nMeta.node("item-damage").getInt(0));

            if (nMeta.hasChild("enchants")) {
                nMeta.node("enchants").childrenMap().forEach(((key, value) -> {
                    final var ench = Enchantment.getByKey(NamespacedKey.minecraft(key.toString().toLowerCase()));
                    if (ench == null) return;
                    final var level = value.getInt();
                    meta.addEnchant(ench, level, true);
                }));
            }

            stack.setItemMeta(meta);
        }

        return stack;
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack stack, ConfigurationNode node) throws SerializationException {
        if (stack != null) {
            node.node("material").set(stack.getType());
            if (stack.getAmount() > 1) node.node("amount").set(stack.getAmount());

            if (stack.hasItemMeta()) {
                final var nMeta = node.node("meta");
                final var meta = stack.getItemMeta();

                if (meta.hasDisplayName()) nMeta.node("display-name").set(meta.getDisplayName().replaceAll("§", "&"));
                if (meta.hasLore())
                    nMeta.node("lore").setList(String.class, meta.getLore().stream().map(s -> s.replaceAll("§", "&")).toList());

                if (meta.isUnbreakable()) nMeta.node("unbreakable").set(true);
                if (meta.hasCustomModelData()) nMeta.node("custom-model-data").set(meta.getCustomModelData());
                final var flags = new ArrayList<>(meta.getItemFlags());
                if (flags.size() > 0) nMeta.setList(ItemFlag.class, flags);

                if (meta instanceof Damageable damageable && damageable.hasDamage())
                    nMeta.node("damage").set(damageable.getDamage());

                if (meta.hasEnchants())
                    meta.getEnchants().forEach(((enchantment, level) -> node.node("enchantments", enchantment.getKey().getKey())));
            }
        }
    }
}
