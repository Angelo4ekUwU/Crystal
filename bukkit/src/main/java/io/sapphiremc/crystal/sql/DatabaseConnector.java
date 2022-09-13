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
import io.sapphiremc.crystal.CrystalPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class DatabaseConnector {

    private final CrystalPlugin plugin;
    private IDatabaseConfig config;
    private HikariDataSource hikariDataSource = null;
    private boolean initialized = false;

    public DatabaseConnector(CrystalPlugin plugin) {
        this.plugin = plugin;
    }

    public void load(IDatabaseConfig config) {
        this.config = config;
        if (config == null) {
            plugin.logError("Storage section in the configuration not found.");
            return;
        }

        final var type = config.databaseType();
        if (type.equals(DatabaseType.SQLITE)) {
            File storage = new File(plugin.getDataFolder(), "storage.db");
            if (!storage.exists()) {
                try {
                    storage.createNewFile();
                } catch (IOException ex) {
                    plugin.logError("Failed to create sqlite storage file!", ex);
                }
            }

            HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setPoolName(plugin.getName() + "-SQLite");
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + storage.getPath());

            this.hikariDataSource = new HikariDataSource(hikariConfig);
            this.initialized = true;
            plugin.logDebug("Successfully loaded SQLite storage!");
        } else if (type.equals(DatabaseType.MYSQL)) {
            HikariConfig hikariConfig = setupHikariConfig(
                config.address(), config.port(), config.database(),
                config.username(), config.password(),
                config.maxPoolSize(), config.minimumIdle(),
                config.maxLifeTime(), config.keepAliveTime(), config.connectionTimeout());

            if (!config.properties().isEmpty()) {
                config.properties().forEach(hikariConfig::addDataSourceProperty);
            } else {
                hikariConfig.addDataSourceProperty("useUnicode", true);
                hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
            }

            this.hikariDataSource = new HikariDataSource(hikariConfig);
            this.initialized = true;
            plugin.logDebug("Successfully loaded MySQL storage");
        }
    }

    private HikariConfig setupHikariConfig(String address, int port, String databaseName,
                                           String username, String password,
                                           int maxPoolSize, int minimumIdle,
                                           long maxLifetime, long keepaliveTime, long connectionTimeout) {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setPoolName(plugin.getName() + "-MySQL");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + databaseName);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);

        hikariConfig.setMaxLifetime(maxLifetime);
        hikariConfig.setKeepaliveTime(keepaliveTime);
        hikariConfig.setConnectionTimeout(connectionTimeout);

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

    private Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikariDataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
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
    public void connect(ConnectionCallback callback) {
        try (Connection connection = getConnection()) {
            callback.accept(connection, getStorageType());
        } catch (SQLException ex) {
            plugin.logError("An error occured executing a SQL query", ex);
        }
    }

    /**
     * Returns selected database type.
     *
     * @return database type
     */
    public DatabaseType getStorageType() {
        return config.databaseType();
    }

    /**
     * Wraps a connection in a callback which will automagically handle catching sql errors
     */
    public interface ConnectionCallback {
        void accept(Connection connection, DatabaseType type) throws SQLException;
    }
}
