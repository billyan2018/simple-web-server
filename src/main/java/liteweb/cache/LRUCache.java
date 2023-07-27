package liteweb.cache;

import liteweb.http.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache {
    private final Lock LOCK = new ReentrantLock();
    private final Condition IS_UPDATING_CONDITION = LOCK.newCondition();
    private volatile boolean is_updating = false;
    private static final int MAX_SIZE = 3;

    private final Map<String, Response> CACHE_MAP = new HashMap(3);
    private final List<String> LRU_LIST = new ArrayList(3);

    public Response get(String requestURI) throws InterruptedException {
        Response res;
        try {
            LOCK.lock();
            if ((res = CACHE_MAP.get(requestURI)) != null) {
                while (is_updating) {
                    IS_UPDATING_CONDITION.await();
                }
                is_updating = true;
                LRU_LIST.remove(requestURI);
                LRU_LIST.add(requestURI);
                is_updating = false;
                IS_UPDATING_CONDITION.signal();
            }
        } finally {
            LOCK.unlock();
        }
        return res;
    }

    public void put(String requestURI, Response res) throws InterruptedException {

        try {
            LOCK.lock();
            while (is_updating) {
                IS_UPDATING_CONDITION.await();
            }
            is_updating = true;
            removeOldestItemsIfCacheIsFull();
            CACHE_MAP.put(requestURI, res);
            LRU_LIST.add(requestURI);
            is_updating = false;
            IS_UPDATING_CONDITION.signal();
        } finally {
            LOCK.unlock();
        }
    }

    private void removeOldestItemsIfCacheIsFull() {
        if (LRU_LIST.size() >= MAX_SIZE) {
            String removedURL = LRU_LIST.remove(0);
            CACHE_MAP.remove(removedURL);
        }
    }
}
