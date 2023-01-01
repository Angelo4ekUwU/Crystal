/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.locale;

public class Pair {

    private final String key;
    private final Object obj;

    public Pair(String key, Object obj) {
        this.key = key;
        this.obj = obj;
    }

    public static Pair of(final String key, final Object value) {
        return new Pair(key, value);
    }

    public String key() {
        return "%" + key + "%";
    }

    public String value() {
        return obj.toString();
    }
}
