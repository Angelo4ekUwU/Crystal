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
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class DatabaseConnector {

    private final CrystalPlugin plugin;
    private StorageType storageType = StorageType.UNKNOWN;
    private HikariDataSource hikariDataSource = null;
    private boolean initialized = false;

    public DatabaseConnector(CrystalPlugin plugin) {
        this.plugin = plugin;
    }

    public void load(ConfigurationSection config) {
        if (config == null) {
            plugin.logError("Storage section in the configuration not found.");
            return;
        }

        String type = config.getString("type", "");
        if (type.equalsIgnoreCase("sqlite")) {
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

            this.storageType = StorageType.SQLITE;
            this.hikariDataSource = new HikariDataSource(hikariConfig);
            this.initialized = true;
            plugin.logDebug("Successfully loaded SQLite storage!");
        } else if (type.equalsIgnoreCase("mysql")) {
            ConfigurationSection mysqlConfig = plugin.getConfig().getConfigurationSection("storage.mysql");
            if (mysqlConfig == null)
                throw new IllegalStateException("MySQL Settings section in the configuration not found.");

            String address = mysqlConfig.getString("address");
            if (address == null)
                throw new IllegalStateException("MySQL Address must be specified!");

            String port = "3306";
            if (address.contains(":") && address.split(":").length == 2) {
                String[] splitted = address.split(":");
                address = splitted[0];
                port = splitted[1];
            }

            HikariConfig hikariConfig = setupConfig(
                address, port, mysqlConfig.getString("database"),
                mysqlConfig.getString("username"), mysqlConfig.getString("password"),
                mysqlConfig.getInt("maxPoolSize", 6), mysqlConfig.getInt("minimumIdle", 6),
                mysqlConfig.getLong("maxLifetime", 1800000), mysqlConfig.getLong("keepAliveTime", 0), mysqlConfig.getLong("connectionTimeout", 5000));

            if (mysqlConfig.contains("properties") && mysqlConfig.isConfigurationSection("properties")) {
                ConfigurationSection properties = mysqlConfig.getConfigurationSection("properties");
                properties.getKeys(false).forEach(key ->
                    hikariConfig.addDataSourceProperty(key, properties.getString(key)));
            } else {
                hikariConfig.addDataSourceProperty("useUnicode", true);
                hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
            }

            this.storageType = StorageType.MYSQL;
            this.hikariDataSource = new HikariDataSource(hikariConfig);
            this.initialized = true;
            plugin.logDebug("Successfully loaded MySQL storage");
        } else {
            plugin.logError("Could not load storage: Unknown storage type");
            plugin.disable();
        }
    }

    private HikariConfig setupConfig(String address, String port, String databaseName,
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
            callback.accept(connection, storageType);
        } catch (SQLException ex) {
            plugin.logError("An error occured executing a SQL query", ex);
        }
    }

    /**
     * Returns selected storage type.
     *
     * @return storage type
     * @see StorageType
     */
    public StorageType getStorageType() {
        return this.storageType;
    }

    /**
     * Wraps a connection in a callback which will automagically handle catching sql errors
     */
    public interface ConnectionCallback {
        void accept(Connection connection, StorageType type) throws SQLException;
    }

    /**
     * Type of the storage
     */
    public enum StorageType {
        SQLITE,
        MYSQL,
        UNKNOWN
    }
}
