/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.sql;

import java.util.Map;

public interface IDatabaseConfig {

    DatabaseType databaseType();

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
