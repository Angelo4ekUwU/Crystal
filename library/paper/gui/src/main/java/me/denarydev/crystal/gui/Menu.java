/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Menu implements InventoryHolder {

    private final Template template;
    private final long clickCooldown;
    private final Inventory inventory;
    @Nullable
    private final ClickAction clickAction;
    @Nullable
    private final CloseAction closeAction;
    private final Map<Integer, ClickAction> clickActions = new HashMap<>();

    private Player viewer;

    public Menu(Template template, long clickCooldown, @Nullable ClickAction clickAction, @Nullable CloseAction closeAction, Map<Integer, ItemStack> dynamicItems, Map<Integer, ClickAction> dynamicActions) {
        this.template = template;
        this.clickCooldown = clickCooldown;
        this.clickAction = clickAction;
        this.closeAction = closeAction;
        this.inventory = template.type() != null ?
            Bukkit.createInventory(this, template.type(), template.title()) :
            Bukkit.createInventory(this, template.size(), template.title());
        template.items().forEach((slot, item) -> itemInternal(item, false, slot));
        dynamicItems.forEach((slot, item) -> itemInternal(item, false, slot));
        dynamicActions.forEach((slot, action) -> actionInternal(action, slot));
    }

    public static Builder builder(@NotNull Template template) {
        return new Builder(template);
    }

    @NotNull
    public Template template() {
        return template;
    }

    @Nullable
    public Player viewer() {
        return viewer;
    }

    public boolean hasItem(int slot) {
        return inventory.getItem(slot) != null;
    }

    public void item(@NotNull ItemStack item, int... slots) {
        itemInternal(item, false, slots);
    }

    public void item(@NotNull ItemStack item, boolean update, int... slots) {
        itemInternal(item, update, slots);
    }

    public void item(@NotNull ItemStack item, @Nullable ClickAction action, int... slots) {
        itemInternal(item, false, slots);
        actionInternal(action, slots);
    }

    public void item(@NotNull ItemStack item, @Nullable ClickAction action, boolean update, int... slots) {
        itemInternal(item, update, slots);
        actionInternal(action, slots);
    }

    public void action(@Nullable ClickAction action, int... slots) {
        actionInternal(action, slots);
    }

    public void open(@NotNull Player viewer) {
        this.viewer = viewer;
        viewer.openInventory(inventory);
    }

    public void update() {
        if (viewer == null) return;
        viewer.updateInventory();
    }

    public long clickCooldown() {
        return clickCooldown;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    void clickInternal(InventoryClickEvent event) {
        if (clickAction != null) clickAction.click(event);
        final var action = clickActions.get(event.getSlot());

        if (action != null) {
            action.click(event);
        }
    }

    void closeInternal(InventoryCloseEvent event) {
        if (closeAction != null) {
            closeAction.close(event);
        }
    }

    private void itemInternal(ItemStack item, boolean update, int... slots) {
        for (final int slot : slots) {
            if (slot < 0 || slot >= template.size()) continue;
            inventory.setItem(slot, item);
        }
        if (update) update();
    }

    private void actionInternal(ClickAction action, int... slots) {
        for (final int slot : slots) {
            if (slot < 0 || slot >= template.size()) continue;
            clickActions.put(slot, action);
        }
    }

    public static class Builder {

        protected final Template template;
        protected final Map<Integer, ItemStack> dynamicItems = new HashMap<>();
        protected final Map<Integer, ClickAction> dynamicActions = new HashMap<>();
        protected long clickCooldown;
        protected ClickAction clickAction;
        protected CloseAction closeAction;

        public Builder(@NotNull Template template) {
            this.template = template;
        }

        public Builder title(Component title) {
            this.template.title(title);
            return this;
        }

        public Builder titleRich(String title, TagResolver... resolvers) {
            this.template.title(MiniMessage.miniMessage().deserialize(title, resolvers));
            return this;
        }

        public Builder titlePlain(String title) {
            this.template.title(PlainTextComponentSerializer.plainText().deserialize(title));
            return this;
        }

        public Builder size(int size) {
            this.template.size(size);
            return this;
        }

        public Builder rows(int rows) {
            this.template.size(rows * 9);
            return this;
        }

        public Builder item(@NotNull ItemStack item, int @NotNull ... slots) {
            for (final int slot : slots) {
                dynamicItems.put(slot, item);
            }
            return this;
        }

        public Builder item(@NotNull ItemStack item, @NotNull ClickAction action, int @NotNull ... slots) {
            for (final int slot : slots) {
                dynamicItems.put(slot, item);
                dynamicActions.put(slot, action);
            }
            return this;
        }

        public Builder action(@NotNull ClickAction action, int... slots) {
            if (slots.length > 0) {
                for (final int slot : slots) {
                    dynamicActions.put(slot, action);
                }
            } else {
                this.clickAction = action;
            }
            return this;
        }

        public Builder clickCooldown(long Cooldown) {
            this.clickCooldown = Cooldown;
            return this;
        }

        public Builder closeAction(@Nullable CloseAction closeAction) {
            this.closeAction = closeAction;
            return this;
        }

        public Menu build() {
            return new Menu(template, clickCooldown, clickAction, closeAction, dynamicItems, dynamicActions);
        }
    }
}
