/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.utils;

public record Placeholder(String key, Object val) {

    @Override
    public String key() {
        return "%" + key + "%";
    }

    public String value() {
        return val.toString();
    }
}
