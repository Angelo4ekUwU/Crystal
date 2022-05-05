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

public class PoolManager {

    private final CrystalPlugin plugin;
    private StorageType storageType = StorageType.NONE;
    private HikariDataSource hikariDataSource = null;

    public PoolManager(CrystalPlugin plugin) {
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
            plugin.logDebug("Successfully loaded SQLite storage!");
        } else if (type.equalsIgnoreCase("mysql")) {
            ConfigurationSection mysqlConfig = plugin.getConfig().getConfigurationSection("storage.mysql");
            if (mysqlConfig == null)
                throw new IllegalStateException("MySQL Settings section in the configuration not found.");

            HikariConfig hikariConfig = setupConfig(
                    mysqlConfig.getString("address"), mysqlConfig.getString("port"), mysqlConfig.getString("database"),
                    mysqlConfig.getString("username"), mysqlConfig.getString("password"),
                    mysqlConfig.getInt("maxPoolSize"), mysqlConfig.getInt("minimumIdle"),
                    mysqlConfig.getLong("maxLifetime"), mysqlConfig.getLong("keepAliveTime"), mysqlConfig.getLong("connectionTimeout"));

            ConfigurationSection properties = config.getConfigurationSection("properties");
            if (properties != null) {
                properties.getKeys(false).forEach(key ->
                        hikariConfig.addDataSourceProperty(key, properties.getString(key)));
            }

            this.storageType = StorageType.MYSQL;
            this.hikariDataSource = new HikariDataSource(hikariConfig);
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

    public void shutdown() {
        if (hikariDataSource != null)
            hikariDataSource.close();
    }

    public Connection getConnection() throws SQLException {
        if (this.hikariDataSource == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikariDataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    public StorageType getStorageType() {
        return this.storageType;
    }

    public enum StorageType {
        NONE,
        SQLITE,
        MYSQL
    }
}
