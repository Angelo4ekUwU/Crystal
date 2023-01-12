/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.data;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Database configuration class
 */
public abstract class AbstractDatabaseConfig {

    /**
     * {@link org.slf4j.Logger} impl from your plugin
     */
    public abstract Logger logger();

    /**
     * You must implement this using same method on your platform
     *
     * @param task task
     */
    public abstract void runSyncTask(@NotNull final Runnable task);

    /**
     * Database type
     * <p>
     * If database type is SQLite, be sure to override {@link #sqliteStorageFile()} method
     * <p>
     * If database type is MySQL, be sure to override the following methods:
     * <p>
     * {@link #address()}, {@link #database()},
     * {@link #username()}, {@link #password()}
     *
     * @see DatabaseType
     */
    public abstract @NotNull DatabaseType databaseType();

    /**
     * Prefix for pool names in hikari.
     * <p>
     * Unique prefix will allow you to distinguish
     * hikari logs from this plugin from hikari logs of other plugins.
     * <p>
     * <u>It's best to use your plugin name as pool name prefix.
     */
    public String sqlPoolPrefix() {
        return "Crystal";
    }

    /**
     * File for SQLite database
     * <p>
     * <u>Must be implemented only if database type is SQLite.
     */
    public File sqliteStorageFile() {
        return new File("storage.db");
    }

    /**
     * The IP or address of the database.
     */
    public String address() {
        return "localhost";
    }

    /**
     * The port to connect on.
     */
    public int port() {
        return 3306;
    }

    /**
     * The database to use in the database.
     */
    public String database() {
        return "minecraft";
    }

    /**
     * The username to authenticate as.
     */
    public String username() {
        return "root";
    }

    /**
     * The password to authenticate with.
     */
    public String password() {
        return "";
    }

    /**
     * The maximum amount of simultaneous connections.
     * Should be the same as you have cores.
     */
    public short maxPoolSize() {
        return 6;
    }

    /**
     * The amount of connections that should always be open.
     * To avoid issues, set this to the same value as maxPoolSize.
     */
    public short minimumIdle() {
        return 6;
    }

    /**
     * The amount of milliseconds one connection should stay open.
     */
    public int maxLifeTime() {
        return 1800000;
    }

    /**
     * The setting in which interval to 'ping' the database. Set to 0 to disable.
     */
    public int keepAliveTime() {
        return 0;
    }

    /**
     * The amount of seconds we wait for a response from the database before timing out.
     */
    public int connectionTimeout() {
        return 5000;
    }

    /**
     * Other properties you may want to set.
     * <p>
     * You can also add the following properties here:
     * <pre>
     * <strong>useSSL = true
     * verifyServerCertificate = true</strong>
     * ...and any other MySQL properties</pre>
     */
    public Map<String, String> properties() {
        Map<String, String> map = new HashMap<>();
        map.put("useUnicode", "true");
        map.put("characterEncoding", "utf8");
        return map;
    }
}
