package util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Utility data structure that acts a map with class types as keys and bi-consumers as values.
 * All keys must inherit from the same interface or class. First argument of bi-consumers must be of
 * corresponding key's type.
 * <p>
 * Handles the integrity of key and value types. Main use case is to work around ugly instanceof checks.
 *
 * @param <T> Common interface all keys of the behavior must inherit from.
 * @param <U> Second argument type of bi-consumers.
 */
public class Behavior<T, U> {

    private final Map<Class<T>, BiConsumer<T, U>> behaviorMap;
    // TODO: what should fallback be
    private BiConsumer<T, U> fallback = (t, u) -> {};

    public Behavior() {
        behaviorMap = new HashMap<>();
    }

    /**
     * Adds a new key-value pair to the map.
     *
     * @param clazz Class type extending from {@link T}
     * @param func  Bi-consumer
     * @param <V>   Type parameter to define types extending from {@link T}. We can't remove this and use
     *              a wildcard instead because java doesn't allow it. Compiler can't actually verify
     *              whether types of key and value actually match.
     * @return this. Chainable
     * @throws IllegalArgumentException if the internal map already contains `clazz`
     */
    @SuppressWarnings("unchecked")
    public <V extends T> Behavior<T, U> entry(Class<V> clazz, BiConsumer<V, U> func) {
        if (behaviorMap.containsKey(clazz)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " already exists");
        }
        // Looks ugly but this is java's way of handling generics.
        this.behaviorMap.put((Class<T>) clazz, (BiConsumer<T, U>) func);
        return this;
    }

    /**
     * Adds a default bi-consumer to run.
     *
     * @param fallback bi-consumer to run if input doesn't match any of the keys in the map.
     * @return this. Chainable
     */
    public Behavior<T, U> def(BiConsumer<T, U> fallback) {
        this.fallback = fallback;
        return this;
    }

    /**
     * Combines two behaviors. Does not create a new {@link Behavior}, instead updates inplace.
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
     * Execution function. Given the parameters, determines which bi-consumer to run and calls it with those values.
     *
     * @param t any value of type {@link T}
     * @param u any value of type {@link U}
     */
    @SuppressWarnings("unchecked")
    public void apply(T t, U u) {
        BiConsumer<T, U> funcToUse = fallback;
        // TODO: Look if there is a way to get rid of this loop. There could be a specialized map for this usecase
        for (Class<T> clazz: behaviorMap.keySet()) {
            if (clazz.isInstance(t)) {
                funcToUse = behaviorMap.get(clazz);
            }
        }
        funcToUse.accept(t, u);
    }
}
