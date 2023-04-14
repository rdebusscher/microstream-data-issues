package be.rubus.microstream.data.test;

import be.rubus.microstream.data.model.BookEntity;
import one.microstream.collections.lazy.LazyHashMap;
import one.microstream.persistence.types.Persister;
import one.microstream.persistence.types.Storer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStorage {

    private final Map<Object, Object> data = new LazyHashMap<>();

    private Persister persister;

    public void setPersister(Persister persister) {
        this.persister = persister;
    }

    public synchronized <K, V> void put(K key, V value) {
        Objects.requireNonNull(key, "key is required");
        Objects.requireNonNull(value, "value is required");
        this.data.put(key, value);

        Storer eagerStorer = persister.createEagerStorer();
        eagerStorer.store(this.data);
        eagerStorer.commit();
    }

    /**
     * * Returns the value to which the specified key is mapped,
     * or {@code Optional#empty()} if this map contains no mapping for the key.
     *
     * @param key the key or ID
     * @param <K> the key type
     * @param <V> the entity type
     * @return the entity of {@link Optional#empty()}
     */
    public synchronized <K, V> Optional<V> get(K key) {
        Objects.requireNonNull(key, "key is required");
        return (Optional<V>) Optional.ofNullable(this.data.get(key));
    }

    public synchronized BookEntity find(String isbn) {
        List<BookEntity> matching = values().map(o -> (BookEntity) o)
                .filter(b -> b.isbn().equals(isbn))
                .collect(Collectors.toList());
        // Using this Collector to simulate the Jakarta Data operation

        return matching.isEmpty() ? null : matching.get(0);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public synchronized int size() {
        return this.data.size();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     *
     * @param <V> the entity type
     * @return a collection view of the values contained in this map
     */
    public synchronized <V> Stream<V> values() {
        if (data.isEmpty()) {
            return Stream.empty();
        }
        return (Stream<V>) this.data.values().stream();
    }
}
