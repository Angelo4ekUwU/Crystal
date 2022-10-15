/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.gui.interfaces;

import io.sapphiremc.crystal.gui.events.GuiClickEvent;

public interface ClickHandler {
    void onClick(final GuiClickEvent event);
}
