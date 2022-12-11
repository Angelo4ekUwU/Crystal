/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.UUID;

public abstract class CrystalPlugin {
    public abstract @NotNull String name();

    public abstract @NotNull Logger logger();

   public abstract @NotNull File dataFolder();

    public void disable() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void runSyncTask(@NotNull final Runnable runnable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void runAsyncTask(@NotNull final Runnable runnable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public @NotNull String format(@NotNull final String s) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public @NotNull List<String> format(@NotNull final List<String> s) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void sendMessage(@Nullable final UUID uuid, @NotNull String message) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void sendMessage(@Nullable final UUID uuid, @NotNull String[] messages) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
