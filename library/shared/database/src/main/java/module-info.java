/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
module crystal.shared.database {
    requires java.sql;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires org.xerial.sqlitejdbc;
    requires com.h2database;
    requires com.zaxxer.hikari;
    requires org.mariadb.jdbc;

    exports me.denarydev.crystal.db;
    exports me.denarydev.crystal.db.settings;
    exports me.denarydev.crystal.db.connection;
    exports me.denarydev.crystal.db.util;
}
