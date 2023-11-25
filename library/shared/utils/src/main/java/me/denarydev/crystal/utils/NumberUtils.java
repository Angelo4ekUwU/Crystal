/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public final class NumberUtils {

    @NotNull
    public static String formatNumber(final double number) {
        final var decimalFormatter = new DecimalFormat(number == Math.ceil(number) ? "#,###" : "#,###.00");
        return formatNumber(number, decimalFormatter);
    }

    @NotNull
    public static String formatNumber(final double number, @NotNull final DecimalFormat decimalFormatter) {
        // This is done to specifically prevent the NBSP character from printing in foreign languages.
        final var symbols = decimalFormatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        decimalFormatter.setDecimalFormatSymbols(symbols);

        return decimalFormatter.format(number);
    }

    public static boolean integer(final String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean numeric(@NotNull final String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
