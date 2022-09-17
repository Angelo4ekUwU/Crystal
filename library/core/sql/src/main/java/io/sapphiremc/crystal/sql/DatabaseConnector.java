/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class DatabaseConnector {

    private final ICrystalSQLConfig config;
    private HikariDataSource hikariDataSource = null;
    private boolean initialized = false;

    public DatabaseConnector(@NotNull ICrystalSQLConfig config) {
        this.config = config;

        final var type = config.databaseType();
        if (type.equals(DatabaseType.SQLITE)) {
            File storage = new File(config.getDataFolder(), "storage.db");
            if (!storage.exists()) {
                try {
                    storage.createNewFile();
                } catch (IOException ex) {
                    config.getLogger().error("Failed to create sqlite storage file!", ex);
                }
            }

            HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setPoolName(config.getPluginName() + "-SQLite");
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + storage.getPath());

            this.hikariDataSource = new HikariDataSource(hikariConfig);
            this.initialized = true;
            config.getLogger().debug("Successfully loaded SQLite storage!");
        } else if (type.equals(DatabaseType.MYSQL)) {
            HikariConfig hikariConfig = setupHikariConfig();

            if (!config.properties().isEmpty()) {
                config.properties().forEach(hikariConfig::addDataSourceProperty);
            } else {
                hikariConfig.addDataSourceProperty("useUnicode", true);
                hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
            }

            this.hikariDataSource = new HikariDataSource(hikariConfig);
            this.initialized = true;
            config.getLogger().debug("Successfully loaded MySQL storage");
        }
    }

    private @NotNull HikariConfig setupHikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setPoolName(config.getPluginName() + "-MySQL");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.address() + ":" + config.port() + "/" + config.database());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());

        hikariConfig.setMaximumPoolSize(config.maxPoolSize());
        hikariConfig.setMinimumIdle(config.minimumIdle());

        hikariConfig.setMaxLifetime(config.maxLifeTime());
        hikariConfig.setKeepaliveTime(config.keepAliveTime());
        hikariConfig.setConnectionTimeout(config.connectionTimeout());

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        hikariConfig.addDataSourceProperty("alwaysSendSetIsolation", "false");
        hikariConfig.addDataSourceProperty("cacheCallableStmts", "true");

        return hikariConfig;
    }

    public @NotNull Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikariDataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    public @NotNull ICrystalSQLConfig getConfig() {
        return config;
    }

    /**
     * Checks if the connection to the database has been created
     *
     * @return true if the connection is created, otherwise false
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Closes all open connections to the database
     */
    public void closeConnection() {
        if (hikariDataSource != null)
            hikariDataSource.close();
    }

    /**
     * Executes a callback with a Connection passed and automatically closes it when finished
     *
     * @param callback The callback to execute once the connection is retrieved
     */
    public void connect(@NotNull ConnectionCallback callback) {
        try (Connection connection = getConnection()) {
            callback.accept(connection, getStorageType());
        } catch (SQLException ex) {
            config.getLogger().error("An error occured executing a SQL query", ex);
        }
    }

    /**
     * Returns selected database type.
     *
     * @return database type
     */
    public @NotNull DatabaseType getStorageType() {
        return config.databaseType();
    }

    /**
     * Wraps a connection in a callback which will automagically handle catching sql errors
     */
    public interface ConnectionCallback {
        void accept(@NotNull Connection connection, @NotNull DatabaseType type) throws SQLException;
    }
}
