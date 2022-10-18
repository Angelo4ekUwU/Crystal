/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.data;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;

/**
 * Database configuration class
 */
public abstract class AbstractDatabaseConfig {
    public abstract @NotNull Logger getLogger();

    public abstract void runSyncTask(@NotNull final Runnable task);

    public abstract @NotNull File getDataFolder();

    public abstract @NotNull String getPluginName();


    /**
     * Database type
     * <p>
     * If database type is MySQL, be sure to override the following methods:
     * <p>
     * {@link AbstractDatabaseConfig#address()}, {@link AbstractDatabaseConfig#database()},
     * {@link AbstractDatabaseConfig#username()}, {@link AbstractDatabaseConfig#password()}
     * @see DatabaseType
     */
    public abstract @NotNull DatabaseType databaseType();

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
        return Map.of(
            "useUnicode", "true",
            "characterEncoding", "utf8"
        );
    }
}
