/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db;

public enum DatabaseType {
    SQLITE("SQLite", true),
    H2("H2", true),
    MYSQL("MySQL", false),
    MARIADB("MariaDB", false),
    POSTGRESQL("PostgreSQL", false);

    private final String friendlyName;
    private final boolean local;
    DatabaseType(String friendlyName, boolean local) {
        this.friendlyName = friendlyName;
        this.local = local;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public boolean isLocal() {
        return local;
    }
}
