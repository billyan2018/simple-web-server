package liteweb.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {

	@Test
	public void doHeadRequest() throws IOException {
		Request req = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\n\n".getBytes()));
		assertEquals(Method.HEAD, req.getMethod());
	}

	@Test
	public void doGetRequest() throws IOException {
		Request req = new Request(new ByteArrayInputStream("GET / HTTP/1.1\n\n".getBytes()));
		assertEquals(Method.GET, req.getMethod());
	}

	@Test
	public void unknownRequest() throws IOException {
		Request req = new Request(new ByteArrayInputStream("WHAT / HTTP/1.1\n\n".getBytes()));
		assertEquals(Method.UNRECOGNIZED, req.getMethod());
	}
}
