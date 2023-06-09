package liteweb.http;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseTest {

    @ParameterizedTest
    @MethodSource("additionProvider")
    void verifyResponseStatus(String requestHeader, Status expectedStatus) throws Exception {

            Request req = new Request(Collections.singletonList(requestHeader));
            Response res = new Response(req);
            List<String> headers = res.getHeaders();
            assertTrue(headers.size() > 0);
            assertEquals(Response.VERSION + " " + expectedStatus, headers.get(0));
    }

    static Stream<Arguments> additionProvider() {
        return Stream.of(
                Arguments.of("HEAD / HTTP/1.1\n\n", Status._200),
                Arguments.of("GET / HTTP/1.1\n\n", Status._200),
                Arguments.of("GET /index.html HTTP/1.1\n\n", Status._404),
                Arguments.of("GET /readme.md HTTP/1.1\n\n", Status._200),
                Arguments.of("WHAT / HTTP/1.1\n\n", Status._400)
        );
    }
}
