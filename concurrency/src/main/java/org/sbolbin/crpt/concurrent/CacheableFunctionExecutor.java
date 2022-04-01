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

    /**
     * Default constructor,
     * the cache will be created as java.util.concurrent.ConcurrentHashMap with default initial capacity (16)
     * default common executor will be used for calculation
     * (see java.util.concurrent.CompletableFuture#ASYNC_POOL)
     */
    public CacheableFunctionExecutor() {
        this(DEFAULT_EXECUTOR, DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * The executor constructor, that allows to customize executor and cache initial capacity
     *
     * @param executor - executor service that will be used for the calculation
     * @param cacheCapacity - the initial capacity of the underlying cache (the cache will be created as
     *                      java.util.concurrent.ConcurrentHashMap with specified capacity)
     */
    public CacheableFunctionExecutor(Executor executor, int cacheCapacity) {
        this.cache = new ConcurrentHashMap<>(cacheCapacity);
        this.executor = executor;
    }

    /**
     * If the specified key is not already present in the cache, attempts to compute
     * its value using the given function.
     * If value is already present in the cache, the cached value will be returned
     * (event the given function differs from the function that was used before).
     *
     * @param key the argument of the function, acts as a key in the cache
     * @param function the calculation function
     * @return the future represents the result is calculated
     *
     * @throws NullPointerException if the key is null
     */
    public Future<V> calculate(K key, Function<K, V> function) {
        return cache.computeIfAbsent(key,
                (k) -> CompletableFuture.supplyAsync(() -> function.apply(k), executor));
    }
}
