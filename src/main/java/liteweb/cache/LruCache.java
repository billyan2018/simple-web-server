package liteweb.cache;

import liteweb.lock.CustomizedLock;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class LruCache<T> {

    private static final Integer CAPACITY = 3;

    private final CustomizedLock lock = new CustomizedLock();
    private final Map<String, T> cache = new ConcurrentHashMap<>(CAPACITY);
    private final Deque<String> deque = new ConcurrentLinkedDeque<>();

    public T putIfAbsent(String key, Supplier<T> supplier) {
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
                return cache.put(key, supplier.get());
            }
        } finally {
            lock.unlock();
        }
    }
}
