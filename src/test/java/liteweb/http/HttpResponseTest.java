package liteweb.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpResponseTest {

	@Test
	void doHeadRequest() throws IOException {
		HttpRequest req = new HttpRequest(new ByteArrayInputStream("HEAD / HTTP/1.1\n\n".getBytes()));
		HttpResponse res = new HttpResponse(req);
		List<String> headers = res.getHeaders();
		assertTrue(headers.size() > 0);
		assertEquals(HttpResponse.VERSION + " " + Status._200, headers.get(0));
	}

	@Test
	void doGetRequest() throws IOException {
		HttpRequest req = new HttpRequest(new ByteArrayInputStream("GET / HTTP/1.1\n\n".getBytes()));
		HttpResponse res = new HttpResponse(req);
		List<String> headers = res.getHeaders();
		assertTrue(headers.size() > 0);
		assertEquals(HttpResponse.VERSION + " " + Status._200, headers.get(0));
	}

	@Test
	void unknownRequest() throws IOException {
		HttpRequest req = new HttpRequest(new ByteArrayInputStream("WHAT / HTTP/1.1\n\n".getBytes()));
		HttpResponse res = new HttpResponse(req);
		List<String> headers = res.getHeaders();
		assertTrue(headers.size() > 0);
		assertEquals(HttpResponse.VERSION + " " + Status._400, headers.get(0));
	}
}
