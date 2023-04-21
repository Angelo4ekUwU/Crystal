/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.nms;

import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class CrystalNMS {

    public static ItemStack applySpawnerEntity(final ItemStack item, final EntityType entity) {
        final var stack = CraftItemStack.asNMSCopy(item);

        final var tag = stack.getOrCreateTag();
        final var beTag = tag.getCompound("BlockEntityTag");
        final var sdTag = beTag.getCompound("SpawnData");
        final var eTag = sdTag.getCompound("entity");

        eTag.putString("id", "minecraft:" + entity.name().toLowerCase());
        sdTag.put("entity", eTag);
        beTag.put("SpawnData", sdTag);
        beTag.putString("id", "minecraft:mob_spawner");
        tag.put("BlockEntityTag", beTag);
        stack.setTag(tag);
        return stack.getBukkitStack();
    }

    public static EntityType getSpawnerEntity(final ItemStack item) {
        final var stack = CraftItemStack.asNMSCopy(item);

        final var tag = stack.getOrCreateTag();
        if (tag.contains("BlockEntityTag")) {
            final var beTag = tag.getCompound("BlockEntityTag");
            if (beTag.contains("SpawnData")) {
                final var sdTag = beTag.getCompound("SpawnData");
                if (sdTag.contains("entity")) {
                    final var eTag = sdTag.getCompound("entity");
                    if (eTag.contains("id")) {
                        final var s = eTag.getString("id").replace("minecraft:", "");
                        try {
                            return EntityType.valueOf(s.toUpperCase());
                        } catch (IllegalArgumentException ex) {
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }
}
