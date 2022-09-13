/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.config;

import io.sapphiremc.crystal.sql.DatabaseType;
import io.sapphiremc.crystal.sql.IDatabaseConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.nio.file.Path;
import java.util.Map;

public final class ConfigurateDatabaseConfig implements IDatabaseConfig {

    private final Storage storage;

    public ConfigurateDatabaseConfig(Path file) throws ConfigurateException {
        storage = ConfigManager.load(file, ConfigManager.creator(Storage.class, true));
    }

    @ConfigSerializable
    public static final class Storage {
        @Comment("Тип хранения данных, SQLite или MySQL")
        public DatabaseType type = DatabaseType.SQLITE;
        @Comment("Настройки для MySQL")
        public MySQL mysql = new MySQL();

        @ConfigSerializable
        public static final class MySQL {
            @Comment("Настройки подключения")
            public Connection connection = new Connection();
            @Comment("Пользователь и пароль")
            public Credentials credentials = new Credentials();
            @Comment("Другие настройки")
            public Settings settings = new Settings();

            @ConfigSerializable
            public static final class Connection {
                @Comment("IP или адрес базы данных.")
                public String address = "localhost";
                @Comment("Порт для подключения.")
                public short port = 3306;
                @Comment("База данных для использования в базе данных.")
                public String database = "minecraft";
            }

            @ConfigSerializable
            public static final class Credentials {
                @Comment("Имя пользователя для аутентификации.")
                public String username = "root";
                @Comment("Пароль для аутентификации.")
                public String password = "";
            }

            @ConfigSerializable
            public static final class Settings {
                @Comment("""
                    Максимальное количество одновременных подключений.
                    Должно быть так же, сколько у вас ядер.""")
                public short maxPoolSize = 6;
                @Comment("""
                    Количество соединений, которые всегда должны быть открыты.
                    Чтобы избежать проблем, установите то же значение, что и maxPoolSize.""")
                public short minimumIdle = 6;

                @Comment("Количество миллисекунд, в течение которых одно соединение должно оставаться открытым.")
                public int maxLifeTime = 1800000;
                @Comment("Установка интервала, в течение которого нужно «пинговать» базу данных. Установите 0, чтобы отключить.")
                public int keepAliveTime = 0;
                @Comment("Количество секунд, в течение которых мы ждем ответа от базы данных, прежде чем истечет время ожидания.")
                public int connectionTimeout = 5000;

                @Comment("""
                    Дополнительные параметры, которые вы можете установить.
                    Вы так же можете добавить сюда такие параметры, как:
                    useSSL="false"
                    verifyServerCertificate="false\"""")
                public Map<String, String> properties = Map.of(
                    "useUnicode", "true",
                    "characterEncoding", "utf8"
                );
            }
        }
    }

    @Override
    public DatabaseType databaseType() {
        return storage.type;
    }

    @Override
    public String address() {
        return storage.mysql.connection.address;
    }

    @Override
    public int port() {
        return storage.mysql.connection.port;
    }

    @Override
    public String database() {
        return storage.mysql.connection.database;
    }

    @Override
    public String username() {
        return storage.mysql.credentials.username;
    }

    @Override
    public String password() {
        return storage.mysql.credentials.password;
    }

    @Override
    public short maxPoolSize() {
        return storage.mysql.settings.maxPoolSize;
    }

    @Override
    public short minimumIdle() {
        return storage.mysql.settings.minimumIdle;
    }

    @Override
    public int maxLifeTime() {
        return storage.mysql.settings.maxLifeTime;
    }

    @Override
    public int keepAliveTime() {
        return storage.mysql.settings.keepAliveTime;
    }

    @Override
    public int connectionTimeout() {
        return storage.mysql.settings.connectionTimeout;
    }

    @Override
    public Map<String, String> properties() {
        return storage.mysql.settings.properties;
    }
}
