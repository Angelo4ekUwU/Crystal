/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.sql;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;

public interface ICrystalSQLConfig {
    @NotNull Logger getLogger();

    void runSyncTask(@NotNull Runnable task);

    @NotNull File getDataFolder();

    @NotNull String getPluginName();


    @NotNull DatabaseType databaseType();

    @NotNull String address();

    int port();

    @NotNull String database();

    @NotNull String username();

    @NotNull String password();

    short maxPoolSize();

    short minimumIdle();

    int maxLifeTime();

    int keepAliveTime();

    int connectionTimeout();

    @NotNull Map<String, String> properties();
}
