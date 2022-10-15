/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.utils;

import org.bukkit.entity.Player;

import java.util.logging.Logger;

public final class PermissionUtils {

    public static int getNumberFromPermission(final Player player, final String permission) {
        Logger.getLogger("");
        final var values = player.getEffectivePermissions().stream()
            .filter(info -> info.getPermission().startsWith(permission))
            .map(info -> info.getPermission().substring(permission.length()))
            .map(Integer::parseInt)
            .sorted()
            .toList();
        return values.get(values.size() - 1);
    }
}
