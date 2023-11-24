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

/**
 * @author DenaryDev
 * @since 0:09 24.11.2023
 */
public class MariaDBConnectionFactory extends DriverBasedHikariConnectionFactory {
    public MariaDBConnectionFactory(HikariConnectionSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull DatabaseType databaseType() {
        return DatabaseType.MARIADB;
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected String driverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mariadb";
    }
}
