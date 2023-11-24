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
public class H2ConnectionFactory extends FlatfileConnectionFactory {
    private Constructor<?> connectionConstructor;

    public H2ConnectionFactory(Path file) {
        super(file);
    }

    @Override
    public @NotNull DatabaseType getDatabaseType() {
        return DatabaseType.H2;
    }

    @Override
    public void initialize() {
        try {
            Class<?> clazz = Class.forName("org.h2.jdbc.JdbcDataSource");
            this.connectionConstructor = clazz.getConstructor(String.class, Properties.class, String.class, boolean.class);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Connection createConnection(Path file) throws SQLException {
        try {
            return (Connection) this.connectionConstructor.newInstance("jdbc:h2:" + file.toString(), new Properties(), null, null, false);
        } catch (ReflectiveOperationException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getWriteFile() {
        // h2 appends '.mv.db' to the end of the database name
        Path writeFile = super.getWriteFile();
        return writeFile.getParent().resolve(writeFile.getFileName().toString() + ".mv.db");
    }
}
