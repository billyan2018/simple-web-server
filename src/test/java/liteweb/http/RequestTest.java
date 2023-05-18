package liteweb.http;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {

    @ParameterizedTest
    @MethodSource("additionProvider")
    void verifyRequestMethod(String requestHeader, Method expectedMethod) throws Exception {

            Request req = new Request(Collections.singletonList(requestHeader));
            assertEquals(expectedMethod, req.getMethod());
    }

    static Stream<Arguments> additionProvider() {
        return Stream.of(
                Arguments.of("HEAD / HTTP/1.1\n\n", Method.HEAD),
                Arguments.of("GET / HTTP/1.1\n\n", Method.GET),
                Arguments.of("WHAT / HTTP/1.1\n\n", Method.UNRECOGNIZED)
        );
    }
}
