/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.CrystalPlugin;
import io.sapphiremc.crystal.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class Message {
    private final CrystalPlugin plugin;
    private final boolean isString;
    private final List<Pair> placeholders = new ArrayList<>();
    private String msg;
    private List<String> listMsg;

    Message(@NotNull final CrystalPlugin plugin, @NotNull final String msg) {
        this.plugin = plugin;
        this.msg = msg;
        this.isString = true;
    }

    Message(@NotNull final CrystalPlugin plugin, @NotNull final List<String> listMsg) {
        this.plugin = plugin;
        this.listMsg = listMsg;
        this.isString = false;
    }

    /**
     * Adds placeholders for this message
     *
     * @param placeholders Array of placeholders
     * @return this message
     */
    public Message placeholders(final Pair... placeholders) {
        Collections.addAll(this.placeholders, placeholders);
        return this;
    }

    /**
     * Send this message to the console
     */
    public void send() {
        send(null);
    }

    /**
     * Send this message to the player
     *
     * @param target target
     */
    public void send(@Nullable final UUID target) {
        prepareMessage();
        if (this.isString) {
            plugin.sendMessage(target, asString());
        } else {
            plugin.sendMessage(target, asList().toArray(new String[0]));
        }
    }

    /**
     * @return Formatted string
     */
    @NotNull
    public String asString() {
        prepareMessage();
        return msg;
    }

    /**
     * @return Formatted list
     */
    @NotNull
    public List<String> asList() {
        prepareMessage();
        return listMsg;
    }

    private void prepareMessage() {
        if (isString) {
            if (placeholders.size() > 0) {
                for (final var placeholder : placeholders) {
                    msg = msg.replace(placeholder.key(), placeholder.value());
                }
            }
            msg = TextUtils.stylish(msg);
        } else {
            this.listMsg = listMsg.stream().map(s -> {
                if (placeholders.size() > 0) {
                    for (final var placeholder : placeholders) {
                        s = s.replace(placeholder.key(), placeholder.value());
                    }
                }
                return TextUtils.stylish(s);
            }).toList();
        }
    }
}
