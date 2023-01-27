/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberUtils {
    public static String formatEconomy(char currencySymbol, double number) {
        return currencySymbol + formatNumber(number);
    }

    public static String formatNumber(double number) {
        final var decimalFormatter = new DecimalFormat(number == Math.ceil(number) ? "#,###" : "#,###.00");

        // This is done to specifically prevent the NBSP character from printing in foreign languages.
        final var symbols = decimalFormatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        decimalFormatter.setDecimalFormatSymbols(symbols);

        return decimalFormatter.format(number);
    }

    public static String formatWithSuffix(long count) {
        if (count < 1000) {
            return String.valueOf(count);
        }

        final int exp = (int) (Math.log(count) / Math.log(1000));

        return String.format("%.1f%c", count / Math.pow(1000, exp),
            "kMBTPE".charAt(exp - 1)).replace(".0", "");
    }

    public static boolean isInt(String number) {
        if (number == null || number.equals("")) {
            return false;
        }

        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException ignore) {
        }

        return false;
    }

    public static boolean isNumeric(String s) {
        if (s == null || s.equals("")) {
            return false;
        }

        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
