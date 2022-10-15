/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.data;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;

public interface IDatabaseConfig {
    @NotNull Logger getLogger();

    void runSyncTask(@NotNull final Runnable task);

    @NotNull File getDataFolder();

    @NotNull String getPluginName();


    @NotNull DatabaseType databaseType();


    /* The properties below are for MySQL only. */

    String address();

    int port();

    String database();

    String username();

    String password();

    short maxPoolSize();

    short minimumIdle();

    int maxLifeTime();

    int keepAliveTime();

    int connectionTimeout();

    Map<String, String> properties();
}
