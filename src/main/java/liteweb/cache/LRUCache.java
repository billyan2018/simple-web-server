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
    private static final Lock LOCK = new ReentrantLock();
    private static final Condition IS_FULL = LOCK.newCondition();
    private static final Condition IS_EMPTY = LOCK.newCondition();
    private static final int MAX_SIZE = 3;

    private static final Map<String, Response> CACHE_MAP = new HashMap(3);
    private static final List<String> LRU_LIST = new ArrayList(3);

    public Response get(String requestURI) {
        Response res;
        try {
            LOCK.lock();
            if ((res = CACHE_MAP.get(requestURI)) != null) {
                LRU_LIST.remove(requestURI);
                LRU_LIST.add(requestURI);
            }
        } finally {
            LOCK.unlock();
        }
        return res;
    }

    public void put(String requestURI, Response res) {
        try {
            LOCK.lock();
            if (LRU_LIST.size() >= MAX_SIZE) {
                String removedURL = LRU_LIST.remove(0);
                CACHE_MAP.remove(removedURL);
            }
            CACHE_MAP.put(requestURI, res);
            LRU_LIST.add(requestURI);
        } finally {
            LOCK.unlock();
        }
    }
}
