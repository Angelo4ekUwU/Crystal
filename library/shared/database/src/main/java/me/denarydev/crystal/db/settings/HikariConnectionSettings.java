/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DenaryDev
 * @since 0:58 24.11.2023
 */
public interface HikariConnectionSettings extends ConnectionSettings {

    /**
     * The IP or address of the database.
     */
    String address();

    /**
     * The port to connect on.
     * <p>
     * If null, the default port of selected type will be used.
     */
    default String port() {
        return null;
    }

    /**
     * The database to use in the database.
     */
    String database();

    /**
     * The username to authenticate as.
     */
    String username();

    /**
     * The password to authenticate with.
     */
    String password();

    /**
     * The maximum amount of simultaneous connections.
     * Should be the same as you have cores.
     */
    default int maxPoolSize() {
        return 6;
    }

    /**
     * The amount of connections that should always be open.
     * To avoid issues, set this to the same value as maxPoolSize.
     */
    default int minimumIdle() {
        return 6;
    }

    /**
     * The amount of milliseconds one connection should stay open.
     */
    default int maxLifetime() {
        return 1800000;
    }

    /**
     * The setting in which interval to 'ping' the database. Set to 0 to disable.
     */
    default int keepAliveTime() {
        return 0;
    }

    /**
     * The amount of seconds we wait for a response from the database before timing out.
     */
    default int connectionTimeout() {
        return 5000;
    }

    /**
     * Other properties you may want to set.
     * <p>
     * You can also add the following properties here:
     * <pre>
     * <strong>useSSL = true
     * verifyServerCertificate = true</strong>
     * ...and any other sql properties</pre>
     */
    default Map<String, String> properties() {
        final var map = new HashMap<String, String>();
        map.put("useUnicode", "true");
        map.put("characterEncoding", "utf8");
        return map;
    }
}
