/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.crystal.db;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class AbstractDataManager {
    private AbstractDatabaseConfig config;
    protected DatabaseConnector databaseConnector;

    protected final ExecutorService asyncPool = Executors.newSingleThreadExecutor();

    public void loadDatabase(@NotNull AbstractDatabaseConfig config) {
        this.config = config;
        this.databaseConnector = new DatabaseConnector(config);
        onDatabaseLoad();
    }

    /**
     * Called when loading the database
     * <p>
     * You can use this method to create tables in the database.
     */
    protected abstract void onDatabaseLoad();

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
        config.runSyncTask(runnable);
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
     * @param callback callback
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

    public boolean isTaskQueueTerminated() {
        return this.asyncPool.isTerminated();
    }

    public long getTaskQueueSize() {
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
