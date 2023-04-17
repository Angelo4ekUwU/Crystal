/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.config.serializers;

import io.leangen.geantyref.TypeToken;
import me.denarydev.crystal.nms.CrystalNMS;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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

public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    public static final TypeToken<ItemStack> TYPE = TypeToken.get(ItemStack.class);

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.hasChild("material")) {
            //noinspection DataFlowIssue
            final var material = Material.matchMaterial(node.node("material").getString());
            if (material == null)
                throw new SerializationException("Unknown item material at " + Arrays.toString(node.path().array()));
            int amount = 1;
            if (node.hasChild("amount"))
                amount = node.node("amount").getInt();

            var item = new ItemStack(material, amount);
            final var meta = item.getItemMeta();

            if (node.hasChild("name"))
                meta.displayName(node.node("name").get(Component.class));
            if (node.hasChild("lore"))
                meta.lore(node.node("lore").getList(Component.class, Collections.emptyList()));

            if (node.hasChild("unbreakable"))
                meta.setUnbreakable(node.node("unbreakable").getBoolean(false));
            if (node.hasChild("flags"))
                node.node("flags").getList(String.class, Collections.emptyList()).stream()
                    .map(s -> {
                        try {
                            return ItemFlag.valueOf(s);
                        } catch (IllegalArgumentException ex) {
                            return null;
                        }
                    })
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

            item.setItemMeta(meta);

            if (material.equals(Material.SPAWNER) && node.hasChild("spawner-entity")) {
                final var entity = node.node("spawner-entity").get(EntityType.class);
                System.out.println("Entity = " + entity);
                if (entity != null) {
                    return CrystalNMS.applySpawnerEntity(item, entity);
                } else {
                    throw new SerializationException("Unknown spawner entity type at " + Arrays.toString(node.path().array()));
                }
            }

            return item;
        }
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack item, ConfigurationNode node) throws SerializationException {
        if (item != null) {
            node.node("material").set(item.getType());
            if (item.getAmount() > 1) node.node("amount").set(item.getAmount());

            if (item.hasItemMeta()) {
                final var meta = item.getItemMeta();

                if (meta.hasDisplayName()) node.node("name").set(Component.class, meta.displayName());
                if (meta.hasLore()) node.node("lore").setList(Component.class, meta.lore());

                if (meta.isUnbreakable()) node.node("unbreakable").set(true);
                if (meta.hasCustomModelData()) node.node("custom-model-data").set(meta.getCustomModelData());

                final var flags = new ArrayList<>(meta.getItemFlags());
                if (flags.size() > 0) node.node("flags").setList(ItemFlag.class, flags);

                if (meta.hasEnchants()) {
                    final var enchants = meta.getEnchants();
                    for (final var entry : enchants.entrySet()) {
                        node.node("enchants", entry.getKey().getKey().getKey().toLowerCase()).set(entry.getValue());
                    }
                }

                if (meta instanceof final Damageable damageable) {
                    if (damageable.hasDamage()) {
                        node.node("damage").set(damageable.getDamage());
                    }
                }

                if (item.getType().equals(Material.SPAWNER)) {
                    final var entity = CrystalNMS.getSpawnerEntity(item);
                    if (entity != null) {
                        node.node("spawner-entity").set(entity);
                    }
                }
            }
        }
    }
}
