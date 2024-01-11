/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author DenaryDev
 * @since 13:47 25.11.2023
 */
public class TextUtils {

    @NotNull
    public static String capitalize(@NotNull final String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @NotNull
    public static List<String> capitalizeAll(@NotNull final List<String> text) {
        return text.stream().map(TextUtils::capitalize).toList();
    }

    @NotNull
    public static String[] capitalizeAll(@NotNull final String... text) {
        return Arrays.stream(text).map(TextUtils::capitalize).toArray(String[]::new);
    }
}
