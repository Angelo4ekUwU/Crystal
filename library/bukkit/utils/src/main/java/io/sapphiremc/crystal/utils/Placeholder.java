/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.utils;

public class Placeholder {

    private final String key;
    private final Object val;

    public Placeholder(String key, Object val) {
        this.key = key;
        this.val = val;
    }

    public String key() {
        return "%" + key + "%";
    }

    public String value() {
        return val.toString();
    }
}
