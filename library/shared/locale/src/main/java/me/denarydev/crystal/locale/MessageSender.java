/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.locale;

import java.util.UUID;

@Deprecated(forRemoval = true)
public interface MessageSender {

    void sendMessage(UUID uuid, String message);

    void sendMessage(UUID uuid, String... messages);
}
