/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.hook;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Hook {

    /**
     * Get the name of the plugin being used.
     *
     * @return name of the plugin
     */
    @NotNull String getPluginName();

    /**
     * Check to see if the plugin being used is active
     *
     * @return true if the plugin is loaded and active
     */
    boolean isEnabled();
}
