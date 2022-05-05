/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TextUtils {

    private final Pattern HEX_COLORS_PATTERN = Pattern.compile("\\{#([a-fA-F0-9]{6})}");
    private final Pattern HEX_GRADIENT_PATTERN = Pattern.compile("\\{#([a-fA-F0-9]{6})(:#([a-fA-F0-9]{6}))+( )([^{}])*(})");
    private final Pattern HEX_SPIGOT_PATTERN = Pattern.compile("§[xX](§[a-fA-F0-9]){6}");

    private final List<ChatColor> FORMAT_COLORS = List.of(ChatColor.BOLD, ChatColor.ITALIC, ChatColor.UNDERLINE, ChatColor.MAGIC, ChatColor.STRIKETHROUGH, ChatColor.RESET);

    public boolean isColor(ChatColor color) {
        return !FORMAT_COLORS.contains(color);
    }

    public boolean isFormat(ChatColor color) {
        return !isColor(color);
    }

    /**
     * Removes all hex-codes from string
     *
     * @param str string to strip hex
     * @return stripped string
     */
    public String stripHex(String str, boolean stripGradients) {
        if (str == null) {
            return null;
        }

        str = HEX_COLORS_PATTERN.matcher(str).replaceAll("");
        str = HEX_SPIGOT_PATTERN.matcher(str).replaceAll("");
        if (stripGradients) {
            str = HEX_GRADIENT_PATTERN.matcher(str).replaceAll("");
        }
        return str;
    }

    /**
     * Finds simple and gradient hex patterns in string and converts it to Spigot format
     *
     * @param text string to stylish
     * @return stylished string
     */
    public String stylish(String text) {
        if (text == null) {
            return null;
        }

        Matcher matcher = HEX_GRADIENT_PATTERN.matcher(text);

        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String gradient = matcher.group();

            int groups = 0;
            for (int i = 1; gradient.charAt(i) == '#'; i += 8) {
                groups++;
            }

            Color[] colors = new Color[groups];
            for (int i = 0; i < groups; i++) {
                colors[i] = ChatColor.of(gradient.substring((8 * i) + 1, (8 * i) + 8)).getColor();
            }

            String substring = gradient.substring((groups - 1) * 8 + 9, gradient.length() - 1);

            char[] chars = substring.toCharArray();

            StringBuilder gradientBuilder = new StringBuilder();

            int colorLength = chars.length / (colors.length - 1);
            int lastColorLength;
            if (colorLength == 0) {
                colorLength = 1;
                lastColorLength = 1;
                colors = Arrays.copyOfRange(colors, 0, chars.length);
            } else {
                lastColorLength = chars.length % (colorLength * (colors.length - 1)) + colorLength;
            }

            List<ChatColor> currentStyles = new ArrayList<>();
            for (int i = 0; i < (colors.length - 1); i++) {
                int currentColorLength = ((i == colors.length - 2) ? lastColorLength : colorLength);
                for (int j = 0; j < currentColorLength; j++) {
                    Color color = calculateGradientColor(j + 1, currentColorLength, colors[i], colors[i + 1]);
                    ChatColor chatColor = ChatColor.of(color);

                    int charIndex = colorLength * i + j;
                    if (charIndex + 1 < chars.length) {
                        if (chars[charIndex] == '&' || chars[charIndex] == '§') {
                            if (chars[charIndex + 1] == 'r') {
                                currentStyles.clear();
                                j++;
                                continue;
                            }

                            ChatColor style = ChatColor.getByChar(chars[charIndex + 1]);
                            if (style != null) {
                                currentStyles.add(style);
                                j++;
                                continue;
                            }
                        }
                    }

                    StringBuilder colorBuilder = gradientBuilder.append(chatColor.toString());

                    for (ChatColor currentStyle : currentStyles) {
                        colorBuilder.append(currentStyle.toString());
                    }

                    colorBuilder.append(chars[charIndex]);
                }
            }

            matcher.appendReplacement(builder, gradientBuilder.toString());
        }

        matcher.appendTail(builder);
        text = builder.toString();

        matcher = HEX_COLORS_PATTERN.matcher(text);
        builder = new StringBuilder();

        while (matcher.find()) {
            String hexColorString = matcher.group();
            matcher.appendReplacement(builder, ChatColor.of(hexColorString.substring(1, hexColorString.length() - 1)).toString());
        }

        matcher.appendTail(builder);

        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }

    /**
     * Finds simple and gradient hex patterns in string list and converts it to Spigot format
     *
     * @param input string list to stylish
     * @return stylished string list
     */
    public List<String> stylish(List<String> input) {
        return input.stream().map(TextUtils::stylish).toList();
    }

    private Color calculateGradientColor(int x, int parts, Color from, Color to) {
        double p = (double) (parts - x + 1) / (double) parts;

        return new Color(
                (int) (from.getRed() * p + to.getRed() * (1 - p)),
                (int) (from.getGreen() * p + to.getGreen() * (1 - p)),
                (int) (from.getBlue() * p + to.getBlue() * (1 - p))
        );
    }
}
