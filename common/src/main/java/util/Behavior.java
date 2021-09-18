package util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility data structure that acts a map with class types as keys and functions as values.
 * All keys must inherit from the same interface or class. Functions must accept arguments of
 * corresponding key's type.
 * <p>
 * Handles the integrity of key and value types. Main use case is to work around ugly instanceof checks.
 *
 * @param <T> Common interface all keys of the behavior must inherit from.
 * @param <U> Return type of the functions.
 */
public class Behavior<T, U> {

    private final Map<Class<T>, Function<T, U>> behaviorMap;
    // TODO: what should fallback be
    private Function<T, U> fallback = t -> null;

    public Behavior() {
        behaviorMap = new HashMap<>();
    }

    /**
     * Adds a new key-value pair to the map.
     *
     * @param clazz Class type extending from {@link T}
     * @param func  Function accepting
     * @param <V>   Type parameter to define types extending from {@link T}. We can't remove this and use
     *              a wildcard instead because of course java doesn't allow it. Compiler can't actually verify the
     *              types of key and value match.
     * @return this. Chainable
     * @throws IllegalArgumentException if the internal map already contains `clazz`
     */
    @SuppressWarnings("unchecked")
    public <V extends T> Behavior<T, U> entry(Class<V> clazz, Function<V, U> func) {
        if (behaviorMap.containsKey(clazz)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " already exists");
        }
        // Looks ugly but this is java's way of handling generics.
        this.behaviorMap.put((Class<T>) clazz, (Function<T, U>) func);
        return this;
    }

    /**
     * Adds a default function to run.
     *
     * @param fallback function to run if input doesn't match any of the keys in the map.
     * @return this. Chainable
     */
    public Behavior<T, U> def(Function<T, U> fallback) {
        this.fallback = fallback;
        return this;
    }

    /**
     * Combines two behaviors. Does not create a new {@link Behavior}, instead updates inplace. If there are
     * common keys, keys in this one gets overwritten.
     *
     * @param other behavior to combine
     * @return this. Chainable
     */
    public Behavior<T, U> overlay(Behavior<T, U> other) {
        other.behaviorMap.forEach(this.behaviorMap::put);
        fallback = other.fallback;
        return this;
    }

    /**
     * Execution function. Given the input, determines which function to run and calls it.
     *
     * @param input any value of type {@link T}
     * @return resulting return value
     */
    @SuppressWarnings("unchecked")
    public U apply(T input) {
        Class<T> clazz = (Class<T>) input.getClass();
        return this.behaviorMap.getOrDefault(clazz, fallback).apply(input);
    }
}
