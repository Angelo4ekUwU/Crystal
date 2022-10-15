/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.sapphiremc.crystal.gui.events.GuiClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GUI {

    private final Player target;
    private final String title;
    private final int size;
    private final InventoryType type;

    private Listener listener;
    private Inventory inventory;

    private final Map<String, Item> items = new HashMap<>();

    public GUI(@NotNull final Plugin plugin, @NotNull final Player target) {
        this(plugin, target, null, 9, null);
    }

    public GUI(@NotNull final Plugin plugin, @NotNull final Player target, @NotNull final String title) {
        this(plugin, target, title, 9, null);
    }

    public GUI(@NotNull final Plugin plugin, @NotNull final Player target, @NotNull final String title, final int size) {
        this(plugin, target, title, size, null);
    }

    public GUI(@NotNull final Plugin plugin, @NotNull final Player target, @NotNull final String title, @NotNull final InventoryType type) {
        this(plugin, target, title, -1, type);
    }

    private GUI(@NotNull final Plugin plugin, @NotNull final Player target, @Nullable final String title, final int size, @Nullable final InventoryType type) {
        this.target = target;
        this.title = title;
        this.size = size;
        this.type = type;

        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(final InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player whoClicked
                    && inventory != null && event.getInventory().equals(inventory) && whoClicked.getUniqueId().equals(target.getUniqueId())) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

                    final var itemId = new NBTItem(event.getCurrentItem()).getString("ItemId");

                    if (items.containsKey(itemId)) {
                        final var item = items.get(itemId);
                        if (item.getClickHandler() != null) {
                            final var clickEvent = new GuiClickEvent(event.getClick(), event.getSlot(), event.getCurrentItem());
                            item.getClickHandler().onClick(clickEvent);

                            if (!clickEvent.isCancelled()) {
                                event.setCancelled(false);
                            }

                            if (clickEvent.isCloseInventory()) {
                                event.getWhoClicked().closeInventory();
                            }

                            if (clickEvent.isDestroyInventory()) {
                                destroy();
                            }
                        }
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player && event.getInventory().equals(inventory)) {
                    inventory.clear();
                    destroy();
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().getUniqueId().equals(target.getUniqueId())) {
                    destroy();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public void addItem(final Item item) {
        items.put(item.getId(), item);
    }

    @SuppressWarnings("deprecation")
    public Inventory createInventory() {
        final Inventory inv;
        if (type == null) {
            inv = title == null ? Bukkit.createInventory(null, size) :
                Bukkit.createInventory(null, size, title);
        } else {
            inv = title == null ? Bukkit.createInventory(null, type) :
                Bukkit.createInventory(null, type, title);
        }

        items.values().forEach(item -> {
            final var slots = item.getSlots();
            if (slots.length > 1) {
                Arrays.stream(slots).forEachOrdered(slot -> inv.setItem(slot, item.getItemStack()));
            } else {
                inv.setItem(slots[0], item.getItemStack());
            }
        });
        return inv;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Map<String, Item> getItems() {
        return items;
    }

    public void open() {
        this.inventory = createInventory();
        target.openInventory(inventory);
    }

    public void destroy() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }
}
