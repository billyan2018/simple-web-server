package liteweb.cache;

import liteweb.lock.CustomizedLock;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class LruCache<K, V> {

    private static final Integer CAPACITY = 3;

    private final Map<K, V> cache = new ConcurrentHashMap<>(CAPACITY);
    private final Deque<K> deque = new ConcurrentLinkedDeque<>();
    private final CustomizedLock lock = new CustomizedLock();

    public V putIfAbsent(K key, Supplier<V> supplier) {
        try {
            lock.lock();

            if (deque.contains(key)) {
                deque.remove(key);
                deque.addFirst(key);
                return cache.get(key);
            } else {
                deque.addFirst(key);
                if (deque.size() > CAPACITY)
                    cache.remove(key);

                V value = supplier.get();
                cache.put(key, value);
                return value;
            }
        } finally {
            lock.unlock();
        }
    }
}
