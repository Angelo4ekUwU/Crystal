/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Message {
    private final MessageSender sender;
    private final boolean isString;
    private final List<Pair> placeholders = new ArrayList<>();
    private String msg;
    private List<String> listMsg;

    Message(MessageSender sender, String msg) {
        this.sender = sender;
        this.msg = msg;
        this.isString = true;
    }

    Message(MessageSender sender, List<String> listMsg) {
        this.sender = sender;
        this.listMsg = listMsg;
        this.isString = false;
    }

    /**
     * Adds placeholders for this message
     *
     * @param placeholders Array of placeholders
     * @return this message
     */
    public Message placeholders(Pair... placeholders) {
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
    public void send(@Nullable UUID target) {
        prepareMessage();
        if (this.isString) {
            sender.sendMessage(target, asString());
        } else {
            sender.sendMessage(target, asList().toArray(new String[0]));
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
                for (final Pair placeholder : placeholders) {
                    msg = msg.replace(placeholder.key(), placeholder.value());
                }
            }
        } else {
            this.listMsg = listMsg.stream().map(s -> {
                if (placeholders.size() > 0) {
                    for (final Pair placeholder : placeholders) {
                        s = s.replace(placeholder.key(), placeholder.value());
                    }
                }
                return s;
            }).collect(Collectors.toList());
        }
    }
}
