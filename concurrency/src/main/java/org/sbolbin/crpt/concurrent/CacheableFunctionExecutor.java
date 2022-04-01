package org.sbolbin.crpt.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Function;

public class CacheableFunctionExecutor<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final Executor DEFAULT_EXECUTOR = ForkJoinPool.commonPool();

    private final ConcurrentHashMap<K, Future<V>> cache;
    private final Executor executor;

    public CacheableFunctionExecutor() {
        this(DEFAULT_EXECUTOR, DEFAULT_INITIAL_CAPACITY);
    }

    public CacheableFunctionExecutor(Executor executor, int cacheCapacity) {
        this.cache = new ConcurrentHashMap<>(cacheCapacity);
        this.executor = executor;
    }

    public Future<V> calculate(K key, Function<K, V> function) {
        return cache.computeIfAbsent(key,
                (k) -> CompletableFuture.supplyAsync(() -> function.apply(k), executor));
    }
}
