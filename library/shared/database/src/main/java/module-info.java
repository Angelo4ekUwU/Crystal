/**
 * @author DenaryDev
 * @since 2:16 23.12.2023
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
