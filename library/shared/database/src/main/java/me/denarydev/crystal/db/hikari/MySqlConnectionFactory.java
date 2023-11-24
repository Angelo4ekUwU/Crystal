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
 * @since 0:09 24.11.2023
 */
public class MySqlConnectionFactory extends DriverBasedHikariConnectionFactory {
    public MySqlConnectionFactory(HikariConnectionSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull DatabaseType databaseType() {
        return DatabaseType.MYSQL;
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected String driverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mysql";
    }

    @Override
    protected void overrideProperties(Map<String, Object> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        properties.putIfAbsent("cachePrepStmts", "true");
        properties.putIfAbsent("prepStmtCacheSize", "250");
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        properties.putIfAbsent("useServerPrepStmts", "true");
        properties.putIfAbsent("useLocalSessionState", "true");
        properties.putIfAbsent("rewriteBatchedStatements", "true");
        properties.putIfAbsent("cacheResultSetMetadata", "true");
        properties.putIfAbsent("cacheServerConfiguration", "true");
        properties.putIfAbsent("elideSetAutoCommits", "true");
        properties.putIfAbsent("maintainTimeStats", "false");
        properties.putIfAbsent("alwaysSendSetIsolation", "false");
        properties.putIfAbsent("cacheCallableStmts", "true");

        // https://stackoverflow.com/a/54256150
        // It's not super important which timezone we pick, because we don't use time-based
        // data types in any of our schemas/queries.
        properties.putIfAbsent("serverTimezone", "UTC");

        super.overrideProperties(properties);
    }
}
