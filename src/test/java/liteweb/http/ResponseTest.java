package liteweb.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseTest {

	@Test
	void doHeadRequest() throws IOException {
		Request req = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\n\n".getBytes()));
		Response res = new Response(req);
		List<String> headers = res.getHeaders();
		assertTrue(headers.size() > 0);
		assertEquals(Response.VERSION + " " + Status._200, headers.get(0));
	}

	@Test
	void doGetRequest() throws IOException {
		Request req = new Request(new ByteArrayInputStream("GET / HTTP/1.1\n\n".getBytes()));
		Response res = new Response(req);
		List<String> headers = res.getHeaders();
		assertTrue(headers.size() > 0);
		assertEquals(Response.VERSION + " " + Status._200, headers.get(0));
	}

	@Test
	void unknownRequest() throws IOException {
		Request req = new Request(new ByteArrayInputStream("WHAT / HTTP/1.1\n\n".getBytes()));
		Response res = new Response(req);
		List<String> headers = res.getHeaders();
		assertTrue(headers.size() > 0);
		assertEquals(Response.VERSION + " " + Status._400, headers.get(0));
	}
}
