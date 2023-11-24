/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db;

import me.denarydev.crystal.db.file.H2ConnectionFactory;
import me.denarydev.crystal.db.file.SQLiteConnectionFactory;
import me.denarydev.crystal.db.hikari.MariaDBConnectionFactory;
import me.denarydev.crystal.db.hikari.MySqlConnectionFactory;
import me.denarydev.crystal.db.hikari.PostgresConnectionFactory;
import me.denarydev.crystal.db.settings.ConnectionSettings;
import me.denarydev.crystal.db.settings.FlatfileConnectionSettings;
import me.denarydev.crystal.db.settings.HikariConnectionSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DatabaseManager {
    private ConnectionSettings settings;
    private ConnectionFactory connectionFactory;

    private final ExecutorService asyncPool = Executors.newSingleThreadExecutor();

    public void initialize(@NotNull ConnectionSettings settings) throws IllegalArgumentException {
        this.settings = settings;

        final var type = settings.databaseType();
        if (type.local()) {
            if (!(settings instanceof FlatfileConnectionSettings)) {
                throw new IllegalArgumentException("FlatfileConnectionSettings class must be implemented for local databases such as SQLite and H2!");
            }
        } else {
            if (!(settings instanceof HikariConnectionSettings)) {
                throw new IllegalArgumentException("HikariConnectionSettings class must be implemented for the MySQL database!");
            }
        }

        this.connectionFactory = switch (type) {
            case H2 -> new H2ConnectionFactory(((FlatfileConnectionSettings) settings).databaseFile());
            case SQLITE -> new SQLiteConnectionFactory(((FlatfileConnectionSettings) settings).databaseFile());
            case MYSQL -> new MySqlConnectionFactory((HikariConnectionSettings) settings);
            case MARIADB -> new MariaDBConnectionFactory((HikariConnectionSettings) settings);
            case POSTGRESQL -> new PostgresConnectionFactory((HikariConnectionSettings) settings);
        };
        connectionFactory.initialize();
    }

    public void shutdown() {
        // 3 minutes is overkill, but we just want to make sure
        shutdown(15, TimeUnit.MINUTES.toSeconds(3));
    }

    public void shutdown(int reportInterval, long secondsUntilForceShutdown) {
        shutdownTaskQueue();

        while (!taskQueueTerminated() && secondsUntilForceShutdown > 0) {
            long secondsToWait = Math.min(reportInterval, secondsUntilForceShutdown);

            try {
                if (waitForShutdown(secondsToWait, TimeUnit.SECONDS)) {
                    break;
                }

                settings.logger().info(String.format("A DataManager is currently working on %d tasks... " +
                        "We are giving him another %d seconds until we forcefully shut him down " +
                        "(continuing to report in %d second intervals)",
                    taskQueueSize(), secondsUntilForceShutdown, reportInterval));
            } catch (InterruptedException ignore) {
            } finally {
                secondsUntilForceShutdown -= secondsToWait;
            }
        }

        if (!taskQueueTerminated()) {
            int unfinishedTasks = forceShutdownTaskQueue().size();

            if (unfinishedTasks > 0) {
                settings.logger().warn("A DataManager has been forcefully terminated with {} unfinished tasks - This can be a serious problem, please report it to plugin developer!", unfinishedTasks);
            }
        }
    }

    public ConnectionSettings settings() {
        return settings;
    }

    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    /**
     * Queue a task to be run asynchronously with all the
     * advantages of CompletableFuture api <br>
     *
     * @param runnable task to run
     * @return CompletableFuture
     * @see CompletableFuture
     */
    @NotNull
    public CompletableFuture<Void> asyncFuture(@NotNull final Runnable runnable) {
        return CompletableFuture.runAsync(runnable, this.asyncPool);
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public void sync(@NotNull final Runnable runnable) {
        settings.runSyncTask(runnable);
    }

    /**
     * Queue a task to be run asynchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public void runAsync(@NotNull final Runnable runnable) {
        runAsync(runnable, null);
    }

    /**
     * Queue a task to be run asynchronously.
     *
     * @param runnable task to run on the next server tick
     * @param callback exception callback
     */
    public void runAsync(@NotNull final Runnable runnable, @Nullable final Consumer<Throwable> callback) {
        this.asyncPool.execute(() -> {
            try {
                runnable.run();

                if (callback != null) {
                    callback.accept(null);
                }
            } catch (Throwable th) {
                if (callback != null) {
                    callback.accept(th);
                    return;
                }

                th.printStackTrace();
            }
        });
    }

    public void shutdownTaskQueue() {
        this.asyncPool.shutdown();
    }

    @NotNull
    public List<Runnable> forceShutdownTaskQueue() {
        return this.asyncPool.shutdownNow();
    }

    public boolean taskQueueTerminated() {
        return this.asyncPool.isTerminated();
    }

    public long taskQueueSize() {
        if (this.asyncPool instanceof final ThreadPoolExecutor executor) {
            return executor.getTaskCount();
        }

        return -1;
    }

    /**
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     */
    public boolean waitForShutdown(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return this.asyncPool.awaitTermination(timeout, unit);
    }
}
