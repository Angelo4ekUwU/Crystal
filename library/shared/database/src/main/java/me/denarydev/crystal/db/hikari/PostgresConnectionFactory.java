/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.hikari;

import me.denarydev.crystal.db.DatabaseType;
import me.denarydev.crystal.db.settings.HikariConnectionSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author DenaryDev
 * @since 0:50 24.11.2023
 */
public class PostgresConnectionFactory extends DriverBasedHikariConnectionFactory {
    public PostgresConnectionFactory(HikariConnectionSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull DatabaseType databaseType() {
        return DatabaseType.POSTGRESQL;
    }

    @Override
    protected String defaultPort() {
        return "5432";
    }

    @Override
    protected String driverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "postgresql";
    }

    @Override
    protected void overrideProperties(Map<String, Object> properties) {
        super.overrideProperties(properties);

        // remove the default config properties which don't exist for PostgreSQL
        properties.remove("useUnicode");
        properties.remove("characterEncoding");
    }
}
