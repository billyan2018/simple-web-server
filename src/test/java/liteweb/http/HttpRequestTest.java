package liteweb.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

	@Test
	public void doHeadRequest() throws IOException {
		HttpRequest req = new HttpRequest(new ByteArrayInputStream("HEAD / HTTP/1.1\n\n".getBytes()));
		assertEquals(Method.HEAD, req.getMethod());
	}

	@Test
	public void doGetRequest() throws IOException {
		HttpRequest req = new HttpRequest(new ByteArrayInputStream("GET / HTTP/1.1\n\n".getBytes()));
		assertEquals(Method.GET, req.getMethod());
	}

	@Test
	public void unknownRequest() throws IOException {
		HttpRequest req = new HttpRequest(new ByteArrayInputStream("WHAT / HTTP/1.1\n\n".getBytes()));
		assertEquals(Method.UNRECOGNIZED, req.getMethod());
	}
}
