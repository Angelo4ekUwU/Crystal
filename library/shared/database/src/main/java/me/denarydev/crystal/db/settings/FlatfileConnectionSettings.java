/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db.settings;

import java.nio.file.Path;

/**
 * @author DenaryDev
 * @since 0:58 24.11.2023
 */
public interface FlatfileConnectionSettings extends ConnectionSettings {

    /**
     * File for SQLite or H2 database
     * <p>
     * <u>Must be implemented only if database type is SQLite or H2.
     */
    Path databaseFile();
}
