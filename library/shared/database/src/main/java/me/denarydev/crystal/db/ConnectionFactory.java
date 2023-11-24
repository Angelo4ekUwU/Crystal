/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    /**
     * Returns selected database type.
     *
     * @return database type
     */
    @NotNull
    DatabaseType databaseType();

    void initialize();

    /**
     * Shutdown the database
     */
    void shutdown() throws SQLException;

    /**
     * @return {@link Connection}
     * @throws SQLException when the connection could not be received
     */
    @NotNull
    Connection connection() throws SQLException;

    /**
     * Executes a callback with a Connection passed and automatically closes it when finished
     *
     * @param callback The callback to execute once the connection is retrieved
     */
    void connect(@NotNull final ConnectionCallback callback);

    /**
     * Wraps a connection in a callback which will automagically handle catching sql errors
     */
    interface ConnectionCallback {
        void accept(@NotNull final Connection connection) throws SQLException;
    }
}
