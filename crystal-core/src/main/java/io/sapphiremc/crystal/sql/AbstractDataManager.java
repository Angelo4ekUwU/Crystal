/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.sql;

import io.sapphiremc.crystal.CrystalPlugin;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class AbstractDataManager {
    protected final CrystalPlugin plugin;
    protected final DatabaseConnector databaseConnector;

    protected final ExecutorService asyncPool = Executors.newSingleThreadExecutor();

    public AbstractDataManager(CrystalPlugin plugin, DatabaseConnector databaseConnector) {
        this.plugin = plugin;
        this.databaseConnector = databaseConnector;
    }

    /**
     * Queue a task to be run asynchronously with all the
     * advantages of CompletableFuture api <br>
     *
     * @param runnable task to run
     * @return CompletableFuture
     * @see CompletableFuture
     */
    public CompletableFuture<Void> asyncFuture(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, this.asyncPool);
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    /**
     * Queue a task to be run asynchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public void runAsync(Runnable runnable) {
        runAsync(runnable, null);
    }

    /**
     * Queue a task to be run asynchronously.
     *
     * @param runnable task to run on the next server tick
     * @param callback callback
     */
    public void runAsync(Runnable runnable, Consumer<Throwable> callback) {
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

    public List<Runnable> forceShutdownTaskQueue() {
        return this.asyncPool.shutdownNow();
    }

    public boolean isTaskQueueTerminated() {
        return this.asyncPool.isTerminated();
    }

    public long getTaskQueueSize() {
        if (this.asyncPool instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) this.asyncPool).getTaskCount();
        }

        return -1;
    }

    /**
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     */
    public boolean waitForShutdown(long timeout, TimeUnit unit) throws InterruptedException {
        return this.asyncPool.awaitTermination(timeout, unit);
    }
}
