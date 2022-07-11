/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

@UtilityClass
public class ItemUtils {

    public ItemStack addGlow(ItemStack item) {
        item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.getNBT(true).setBoolean("glowing", true);

        return item;
    }

    public ItemStack removeGlow(ItemStack item) {
        if (item.getNBT().hasKey("glowing") && item.getNBT().getBoolean("glowing")) {
            item.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
            ItemMeta meta = item.getItemMeta();
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.getNBT(true).removeKey("glowing");
        }
        return item;
    }

    public static ItemStack getCustomHead(String texture) {
        return getCustomHead(null, texture);
    }

    public static ItemStack getCustomHead(String signature, String texture) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        GameProfile gameProfile;
        if (texture.endsWith("=")) {
            gameProfile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CustomHead");

            if (signature == null) {
                gameProfile.getProperties().put("textures", new Property("texture", texture.replaceAll("=", "")));
            } else {
                gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
            }
        } else {
            gameProfile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "CustomHead");
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", texture).getBytes());
            gameProfile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        }

        try {
            Field profileField;
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
            skullItem.setItemMeta(skullMeta);

            return skullItem;
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
            throw new RuntimeException("Reflection error while setting head texture", ex);
        }
    }

}
