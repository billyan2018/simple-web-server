package liteweb.cache;

import liteweb.Server;
import liteweb.http.Request;
import liteweb.http.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    @Test
    public void testPutItemsAndGetItems() {
        Server s = new Server();
        LRUCache cache = new LRUCache();
        List<String> list = Arrays.asList("GET / HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cache-Control: max-age=0",
                "sec-ch-ua: \"Chromium\";v=\"106\"");
        cache.put("1", new Response(new Request(list)));
        cache.put("2", new Response(new Request(list)));
        cache.put("3", new Response(new Request(list)));
        cache.put("4", new Response(new Request(list)));
        assertNull(cache.get("1"));
        assertNotNull(cache.get("2"));
        cache.put("5", new Response(new Request(list)));
        assertNull(cache.get("3"));
        assertNotNull(cache.get("2"));
    }
}
