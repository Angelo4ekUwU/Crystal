/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

public record Pair(String key, Object obj) {

    public static Pair of(final String key, final Object value) {
        return new Pair(key, value);
    }

    @Override
    public String key() {
        return "%" + key + "%";
    }

    public String value() {
        return obj.toString();
    }
}
