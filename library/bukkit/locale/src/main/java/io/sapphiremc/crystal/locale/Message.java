/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import io.sapphiremc.crystal.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Message {
    private final boolean isString;
    private final List<Pair> placeholders = new ArrayList<>();
    private String msg;
    private List<String> listMsg;

    Message(@NotNull final String msg) {
        this.isString = true;
        this.msg = msg;
    }

    Message(@NotNull final List<String> listMsg) {
        this.isString = false;
        this.listMsg = listMsg;
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
     * Send this message to the {@link CommandSender}
     *
     * @param target target
     */
    public void send(@NotNull final CommandSender target) {
        prepareMessage();
        if (this.isString) {
            target.sendMessage(asString());
        } else {
            target.sendMessage(asList().toArray(new String[0]));
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
