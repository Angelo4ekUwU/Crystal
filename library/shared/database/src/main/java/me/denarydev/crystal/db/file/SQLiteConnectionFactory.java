/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.file;

import me.denarydev.crystal.db.DatabaseType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author DenaryDev
 * @since 16:46 23.11.2023
 */
public class SQLiteConnectionFactory extends FlatfileConnectionFactory {
    private Constructor<?> connectionConstructor;

    public SQLiteConnectionFactory(Path file) {
        super(file);
    }

    @Override
    public @NotNull DatabaseType databaseType() {
        return DatabaseType.SQLITE;
    }

    @Override
    public void initialize() {
        try {
            Class<?> clazz = Class.forName("org.sqlite.jdbc4.JDBC4Connection");
            this.connectionConstructor = clazz.getConstructor(String.class, String.class, Properties.class);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Connection createConnection(Path file) throws SQLException {
        try {
            return (Connection) this.connectionConstructor.newInstance("jdbc:sqlite:" + file.toString(), file.toString(), new Properties());
        } catch (ReflectiveOperationException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }
}
