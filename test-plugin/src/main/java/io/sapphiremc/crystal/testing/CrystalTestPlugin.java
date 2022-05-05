/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.testing;

import io.sapphiremc.crystal.CrystalPlugin;

public class CrystalTestPlugin extends CrystalPlugin {

    @Override
    protected void onPluginLoad() {
        instance = this;
        getSLF4JLogger().info("Called onPluginLoad()");
    }

    @Override
    protected void onPluginEnable() {
        saveDefaultConfig();

        getSLF4JLogger().info("Called onPluginEnable()");
    }

    @Override
    protected void onPluginDisable() {
        getSLF4JLogger().info("Called onPluginDisable()");
    }
}
